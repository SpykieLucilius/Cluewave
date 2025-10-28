# Cluewave with Spring Boot Backend

This repository contains a reworked version of the **Cluewave** project where the original
Node/Express backend has been replaced by a **Spring Boot** backend while keeping the
frontend intact.  The new backend serves REST endpoints and real‑time updates via
WebSocket (STOMP) and is fully deployable on **Heroku**.

## Project Structure

```
cluewave/
├── backend/              # Spring Boot application
│   ├── pom.xml           # Maven build configuration
│   └── src/main/         # Java sources, resources and static assets
├── frontend/             # Existing frontend code (React/Vite)
│   ├── package.json      # Defines dev/build scripts and dependencies
│   └── README.md         # Instructions for front‑end usage
├── Procfile              # Heroku launch script (runs the Spring Boot jar)
├── package.json          # Monorepo scripts for Heroku build pipeline
└── .gitignore
```

### Backend Highlights

- Built with Spring Boot 3.3 and Java 21.
- Uses `spring‑boot-starter-web` for REST controllers and `spring‑boot-starter-websocket` for
  STOMP over WebSocket real‑time communication.
- In‑memory game state managed by `RoomService`; rooms and players are stored in a
  `ConcurrentHashMap`.
- Endpoints exposed under `/api/rooms`:
  - `POST /api/rooms` – create a new room (body: `{ "hostName": "..." }`).
  - `POST /api/rooms/{code}/join` – join an existing room (body: `{ "playerName": "..." }`).
  - `GET /api/rooms/{code}` – retrieve the current room state.
  - `POST /api/rooms/{code}/start-round` – start a new round.
- Broadcasts room updates on `/topic/room/{code}` using STOMP.  Frontend clients can
  subscribe with a library such as `@stomp/stompjs`.

### Frontend Notes

The `frontend` directory is intentionally minimal here.  To migrate from the original
repository, copy your existing `frontend` contents into this directory.  A minimal
`package.json` and README are provided.  Use Vite or your preferred bundler to build
the assets.

### Heroku Deployment

Heroku automatically detects this as a multi‑buildpack app (Node and Java).  The
`package.json` at the root defines two lifecycle scripts:

* `heroku-prebuild` – installs dependencies in the `frontend` directory.
* `heroku-postbuild` – builds the frontend and copies the generated assets into
  `backend/src/main/resources/static/` so Spring Boot can serve them.
* `start` – launches the Spring Boot jar.  The `Procfile` instructs Heroku to
  run this command with the appropriate port.

### Local Development

1. **Frontend:**
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

2. **Backend:** Ensure you have Java 21 and Maven installed.
   ```bash
   cd backend
   mvn spring-boot:run
   ```

During development you may want to allow cross‑origin requests.  The
`application.properties` file is configured to allow all origins and methods for
simplicity.  Adjust these settings as needed for production.