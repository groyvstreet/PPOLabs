package com.example.timer.components

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.timer.R
import com.example.timer.models.Sequence
import com.example.timer.services.TimerService
import com.example.timer.utils.Routes

@SuppressLint("ShowToast")
@Composable
fun SequenceCard(
    sequence: Sequence,
    onClick: () -> Unit,
    navController: NavHostController,
    timerService: TimerService
) {
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
                Text(stringResource(id = R.string.sequence_deleting_confirm_message))
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

    val context = LocalContext.current
    val toast = Toast.makeText(context, "", Toast.LENGTH_SHORT)
    val toastText = stringResource(id = R.string.sequence_card_empty)
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 16.dp, end = 16.dp)
            .clickable { expanded = true },
        elevation = 8.dp,
        //backgroundColor = Color(android.graphics.Color.parseColor("#${sequence.color}"))
    ) {
        Row {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(8.dp)
            ) {
                Text(
                    text = sequence.title,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Box {
                Column(modifier = Modifier.fillMaxSize()) {
                    IconButton(
                        onClick = { expanded = true },
                        modifier = Modifier.align(alignment = Alignment.End)
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = stringResource(id = R.string.content_description_show_menu)
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                if (expanded) {
                                    if (sequence.elementAmount > 0) {
                                        timerService.restartService()
                                        //context.foregroundStartService("Start")
                                        navController.navigate(
                                            route = "${Routes.START_SEQUENCE}/${sequence.id}"
                                        )
                                    } else {
                                        toast.cancel()
                                        toast.setText(toastText)
                                        toast.show()
                                    }
                                }
                                expanded = false
                            }
                        ) {
                            Text(
                                text = stringResource(id = R.string.sequence_card_start)
                            )
                        }
                        DropdownMenuItem(
                            onClick = {
                                if (expanded) {
                                    navController.navigate(route = "${Routes.EDIT_SEQUENCE}/${sequence.id}")
                                }
                                expanded = false
                            }
                        ) {
                            Text(
                                text = stringResource(id = R.string.sequence_card_edit)
                            )
                        }
                    }
                    IconButton(
                        onClick = { isDialogOpened = true },
                        modifier = Modifier.align(alignment = Alignment.End)
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
}
