package com.tictactoe.common.dto;

import com.tictactoe.common.enums.Cell;
import com.tictactoe.common.enums.Player;
import com.tictactoe.common.enums.GameStatus;
import com.tictactoe.common.enums.GameResult;

public record GameStateDTO(
    String gameId,
    Cell[] board,
    Player currentPlayer,
    GameStatus status,
    GameResult winner
) {
    public GameStateDTO {
        if (gameId == null || gameId.isBlank()) {
            throw new IllegalArgumentException("Game ID cannot be null or blank");
        }
        if (board == null || board.length != 9) {
            throw new IllegalArgumentException("Board must be a 9-element array");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
    }
}
