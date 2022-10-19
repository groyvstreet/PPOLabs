package com.example.timer.screens

import android.content.Context
import android.content.SharedPreferences
import android.view.Window
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.timer.R

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    isDarkTheme: Boolean,
    fontSize: String,
    language: String,
    updateIsDarkTheme: () -> Unit,
    updateFontSize: () -> Unit,
    updateLanguage: () -> Unit
) {
    val preferences: SharedPreferences =
        LocalContext.current.getSharedPreferences("settings", Context.MODE_PRIVATE)

    val fontSizeValues = stringArrayResource(id = R.array.font_size_values)
    val fontSizeIndex = fontSizeValues.indexOf(fontSize)
    val fontSizeEntries = stringArrayResource(id = R.array.font_size_entries)

    val languageValues = stringArrayResource(id = R.array.language_values)
    val languageIndex = languageValues.indexOf(language)
    val languageEntries = stringArrayResource(id = R.array.language_entries)

    var isFontSizeDialogOpened by remember { mutableStateOf(false) }
    if (isFontSizeDialogOpened) {
        AlertDialog(
            onDismissRequest = {
                isFontSizeDialogOpened = false
            },
            title = {
                Text(text = stringResource(R.string.font_size))
            },
            text = {
                Column {
                    for (i in fontSizeEntries.indices) {
                        TextButton(
                            onClick = {
                                isFontSizeDialogOpened = false
                                val editor = preferences.edit()
                                editor.putString("font_size", fontSizeValues[i])
                                editor.apply()
                                updateFontSize()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = fontSizeEntries[i],
                                fontSize = fontSizeValues[i].toInt().sp
                            )
                        }
                    }
                }
            },
            buttons = {
            }
        )
    }

    var isLanguageDialogOpened by remember { mutableStateOf(false) }
    if (isLanguageDialogOpened) {
        AlertDialog(
            onDismissRequest = {
                isLanguageDialogOpened = false
            },
            title = {
                Text(text = stringResource(R.string.language))
            },
            text = {
                Column {
                    for (i in languageEntries.indices) {
                        TextButton(
                            onClick = {
                                isLanguageDialogOpened = false
                                val editor = preferences.edit()
                                editor.putString("language", languageValues[i])
                                editor.apply()
                                updateLanguage()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = languageEntries[i])
                        }
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
            text = stringResource(id = R.string.category_general),
            color = MaterialTheme.colors.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(vertical = 8.dp)
        )
        Card(
            onClick = {
                val editor = preferences.edit()
                editor.putInt(
                    "dark_theme",
                    if (isDarkTheme) {
                        0
                    } else {
                        1
                    }
                )
                editor.apply()
                updateIsDarkTheme()
            },
            modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth(),
            elevation = 0.dp
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
                            .clip(shape = Shapes().medium)
                            .background(Color.White)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DarkMode,
                            contentDescription = "",
                            tint = Color.Unspecified,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Text(
                        text = stringResource(id = R.string.dark_theme),
                        fontWeight = FontWeight.Bold
                    )
                }
                Icon(
                    imageVector = if (isDarkTheme) {
                        Icons.Filled.ToggleOn
                    } else {
                        Icons.Filled.ToggleOff
                    },
                    contentDescription = "",
                    modifier = Modifier.size(32.dp)
                )

            }
        }
        GeneralSettingsItem(
            icon = Icons.Filled.FormatSize,
            mainText = stringResource(id = R.string.font_size),
            subText = fontSizeEntries[fontSizeIndex],
            onClick = { isFontSizeDialogOpened = true }
        )
        GeneralSettingsItem(
            icon = Icons.Filled.Language,
            mainText = stringResource(id = R.string.language),
            subText = languageEntries[languageIndex],
            onClick = { isLanguageDialogOpened = true }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GeneralSettingsItem(icon: ImageVector, mainText: String, subText: String, onClick: () -> Unit) {
    Card(
        onClick = { onClick() },
        modifier = Modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth(),
        elevation = 0.dp
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
                        .clip(shape = Shapes().medium)
                        .background(Color.White)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "",
                        tint = Color.Unspecified,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))
                Column(
                    modifier = Modifier.offset(y = (2).dp)
                ) {
                    Text(
                        text = mainText,
                        fontWeight = FontWeight.Bold,
                    )

                    Text(
                        text = subText,
                        color = Color.Gray,
                        fontSize = (MaterialTheme.typography.body1.fontSize.value - 4).sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.offset(y = (-4).dp)
                    )
                }
            }
            Icon(
                imageVector = Icons.Filled.ArrowForwardIos,
                contentDescription = "",
                modifier = Modifier.size(16.dp)
            )

        }
    }
}
