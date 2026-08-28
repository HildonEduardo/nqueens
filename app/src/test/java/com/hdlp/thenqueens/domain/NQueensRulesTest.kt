package com.hdlp.thenqueens.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NQueensRulesTest {
    private fun queens(vararg positions: Pair<Int, Int>): Set<BoardPosition> = positions.map { BoardPosition(it.first, it.second) }.toSet()

    private fun solution(vararg columns: Int): Set<BoardPosition> = columns.mapIndexed { row, column -> BoardPosition(row, column) }.toSet()

    @Test
    fun `empty board has no conflicts`() {
        assertEquals(emptySet<BoardPosition>(), NQueensRules.conflictingQueens(emptySet()))
    }

    @Test
    fun `single queen has no conflicts`() {
        assertEquals(emptySet<BoardPosition>(), NQueensRules.conflictingQueens(queens(2 to 3)))
    }

    @Test
    fun `queens in the same row conflict`() {
        val board = queens(1 to 0, 1 to 3)
        assertEquals(board, NQueensRules.conflictingQueens(board))
    }

    @Test
    fun `queens in the same column conflict`() {
        val board = queens(0 to 2, 3 to 2)
        assertEquals(board, NQueensRules.conflictingQueens(board))
    }

    @Test
    fun `queens on a descending diagonal conflict`() {
        val board = queens(0 to 0, 2 to 2)
        assertEquals(board, NQueensRules.conflictingQueens(board))
    }

    @Test
    fun `queens on an ascending diagonal conflict`() {
        val board = queens(3 to 0, 1 to 2)
        assertEquals(board, NQueensRules.conflictingQueens(board))
    }

    @Test
    fun `non-attacking queens do not conflict`() {
        assertEquals(emptySet<BoardPosition>(), NQueensRules.conflictingQueens(queens(0 to 1, 1 to 3)))
    }

    @Test
    fun `every participant of a multi-queen conflict is returned`() {
        val board = queens(0 to 0, 0 to 2, 2 to 0, 3 to 3)
        assertEquals(queens(0 to 0, 0 to 2, 2 to 0, 3 to 3), NQueensRules.conflictingQueens(board))
    }

    @Test
    fun `innocent bystander is not reported`() {
        val board = queens(0 to 0, 0 to 2, 2 to 1)
        assertEquals(queens(0 to 0, 0 to 2), NQueensRules.conflictingQueens(board))
    }

    @Test
    fun `conflict detection is symmetric`() {
        val a = BoardPosition(1, 1)
        val b = BoardPosition(3, 3)
        assertEquals(
            NQueensRules.conflictingQueens(setOf(a, b)),
            NQueensRules.conflictingQueens(setOf(b, a)),
        )
    }

    @Test
    fun `translating all queens preserves attack relationships`() {
        val board = queens(0 to 0, 1 to 2, 4 to 1)
        val translated = board.map { BoardPosition(it.row + 3, it.column + 5) }.toSet()
        val original = NQueensRules.conflictingQueens(board)
        val expected = original.map { BoardPosition(it.row + 3, it.column + 5) }.toSet()
        assertEquals(expected, NQueensRules.conflictingQueens(translated))
    }

    @Test
    fun `known solutions are solved`() {
        assertTrue(NQueensRules.isSolved(4, solution(1, 3, 0, 2)))
        assertTrue(NQueensRules.isSolved(5, solution(0, 2, 4, 1, 3)))
        assertTrue(NQueensRules.isSolved(8, solution(0, 4, 7, 5, 2, 6, 1, 3)))
    }

    @Test
    fun `n conflicting queens are not solved`() {
        assertFalse(NQueensRules.isSolved(4, solution(1, 3, 0, 0)))
    }

    @Test
    fun `fewer than n conflict-free queens are not solved`() {
        assertFalse(NQueensRules.isSolved(4, queens(0 to 1, 1 to 3)))
    }

    @Test
    fun `minimum board size is four`() {
        assertEquals(4, NQueensRules.MIN_BOARD_SIZE)
    }
}
