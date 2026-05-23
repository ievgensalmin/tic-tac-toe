package com.tictactoe.session.service;

import com.tictactoe.common.enums.Cell;
import com.tictactoe.common.enums.Player;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class MoveGenerationService {

    private final Random random = new Random();

    public int generateMove(Cell[] board, Player currentPlayer) {
        Cell playerSymbol = currentPlayer.toCell();
        Cell opponentSymbol = currentPlayer.opponent().toCell();

        // 1. Check if current player can win
        Integer winningMove = findWinningMove(board, playerSymbol);
        if (winningMove != null) {
            return winningMove;
        }

        // 2. Check if opponent can win and block
        Integer blockingMove = findWinningMove(board, opponentSymbol);
        if (blockingMove != null) {
            return blockingMove;
        }

        // 3. Random move from available positions
        List<Integer> emptyPositions = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            if (board[i] == Cell.EMPTY) {
                emptyPositions.add(i);
            }
        }

        if (emptyPositions.isEmpty()) {
            throw new IllegalStateException("No empty positions available");
        }

        return emptyPositions.get(random.nextInt(emptyPositions.size()));
    }

    private Integer findWinningMove(Cell[] board, Cell symbol) {
        int[][] winningLines = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8},  // Rows
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},  // Columns
            {0, 4, 8}, {2, 4, 6}              // Diagonals
        };

        for (int[] line : winningLines) {
            int symbolCount = 0;
            int emptyIndex = -1;

            for (int pos : line) {
                if (board[pos] == symbol) {
                    symbolCount++;
                } else if (board[pos] == Cell.EMPTY) {
                    emptyIndex = pos;
                }
            }

            if (symbolCount == 2 && emptyIndex != -1) {
                return emptyIndex;
            }
        }

        return null;
    }
}
