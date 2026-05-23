package com.tictactoe.common.dto;

import com.tictactoe.common.enums.Cell;
import com.tictactoe.common.enums.GameStatus;
import com.tictactoe.common.enums.GameResult;

public record MoveResponse(
    Cell[] board,
    GameStatus status,
    GameResult winner
) {
    public MoveResponse {
        if (board == null || board.length != 9) {
            throw new IllegalArgumentException("Board must be a 9-element array");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
    }
}
