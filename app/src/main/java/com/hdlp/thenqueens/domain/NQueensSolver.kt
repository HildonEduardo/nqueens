package com.hdlp.thenqueens.domain

object NQueensSolver {
    /**
     * Returns a full n-queens solution containing every queen in [queens], or null when
     * none exists. [queens] must be conflict-free. Columns are tried in ascending order,
     * so the result is deterministic.
     */
    fun solutionContaining(
        size: Int,
        queens: Set<BoardPosition>,
    ): Set<BoardPosition>? {
        // Conflict-free input means at most one queen per row, so a row-by-row search
        // with fixed rows pinned to their column covers the whole space.
        val fixedByRow = queens.associateBy { it.row }
        val usedColumns = BooleanArray(size)
        val usedSumDiagonals = BooleanArray(2 * size - 1)
        val usedDiffDiagonals = BooleanArray(2 * size - 1)
        val solution = ArrayList<BoardPosition>(size)

        fun isFree(row: Int, column: Int): Boolean =
            !usedColumns[column] &&
                !usedSumDiagonals[row + column] &&
                !usedDiffDiagonals[row - column + size - 1]

        fun setUsed(row: Int, column: Int, used: Boolean) {
            usedColumns[column] = used
            usedSumDiagonals[row + column] = used
            usedDiffDiagonals[row - column + size - 1] = used
        }

        fun solveFrom(row: Int): Boolean {
            if (row == size) return true
            val candidates: Iterable<Int> =
                fixedByRow[row]?.let { listOf(it.column) } ?: (0 until size)
            for (column in candidates) {
                if (!isFree(row, column)) continue
                setUsed(row, column, used = true)
                solution.add(BoardPosition(row, column))
                if (solveFrom(row + 1)) return true
                solution.removeAt(solution.size - 1)
                setUsed(row, column, used = false)
            }
            return false
        }

        return if (solveFrom(0)) solution.toSet() else null
    }
}
