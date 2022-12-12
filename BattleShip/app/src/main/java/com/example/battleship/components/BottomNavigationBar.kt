package com.example.battleship.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.battleship.models.NavigationItem
import com.example.battleship.viewModels.BattleShipViewModel

@Composable
fun BottomNavigationBar(
    viewModel: BattleShipViewModel,
    navController: NavController
) {
    var toRoute by remember { mutableStateOf("") }

    fun navigate(toRoute: String) {
        navController.navigate(toRoute) {
            navController.graph.startDestinationRoute?.let { route ->
                popUpTo(route) {
                    saveState = true
                }
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    var isDialogOpened by remember { mutableStateOf(false) }
    if (isDialogOpened) {
        AlertDialog(
            onDismissRequest = {
                isDialogOpened = false
            },
            title = {
                Text(text = "Profile info")
            },
            text = {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Changes will not be saved")
                }
            },
            buttons = {
                Row(horizontalArrangement = Arrangement.Center) {
                    Button(
                        onClick = {
                            BattleShipViewModel.selectedImg = BattleShipViewModel.oldSelectedImg
                            BattleShipViewModel.nickname = BattleShipViewModel.oldNickname
                            BattleShipViewModel.isGravatar = BattleShipViewModel.oldIsGravatar
                            isDialogOpened = false
                            navigate(toRoute)
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(all = 8.dp)
                    ) {
                        Text("OK")
                    }
                    Button(
                        onClick = { isDialogOpened = false },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 8.dp),
                    ) {
                        Text("Cancel")
                    }
                }
            }
        )
    }

    val items = listOf(
        NavigationItem.Profile,
        NavigationItem.Home,
        NavigationItem.Statistic
    )

    BottomNavigation(
        backgroundColor = MaterialTheme.colors.background,
        contentColor = Color.White
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(painterResource(id = item.icon), contentDescription = item.title) },
                label = { Text(text = item.title) },
                selectedContentColor = MaterialTheme.colors.onBackground,
                unselectedContentColor = MaterialTheme.colors.onBackground.copy(0.4f),
                alwaysShowLabel = true,
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute == NavigationItem.Profile.route) {
                        if (BattleShipViewModel.oldSelectedImg != BattleShipViewModel.selectedImg ||
                            BattleShipViewModel.oldNickname != BattleShipViewModel.nickname ||
                            BattleShipViewModel.oldIsGravatar != BattleShipViewModel.isGravatar
                        ) {
                            toRoute = item.route
                            isDialogOpened = true
                        } else {
                            navigate(item.route)
                        }
                    } else {
                        navigate(item.route)

                        if (!BattleShipViewModel.isAppLoaded &&
                            (item.route == NavigationItem.Profile.route ||
                                    item.route == NavigationItem.Statistic.route)
                        ) {
                            viewModel.load(viewModel)
                        }
                    }
                }
            )
        }
    }
}
