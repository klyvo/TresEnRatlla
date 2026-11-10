package org.proven.tresenrattla

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.proven.tresenrattla.model.BoardModel

class BoardModelTest {

    @Test
    fun initialBoard_hasNineEmptyCellsAndTurnX() {
        val board = BoardModel.createInitialBoard()

        assertEquals(9, board.cells.size)
        assertTrue(board.cells.all { it.symbol.isEmpty() })
        assertEquals("X", board.currentTurn)
        assertNull(board.winner)
        assertFalse(board.isDraw)
        assertFalse(board.isGameOver)
    }

    @Test
    fun makeMove_updatesCellAndSwitchesTurn() {
        val board = BoardModel.createInitialBoard()
        val nextBoard = board.makeMove(0)

        assertEquals("X", nextBoard.cells[0].symbol)
        assertEquals("O", nextBoard.currentTurn)
        assertFalse(nextBoard.isGameOver)
    }

    @Test
    fun makeMove_horizontalWinForX() {
        var board = BoardModel.createInitialBoard()
        // X: 0, O: 3, X: 1, O: 4, X: 2
        board = board.makeMove(0) // X at 0
        board = board.makeMove(3) // O at 3
        board = board.makeMove(1) // X at 1
        board = board.makeMove(4) // O at 4
        board = board.makeMove(2) // X at 2 -> Win!

        assertEquals("X", board.winner)
        assertFalse(board.isDraw)
        assertTrue(board.isGameOver)
    }

    @Test
    fun makeMove_drawGame() {
        var board = BoardModel.createInitialBoard()
        /*
          X O X
          X O O
          O X X
        */
        val moves = listOf(0, 1, 2, 4, 3, 5, 7, 6, 8)
        for (move in moves) {
            board = board.makeMove(move)
        }

        assertNull(board.winner)
        assertTrue(board.isDraw)
        assertTrue(board.isGameOver)
    }
}
