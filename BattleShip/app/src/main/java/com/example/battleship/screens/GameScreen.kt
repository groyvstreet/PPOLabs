package com.example.battleship.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.battleship.R
import com.example.battleship.components.*
import com.example.battleship.models.Ship
import com.example.battleship.ui.theme.Shapes
import com.example.battleship.viewModels.BattleShipViewModel
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun GameScreen(
    viewModel: BattleShipViewModel
) {
    if (!BattleShipViewModel.isConnected) {
        AlertDialog(
            onDismissRequest = {
            },
            title = {
                Text(text = ("A network error"))
            },
            text = {
                Text(text = "Connection lost")
            },
            buttons = {
            }
        )
    }

    if (BattleShipViewModel.isExiting) {
        AlertDialog(
            onDismissRequest = {
            },
            title = {
                Text(text = ("Info"))
            },
            text = {
                Text(text = "Exiting...")
            },
            buttons = {
            }
        )
    }

    if (BattleShipViewModel.isPlayerWaiting) {
        AlertDialog(
            onDismissRequest = {
            },
            title = {
                Text(text = ("Enemy player is offline"))
            },
            text = {
                Text(text = "Please wait for the enemy player to connect, after 5 seconds game will be lost")
            },
            buttons = {
            }
        )
    }

    if (BattleShipViewModel.isGameOver) {
        AlertDialog(
            onDismissRequest = {
            },
            title = {
                Text(
                    text = if (BattleShipViewModel.currentUser!!.uid != BattleShipViewModel.winnerId) {
                        "Lose"
                    } else {
                        "Win"
                    },
                    color = if (BattleShipViewModel.currentUser!!.uid != BattleShipViewModel.winnerId) {
                        Color.Red
                    } else {
                        Color.Green
                    }
                )
            },
            text = {
                Text(
                    text = "Score: ${
                        if (BattleShipViewModel.currentUser!!.uid == BattleShipViewModel.firstPlayerId) {
                            BattleShipViewModel.firstPlayerScore
                        } else {
                            BattleShipViewModel.secondPlayerScore
                        }
                    }"
                )
            },
            buttons = {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            BattleShipViewModel.isGameOver = false
                        }
                    ) {
                        Text("OK")
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
                Text(text = "Exit", color = MaterialTheme.colors.primary)
            },
            text = {
                Text(text = "When you leave the game, you accept defeat.")
            },
            buttons = {
                Row(horizontalArrangement = Arrangement.Center) {
                    Button(
                        onClick = {
                            viewModel.leaveRoom()
                            isDialogOpened = false
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(all = 8.dp)
                    ) {
                        Text("Leave")
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

    val coroutineScope = rememberCoroutineScope()
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = if (BattleShipViewModel.enemy != null && BattleShipViewModel.enemy!!.isGravatar) {
                    rememberAsyncImagePainter(
                        ImageRequest
                            .Builder(LocalContext.current)
                            .data(
                                data = "https://www.gravatar.com/avatar/${
                                    viewModel.md5(
                                        BattleShipViewModel.enemy!!.email.lowercase(Locale.ROOT)
                                    )
                                }?s=640"
                            )
                            .build()
                    )
                } else {
                    if (BattleShipViewModel.enemy != null && BattleShipViewModel.enemy!!.imageUrl != "null") {
                        rememberAsyncImagePainter(
                            ImageRequest
                                .Builder(LocalContext.current)
                                .data(data = BattleShipViewModel.enemy!!.imageUrl)
                                .build()
                        )
                    } else {
                        painterResource(id = R.drawable.profile_image)
                    }
                },
                contentDescription = "",
                modifier = Modifier
                    .size(60.dp)
                    .padding(start = 16.dp)
            )
            Column {
                Text(
                    text = BattleShipViewModel.enemy?.nickname ?: "player",
                    color = MaterialTheme.colors.secondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, top = 6.dp)
                )
                Text(
                    text = if (BattleShipViewModel.isFirstPlayerReady && BattleShipViewModel.isSecondPlayerReady) {
                        if (BattleShipViewModel.isEnemyMoving) {
                            "Moving"
                        } else {
                            "Waiting"
                        }
                    } else {
                        if (BattleShipViewModel.currentUser!!.uid == BattleShipViewModel.firstPlayerId) {
                            if (BattleShipViewModel.isSecondPlayerReady) {
                                "Ready"
                            } else {
                                "Not ready"
                            }
                        } else {
                            if (BattleShipViewModel.isFirstPlayerReady) {
                                "Ready"
                            } else {
                                "Not ready"
                            }
                        }
                    },
                    color = if (BattleShipViewModel.isFirstPlayerReady && BattleShipViewModel.isSecondPlayerReady) {
                        if (BattleShipViewModel.isEnemyMoving) {
                            Color.Green
                        } else {
                            Color.Red
                        }
                    } else {
                        if (BattleShipViewModel.currentUser!!.uid == BattleShipViewModel.firstPlayerId) {
                            if (BattleShipViewModel.isSecondPlayerReady) {
                                Color.Green
                            } else {
                                Color.Red
                            }
                        } else {
                            if (BattleShipViewModel.isFirstPlayerReady) {
                                Color.Green
                            } else {
                                Color.Red
                            }
                        }
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }
            Box(
                contentAlignment = Alignment.CenterEnd,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { isDialogOpened = true }) {
                    Icon(
                        imageVector = Icons.Filled.ExitToApp,
                        contentDescription = "",
                        modifier = Modifier.size(60.dp)
                    )
                }
            }
        }

        if (BattleShipViewModel.isFirstPlayerReady && BattleShipViewModel.isSecondPlayerReady) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(310.dp)
            ) {
                items(10) { index ->
                    Row {
                        for (j in 1..10) {
                            Button(
                                onClick = {
                                    if (BattleShipViewModel.currentUser!!.uid == BattleShipViewModel.firstPlayerId) {
                                        if (BattleShipViewModel.secondPlayerMap!![index][j - 1] == 0) {
                                            viewModel.selectedPoint = listOf(index, j - 1)
                                        }
                                    }
                                    if (BattleShipViewModel.currentUser!!.uid == BattleShipViewModel.secondPlayerId) {
                                        if (BattleShipViewModel.firstPlayerMap!![index][j - 1] == 0) {
                                            viewModel.selectedPoint = listOf(index, j - 1)
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = viewModel.getEnemyShipPointColor(index, j - 1)
                                ),
                                shape = RoundedCornerShape(0.dp),
                                border = BorderStroke(1.dp, Color.Black),
                                modifier = Modifier.size(30.dp)
                            ) {}
                        }
                    }
                }
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
            )
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                items(10) { index ->
                    Row {
                        for (j in 1..10) {
                            Button(
                                onClick = {},
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = viewModel.getShipPointColor(index, j - 1)
                                ),
                                shape = RoundedCornerShape(0.dp),
                                border = BorderStroke(1.dp, Color.Black),
                                modifier = Modifier.size(20.dp)
                            ) {}
                        }
                    }
                }
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
            )
            Button(
                onClick = {
                    coroutineScope.launch {
                        BattleShipViewModel.isAttackButtonEnabled = false
                        if (viewModel.selectedPoint.isNotEmpty()) {
                            viewModel.attackEnemy(
                                viewModel.selectedPoint[0],
                                viewModel.selectedPoint[1]
                            )
                        }
                        BattleShipViewModel.isFirstPlayerMoving = false
                        BattleShipViewModel.isSecondPlayerMoving = false
                        viewModel.selectedPoint = listOf()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.background
                ),
                shape = Shapes.large,
                enabled = BattleShipViewModel.isAttackButtonEnabled && viewModel.selectedPoint.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp)
            ) {
                Text(text = "Attack")
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "Score: ${
                        if (BattleShipViewModel.currentUser!!.uid == BattleShipViewModel.firstPlayerId) {
                            BattleShipViewModel.firstPlayerScore
                        } else {
                            BattleShipViewModel.secondPlayerScore
                        }
                    }"
                )
            }
        } else {
            LongPressDraggable(modifier = Modifier.fillMaxSize()) {
                Column {
                    LazyColumn(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(310.dp)
                    ) {
                        items(10) { index ->
                            Row {
                                for (j in 1..10) {
                                    DropTarget<Ship>(
                                        modifier = Modifier.size(30.dp)
                                    ) { isInBound, ship, shipIndex ->

                                        var isPossible = true
                                        var coordinates = listOf<List<Int>>()
                                        ship?.let {
                                            for (i in ship.coordinates.indices) {
                                                if (BattleShipViewModel.isVerticalShips[shipIndex].value) {
                                                    if (index + i >= 10) {
                                                        isPossible = false
                                                    }

                                                    coordinates = coordinates.plus(
                                                        listOf(
                                                            listOf(
                                                                index + i,
                                                                j - 1
                                                            )
                                                        )
                                                    )
                                                } else {
                                                    if (j - 1 + i >= 10) {
                                                        isPossible = false
                                                    }

                                                    coordinates = coordinates.plus(
                                                        listOf(
                                                            listOf(
                                                                index,
                                                                j - 1 + i
                                                            )
                                                        )
                                                    )
                                                }
                                            }

                                            if (isPossible) {
                                                viewModel.addShip(Ship(coordinates), shipIndex)
                                            }
                                        }

                                        Button(
                                            onClick = {
                                                if (BattleShipViewModel.isReadyButtonEnabled) {
                                                    viewModel.dropShip(index, j - 1)
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                backgroundColor = if (isInBound) {
                                                    Color.Red
                                                } else {
                                                    if (BattleShipViewModel.firstPlayerShips != null && BattleShipViewModel.secondPlayerShips != null) {
                                                        viewModel.getShipPointColor(
                                                            index,
                                                            j - 1
                                                        )
                                                    } else {
                                                        Color.Cyan
                                                    }
                                                }
                                            ),
                                            shape = RoundedCornerShape(0.dp),
                                            border = BorderStroke(1.dp, Color.Black),
                                            modifier = Modifier.size(30.dp)
                                        ) {}
                                    }
                                }
                            }
                        }
                    }
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                    )
                    LazyColumn(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            Column {
                                Row {
                                    if (BattleShipViewModel.isVerticalShips[0].value) {
                                        Vertical4(0)
                                    } else {
                                        Horizontal4(0)
                                    }
                                    if (BattleShipViewModel.isVerticalShips[1].value) {
                                        Vertical3(1)
                                    } else {
                                        Horizontal3(1)
                                    }
                                    if (BattleShipViewModel.isVerticalShips[2].value) {
                                        Vertical3(2)
                                    } else {
                                        Horizontal3(2)
                                    }
                                }
                                Row {
                                    if (BattleShipViewModel.isVerticalShips[3].value) {
                                        Vertical2(3)
                                    } else {
                                        Horizontal2(3)
                                    }
                                    if (BattleShipViewModel.isVerticalShips[4].value) {
                                        Vertical2(4)
                                    } else {
                                        Horizontal2(4)
                                    }
                                    if (BattleShipViewModel.isVerticalShips[5].value) {
                                        Vertical2(5)
                                    } else {
                                        Horizontal2(5)
                                    }
                                    Horizontal1(6)
                                    Horizontal1(7)
                                    Horizontal1(8)
                                    Horizontal1(9)
                                }
                            }
                        }
                    }
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                    )
                    Button(
                        onClick = {
                            BattleShipViewModel.isReadyButtonEnabled = false
                            viewModel.setReady()
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.background
                        ),
                        shape = Shapes.large,
                        enabled = if (BattleShipViewModel.currentUser!!.uid == BattleShipViewModel.firstPlayerId) {
                            BattleShipViewModel.firstPlayerShips != null &&
                                    BattleShipViewModel.firstPlayerShips!!.size == 10 &&
                                    !BattleShipViewModel.isFirstPlayerReady
                        } else {
                            BattleShipViewModel.secondPlayerShips != null &&
                                    BattleShipViewModel.secondPlayerShips!!.size == 10 &&
                                    !BattleShipViewModel.isSecondPlayerReady
                        } && BattleShipViewModel.isReadyButtonEnabled,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp)
                    ) {
                        Text(text = "Ready")
                    }
                }
            }
        }
    }
}
