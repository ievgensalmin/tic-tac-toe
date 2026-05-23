package com.tictactoe.common.dto;

import com.tictactoe.common.enums.SessionStatus;
import com.tictactoe.common.enums.GameResult;
import java.util.List;

public record SessionDTO(
    String sessionId,
    SessionStatus status,
    GameResult winner,
    List<MoveDTO> moveHistory
) {
    public SessionDTO {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("Session ID cannot be null or blank");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        if (moveHistory == null) {
            throw new IllegalArgumentException("Move history cannot be null");
        }
    }
}
