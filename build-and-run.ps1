# TicTacToe Microservices - Build and Launch Script (Windows)
# This script builds all modules and launches all services in the correct order

param(
    [switch]$SkipBuild,
    [switch]$SkipFrontend
)

$ErrorActionPreference = "Stop"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "TicTacToe Microservices Launcher" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Get project root
$ProjectRoot = $PSScriptRoot

# Build phase
if (-not $SkipBuild) {
    Write-Host "[1/2] Building all modules..." -ForegroundColor Yellow
    Set-Location $ProjectRoot
    mvn clean install
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Build failed!" -ForegroundColor Red
        exit 1
    }
    Write-Host "Build completed successfully!" -ForegroundColor Green
    Write-Host ""

    if (-not $SkipFrontend) {
        Write-Host "[2/2] Building React frontend..." -ForegroundColor Yellow
        Set-Location "$ProjectRoot\ui-backend\frontend"
        npm install
        npm run build
        if ($LASTEXITCODE -ne 0) {
            Write-Host "Frontend build failed!" -ForegroundColor Red
            exit 1
        }
        Write-Host "Frontend build completed!" -ForegroundColor Green
        Write-Host ""
    }
} else {
    Write-Host "Skipping build phase (--SkipBuild flag set)" -ForegroundColor Yellow
    Write-Host ""
}

# Launch phase
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Starting Services" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Services will start in separate windows." -ForegroundColor Yellow
Write-Host "Close each window or press Ctrl+C to stop a service." -ForegroundColor Yellow
Write-Host ""

# Start Eureka Server
Write-Host "[1/4] Starting Eureka Server (port 8761)..." -ForegroundColor Green
$eurekaCmd = "cd '$ProjectRoot\eureka-server'; Write-Host 'Eureka Server' -ForegroundColor Cyan; mvn spring-boot:run"
Start-Process powershell -ArgumentList "-NoExit", "-Command", $eurekaCmd
Write-Host "Waiting 15 seconds for Eureka to start..." -ForegroundColor Yellow
Start-Sleep -Seconds 15

# Start Game Engine
Write-Host "[2/4] Starting Game Engine (port 8081)..." -ForegroundColor Green
$engineCmd = "cd '$ProjectRoot\game-engine'; Write-Host 'Game Engine' -ForegroundColor Cyan; mvn spring-boot:run"
Start-Process powershell -ArgumentList "-NoExit", "-Command", $engineCmd
Write-Host "Waiting 10 seconds for Eureka registration..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

# Start Game Session
Write-Host "[3/4] Starting Game Session (port 8082)..." -ForegroundColor Green
$sessionCmd = "cd '$ProjectRoot\game-session'; Write-Host 'Game Session' -ForegroundColor Cyan; mvn spring-boot:run"
Start-Process powershell -ArgumentList "-NoExit", "-Command", $sessionCmd
Write-Host "Waiting 10 seconds for Eureka registration..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

# Start UI Backend
Write-Host "[4/4] Starting UI Backend (port 8080)..." -ForegroundColor Green
$uiCmd = "cd '$ProjectRoot\ui-backend'; Write-Host 'UI Backend' -ForegroundColor Cyan; mvn spring-boot:run"
Start-Process powershell -ArgumentList "-NoExit", "-Command", $uiCmd
Write-Host "Waiting 10 seconds for UI Backend to start..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "All Services Started!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Eureka Dashboard: http://localhost:8761" -ForegroundColor Yellow
Write-Host "Application UI:   http://localhost:8080" -ForegroundColor Yellow
Write-Host ""
Write-Host "To stop services: Close each PowerShell window or press Ctrl+C" -ForegroundColor Yellow
Write-Host ""
Write-Host "Press any key to open the application in your browser..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
Start-Process "http://localhost:8080"
