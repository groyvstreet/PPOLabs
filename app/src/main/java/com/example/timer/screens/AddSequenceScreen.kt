package com.example.timer.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.timer.R
import com.example.timer.components.ColorPicker
import com.example.timer.components.OutlinedTextFieldWithError
import com.example.timer.viewModels.AddEditSequenceViewModel

@Composable
fun AddSequenceScreen(
    viewModel: AddEditSequenceViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val openDialog = remember { mutableStateOf(false) }
    var isChecking by remember { mutableStateOf(false) }
    var isButtonEnabled by remember { mutableStateOf(true) }
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
                        Text(stringResource(id = R.string.button_close))
                    }
                }
            }
        )
    }
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        if (isChecking) {
            OutlinedTextFieldWithError(
                value = viewModel.title,
                onValueChange = { viewModel.updateTitle(it) },
                label = stringResource(id = R.string.title),
                showError = viewModel.isTitleInvalid(),
                errorMessage = stringResource(id = R.string.title_error_message),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            OutlinedTextField(
                value = viewModel.title,
                onValueChange = { viewModel.updateTitle(it) },
                label = { Text(text = stringResource(id = R.string.title)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
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
                }
            )
        }
        if (isChecking) {
            Button(
                onClick = {
                    isButtonEnabled = false
                    viewModel.addSequence()
                    navController.navigateUp()
                },
                enabled = !viewModel.isTitleInvalid() && isButtonEnabled,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
            ) {
                Text(text = stringResource(id = R.string.button_add))
            }
        } else {
            Button(
                onClick = {
                    if (viewModel.isTitleInvalid()) {
                        isChecking = true
                    } else {
                        isButtonEnabled = false
                        viewModel.addSequence()
                        navController.navigateUp()
                    }
                },
                enabled = isButtonEnabled,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            ) {
                Text(text = stringResource(id = R.string.button_add))
            }
        }
    }
}
