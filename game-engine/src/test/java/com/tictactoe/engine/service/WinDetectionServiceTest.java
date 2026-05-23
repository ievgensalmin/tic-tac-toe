package com.tictactoe.engine.service;

import com.tictactoe.common.enums.Cell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WinDetectionServiceTest {

    private WinDetectionService service;

    @BeforeEach
    void setUp() {
        service = new WinDetectionService();
    }

    @Test
    void testHorizontalWin_Row0() {
        Cell[] board = {
            Cell.X, Cell.X, Cell.X,
            Cell.O, Cell.O, Cell.EMPTY,
            Cell.EMPTY, Cell.EMPTY, Cell.EMPTY
        };
        assertTrue(service.checkWin(board, Cell.X));
    }

    @Test
    void testHorizontalWin_Row1() {
        Cell[] board = {
            Cell.O, Cell.X, Cell.EMPTY,
            Cell.X, Cell.X, Cell.X,
            Cell.O, Cell.EMPTY, Cell.EMPTY
        };
        assertTrue(service.checkWin(board, Cell.X));
    }

    @Test
    void testHorizontalWin_Row2() {
        Cell[] board = {
            Cell.O, Cell.X, Cell.EMPTY,
            Cell.EMPTY, Cell.O, Cell.EMPTY,
            Cell.X, Cell.X, Cell.X
        };
        assertTrue(service.checkWin(board, Cell.X));
    }

    @Test
    void testVerticalWin_Col0() {
        Cell[] board = {
            Cell.O, Cell.X, Cell.EMPTY,
            Cell.O, Cell.X, Cell.EMPTY,
            Cell.O, Cell.EMPTY, Cell.X
        };
        assertTrue(service.checkWin(board, Cell.O));
    }

    @Test
    void testVerticalWin_Col1() {
        Cell[] board = {
            Cell.X, Cell.O, Cell.EMPTY,
            Cell.EMPTY, Cell.O, Cell.X,
            Cell.X, Cell.O, Cell.EMPTY
        };
        assertTrue(service.checkWin(board, Cell.O));
    }

    @Test
    void testVerticalWin_Col2() {
        Cell[] board = {
            Cell.X, Cell.O, Cell.X,
            Cell.O, Cell.EMPTY, Cell.X,
            Cell.EMPTY, Cell.O, Cell.X
        };
        assertTrue(service.checkWin(board, Cell.X));
    }

    @Test
    void testDiagonalWin_TopLeftToBottomRight() {
        Cell[] board = {
            Cell.O, Cell.X, Cell.EMPTY,
            Cell.X, Cell.O, Cell.EMPTY,
            Cell.EMPTY, Cell.X, Cell.O
        };
        assertTrue(service.checkWin(board, Cell.O));
    }

    @Test
    void testDiagonalWin_TopRightToBottomLeft() {
        Cell[] board = {
            Cell.X, Cell.O, Cell.X,
            Cell.O, Cell.X, Cell.EMPTY,
            Cell.X, Cell.EMPTY, Cell.O
        };
        assertTrue(service.checkWin(board, Cell.X));
    }

    @Test
    void testNoWin() {
        Cell[] board = {
            Cell.X, Cell.O, Cell.X,
            Cell.O, Cell.X, Cell.O,
            Cell.O, Cell.X, Cell.EMPTY
        };
        assertFalse(service.checkWin(board, Cell.X));
        assertFalse(service.checkWin(board, Cell.O));
    }

    @Test
    void testDraw() {
        Cell[] board = {
            Cell.X, Cell.O, Cell.X,
            Cell.O, Cell.X, Cell.O,
            Cell.O, Cell.X, Cell.O
        };
        assertTrue(service.isDraw(board));
    }

    @Test
    void testNotDraw_EmptyCells() {
        Cell[] board = {
            Cell.X, Cell.O, Cell.X,
            Cell.O, Cell.X, Cell.O,
            Cell.O, Cell.EMPTY, Cell.EMPTY
        };
        assertFalse(service.isDraw(board));
    }
}
