# Backend Structure

We use package-by-feature. Each feature owns its own controller, service,
repository, DTOs, and entities.

All backend code lives in `backend/src/main/java/ie/schrodingerscode/skillswap/`.

| Package    | Responsibility                                      |
|------------|-----------------------------------------------------|
| auth       | Login, registration, tokens                         |
| user       | User profiles                                       |
| skill      | Skills a user offers or wants                       |
| match      | Matching users by skills                            |
| connection | Connection requests between users                   |
| chat       | Messaging                                           |
| common     | Shared code: exceptions, utilities                  |
| config     | App-wide configuration (e.g. SecurityConfig)        |
| health     | Health check endpoint                               |

## Where do new files go?

- A new endpoint for a feature: that feature's `controller` package
- Business logic: that feature's `service` package
- Database access: that feature's `repository` package
- Request/response objects: that feature's `dto` package
- Something used by 2 or more features: `common`

## Error handling

All API errors use one format:

```json
{ "status": 404, "message": "User not found" }
```

To return an error, do not edit `GlobalExceptionHandler`. Instead, create a subclass of one
of the base exceptions in `common.exception`:

| Base exception        | Use when                                  | HTTP |
|-----------------------|-------------------------------------------|------|
| `ValidationException` | the input breaks a business rule          | 400  |
| `NotFoundException`   | the requested item does not exist         | 404  |
| `ConflictException`   | the request clashes with existing data    | 409  |
| `ForbiddenException`  | the user is not allowed to do this action | 403  |
| `UnauthorizedException` | nobody is logged in (thrown by `CurrentUser`, don't throw it yourself) | 401 |

Example:

    public class EmailAlreadyUsedException extends ConflictException {
        public EmailAlreadyUsedException(String email) {
            super("Email " + email + " is already registered");
        }
    }

The message is returned to the client, so keep it user-friendly and never include
passwords, SQL or stack traces. The status code is set in `GlobalExceptionHandler`.

## Current user

To get the ID of the user making the request, inject `CurrentUser`
(in `auth`) and call `getId()`:

```java
@RestController
public class SkillController {
    private final CurrentUser currentUser;

    public SkillController(CurrentUser currentUser) {
        this.currentUser = currentUser;
    }

    @GetMapping("/api/skills/mine")
    public List<SkillDto> mine() {
        return skillService.findByUser(currentUser.getId());
    }
}
```

If nobody is logged in, `getId()` throws and the API returns 401. You don't
need to check for that yourself.

**Until real login exists**, the ID comes from the `X-User-Id` request header.
In Postman, add `X-User-Id: 1` to act as user 1. `GET /api/auth/me` shows
which ID the backend sees. When real login lands, only the implementation
changes; code that uses `CurrentUser` stays the same.
