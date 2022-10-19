package com.example.timer.screens

import android.widget.NumberPicker
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.timer.R
import com.example.timer.viewModels.AddEditElementViewModel

@Composable
fun EditSetCycleScreen(
    viewModel: AddEditElementViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val textColor = MaterialTheme.colors.onPrimary
    var isRepetitionsDialogOpened by remember { mutableStateOf(false) }
    if (isRepetitionsDialogOpened) {
        AlertDialog(
            onDismissRequest = {
                isRepetitionsDialogOpened = false
            },
            title = {
                Text(text = stringResource(R.string.element_card_repetitions))
            },
            text = {
                Row(modifier = Modifier.fillMaxWidth()) {
                    AndroidView(
                        modifier = Modifier.fillMaxWidth(),
                        factory = { context ->
                            NumberPicker(context).apply {
                                this.textColor = textColor.toArgb()
                                setOnValueChangedListener { numberPicker, _, _ ->
                                    viewModel.updateRepetition(numberPicker.value)
                                }
                                minValue = 0
                                maxValue = 100
                            }
                        }
                    )
                }
            },
            buttons = {
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TextButton(
            onClick = { isRepetitionsDialogOpened = true },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = "${stringResource(id = R.string.element_card_repetitions)}: ${viewModel.repetition}",
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(0.5f)
            )
            Spacer(modifier = Modifier.weight(1f, true))
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = ""
            )

        }
        Button(
            onClick = {
                viewModel.updateElement()
                navController.navigateUp()
            },
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
        ) {
            Text(text = stringResource(id = R.string.button_save))
        }
    }
}
