package com.hdlp.thenqueens.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hdlp.thenqueens.ui.game.GameScreen
import com.hdlp.thenqueens.ui.leaderboard.LeaderboardScreen
import com.hdlp.thenqueens.ui.setup.SetupScreen
import kotlinx.serialization.Serializable

@Serializable
data object SetupRoute

// The property name is the SavedStateHandle key GameViewModel reads ("boardSize").
@Serializable
data class GameRoute(
    val boardSize: Int,
)

@Serializable
data object LeaderboardRoute

@Composable
fun NQueensApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = SetupRoute) {
        composable<SetupRoute> {
            SetupScreen(
                onStart = { size -> navController.navigate(GameRoute(size)) },
                onLeaderboards = { navController.navigate(LeaderboardRoute) },
            )
        }
        composable<LeaderboardRoute> {
            LeaderboardScreen(onBack = { navController.popBackStack() })
        }
        composable<GameRoute> {
            GameScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
