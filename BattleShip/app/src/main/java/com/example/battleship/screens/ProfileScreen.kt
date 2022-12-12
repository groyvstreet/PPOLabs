package com.example.battleship.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
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
fun ProfileScreen(viewModel: BattleShipViewModel) {
    if (BattleShipViewModel.isProfileUpdating) {
        AlertDialog(
            onDismissRequest = {
            },
            title = {
                Text(text = "Profile info")
            },
            text = {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Profile updating...")
                }
            },
            buttons = {
            }
        )
    }
    if (viewModel.isProfileLoading) {
        AlertDialog(
            onDismissRequest = {
            },
            title = {
                Text(text = "Profile info")
            },
            text = {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Profile loading...")
                }
            },
            buttons = {
            }
        )
    }
    Box(
        contentAlignment = Alignment.TopEnd,
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(onClick = { viewModel.load(viewModel) }) {
            Icon(imageVector = Icons.Filled.Refresh, contentDescription = "")
        }
    }
    Column {
        HeaderText("Profile")
        ProfileCard(viewModel)
        GeneralOptions()
        AccountOptions(viewModel)
    }
}

@Composable
fun HeaderText(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colors.secondary,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp, bottom = 10.dp),
        fontWeight = FontWeight.ExtraBold,
        fontSize = 16.sp
    )
}

@Composable
fun ProfileCard(viewModel: BattleShipViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(10.dp),
        backgroundColor = MaterialTheme.colors.background,
        elevation = 0.dp,
        shape = Shapes.large
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = BattleShipViewModel.nickname,
                    color = MaterialTheme.colors.secondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = BattleShipViewModel.email,
                    color = Color.Gray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Button(
                    modifier = Modifier.padding(top = 10.dp),
                    onClick = { viewModel.updateProfile() },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary
                    ),
                    contentPadding = PaddingValues(horizontal = 30.dp),
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 2.dp
                    ),
                    shape = Shapes.medium,
                    enabled = (BattleShipViewModel.oldSelectedImg != BattleShipViewModel.selectedImg ||
                            BattleShipViewModel.oldNickname != BattleShipViewModel.nickname ||
                            BattleShipViewModel.oldIsGravatar != BattleShipViewModel.isGravatar) &&
                            BattleShipViewModel.nickname.isNotBlank()
                ) {
                    Text(
                        text = "Save",
                        color = MaterialTheme.colors.secondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Image(
                painter = if (BattleShipViewModel.isGravatar) {
                    rememberAsyncImagePainter(
                        ImageRequest
                            .Builder(LocalContext.current)
                            .data(
                                data = "https://www.gravatar.com/avatar/${
                                    viewModel.md5(
                                        BattleShipViewModel.email.lowercase(Locale.ROOT)
                                    )
                                }?s=640"
                            )
                            .build()
                    )
                } else {
                    if (BattleShipViewModel.selectedImg != null &&
                        BattleShipViewModel.selectedImg.toString() != "null"
                    ) {
                        rememberAsyncImagePainter(
                            ImageRequest
                                .Builder(LocalContext.current)
                                .data(data = BattleShipViewModel.selectedImg)
                                .build()
                        )
                    } else {
                        painterResource(id = R.drawable.profile_image)
                    }
                },
                contentDescription = "",
                modifier = if (BattleShipViewModel.isGravatar) {
                    Modifier.height(120.dp)
                } else {
                    Modifier
                        .height(120.dp)
                        .clickable { viewModel.selectImg() }
                }
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun GeneralOptions() {
    var isDialogOpened by remember { mutableStateOf(false) }
    if (isDialogOpened) {
        AlertDialog(
            onDismissRequest = {
                BattleShipViewModel.nickname = BattleShipViewModel.nickname.trim()
                isDialogOpened = false
            },
            text = {
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = BattleShipViewModel.nickname,
                        onValueChange = { BattleShipViewModel.nickname = it },
                        label = {
                            Text(text = "Nickname", color = MaterialTheme.colors.primary)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            unfocusedBorderColor = MaterialTheme.colors.primary,
                            textColor = MaterialTheme.colors.primary
                        ),
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_round_badge_24),
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
                        onClick = { isDialogOpened = false },
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(all = 8.dp)
                    ) {
                        Text("OK")
                    }
                    Button(
                        onClick = {
                            BattleShipViewModel.nickname = BattleShipViewModel.oldNickname
                            isDialogOpened = false
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

    var isAvatarStorageDialogOpened by remember { mutableStateOf(false) }
    if (isAvatarStorageDialogOpened) {
        AlertDialog(
            onDismissRequest = {
                isAvatarStorageDialogOpened = false
            },
            title = {
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    TextButton(
                        onClick = {
                            isAvatarStorageDialogOpened = false
                            BattleShipViewModel.isGravatar = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Firebase")
                    }
                    TextButton(
                        onClick = {
                            isAvatarStorageDialogOpened = false
                            BattleShipViewModel.isGravatar = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Gravatar")
                    }
                }
            },
            buttons = {
            }
        )
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 14.dp)
            .padding(top = 10.dp)
    ) {
        Text(
            text = "General",
            color = MaterialTheme.colors.secondary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(vertical = 8.dp)
        )
        GeneralSettingItem(
            icon = R.drawable.ic_round_badge_24,
            mainText = BattleShipViewModel.nickname,
            subText = "Your nickname",
            onClick = { isDialogOpened = true }
        )
        GeneralSettingItem(
            icon = R.drawable.ic_baseline_image_24,
            mainText = if (BattleShipViewModel.isGravatar) {
                "Gravatar"
            } else {
                "Firebase"
            },
            subText = "Storage for your avatar",
            onClick = { isAvatarStorageDialogOpened = true }
        )
    }
}

@ExperimentalMaterialApi
@Composable
fun GeneralSettingItem(icon: Int, mainText: String, subText: String, onClick: () -> Unit) {
    Card(
        onClick = { onClick() },
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(shape = Shapes.medium)
                        .background(LightPrimaryColor)
                ) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = "",
                        tint = MaterialTheme.colors.primary,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))
                Column(
                    modifier = Modifier.offset(y = (2).dp)
                ) {
                    Text(
                        text = mainText,
                        color = MaterialTheme.colors.secondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    Text(
                        text = subText,
                        color = Color.Gray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.offset(y = (-4).dp)
                    )
                }
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_arrow_forward_ios_24),
                contentDescription = "",
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun AccountOptions(viewModel: BattleShipViewModel) {
    var isDialogOpened by remember { mutableStateOf(false) }
    if (isDialogOpened) {
        AlertDialog(
            onDismissRequest = {
                isDialogOpened = false
            },
            title = {
                Text(text = "Log Out")
            },
            text = {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Are you sure?")
                }
            },
            buttons = {
                Row(horizontalArrangement = Arrangement.Center) {
                    Button(
                        onClick = {
                            viewModel.signOut()
                            isDialogOpened = false
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(all = 8.dp)
                    ) {
                        Text("Log Out")
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
        modifier = Modifier
            .padding(horizontal = 14.dp)
            .padding(top = 10.dp)
    ) {
        Text(
            text = "Account",
            color = MaterialTheme.colors.secondary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        AccountItem(
            icon = R.drawable.ic_baseline_logout_24,
            mainText = "Log Out",
            onClick = { isDialogOpened = true }
        )
    }
}

@ExperimentalMaterialApi
@Composable
fun AccountItem(icon: Int, mainText: String, onClick: () -> Unit) {
    Card(
        onClick = { onClick() },
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
                        painter = painterResource(id = icon),
                        contentDescription = "",
                        tint = MaterialTheme.colors.primary,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Text(
                    text = mainText,
                    color = MaterialTheme.colors.secondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_arrow_forward_ios_24),
                contentDescription = "",
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
