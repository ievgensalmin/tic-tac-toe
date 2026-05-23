package com.tictactoe.session.service;

import com.tictactoe.common.enums.Cell;
import com.tictactoe.common.enums.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoveGenerationServiceTest {

    private MoveGenerationService service;

    @BeforeEach
    void setUp() {
        service = new MoveGenerationService();
    }

    @Test
    void testGenerateMove_WinningMove() {
        // X can win by playing position 2
        Cell[] board = {
            Cell.X, Cell.X, Cell.EMPTY,
            Cell.O, Cell.O, Cell.EMPTY,
            Cell.EMPTY, Cell.EMPTY, Cell.EMPTY
        };

        int move = service.generateMove(board, Player.PLAYER1);
        assertEquals(2, move);
    }

    @Test
    void testGenerateMove_BlockingMove() {
        // O needs to block X from winning at position 2
        Cell[] board = {
            Cell.X, Cell.X, Cell.EMPTY,
            Cell.O, Cell.EMPTY, Cell.EMPTY,
            Cell.EMPTY, Cell.EMPTY, Cell.EMPTY
        };

        int move = service.generateMove(board, Player.PLAYER2);
        assertEquals(2, move);
    }

    @Test
    void testGenerateMove_RandomMove() {
        // No immediate win or block, should return any empty position
        Cell[] board = {
            Cell.X, Cell.EMPTY, Cell.EMPTY,
            Cell.EMPTY, Cell.O, Cell.EMPTY,
            Cell.EMPTY, Cell.EMPTY, Cell.EMPTY
        };

        int move = service.generateMove(board, Player.PLAYER1);
        assertTrue(move >= 0 && move <= 8);
        assertEquals(Cell.EMPTY, board[move]);
    }

    @Test
    void testGenerateMove_VerticalWin() {
        // X can win vertically at position 6
        Cell[] board = {
            Cell.X, Cell.O, Cell.EMPTY,
            Cell.X, Cell.O, Cell.EMPTY,
            Cell.EMPTY, Cell.EMPTY, Cell.EMPTY
        };

        int move = service.generateMove(board, Player.PLAYER1);
        assertEquals(6, move);
    }

    @Test
    void testGenerateMove_DiagonalWin() {
        // X can win diagonally at position 8
        Cell[] board = {
            Cell.X, Cell.O, Cell.EMPTY,
            Cell.O, Cell.X, Cell.EMPTY,
            Cell.EMPTY, Cell.EMPTY, Cell.EMPTY
        };

        int move = service.generateMove(board, Player.PLAYER1);
        assertEquals(8, move);
    }
}
