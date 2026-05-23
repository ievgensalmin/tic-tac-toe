package com.tictactoe.common.dto;

import com.tictactoe.common.enums.Player;

public record MoveRequest(
    Player player,
    int position
) {
    public MoveRequest {
        if (position < 0 || position > 8) {
            throw new IllegalArgumentException("Position must be between 0 and 8");
        }
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
    }
}
