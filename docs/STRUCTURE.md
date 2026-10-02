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

To return an error, throw an exception (for example `ResourceNotFoundException`)
from your service. `GlobalExceptionHandler` in `common/exception` converts it
to the format above. Don't build error responses by hand in controllers.