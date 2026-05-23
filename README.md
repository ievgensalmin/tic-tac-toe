# Distributed Tic Tac Toe Microservices

A distributed Tic Tac Toe application where the game is played automatically by microservices with real-time updates via Server-Sent Events (SSE).

## Architecture

- **Eureka Server** (port 8761) - Service discovery and registration
- **Game Engine** (port 8081) - Core game logic and win detection
- **Game Session** (port 8082) - Session orchestration, automated gameplay, and SSE streaming
- **UI Backend** (port 8080) - React SPA with proxy to Game Session service

## Tech Stack

- Java 21
- Spring Boot 3.2.0
- Spring Cloud 2023.0.0 (Eureka, OpenFeign)
- React 18.2.0
- Maven
- H2 Database (in-memory)
- Server-Sent Events (SSE)

## Running Locally

### Quick Start (Recommended)

Use the provided scripts to build and launch all services automatically:

**Windows (PowerShell):**
```powershell
.\build-and-run.ps1
```

**Linux/Mac (Bash):**
```bash
./build-and-run.sh
```

The scripts will:
1. Build all Maven modules
2. Build the React frontend
3. Start all services in the correct order (Eureka → Game Engine → Game Session → UI Backend)
4. Open the application in your browser

**Script Options:**
- Skip build phase: `.\build-and-run.ps1 -SkipBuild` or `./build-and-run.sh --skip-build`
- Skip frontend build: `.\build-and-run.ps1 -SkipFrontend` or `./build-and-run.sh --skip-frontend`

**Stopping Services:**
- Windows: Close each PowerShell window or press Ctrl+C
- Linux/Mac: Run `./stop-services.sh`

### Manual Start (Alternative)

If you prefer to start services manually:

1. **Start Eureka Server** (port 8761)
   ```bash
   cd eureka-server
   mvn spring-boot:run
   ```
   Wait until you see: `Started EurekaServerApplication`

2. **Start Game Engine** (port 8081)
   ```bash
   cd game-engine
   mvn spring-boot:run
   ```
   Wait ~10 seconds for Eureka registration

3. **Start Game Session** (port 8082)
   ```bash
   cd game-session
   mvn spring-boot:run
   ```
   Wait ~10 seconds for Eureka registration

4. **Build React Frontend** (one-time)
   ```bash
   cd ui-backend/frontend
   npm install
   npm run build
   ```

5. **Start UI Backend** (port 8080)
   ```bash
   cd ui-backend
   mvn spring-boot:run
   ```

6. **Open Browser**
   Navigate to: http://localhost:8080

Click **"Start New Game"** to watch the automated gameplay with real-time updates.

## How It Works

1. **Start New Game**: Creates a new session and clears the board
2. **Automated Gameplay**: Two AI players make moves automatically
3. **Real-time Updates**: SSE streams each move to the UI instantly
4. **Game Completion**: Winner is displayed when game ends
5. **New Game**: Click "Start New Game" again to play another round (board resets automatically)

## Building

Build all modules:
```bash
mvn clean package
```

## Testing

Run all tests:
```bash
mvn clean test
```

Test results: 16 tests across Game Engine and Game Session modules (100% pass rate).

## Troubleshooting

### "Failed to create session" error
- Ensure Eureka Server is running first
- Wait 10-15 seconds after starting each service for Eureka registration
- Verify all services are registered at http://localhost:8761

### No moves appearing in UI
- Check that Game Engine is registered with Eureka
- Verify SSE connection in browser DevTools (Network tab)
- Check Game Session logs for simulation errors

### Service won't start
- Ensure ports 8761, 8080, 8081, 8082 are available
- Check Java version (requires Java 21)
- Verify Maven dependencies are downloaded

## API Endpoints

### UI Backend (port 8080)
- `POST /sessions` - Create new game session
- `POST /sessions/{sessionId}/simulate` - Start automated gameplay
- `GET /sessions/{sessionId}` - Get session details
- `GET /sessions/{sessionId}/stream` - SSE stream for real-time updates

### Game Session (port 8082)
- Same endpoints as UI Backend (proxied through)

### Game Engine (port 8081)
- `POST /games/{gameId}/move` - Make a move (creates game if not exists)
- `GET /games/{gameId}` - Get current game state

### Eureka Server (port 8761)
- `GET /` - Eureka dashboard
- `GET /eureka/apps` - List registered services
