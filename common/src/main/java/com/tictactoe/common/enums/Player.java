package com.tictactoe.common.enums;

public enum Player {
    PLAYER1,
    PLAYER2;

    public Cell toCell() {
        return this == PLAYER1 ? Cell.X : Cell.O;
    }

    public Player opponent() {
        return this == PLAYER1 ? PLAYER2 : PLAYER1;
    }
}
