package com.example.timer.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.timer.R
import com.example.timer.components.ColorPicker
import com.example.timer.components.ElementList
import com.example.timer.components.OutlinedTextFieldWithError
import com.example.timer.viewModels.AddEditSequenceViewModel

@Composable
fun EditSequenceScreen(
    viewModel: AddEditSequenceViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val openDialog = remember { mutableStateOf(false) }
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            text = {
                ColorPicker(onColorChanged = viewModel::updateColor)
            },
            buttons = {
                Row(
                    modifier = Modifier.padding(all = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { openDialog.value = false }
                    ) {
                        Text(text = stringResource(id = R.string.button_close))
                    }
                }
            }
        )
    }
    if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) {
        Column {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = 8.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextFieldWithError(
                        value = viewModel.title,
                        onValueChange = { viewModel.updateTitle(it) },
                        label = stringResource(id = R.string.title),
                        showError = viewModel.isTitleInvalid(),
                        errorMessage = stringResource(id = R.string.title_error_message),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = { openDialog.value = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(
                                android.graphics.Color.parseColor("#${viewModel.color}")
                            )
                        )
                    ) {
                        Text(
                            text = stringResource(id = R.string.button_color),
                            color = if (android.graphics.Color.parseColor("#${viewModel.color}") < -8388608) {
                                Color.White
                            } else {
                                Color.Black
                            },
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Button(
                        onClick = {
                            viewModel.updateSequence()
                            navController.navigateUp()
                        },
                        enabled = !viewModel.isTitleInvalid(),
                        modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                    ) {
                        Text(text = stringResource(id = R.string.button_save))
                    }
                }
            }
            Divider()
            ElementList(navController = navController)
        }
    } else {
        Row {
            Card(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.5f)
                    .padding(16.dp),
                elevation = 8.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextFieldWithError(
                        value = viewModel.title,
                        onValueChange = { viewModel.updateTitle(it) },
                        label = stringResource(id = R.string.title),
                        showError = viewModel.isTitleInvalid(),
                        errorMessage = stringResource(id = R.string.title_error_message),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = { openDialog.value = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(
                                android.graphics.Color.parseColor("#${viewModel.color}")
                            )
                        )
                    ) {
                        Text(
                            text = stringResource(id = R.string.button_color),
                            color = if (android.graphics.Color.parseColor("#${viewModel.color}") < -8388608) {
                                Color.White
                            } else {
                                Color.Black
                            },
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Button(
                        onClick = {
                            viewModel.updateSequence()
                            navController.navigateUp()
                        },
                        enabled = !viewModel.isTitleInvalid(),
                        modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                    ) {
                        Text(text = stringResource(id = R.string.button_save))
                    }
                }
            }
            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.padding(8.dp))
                Divider(modifier = Modifier.padding(end = 16.dp))
                ElementList(navController = navController)
            }
        }
    }
}
