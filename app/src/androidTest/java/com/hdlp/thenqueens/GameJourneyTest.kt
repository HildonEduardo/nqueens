package com.hdlp.thenqueens

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class GameJourneyTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private fun startGame(size: Int) {
        composeRule.onNodeWithText("$size × $size").performClick()
        composeRule.onNodeWithText("Start").performClick()
    }

    private fun tapCell(row: Int, column: Int) {
        composeRule
            .onNodeWithContentDescription("Row $row, column $column", substring = true)
            .performClick()
    }

    @Test
    fun selectSizeAndStart_rendersTheBoard() {
        startGame(5)
        tapCell(5, 5)
        composeRule
            .onNodeWithContentDescription("Row 5, column 5, queen")
            .assertExists()
    }

    @Test
    fun conflictingQueens_areBothMarked() {
        startGame(4)
        tapCell(1, 1)
        tapCell(1, 3)
        composeRule
            .onAllNodes(hasContentDescription("conflicting", substring = true))
            .assertCountEquals(2)
    }

    @Test
    fun reset_clearsAllQueens() {
        startGame(4)
        tapCell(1, 1)
        tapCell(2, 3)
        composeRule.onNodeWithText("Reset").performClick()
        composeRule
            .onAllNodes(hasContentDescription("queen", substring = true))
            .assertCountEquals(0)
    }

    @Test
    fun undoAndRedo_stepThroughTheMoveHistory() {
        startGame(4)
        tapCell(1, 1)
        tapCell(2, 3)
        composeRule.onNodeWithContentDescription("Undo").performClick()
        composeRule
            .onAllNodes(hasContentDescription("queen", substring = true))
            .assertCountEquals(1)
        composeRule.onNodeWithContentDescription("Redo").performClick()
        composeRule
            .onAllNodes(hasContentDescription("queen", substring = true))
            .assertCountEquals(2)
    }

    @Test
    fun leaderboard_opensAndNavigatesBack() {
        composeRule.onNodeWithText("Leaderboards").performClick()
        composeRule.onNodeWithContentDescription("Back").assertExists()
        composeRule.onNodeWithText("Leaderboards").assertExists()
        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.onNodeWithText("Choose your board").assertExists()
    }

    @Test
    fun solvingFourByFour_showsVictoryDialog() {
        startGame(4)
        tapCell(1, 2)
        tapCell(2, 4)
        tapCell(3, 1)
        tapCell(4, 3)
        composeRule.onNodeWithText("You solved it!").assertExists()
    }
}
