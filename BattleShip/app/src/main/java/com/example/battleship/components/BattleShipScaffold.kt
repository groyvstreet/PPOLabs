package com.example.battleship.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.battleship.models.NavigationItem
import com.example.battleship.screens.*
import com.example.battleship.viewModels.BattleShipViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun BattleShipScaffold(
    viewModel: BattleShipViewModel,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Scaffold(
        bottomBar = {
            if (currentRoute == NavigationItem.Profile.route ||
                currentRoute == NavigationItem.Home.route ||
                currentRoute == NavigationItem.Statistic.route
            ) {
                BottomNavigationBar(viewModel = viewModel, navController = navController)
            }
        },
        //backgroundColor = MaterialTheme.colors.background
        backgroundColor = if (currentRoute != "signup") {
            MaterialTheme.colors.primary
        } else {
            MaterialTheme.colors.background
        },
        modifier = Modifier.fillMaxSize()
    ) {
        NavHost(
            navController = navController,
            startDestination = if (BattleShipViewModel.isAuth) {
                if (BattleShipViewModel.isPlayerConnected || BattleShipViewModel.isGameOver) {
                    "game"
                } else {
                    NavigationItem.Home.route
                }
            } else {
                "login"
            },
            modifier = Modifier.fillMaxSize()
        ) {
            composable(route = NavigationItem.Profile.route) {
                ProfileScreen(viewModel = viewModel)
            }
            composable(route = NavigationItem.Home.route) {
                MainScreen(viewModel = viewModel, navController = navController)
            }
            composable(route = NavigationItem.Statistic.route) {
                StatisticScreen(viewModel = viewModel)
            }
            composable(route = "login") {
                LoginScreen(viewModel = viewModel, navController = navController)
            }
            composable(route = "signup") {
                SignupScreen(viewModel = viewModel, navController = navController)
            }
            composable(route = "game") {
                GameScreen(viewModel = viewModel)
            }
        }
    }
}
