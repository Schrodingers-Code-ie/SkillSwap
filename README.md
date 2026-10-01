# SkillSwap

## Requirements

- Java 21 or newer
- Node.js (LTS)
- Docker Desktop

## Run the backend

Start the database first (see [Database](#database)), then:

```
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev          # Linux / macOS
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"    # Windows
```

In the dev container the `dev` profile is already set, so `./mvnw spring-boot:run` is enough.

Runs on http://localhost:8080

## Run the frontend

```
cd frontend
npm install
npm run dev
```

Runs on http://localhost:5173

## Database

The backend uses PostgreSQL, running in Docker.

- **In the dev container:** Postgres starts automatically.
- **Without the dev container:** run `docker compose up -d` from the repo root.

To wipe the database and start fresh:

```
docker compose down -v
```

To see the tables:

```
docker exec -it skillswap-db psql -U skillswap -d skillswap -c '\dt'
```

### Changing the schema

The schema is managed by Flyway. Changes go in `backend/src/main/resources/db/migration` as new files named `V2__add_skills_table.sql`, `V3__...` and so on. They run automatically when the backend starts.

Never edit a migration that's already on `main`. Flyway checks every migration it has run, and a changed file stops the backend from starting for everyone. Add a new migration instead.

### Tests

Tests start their own temporary Postgres through Testcontainers, so Docker needs to be running. They never touch your local database.

Any test class with `@SpringBootTest` needs `@Import(TestcontainersConfiguration.class)`.

## Run in a dev container (no Java/Node install needed)

- **Codespaces:** Code → Codespaces → Create codespace on main
- **Locally:** install Docker Desktop and VS Code with the Dev Containers extension, open the repo, then "Reopen in Container"

Then run the backend and frontend as above, from the container's terminal.

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

The backend started without the `dev` profile, so it doesn't know where the database is. Check the second line of the startup log. It should say `The following 1 profile is active: "dev"`. If it says `No active profile set`, start it with the command from [Run the backend](#run-the-backend).

In a test, the same error means the test class is missing `@Import(TestcontainersConfiguration.class)`.
