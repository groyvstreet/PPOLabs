package com.example.battleship.screens

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.battleship.R
import com.example.battleship.viewModels.BattleShipViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    viewModel: BattleShipViewModel,
    navController: NavController
) {
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    if (BattleShipViewModel.isRoomCreated && BattleShipViewModel.isPlayerConnected) {
        BattleShipViewModel.isRoomCreated = false
        navController.navigate("game")
    }

    if (BattleShipViewModel.isRoomCreating) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text(text = "Info")
            },
            text = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text(text = "Room creating...")
                }
            },
            buttons = {}
        )
    }

    if (BattleShipViewModel.isGameLoading) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text(text = "Info")
            },
            text = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text(text = "Game loading...")
                }
            },
            buttons = {}
        )
    }

    if (BattleShipViewModel.isRoomCreated && !BattleShipViewModel.isPlayerConnected) {
        AlertDialog(
            onDismissRequest = {
            },
            title = {
                Text(text = "Room id")
            },
            text = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text(
                        text = BattleShipViewModel.roomId,
                        modifier = Modifier
                            .combinedClickable(
                                onLongClick = {
                                    clipboardManager.setText(
                                        AnnotatedString(
                                            BattleShipViewModel.roomId
                                        )
                                    )
                                    Toast.makeText(context, "Room id copied", Toast.LENGTH_SHORT)
                                        .show()
                                },
                                onDoubleClick = {},
                                onClick = {}
                            )
                    )
                }
            },
            buttons = {
                Row(horizontalArrangement = Arrangement.Center) {
                    Button(
                        onClick = {
                            viewModel.deleteRoom()
                        },
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

    var isDialogOpened by remember { mutableStateOf(false) }
    if (isDialogOpened) {
        AlertDialog(
            onDismissRequest = {
                isDialogOpened = false
            },
            title = {
            },
            text = {
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = BattleShipViewModel.roomId,
                        onValueChange = { BattleShipViewModel.roomId = it },
                        label = {
                            Text(text = "Room id", color = MaterialTheme.colors.primary)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            unfocusedBorderColor = MaterialTheme.colors.primary,
                            textColor = MaterialTheme.colors.primary
                        ),
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_baseline_key_24),
                                contentDescription = "",
                                tint = MaterialTheme.colors.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    )
                }
            },
            buttons = {
                Row(horizontalArrangement = Arrangement.Center) {
                    Button(
                        onClick = {
                            BattleShipViewModel.isGameLoading = true
                            coroutineScope.launch {
                                viewModel.enterRoom()
                            }
                            isDialogOpened = false
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(all = 8.dp)
                    ) {
                        Text("Enter")
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

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = {
                viewModel.createRoom()
            },
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_add_circle_24),
                contentDescription = "",
                modifier = Modifier.fillMaxSize()
            )
        }
        IconButton(
            onClick = { isDialogOpened = true },
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.67f)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_login_24),
                contentDescription = "",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
