# Distributed Tic Tac Toe Microservices - Complete Implementation

## 📦 Architecture Overview

### Services Running:
- **Eureka Server** (Port 8761) - Service Discovery
- **Game Engine** (Port 8081) - Core game logic, move validation, win detection
- **Game Session** (Port 8082) - Session orchestration, AI move generation, SSE streaming
- **UI Backend** (Port 8080) - React SPA with real-time updates

### Technology Stack:
- Java 21
- Spring Boot 3.2.0
- Spring Cloud 2023.0.0 (Eureka, OpenFeign)
- React 18.2.0
- H2 In-Memory Database
- JPA with Optimistic Locking
- Server-Sent Events (SSE)
- Maven Multi-Module

---

## 🚀 How to Use

### Access the Application:
1. Open your browser
2. Navigate to: **http://localhost:8080**
3. Click "Start New Game" button
4. Watch the AI play against itself in real-time!

### What You'll See:
- **Animated Board**: 3x3 grid with X and O symbols appearing in real-time
- **Game Status**: Shows current game state (In Progress, Player 1 Wins, Player 2 Wins, Draw)
- **Move History**: Complete list of all moves with timestamps
- **Real-time Updates**: Board updates instantly via Server-Sent Events

---

## 🧪 Test Results

### All Tests Passing: 16/16 ✅

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

---

## 🏗️ Project Structure


├── pom.xml                           # Parent POM
├── README.md
├── .gitignore
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

---

## 🎯 Key Features Implemented

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
- **REST API**: POST /sessions, POST /sessions/{id}/simulate, GET /sessions/{id}, GET /sessions/{id}/stream

### 3. UI Backend & React Frontend
- **React Components**:
  - Board: Animated 3x3 grid with X/O symbols
  - GameControls: Start button and status display
  - MoveHistory: Scrollable move list with timestamps
- **Real-time Updates**: EventSource API for SSE
- **Responsive Design**: CSS Grid layout
- **Error Handling**: User-friendly error messages
- **Proxy Configuration**: Routes API calls to Game Session

---

## 🔧 Running the Services

### Start All Services:

```bash
# Terminal 1 - Eureka Server
mvn spring-boot:run -pl eureka-server

# Terminal 2 - Game Engine
mvn spring-boot:run -pl game-engine

# Terminal 3 - Game Session
mvn spring-boot:run -pl game-session

# Terminal 4 - UI Backend
mvn spring-boot:run -pl ui-backend
```

### Build Everything:
```bash
mvn clean install
```

### Run Tests:
```bash
mvn test
```

---

## 🌐 Service URLs

- **Eureka Dashboard**: http://localhost:8761
- **Game Engine API**: http://localhost:8081
- **Game Session API**: http://localhost:8082
- **UI Application**: http://localhost:8080

---

## 📊 API Examples

### Create a Session:
```bash
curl -X POST http://localhost:8082/sessions
```

### Start Simulation:
```bash
curl -X POST http://localhost:8082/sessions/{sessionId}/simulate
```

### Get Session Details:
```bash
curl http://localhost:8082/sessions/{sessionId}
```

### Make a Move (Game Engine):
```bash
curl -X POST http://localhost:8081/games/{gameId}/move \
  -H "Content-Type: application/json" \
  -d '{"player":"PLAYER1","position":4}'
```


---

## 🚀 Future Enhancements

1. **Fix Simulation Flow** - Debug move persistence in automated gameplay
2. **Persistent Storage** - Replace H2 with PostgreSQL/MySQL
3. **API Gateway** - Add Spring Cloud Gateway
4. **Distributed Tracing** - Add Sleuth + Zipkin
5. **Metrics** - Add Micrometer + Prometheus
6. **WebSockets** - Replace SSE with WebSocket for bidirectional communication
7. **Minimax AI** - Implement perfect play algorithm
8. **Multi-game Support** - Allow multiple concurrent sessions
9. **User Authentication** - Add Spring Security
10. **Replay Feature** - Replay past games from move history
