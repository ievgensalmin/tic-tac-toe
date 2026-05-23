package com.tictactoe.session.entity;

import com.tictactoe.common.enums.SessionStatus;
import com.tictactoe.common.enums.GameResult;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sessions")
public class Session {
    @Id
    private String sessionId;

    @Enumerated(EnumType.STRING)
    private SessionStatus status;

    @Enumerated(EnumType.STRING)
    private GameResult winner;

    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "session", fetch = FetchType.EAGER)
    private List<Move> moveHistory = new ArrayList<>();

    public Session() {
        this.status = SessionStatus.CREATED;
        this.createdAt = LocalDateTime.now();
    }

    public Session(String sessionId) {
        this();
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public GameResult getWinner() {
        return winner;
    }

    public void setWinner(GameResult winner) {
        this.winner = winner;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public List<Move> getMoveHistory() {
        return moveHistory;
    }

    public void setMoveHistory(List<Move> moveHistory) {
        this.moveHistory = moveHistory;
    }

    public void addMove(Move move) {
        this.moveHistory.add(move);
        move.setSession(this);
    }
}
