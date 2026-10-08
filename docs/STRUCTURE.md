# Backend Structure

We use package-by-feature, with Clean Architecture layers inside each feature.
Each feature package is one bounded context from our domain model.

All backend code lives in `backend/src/main/java/ie/schrodingerscode/skillswap/`.

## Feature packages

| Package    | Bounded context                                          | Story |
|------------|----------------------------------------------------------|-------|
| auth       | Identity: accounts, registration, login, current user    | S1    |
| profile    | Profiles, teach and learn skills, skill catalogue        | S2    |
| match      | Finding and ranking matches                              | S3    |
| connection | Swap requests and connections                            | S4    |
| chat       | Messaging and blocking                                   | S5    |
| common     | Shared code used by 2 or more features (base exceptions) | -     |
| config     | App-wide configuration (e.g. SecurityConfig)             | -     |
| health     | Health check endpoint                                    | -     |

## Layers inside a feature

Every feature has the same four sub-packages:

| Layer            | What goes here                                                                 |
|------------------|--------------------------------------------------------------------------------|
| `domain`         | Aggregates, entities, value objects, repository interfaces, domain exceptions |
| `application`    | Use-case services: load an aggregate, call its method, save it                 |
| `infrastructure` | Repository implementations (in-memory now, JPA later), JPA entities            |
| `web`            | Controllers, request and response DTOs                                         |

Example for registration:

```
auth/
  domain/
    User.java                       aggregate
    Email.java                      value object
    UserRepository.java             interface
    EmailAlreadyUsedException.java
  application/
    RegisterUser.java               use case
  infrastructure/
    InMemoryUserRepository.java     implements UserRepository
  web/
    RegisterController.java
    RegisterRequest.java
    UserResponse.java
```

## Rules

1. **Dependencies point inwards:** `web` -> `application` -> `domain` <- `infrastructure`.
   `domain` imports nothing from the other layers.
2. **`domain` has no Spring or JPA imports.** No `@Entity`, `@Service`, `@Autowired`.
   It is plain Java, so it can be unit tested without starting Spring.
3. **Business rules live on the aggregate.** For example, `swapRequest.accept(userId)`
   refuses an invalid transition itself. Services coordinate; they do not hold the rules.
4. **Repository interfaces live in `domain`, implementations in `infrastructure`.**
   This is what lets us swap the in-memory repository for a JPA one in Sprint 2
   without changing `domain` or `application`. When you add the JPA version, delete
   the in-memory one in the same PR: if both exist, Spring finds two beans for the
   same interface and the app does not start.
5. **Controllers only translate.** They turn the request into a call to an
   application service and the result into a response DTO. No `if` statements
   about business rules.
6. **Never return domain objects from a controller.** Map them to a response DTO in `web`.
7. **Features talk to each other through interfaces.** If `chat` needs to know whether
   two users are connected, `connection` exposes a small interface for it.
   Do not reach into another feature's `infrastructure`.

## Where do new files go?

- A rule about what is allowed (e.g. "no duplicate skills"): the aggregate in `domain`
- A value with its own rules (e.g. an email, a proficiency level): a value object in `domain`
- A new use case (e.g. "send a request"): a service in `application`
- How data is stored: `infrastructure`
- A new endpoint or request/response shape: `web`
- Something used by 2 or more features: `common`

## Validation

There are two kinds of checks, and they go in different places:

| Kind of check                              | Example                                   | Where                                 |
|--------------------------------------------|-------------------------------------------|---------------------------------------|
| Shape of the request                       | field is present, text is not blank       | annotations on the request DTO in `web` |
| Business rule                              | user is 18+, no duplicate skills          | aggregate or value object in `domain` |

Shape checks use annotations such as `@NotNull`, `@NotBlank` and `@Size` on the
request DTO, plus `@Valid` on the controller parameter. If one fails, the API
returns 400 with a message like `email: must not be blank`.

Value objects still validate themselves, even if the DTO already checked. For example,
`Email` rejects a bad format in its constructor. The domain must not rely on the web
layer having checked first, because other code (tests, other features) can create it too.

## Error handling

All API errors use one format:

```json
{ "status": 404, "message": "User not found" }
```

To return an error, do not edit `GlobalExceptionHandler`. Instead, create a subclass of one
of the base exceptions in `common.exception`, and put it in your feature's `domain` package:

| Base exception        | Use when                                  | HTTP |
|-----------------------|-------------------------------------------|------|
| `ValidationException` | the input breaks a business rule          | 400  |
| `NotFoundException`   | the requested item does not exist         | 404  |
| `ConflictException`   | the request clashes with existing data    | 409  |
| `ForbiddenException`  | the user is not allowed to do this action | 403  |

`UnauthorizedException` (401) also exists, but only `CurrentUser` throws it, when
nobody is logged in. Features should not throw it: if the user is logged in but not
allowed to do something, that is `ForbiddenException` (403).

Example:

    public class EmailAlreadyUsedException extends ConflictException {
        public EmailAlreadyUsedException(String email) {
            super("Email " + email + " is already registered");
        }
    }

Throw it from your domain or application code. `GlobalExceptionHandler` converts it
to the format above and sets the status code, so the domain never knows about HTTP.
Don't build error responses by hand in controllers.

The message is returned to the client, so keep it user-friendly and never include
passwords, SQL or stack traces.

## Current user

To get the ID of the user making the request, inject `CurrentUser`
(in `auth`) into your controller, call `getId()`, and pass the ID to your
application service:

```java
@RestController
public class ProfileSkillController {
    private final CurrentUser currentUser;
    private final AddSkill addSkill;

    public ProfileSkillController(CurrentUser currentUser, AddSkill addSkill) {
        this.currentUser = currentUser;
        this.addSkill = addSkill;
    }

    @PostMapping("/api/profile/teach-skills")
    public ProfileResponse addTeachSkill(@Valid @RequestBody AddTeachSkillRequest request) {
        return ProfileResponse.from(addSkill.teach(currentUser.getId(), request.skillId(), request.proficiency()));
    }
}
```

Application services take the user ID as a parameter rather than injecting
`CurrentUser` themselves. That keeps them independent of HTTP and easy to test.

If nobody is logged in, `getId()` throws and the API returns 401. You don't
need to check for that yourself.

**Until real login exists**, the ID comes from the `X-User-Id` request header.
In Postman, add `X-User-Id: 1` to act as user 1. `GET /api/auth/me` shows
which ID the backend sees. When real login lands, only the implementation
changes; code that uses `CurrentUser` stays the same.

## Database migrations

Tables are only created or changed through Flyway migrations in
`backend/src/main/resources/db/migration/`. Flyway runs them in version order
when the app starts.

- **Naming:** `V<number>__<description>.sql` with the next free number, for example
  `V3__create_swap_requests.sql`. Note the two underscores.
- **Before merging, check the number is still free.** If someone else merged the same
  number first, rename yours to the next one. Two files with the same version stop the
  app from starting.
- **Never edit a migration that is already merged.** Flyway stores a checksum of every
  migration it has run, so editing one breaks everyone's database. Fix mistakes with
  a new migration.
- **Tables that reference `users`** need the users migration to be merged first.
- If Flyway complains after you pull, for example that a migration was not applied,
  reset your local database rather than editing migrations.

## Testing

Test each layer in the cheapest way that works:

| Layer            | How to test                                              | Spring? |
|------------------|----------------------------------------------------------|---------|
| `domain`         | Plain JUnit: create objects, call methods, check results | No      |
| `application`    | Plain JUnit with the in-memory repository                | No      |
| `web`            | `@WebMvcTest` for the controller, application service mocked | Partly  |
| `infrastructure` | `@SpringBootTest` with `@Import(TestcontainersConfiguration.class)`, against real Postgres | Yes |

Most tests should be domain and application tests. They run in milliseconds and
test the rules that matter. Name test classes after the class they test, e.g.
`EmailTests` for `Email`.
