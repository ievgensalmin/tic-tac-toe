package com.tictactoe.common.dto;

import com.tictactoe.common.enums.Player;
import java.time.LocalDateTime;

public record MoveDTO(
    Player player,
    int position,
    LocalDateTime timestamp
) {
    public MoveDTO {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        if (position < 0 || position > 8) {
            throw new IllegalArgumentException("Position must be between 0 and 8");
        }
        if (timestamp == null) {
            throw new IllegalArgumentException("Timestamp cannot be null");
        }
    }
}
