package com.example.timer.components

import android.annotation.SuppressLint
import android.view.Window
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.timer.screens.*
import com.example.timer.services.TimerService
import com.example.timer.utils.Routes
import com.example.timer.utils.foregroundStartService
import com.example.timer.R
import com.example.timer.viewModels.SequenceListViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun TimerScaffold(
    viewModel: SequenceListViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController(),
    timerService: TimerService,
    isDarkTheme: Boolean,
    fontSize: String,
    language: String,
    updateIsDarkTheme: () -> Unit,
    updateFontSize: () -> Unit,
    updateLanguage: () -> Unit
) {
    val context = LocalContext.current
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val canNavigateBack =
        currentBackStackEntry?.destination?.route != currentBackStackEntry?.destination?.parent?.startDestDisplayName
    Scaffold(
        topBar = {
            TopAppBar {
                if (canNavigateBack) {
                    IconButton(
                        onClick = {
                            if (currentBackStackEntry?.destination?.route == "${Routes.START_SEQUENCE}/{sequenceId}") {
                                context.foregroundStartService("Exit")
                            } else {
                                navController.navigateUp()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.content_description_back)
                        )
                    }
                } else {
                    IconButton(
                        onClick = {},
                        enabled = false
                    ) {}
                }
                Text(stringResource(id = R.string.app_name), fontSize = 22.sp)
                Spacer(modifier = Modifier.weight(1f, true))
                if (currentBackStackEntry?.destination?.route == Routes.HOME) {
                    IconButton(
                        onClick = {
                            /*val intent = Intent(context, SettingsActivity::class.java)
                            context.startActivity(intent)*/
                            navController.navigate(Routes.SETTINGS)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(id = R.string.content_description_settings)
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (currentBackStackEntry?.destination?.route == Routes.HOME ||
                currentBackStackEntry?.destination?.route == "${Routes.EDIT_SEQUENCE}/{sequenceId}"
            ) {
                FloatingActionButton(
                    content = {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = stringResource(id = R.string.content_description_add)
                        )
                    },
                    onClick = {
                        if (currentBackStackEntry?.destination?.route == Routes.HOME) {
                            navController.navigate(Routes.ADD_SEQUENCE)
                        } else if (currentBackStackEntry?.destination?.route == "${Routes.EDIT_SEQUENCE}/{sequenceId}") {
                            val sequenceId =
                                currentBackStackEntry?.arguments?.getString("sequenceId")
                            navController.navigate("${Routes.EDIT_SEQUENCE}/${sequenceId}/${Routes.ADD_ELEMENT}")
                        }
                    }
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        isFloatingActionButtonDocked = true
    ) {
        NavHost(navController = navController, startDestination = Routes.HOME) {
            composable(route = Routes.HOME) {
                SequenceListScreen(
                    viewModel = viewModel,
                    navController = navController,
                    timerService = timerService
                )
            }
            composable(route = Routes.ADD_SEQUENCE) {
                AddSequenceScreen(navController = navController)
            }
            composable(route = Routes.SETTINGS) {
                SettingsScreen(
                    navController = navController,
                    isDarkTheme = isDarkTheme,
                    fontSize = fontSize,
                    language = language,
                    updateIsDarkTheme = updateIsDarkTheme,
                    updateFontSize = updateFontSize,
                    updateLanguage = updateLanguage
                )
            }
            composable(
                route = "${Routes.EDIT_SEQUENCE}/{sequenceId}",
                arguments = listOf(navArgument(name = "sequenceId") { type = NavType.StringType })
            ) {
                EditSequenceScreen(navController = navController)
            }
            composable(
                route = "${Routes.EDIT_SEQUENCE}/{sequenceId}/${Routes.ADD_ELEMENT}",
                arguments = listOf(navArgument(name = "sequenceId") { type = NavType.StringType })
            ) {
                AddElementScreen(
                    navController = navController
                )
            }
            composable(
                route = "${Routes.EDIT_ELEMENT}/{elementId}",
                arguments = listOf(navArgument(name = "elementId") { type = NavType.StringType })
            ) {
                EditElementScreen(navController = navController)
            }
            composable(
                route = "${Routes.EDIT_SET_CYCLE}/{elementId}",
                arguments = listOf(navArgument(name = "elementId") { type = NavType.StringType })
            ) {
                EditSetCycleScreen(navController = navController)
            }
            composable(
                route = "${Routes.START_SEQUENCE}/{sequenceId}",
                arguments = listOf(navArgument(name = "sequenceId") { type = NavType.StringType }),
                deepLinks = listOf(navDeepLink {
                    uriPattern = "https://example.com/sequenceId={sequenceId}"
                })
            ) {
                TimerScreen(
                    navController = navController,
                    timerService = timerService
                )
            }
        }
    }
}
