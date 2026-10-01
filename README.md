# SkillSwap

## Requirements

- Java 21 or newer
- Node.js (LTS)
- Docker Desktop

## Run the backend

```
cd backend
./mvnw spring-boot:run        # Linux / macOS
.\mvnw.cmd spring-boot:run    # Windows
```

Runs on http://localhost:8080

## Run the frontend

```
cd frontend
npm install
npm run dev
```

Runs on http://localhost:5173

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


See [Backend Structure](docs/STRUCTURE.md) for where files go.
```