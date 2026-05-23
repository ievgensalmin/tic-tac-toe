# Quick Start Guide

## Prerequisites
- Java 21
- Maven 3.6+
- Node.js 16+ (for React frontend)

## 5-Minute Setup

### Option 1: Automated (Recommended)

**Windows (PowerShell):**
```powershell
.\build-and-run.ps1
```

**Linux/Mac (Bash):**
```bash
./build-and-run.sh
```

The script will:
- Build all Maven modules
- Build the React frontend
- Start all services in the correct order
- Open the application in your browser

**Skip build if already built:**
```powershell
.\build-and-run.ps1 -SkipBuild
```
```bash
./build-and-run.sh --skip-build
```

### Option 2: Manual Setup

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

**Terminal 1 - Eureka Server:**
```bash
cd eureka-server
mvn spring-boot:run
```
✅ Wait for: "Started EurekaServerApplication"

**Terminal 2 - Game Engine:**
```bash
cd game-engine
mvn spring-boot:run
```
✅ Wait 10 seconds for Eureka registration

**Terminal 3 - Game Session:**
```bash
cd game-session
mvn spring-boot:run
```
✅ Wait 10 seconds for Eureka registration

**Terminal 4 - UI Backend:**
```bash
cd ui-backend
mvn spring-boot:run
```
✅ Wait for: "Started UiBackendApplication"

### 3. Verify Setup

Check Eureka Dashboard: http://localhost:8761

You should see 3 registered services:
- GAME-ENGINE
- GAME-SESSION
- UI-BACKEND

### 4. Play!

Open browser: http://localhost:8080

Click **"Start New Game"** and watch the automated gameplay!

## What You'll See

1. Board starts empty
2. X and O appear automatically (500ms between moves)
3. Move history updates in real-time
4. Winner is announced when game ends
5. Click "Start New Game" to play again (board resets)

## Stopping Services

**If using automated scripts:**
- Windows: Close each PowerShell window or press Ctrl+C
- Linux/Mac: Run `./stop-services.sh`

**If started manually:**
Press `Ctrl+C` in each terminal to stop services.

Stop in reverse order:
1. UI Backend
2. Game Session
3. Game Engine
4. Eureka Server

## Next Steps

- Read [README.md](README.md) for architecture details
- Check [TEST_REPORT.md](TEST_REPORT.md) for test coverage
- Explore the code in each microservice module
