#!/bin/bash
# TicTacToe Microservices - Stop All Services Script (Linux/Mac)

# Colors
CYAN='\033[0;36m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOGS_DIR="$PROJECT_ROOT/logs"

echo -e "${CYAN}========================================"
echo "Stopping TicTacToe Services"
echo -e "========================================${NC}"
echo ""

if [ ! -d "$LOGS_DIR" ]; then
    echo -e "${YELLOW}No services running (logs directory not found)${NC}"
    exit 0
fi

# Stop services in reverse order
services=("ui-backend" "game-session" "game-engine" "eureka-server")

for service in "${services[@]}"; do
    pid_file="$LOGS_DIR/$service.pid"
    if [ -f "$pid_file" ]; then
        pid=$(cat "$pid_file")
        if ps -p $pid > /dev/null 2>&1; then
            echo -e "${YELLOW}Stopping $service (PID: $pid)...${NC}"
            kill $pid
            # Wait for graceful shutdown
            for i in {1..10}; do
                if ! ps -p $pid > /dev/null 2>&1; then
                    echo -e "${GREEN}$service stopped${NC}"
                    break
                fi
                sleep 1
            done
            # Force kill if still running
            if ps -p $pid > /dev/null 2>&1; then
                echo -e "${RED}Force killing $service...${NC}"
                kill -9 $pid
            fi
        else
            echo -e "${YELLOW}$service not running (stale PID file)${NC}"
        fi
        rm "$pid_file"
    else
        echo -e "${YELLOW}$service PID file not found${NC}"
    fi
done

echo ""
echo -e "${GREEN}All services stopped${NC}"
echo ""
