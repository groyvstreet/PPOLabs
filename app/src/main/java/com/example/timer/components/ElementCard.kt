package com.example.timer.components

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.timer.R
import com.example.timer.models.Element
import com.example.timer.utils.Routes

@Composable
fun ElementCard(
    element: Element,
    onClick: () -> Unit,
    navController: NavHostController
) {
    val hours = element.time / 1000 / 3600
    val minutes = (element.time / 1000 - hours * 3600) / 60
    val seconds = element.time / 1000 - hours * 3600 - minutes * 60
    var isDialogOpened by remember { mutableStateOf(false) }
    if (isDialogOpened) {
        AlertDialog(
            onDismissRequest = {
                isDialogOpened = false
            },
            title = {
                Text(text = stringResource(id = R.string.content_description_delete))
            },
            text = {
                Text(stringResource(id = R.string.element_deleting_confirm_message))
            },
            buttons = {
                Row(
                    modifier = Modifier.padding(all = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(all = 8.dp),
                        onClick = {
                            onClick()
                            isDialogOpened = false
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
                    ) {
                        Text(stringResource(id = R.string.content_description_delete))
                    }
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 8.dp),
                        onClick = { isDialogOpened = false }
                    ) {
                        Text(stringResource(id = R.string.button_cancel))
                    }
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(
                when (MaterialTheme.typography.body1.fontSize) {
                    20.sp -> 158.dp
                    16.sp -> 132.dp
                    12.sp -> 112.dp
                    else -> 200.dp
                }
            )
            .padding(
                start = if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    16.dp
                } else {
                    0.dp
                },
                top = 16.dp,
                end = if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    16.dp
                } else {
                    0.dp
                }
            )
            .clickable {
                if (element.title.contains("{!set!}") || element.title.contains("{!cycle!}")) {
                    navController.navigate("${Routes.EDIT_SET_CYCLE}/${element.id}")
                } else {
                    navController.navigate("${Routes.EDIT_ELEMENT}/${element.id}")
                }
            },
        elevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        )
        {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.9f)
            ) {
                Text(
                    text = if (element.title.contains("{!set!}")) {
                        element.title.drop(7)
                    } else if (element.title.contains("{!cycle!}")) {
                        element.title.drop(9)
                    } else {
                        element.title
                    },
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (element.title.contains("{!set!}") || element.title.contains("{!cycle!}")) {
                    Text(
                        text = "${stringResource(id = R.string.element_card_description)}: ${element.description}",
                        fontWeight = FontWeight.Light
                    )
                } else {
                    Row {
                        Text(
                            text = "${stringResource(id = R.string.element_card_description)}: ${element.description}",
                            fontWeight = FontWeight.Light,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (element.description.isEmpty()) {
                            Text(
                                text = stringResource(id = R.string.element_card_description_empty),
                                fontWeight = FontWeight.Light,
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }
                }
                if (!(element.title.contains("{!set!}") || element.title.contains("{!cycle!}"))) {
                    Text(
                        text = "${stringResource(id = R.string.element_card_time)}: ${
                            if (hours == 0) {
                                ""
                            } else {
                                "$hours ${stringResource(id = R.string.element_card_time_hours)} "
                            }
                        }${
                            if (minutes == 0) {
                                ""
                            } else {
                                "$minutes ${stringResource(id = R.string.element_card_time_minutes)} "
                            }
                        }${
                            if (seconds == 0) {
                                ""
                            } else {
                                "$seconds ${stringResource(id = R.string.element_card_time_seconds)}"
                            }
                        }",
                        fontWeight = FontWeight.Light
                    )
                }
                Text(
                    text = "${stringResource(id = R.string.element_card_repetitions)}: ${element.repetition}",
                    fontWeight = FontWeight.Light
                )
            }
            Box(modifier = Modifier.fillMaxSize()) {
                IconButton(
                    onClick = { isDialogOpened = true },
                    modifier = Modifier.align(alignment = Alignment.BottomEnd)
                ) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = stringResource(id = R.string.content_description_delete),
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                }
            }
        }
    }
}
