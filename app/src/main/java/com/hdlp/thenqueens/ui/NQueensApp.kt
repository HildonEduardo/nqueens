package com.hdlp.thenqueens.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hdlp.thenqueens.ui.game.GameScreen
import com.hdlp.thenqueens.ui.setup.SetupScreen

object Routes {
    const val SETUP = "setup"
    const val GAME = "game/{boardSize}"
    fun game(boardSize: Int) = "game/$boardSize"
}

@Composable
fun NQueensApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.SETUP) {
        composable(Routes.SETUP) {
            SetupScreen(onStart = { size -> navController.navigate(Routes.game(size)) })
        }
        composable(
            route = Routes.GAME,
            arguments = listOf(navArgument("boardSize") { type = NavType.IntType }),
        ) {
            GameScreen(onChangeSize = { navController.popBackStack() })
        }
    }
}
