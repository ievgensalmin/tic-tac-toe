package com.tictactoe.engine.service;

import com.tictactoe.common.enums.Cell;
import org.springframework.stereotype.Service;

@Service
public class WinDetectionService {

    private static final int[][] WINNING_LINES = {
        {0, 1, 2}, {3, 4, 5}, {6, 7, 8},  // Rows
        {0, 3, 6}, {1, 4, 7}, {2, 5, 8},  // Columns
        {0, 4, 8}, {2, 4, 6}              // Diagonals
    };

    public boolean checkWin(Cell[] board, Cell symbol) {
        if (symbol == Cell.EMPTY) {
            return false;
        }

        for (int[] line : WINNING_LINES) {
            if (board[line[0]] == symbol &&
                board[line[1]] == symbol &&
                board[line[2]] == symbol) {
                return true;
            }
        }
        return false;
    }

    public boolean isDraw(Cell[] board) {
        for (Cell cell : board) {
            if (cell == Cell.EMPTY) {
                return false;
            }
        }
        return !checkWin(board, Cell.X) && !checkWin(board, Cell.O);
    }
}
