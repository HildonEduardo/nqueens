package com.hdlp.thenqueens.domain

import kotlin.math.abs

object NQueensRules {

    const val MIN_BOARD_SIZE = 4

    fun conflictingQueens(queens: Set<BoardPosition>): Set<BoardPosition> {
        val list = queens.toList()
        val conflicting = mutableSetOf<BoardPosition>()
        for (i in list.indices) {
            for (j in i + 1 until list.size) {
                if (attacks(list[i], list[j])) {
                    conflicting += list[i]
                    conflicting += list[j]
                }
            }
        }
        return conflicting
    }

    fun isSolved(size: Int, queens: Set<BoardPosition>): Boolean =
        queens.size == size && conflictingQueens(queens).isEmpty()

    private fun attacks(first: BoardPosition, second: BoardPosition): Boolean =
        first.row == second.row ||
            first.column == second.column ||
            abs(first.row - second.row) == abs(first.column - second.column)
}
