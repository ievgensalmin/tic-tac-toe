# Distributed Tic Tac Toe Microservices

A distributed Tic Tac Toe application where the game is played automatically by AI microservices with real-time updates via Server-Sent Events (SSE).

## Table of Contents
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Quick Start](#quick-start)
- [Project Structure](#project-structure)
- [Key Features](#key-features)
- [API Endpoints](#api-endpoints)
- [Testing](#testing)
- [Troubleshooting](#troubleshooting)

## Architecture

### Services
- **Eureka Server** (port 8761) - Service discovery and registration
- **Game Engine** (port 8081) - Core game logic, move validation, and win detection
- **Game Session** (port 8082) - Session orchestration, AI move generation, and SSE streaming
- **UI Backend** (port 8080) - React SPA with proxy to Game Session service

### How It Works
1. **Start New Game**: Creates a new session and clears the board
2. **Automated Gameplay**: Two AI players make moves automatically using smart random strategy
3. **Real-time Updates**: SSE streams each move to the UI instantly (500ms between moves)
4. **Game Completion**: Winner is displayed when game ends
5. **New Game**: Click "Start New Game" again to play another round (board resets automatically)

## Tech Stack

- **Backend**: Java 21, Spring Boot 3.2.0, Spring Cloud 2023.0.0 (Eureka, OpenFeign)
- **Frontend**: React 18.2.0
- **Build Tool**: Maven (multi-module)
- **Database**: H2 (in-memory) with JPA and Optimistic Locking
- **Real-time**: Server-Sent Events (SSE)

## Quick Start

### Prerequisites
- Java 21
- Maven 3.6+
- Node.js 16+ (for React frontend)

### Automated Setup (Recommended)

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
4. Open the application in your browser at http://localhost:8080

**Script Options:**
- Skip build phase: `.\build-and-run.ps1 -SkipBuild` or `./build-and-run.sh --skip-build`
- Skip frontend build: `.\build-and-run.ps1 -SkipFrontend` or `./build-and-run.sh --skip-frontend`

**Stopping Services:**
- Windows: Close each PowerShell window or press Ctrl+C
- Linux/Mac: Run `./stop-services.sh`

### Manual Setup (Alternative)

**1. Build Everything (One-time)**
```bash
# From project root
mvn clean install

# Build React frontend
cd ui-backend/frontend
npm install
npm run build
cd ../..
```

**2. Start Services (In Order)**

Open 4 separate terminals:

**Terminal 1 - Eureka Server (port 8761):**
```bash
cd eureka-server
mvn spring-boot:run
```
✅ Wait for: "Started EurekaServerApplication"

**Terminal 2 - Game Engine (port 8081):**
```bash
cd game-engine
mvn spring-boot:run
```
✅ Wait ~10 seconds for Eureka registration

**Terminal 3 - Game Session (port 8082):**
```bash
cd game-session
mvn spring-boot:run
```
✅ Wait ~10 seconds for Eureka registration

**Terminal 4 - UI Backend (port 8080):**
```bash
cd ui-backend
mvn spring-boot:run
```
✅ Wait for: "Started UiBackendApplication"

**3. Verify Setup**

Check Eureka Dashboard: http://localhost:8761

You should see 3 registered services:
- GAME-ENGINE
- GAME-SESSION
- UI-BACKEND

**4. Play!**

Open browser: http://localhost:8080

Click **"Start New Game"** and watch the automated gameplay!

## Project Structure

```
├── pom.xml                           # Parent POM
├── README.md
├── .gitignore
├── build-and-run.ps1                 # Windows automation script
├── build-and-run.sh                  # Linux/Mac automation script
├── stop-services.sh                  # Service shutdown script
│
├── common/                           # Shared Components
│   ├── pom.xml
│   └── src/main/java/com/tictactoe/common/
│       ├── enums/                    # Cell, Player, GameStatus, etc.
│       ├── dto/                      # Request/Response DTOs
│       └── exception/                # Custom exceptions
│
├── eureka-server/                    # Service Discovery
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/tictactoe/eureka/
│       │   └── EurekaServerApplication.java
│       └── resources/
│           └── application.yml       # Port 8761
│
├── game-engine/                      # Core Game Logic
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/tictactoe/engine/
│       │   ├── GameEngineApplication.java
│       │   ├── entity/               # Game entity with @Version
│       │   ├── repository/           # GameRepository
│       │   ├── service/              # GameService, WinDetectionService
│       │   ├── controller/           # GameController
│       │   └── exception/            # GlobalExceptionHandler
│       └── test/java/                # 11 tests
│
├── game-session/                     # Orchestration & AI
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/tictactoe/session/
│       │   ├── GameSessionApplication.java
│       │   ├── entity/               # Session, Move entities
│       │   ├── repository/           # SessionRepository, MoveRepository
│       │   ├── service/              # SessionService, MoveGenerationService, SimulationService
│       │   ├── client/               # GameEngineClient (Feign)
│       │   ├── controller/           # SessionController with SSE
│       │   └── exception/            # GlobalExceptionHandler
│       └── test/java/                # 5 tests
│
└── ui-backend/                       # React UI
    ├── pom.xml
    ├── frontend/                     # React App
    │   ├── package.json
    │   ├── public/
    │   │   └── index.html
    │   └── src/
    │       ├── App.js                # Main component with SSE
    │       ├── App.css
    │       ├── index.js
    │       ├── index.css
    │       └── components/
    │           ├── Board.js          # 3x3 grid display
    │           ├── Board.css
    │           ├── GameControls.js   # Start button & status
    │           ├── GameControls.css
    │           ├── MoveHistory.js    # Move list
    │           └── MoveHistory.css
    └── src/main/
        ├── java/com/tictactoe/ui/
        │   ├── UiBackendApplication.java
        │   └── config/
        │       └── WebConfig.java    # CORS & routing
        └── resources/
            ├── application.yml       # Port 8080
            └── static/               # React build output
```

## Key Features

### 1. Game Engine Service
- **Move Validation**: Position range, cell occupancy, player turn, game status
- **Win Detection**: All 8 winning lines (3 rows, 3 columns, 2 diagonals)
- **Draw Detection**: Full board with no winner
- **Optimistic Locking**: @Version annotation prevents concurrent modification
- **REST API**: POST /games/{gameId}/move, GET /games/{gameId}

### 2. Game Session Service
- **Smart Random AI**:
  1. Check if current player can win → take winning move
  2. Check if opponent can win → block opponent
  3. Otherwise → random empty position
- **Session Management**: Create, track, complete sessions
- **Move History**: Persist all moves with timestamps
- **SSE Streaming**: Real-time move events to UI
- **Feign Client**: Inter-service communication with Game Engine

### 3. UI Backend & React Frontend
- **React Components**:
  - Board: Animated 3x3 grid with X/O symbols
  - GameControls: Start button and status display
  - MoveHistory: Scrollable move list with timestamps
- **Real-time Updates**: EventSource API for SSE
- **Responsive Design**: CSS Grid layout
- **Error Handling**: User-friendly error messages
- **Proxy Configuration**: Routes API calls to Game Session

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

### API Examples

**Create a Session:**
```bash
curl -X POST http://localhost:8082/sessions
```

**Start Simulation:**
```bash
curl -X POST http://localhost:8082/sessions/{sessionId}/simulate
```

**Get Session Details:**
```bash
curl http://localhost:8082/sessions/{sessionId}
```

**Make a Move (Game Engine):**
```bash
curl -X POST http://localhost:8081/games/{gameId}/move \
  -H "Content-Type: application/json" \
  -d '{"player":"PLAYER1","position":4}'
```

## Testing

### Run All Tests
```bash
mvn clean test
```

### Test Results: 16/16 ✅

**Game Engine Tests (11/11):**
- ✅ Horizontal wins (3 tests - rows 0, 1, 2)
- ✅ Vertical wins (3 tests - columns 0, 1, 2)
- ✅ Diagonal wins (2 tests - both diagonals)
- ✅ No win detection
- ✅ Draw detection
- ✅ Not draw with empty cells

**Game Session Tests (5/5):**
- ✅ Winning move generation
- ✅ Blocking move generation
- ✅ Random move generation
- ✅ Vertical win detection
- ✅ Diagonal win detection


### Build failures
- Run `mvn clean install` from project root
- Check internet connection for dependency downloads
- Verify Node.js is installed for frontend build

## Service URLs

- **UI Application**: http://localhost:8080
- **Eureka Dashboard**: http://localhost:8761
- **Game Engine API**: http://localhost:8081
- **Game Session API**: http://localhost:8082

## Future Enhancements

1. **Persistent Storage** - Replace H2 with PostgreSQL/MySQL
2. **API Gateway** - Add Spring Cloud Gateway
3. **Distributed Tracing** - Add Sleuth + Zipkin
4. **Metrics** - Add Micrometer + Prometheus
5. **WebSockets** - Replace SSE with WebSocket for bidirectional communication
6. **Minimax AI** - Implement perfect play algorithm
7. **Multi-game Support** - Allow multiple concurrent sessions
8. **User Authentication** - Add Spring Security
9. **Replay Feature** - Replay past games from move history
10. **Containerization** - Add Docker and Docker Compose support
