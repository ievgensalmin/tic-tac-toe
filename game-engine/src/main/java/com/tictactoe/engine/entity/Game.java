package com.tictactoe.engine.entity;

import com.tictactoe.common.enums.Cell;
import com.tictactoe.common.enums.Player;
import com.tictactoe.common.enums.GameStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Arrays;

@Entity
@Table(name = "games")
public class Game {
    @Id
    private String gameId;

    @Version
    private Long version;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "game_board", joinColumns = @JoinColumn(name = "game_id"))
    @Column(name = "cell")
    @Enumerated(EnumType.STRING)
    private Cell[] board;

    @Enumerated(EnumType.STRING)
    private Player currentPlayer;

    @Enumerated(EnumType.STRING)
    private GameStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Game() {
        this.board = new Cell[9];
        Arrays.fill(this.board, Cell.EMPTY);
        this.currentPlayer = Player.PLAYER1;
        this.status = GameStatus.IN_PROGRESS;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Game(String gameId) {
        this();
        this.gameId = gameId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Cell[] getBoard() {
        return board;
    }

    public void setBoard(Cell[] board) {
        this.board = board;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
