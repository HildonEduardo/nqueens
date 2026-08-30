package com.hdlp.thenqueens.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NQueensSolverTest {

    private val firstFourByFourSolution =
        setOf(
            BoardPosition(0, 1),
            BoardPosition(1, 3),
            BoardPosition(2, 0),
            BoardPosition(3, 2),
        )

    @Test
    fun `empty four by four board yields the ascending-first solution`() {
        assertEquals(firstFourByFourSolution, NQueensSolver.solutionContaining(4, emptySet()))
    }

    @Test
    fun `empty boards of larger sizes yield valid solutions`() {
        for (size in listOf(5, 8)) {
            val solution = NQueensSolver.solutionContaining(size, emptySet())
            assertNotNull(solution)
            assertEquals(size, solution!!.size)
            assertTrue(NQueensRules.conflictingQueens(solution).isEmpty())
        }
    }

    @Test
    fun `extendable partial board yields a solution containing it`() {
        val queens = setOf(BoardPosition(0, 1))
        val solution = NQueensSolver.solutionContaining(4, queens)
        assertNotNull(solution)
        assertTrue(solution!!.containsAll(queens))
        assertEquals(4, solution.size)
        assertTrue(NQueensRules.conflictingQueens(solution).isEmpty())
    }

    @Test
    fun `dead-end partial board yields null`() {
        assertNull(NQueensSolver.solutionContaining(4, setOf(BoardPosition(0, 0))))
    }

    @Test
    fun `already-complete board passes through unchanged`() {
        assertEquals(
            firstFourByFourSolution,
            NQueensSolver.solutionContaining(4, firstFourByFourSolution),
        )
    }

    @Test
    fun `multiple fixed queens across rows are all preserved`() {
        val queens = setOf(BoardPosition(0, 0), BoardPosition(4, 2))
        val solution = NQueensSolver.solutionContaining(8, queens)
        assertNotNull(solution)
        assertTrue(solution!!.containsAll(queens))
        assertEquals(8, solution.size)
        assertTrue(NQueensRules.conflictingQueens(solution).isEmpty())
    }
}
