#!/bin/bash
# TicTacToe Microservices - Build and Launch Script (Linux/Mac)
# This script builds all modules and launches all services in the correct order

set -e

# Colors
CYAN='\033[0;36m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Parse arguments
SKIP_BUILD=false
SKIP_FRONTEND=false

while [[ $# -gt 0 ]]; do
    case $1 in
        --skip-build)
            SKIP_BUILD=true
            shift
            ;;
        --skip-frontend)
            SKIP_FRONTEND=true
            shift
            ;;
        *)
            echo -e "${RED}Unknown option: $1${NC}"
            echo "Usage: $0 [--skip-build] [--skip-frontend]"
            exit 1
            ;;
    esac
done

echo -e "${CYAN}========================================"
echo "TicTacToe Microservices Launcher"
echo -e "========================================${NC}"
echo ""

# Get project root
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Build phase
if [ "$SKIP_BUILD" = false ]; then
    echo -e "${YELLOW}[1/2] Building all modules...${NC}"
    cd "$PROJECT_ROOT"
    mvn clean install
    echo -e "${GREEN}Build completed successfully!${NC}"
    echo ""

    if [ "$SKIP_FRONTEND" = false ]; then
        echo -e "${YELLOW}[2/2] Building React frontend...${NC}"
        cd "$PROJECT_ROOT/ui-backend/frontend"
        npm install
        npm run build
        echo -e "${GREEN}Frontend build completed!${NC}"
        echo ""
    fi
else
    echo -e "${YELLOW}Skipping build phase (--skip-build flag set)${NC}"
    echo ""
fi

# Create logs directory
mkdir -p "$PROJECT_ROOT/logs"

# Launch phase
echo -e "${CYAN}========================================"
echo "Starting Services"
echo -e "========================================${NC}"
echo ""
echo -e "${YELLOW}Services will run in background with logs in ./logs/${NC}"
echo -e "${YELLOW}Use 'tail -f logs/<service>.log' to view logs${NC}"
echo ""

# Function to start a service
start_service() {
    local service_name=$1
    local service_dir=$2
    local port=$3
    local wait_time=$4

    echo -e "${GREEN}Starting $service_name (port $port)...${NC}"
    cd "$PROJECT_ROOT/$service_dir"
    nohup mvn spring-boot:run > "$PROJECT_ROOT/logs/$service_name.log" 2>&1 &
    echo $! > "$PROJECT_ROOT/logs/$service_name.pid"
    echo -e "${YELLOW}Waiting $wait_time seconds...${NC}"
    sleep $wait_time
}

# Start services in order
start_service "eureka-server" "eureka-server" "8761" 15
start_service "game-engine" "game-engine" "8081" 10
start_service "game-session" "game-session" "8082" 10
start_service "ui-backend" "ui-backend" "8080" 10

echo ""
echo -e "${CYAN}========================================"
echo -e "${GREEN}All Services Started!${NC}"
echo -e "${CYAN}========================================${NC}"
echo ""
echo -e "${YELLOW}Eureka Dashboard: http://localhost:8761${NC}"
echo -e "${YELLOW}Application UI:   http://localhost:8080${NC}"
echo ""
echo -e "${YELLOW}Service PIDs saved in ./logs/*.pid${NC}"
echo -e "${YELLOW}Service logs available in ./logs/*.log${NC}"
echo ""
echo -e "${YELLOW}To stop all services, run: ./stop-services.sh${NC}"
echo ""
