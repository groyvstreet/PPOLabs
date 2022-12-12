package com.example.battleship.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.battleship.R
import com.example.battleship.ui.theme.LightPrimaryColor
import com.example.battleship.ui.theme.Shapes
import com.example.battleship.viewModels.BattleShipViewModel
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StatisticScreen(viewModel: BattleShipViewModel) {
    Box(
        contentAlignment = Alignment.TopEnd,
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(onClick = { viewModel.load(viewModel) }) {
            Icon(imageVector = Icons.Filled.Refresh, contentDescription = "")
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        HeaderText("Statistic")
        LazyColumn {
            BattleShipViewModel.games.forEach { game ->
                item {
                    var isDialogOpened by remember { mutableStateOf(false) }
                    if (isDialogOpened) {
                        AlertDialog(
                            onDismissRequest = {
                                isDialogOpened = false
                            },
                            title = {
                                Column {
                                    Text(
                                        text = if (BattleShipViewModel.currentUser!!.uid == game.winnerId) {
                                            "Win"
                                        } else {
                                            "Lose"
                                        },
                                        color = if (BattleShipViewModel.currentUser!!.uid == game.winnerId) {
                                            Color.Green
                                        } else {
                                            Color.Red
                                        },
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = "Start time: ${game.startTime}",
                                        color = Color.Gray,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraLight,
                                    )
                                    Text(
                                        text = "Duration: ${game.durationTime}",
                                        color = Color.Gray,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraLight,
                                    )
                                }
                            },
                            text = {
                                Column {
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        Image(
                                            painter = if (BattleShipViewModel.winner != null && BattleShipViewModel.winner!!.isGravatar) {
                                                rememberAsyncImagePainter(
                                                    ImageRequest
                                                        .Builder(LocalContext.current)
                                                        .data(
                                                            data = "https://www.gravatar.com/avatar/${
                                                                viewModel.md5(
                                                                    BattleShipViewModel.winner!!.email.lowercase(
                                                                        Locale.ROOT
                                                                    )
                                                                )
                                                            }?s=640"
                                                        )
                                                        .build()
                                                )
                                            } else {
                                                if (BattleShipViewModel.winner != null && BattleShipViewModel.winner!!.imageUrl != "null") {
                                                    rememberAsyncImagePainter(
                                                        ImageRequest
                                                            .Builder(LocalContext.current)
                                                            .data(data = BattleShipViewModel.winner!!.imageUrl)
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
                                                text = BattleShipViewModel.winner?.nickname
                                                    ?: "player",
                                                color = MaterialTheme.colors.secondary,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(
                                                    start = 4.dp,
                                                    top = 6.dp
                                                )
                                            )
                                            Text(
                                                text = "Score: ${game.winnerScore}",
                                                color = Color.Gray,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(
                                                    start = 4.dp,
                                                    top = 4.dp
                                                )
                                            )
                                        }
                                    }
                                    LazyColumn(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(160.dp)
                                    ) {
                                        items(10) { index ->
                                            Row {
                                                for (j in 1..10) {
                                                    Button(
                                                        onClick = {},
                                                        colors = ButtonDefaults.buttonColors(
                                                            backgroundColor = viewModel.getShipPointColor(
                                                                index,
                                                                j - 1,
                                                                game.winnerMap,
                                                                game.winnerShips
                                                            )
                                                        ),
                                                        shape = RoundedCornerShape(0.dp),
                                                        border = BorderStroke(1.dp, Color.Black),
                                                        modifier = Modifier.size(16.dp)
                                                    ) {}
                                                }
                                            }
                                        }
                                    }
                                    Spacer(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                    )
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        Image(
                                            painter = if (BattleShipViewModel.loser != null && BattleShipViewModel.loser!!.isGravatar) {
                                                rememberAsyncImagePainter(
                                                    ImageRequest
                                                        .Builder(LocalContext.current)
                                                        .data(
                                                            data = "https://www.gravatar.com/avatar/${
                                                                viewModel.md5(
                                                                    BattleShipViewModel.loser!!.email.lowercase(
                                                                        Locale.ROOT
                                                                    )
                                                                )
                                                            }?s=640"
                                                        )
                                                        .build()
                                                )
                                            } else {
                                                if (BattleShipViewModel.loser != null && BattleShipViewModel.loser!!.imageUrl != "null") {
                                                    rememberAsyncImagePainter(
                                                        ImageRequest
                                                            .Builder(LocalContext.current)
                                                            .data(data = BattleShipViewModel.loser!!.imageUrl)
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
                                                text = BattleShipViewModel.loser?.nickname
                                                    ?: "player",
                                                color = MaterialTheme.colors.secondary,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(
                                                    start = 4.dp,
                                                    top = 6.dp
                                                )
                                            )
                                            Text(
                                                text = "Score: ${game.loserScore}",
                                                color = Color.Gray,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(
                                                    start = 4.dp,
                                                    top = 4.dp
                                                )
                                            )
                                        }
                                    }
                                    LazyColumn(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(160.dp)
                                    ) {
                                        items(10) { index ->
                                            Row {
                                                for (j in 1..10) {
                                                    Button(
                                                        onClick = {},
                                                        colors = ButtonDefaults.buttonColors(
                                                            backgroundColor = viewModel.getShipPointColor(
                                                                index,
                                                                j - 1,
                                                                game.loserMap,
                                                                game.loserShips
                                                            )
                                                        ),
                                                        shape = RoundedCornerShape(0.dp),
                                                        border = BorderStroke(1.dp, Color.Black),
                                                        modifier = Modifier.size(16.dp)
                                                    ) {}
                                                }
                                            }
                                        }
                                    }
                                }
                            },
                            buttons = {
                                Row(horizontalArrangement = Arrangement.Center) {
                                    Button(
                                        onClick = { isDialogOpened = false },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(all = 8.dp)
                                    ) {
                                        Text("OK")
                                    }
                                }
                            }
                        )
                    }
                    Card(
                        onClick = {
                            viewModel.loadGame(game.winnerId, game.loserId)
                            isDialogOpened = true
                        },
                        backgroundColor = MaterialTheme.colors.background,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .fillMaxWidth(),
                        elevation = 0.dp,
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(shape = Shapes.medium)
                                        .background(LightPrimaryColor)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_baseline_sports_esports_24),
                                        contentDescription = "",
                                        tint = MaterialTheme.colors.primary,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Text(
                                    text = if (BattleShipViewModel.currentUser!!.uid == game.winnerId) {
                                        "Win"
                                    } else {
                                        "Lose"
                                    },
                                    color = if (BattleShipViewModel.currentUser!!.uid == game.winnerId) {
                                        Color.Green
                                    } else {
                                        Color.Red
                                    },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = game.startTime,
                                    color = Color.Gray,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraLight,
                                )
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_baseline_arrow_forward_ios_24),
                                    contentDescription = "",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp))
            }
        }
    }
}
