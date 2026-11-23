# SkillSwap

SkillSwap is a web app for swapping skills. You list what you can teach and what you want to learn, SkillSwap suggests people whose skills complement yours, and you can send them a swap request. A built-in live chat allows you to message your connections and plan study sessions together. You can also block users you don't want to hear from.

Built by Schrödinger's Code.

## Stack

| Part         | Technology                                                                         |
| ------------ | ---------------------------------------------------------------------------------- |
| Backend      | Java 21, Spring Boot, Maven (`backend/`)                                           |
| Database     | PostgreSQL, schema managed by Flyway                                               |
| Frontend     | React, Vite, npm (`frontend/`)                                                     |
| Tests        | JUnit, `@WebMvcTest`, Testcontainers (real Postgres)                               |
| Deployment   | One Docker image on GHCR, auto-updated by Watchtower                               |
| Live updates | WebSocket (STOMP): Spring WebSocket on the backend, @stomp/stompjs in the frontend |

The backend uses package-by-feature with Clean Architecture layers inside each feature. See [Backend Structure](docs/STRUCTURE.md) for where files go.

## Requirements

- Docker Desktop (needed for the database and for the tests)
- Either the dev container (nothing else to install), or:
    - Java 21 or newer
    - Node.js (LTS)

## Run it locally

### Option A: dev container (recommended)

- **Codespaces:** Code → Codespaces → Create codespace on main
- **Locally:** install Docker Desktop and VS Code with the Dev Containers extension, open the repo, then "Reopen in Container"

Postgres starts automatically and the `dev` profile is already set.

In the container's first terminal run the backend:

```
cd backend && ./mvnw spring-boot:run     # http://localhost:8080
```

Open another terminal alongside the first one and run the frontend:

```
cd frontend && npm install && npm run dev  # http://localhost:5173
```

### Option B: Docker Compose

Start the database from the repo root:

```
docker compose up -d
```

Then run the backend with the `dev` profile, and the frontend:

```
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev          # Linux / macOS
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"    # Windows
```

```
cd frontend
npm install
npm run dev
```

Backend: http://localhost:8080 · Frontend: http://localhost:5173

### Database

Wipe the database and start fresh:

```
docker compose down -v
```

See the tables:

```
docker exec -it skillswap-db psql -U skillswap -d skillswap -c '\dt'
```

## Run the tests

Docker must be running: tests start their own temporary Postgres through Testcontainers and never touch your local database.

```
cd backend
./mvnw test            # Linux / macOS
.\mvnw.cmd test        # Windows
```

Any test class with `@SpringBootTest` needs `@Import(TestcontainersConfiguration.class)`.

## Checks before you open a pull request

CI runs these on every pull request, so run them locally first:

In one terminal:

```
cd backend
./mvnw verify                 # tests
./mvnw spotless:check         # Java formatting (fix with ./mvnw spotless:apply)
```

In another terminal alongside the first one:

```
cd frontend
npm run lint
npm run build
npm run format:check          # frontend formatting (fix with npm run format)
```

The pull request title must contain a Jira key such as `SCRUM-12`. A check fails without it.

## Changing the schema

The schema is managed by Flyway. Add a new file in `backend/src/main/resources/db/migration` named `V<next number>__<description>.sql`, for example `V3__create_swap_requests.sql`. Migrations run automatically when the backend starts.

Never edit a migration that is already on `main`. Flyway checks every migration it has run, and a changed file stops the backend from starting for everyone. Add a new migration instead.

## Deployment

Staging is deployed automatically whenever a pull request is merged to `main`. Nobody needs to deploy by hand.

1. The `CI` workflow (`.github/workflows/`) runs the backend tests, the frontend lint and build, and the format checks.
2. If all of those pass on a push to `main`, the `deploy` job builds the Docker image and pushes it to GitHub Container Registry (GHCR) as `ghcr.io/schrodingers-code-ie/skillswap:latest`. The image is tagged `latest` only, and the commit SHA is passed in as the `GIT_SHA` build argument (the app exposes it as `APP_VERSION`).

    The root `Dockerfile` builds everything into one image: it builds the frontend, copies the result into the backend's static resources, and packages the backend as a single jar. So in staging, one container serves both the API and the frontend on port 8080.

3. [Watchtower](https://containrrr.dev/watchtower/) on the staging server checks GHCR, pulls the new image and restarts the container. This takes up to 5 minutes after the push.
4. Staging is live at https://skillswap.ofhovi.com.

Pull requests run the checks but never deploy.

### Environment variables

The container needs these to start:

| Variable                 | Purpose                                                                                           |
| ------------------------ | ------------------------------------------------------------------------------------------------- |
| `SPRING_PROFILES_ACTIVE` | Which config profile to use: `prod`. The image does not set one, so it must be set on the server. |
| `DB_URL`                 | JDBC URL of the Postgres database, e.g. `jdbc:postgresql://<host>:5432/<database>`                |
| `DB_USERNAME`            | Database user                                                                                     |
| `DB_PASSWORD`            | Database password                                                                                 |

These are set on the staging server, not in the repo. Never commit real values.

The `dev` profile (used locally) does not need any of these. It connects to `localhost` with the credentials from `compose.yaml`. The dev container sets `DB_HOST=postgres` for you.

## Common issues

**Backend fails with "release version 21 not supported"**

Maven is using an older Java. Check which one with:

```
.\mvnw.cmd -v      # Windows
./mvnw -v          # macOS / Linux
```

If it shows a version below 21, point `JAVA_HOME` at Java 21 or newer:

- **Windows:** `setx JAVA_HOME "C:\path\to\jdk-21"`, then open a new terminal
- **Linux:** `sudo apt install openjdk-21-jdk`, then add `export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64` to `~/.bashrc` and open a new terminal
- **macOS:** add `export JAVA_HOME=$(/usr/libexec/java_home -v 21)` to `~/.zshrc`
- **IntelliJ:** File → Project Structure → SDK → select 21 or newer

**Backend fails with "Failed to configure a DataSource"**

The backend started without the `dev` profile, so it doesn't know where the database is. Check the second line of the startup log. It should say `The following 1 profile is active: "dev"`. If it says `No active profile set`, start it with the command from [Option B](#option-b-docker-compose).

In a test, the same error means the test class is missing `@Import(TestcontainersConfiguration.class)`.

**Chat only updates every few seconds / console shows [socket] disconnected**

The WebSocket isn't connecting, so the chat is polling. Open the app through the Vite dev server
(http://localhost:5173), not on a different port, so /ws is proxied, and make sure you are logged in:
the /ws handshake is rejected without a session.
