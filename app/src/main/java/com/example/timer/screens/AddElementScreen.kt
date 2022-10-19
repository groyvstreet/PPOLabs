package com.example.timer.screens

import android.widget.NumberPicker
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.timer.R
import com.example.timer.components.OutlinedTextFieldWithError
import com.example.timer.viewModels.AddEditElementViewModel

@Composable
fun AddElementScreen(
    viewModel: AddEditElementViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val textColor = MaterialTheme.colors.onPrimary
    var isTimeDialogOpened by remember { mutableStateOf(false) }
    if (isTimeDialogOpened) {
        AlertDialog(
            onDismissRequest = {
                isTimeDialogOpened = false
            },
            title = {
                Text(text = stringResource(id = R.string.element_card_time))
            },
            text = {
                Row(modifier = Modifier.fillMaxWidth()) {
                    AndroidView(
                        modifier = Modifier.fillMaxWidth(0.33f),
                        factory = { context ->
                            NumberPicker(context).apply {
                                this.textColor = textColor.toArgb()
                                setOnValueChangedListener { numberPicker, _, _ ->
                                    viewModel.updateHours(numberPicker.value)
                                }
                                minValue = 0
                                maxValue = 24
                            }
                        }
                    )
                    AndroidView(
                        modifier = Modifier.fillMaxWidth(0.5f),
                        factory = { context ->
                            NumberPicker(context).apply {
                                this.textColor = textColor.toArgb()
                                setOnValueChangedListener { numberPicker, _, _ ->
                                    viewModel.updateMinutes(numberPicker.value)
                                }
                                minValue = 0
                                maxValue = 59
                            }
                        }
                    )
                    AndroidView(
                        modifier = Modifier.fillMaxWidth(),
                        factory = { context ->
                            NumberPicker(context).apply {
                                this.textColor = textColor.toArgb()
                                setOnValueChangedListener { numberPicker, _, _ ->
                                    viewModel.updateSeconds(numberPicker.value)
                                }
                                minValue = 0
                                maxValue = 59
                            }
                        }
                    )
                }
            },
            buttons = {
            }
        )
    }

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

    var isSetRepetitionsDialogOpened by remember { mutableStateOf(false) }
    if (isSetRepetitionsDialogOpened) {
        AlertDialog(
            onDismissRequest = {
                isSetRepetitionsDialogOpened = false
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
                                    viewModel.updateSetRepetition(numberPicker.value)
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

    var isCycleRepetitionsDialogOpened by remember { mutableStateOf(false) }
    if (isCycleRepetitionsDialogOpened) {
        AlertDialog(
            onDismissRequest = {
                isCycleRepetitionsDialogOpened = false
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
                                    viewModel.updateCycleRepetition(numberPicker.value)
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

    var isChecking by remember { mutableStateOf(false) }
    var singleLineForTitle by remember { mutableStateOf(false) }
    var singleLineForDescription by remember { mutableStateOf(false) }
    var singleLineForTitleWithError by remember { mutableStateOf(false) }
    var singleLineForDescriptionWithError by remember { mutableStateOf(false) }
    var isElementButtonEnabled by remember { mutableStateOf(true) }
    var isSetButtonEnabled by remember { mutableStateOf(true) }
    var isCycleButtonEnabled by remember { mutableStateOf(true) }
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
                singleLine = singleLineForTitleWithError,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { singleLineForTitleWithError = !singleLineForTitleWithError }
            )
            OutlinedTextField(
                value = viewModel.description,
                onValueChange = { viewModel.updateDescription(it) },
                label = { Text(text = stringResource(id = R.string.element_card_description)) },
                singleLine = singleLineForDescriptionWithError,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged {
                        singleLineForDescriptionWithError = !singleLineForDescriptionWithError
                    }

            )
            TextButton(
                onClick = { isTimeDialogOpened = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${stringResource(id = R.string.element_card_time)}: ${
                        if (viewModel.hours < 10) {
                            0
                        } else {
                            ""
                        }
                    }${viewModel.hours}:${
                        if (viewModel.minutes < 10) {
                            0
                        } else {
                            ""
                        }
                    }${viewModel.minutes}:${
                        if (viewModel.seconds < 10) {
                            0
                        } else {
                            ""
                        }
                    }${viewModel.seconds}",
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth(0.5f)
                )
                Spacer(modifier = Modifier.weight(1f, true))
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = ""
                )
            }
            if (viewModel.isTimeInvalid()) {
                Text(
                    text = stringResource(id = R.string.time_error_message),
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp)
                )
            }
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
                    isSetButtonEnabled = false
                    isCycleButtonEnabled = false
                    if (isElementButtonEnabled) {
                        viewModel.addElement()
                        navController.navigateUp()
                    }
                },
                enabled = viewModel.isFieldsValid() && isElementButtonEnabled,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            ) {
                Text(text = stringResource(id = R.string.button_add))
            }
        } else {
            OutlinedTextField(
                value = viewModel.title,
                onValueChange = { viewModel.updateTitle(it) },
                label = { Text(text = stringResource(id = R.string.title)) },
                singleLine = singleLineForTitle,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { singleLineForTitle = !singleLineForTitle }
            )
            OutlinedTextField(
                value = viewModel.description,
                onValueChange = { viewModel.updateDescription(it) },
                label = { Text(text = stringResource(id = R.string.element_card_description)) },
                singleLine = singleLineForDescription,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { singleLineForDescription = !singleLineForDescription }
            )
            TextButton(
                onClick = { isTimeDialogOpened = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${stringResource(id = R.string.element_card_time)}: ${
                        if (viewModel.hours < 10) {
                            0
                        } else {
                            ""
                        }
                    }${viewModel.hours}:${
                        if (viewModel.minutes < 10) {
                            0
                        } else {
                            ""
                        }
                    }${viewModel.minutes}:${
                        if (viewModel.seconds < 10) {
                            0
                        } else {
                            ""
                        }
                    }${viewModel.seconds}",
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth(0.5f)
                )
                Spacer(modifier = Modifier.weight(1f, true))
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = ""
                )
            }
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
                    if (viewModel.isFieldsValid()) {
                        isSetButtonEnabled = false
                        isCycleButtonEnabled = false
                        if (isElementButtonEnabled) {
                            viewModel.addElement()
                            navController.navigateUp()
                        }
                    } else {
                        isChecking = true
                    }
                },
                enabled = isElementButtonEnabled,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            ) {
                Text(text = stringResource(id = R.string.button_add))
            }
        }
        Divider()
        Text(text = "Добавить сет")
        TextButton(
            onClick = { isSetRepetitionsDialogOpened = true },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = "${stringResource(id = R.string.element_card_repetitions)}: ${viewModel.setRepetition}",
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
                isElementButtonEnabled = false
                isCycleButtonEnabled = false
                if (isSetButtonEnabled) {
                    viewModel.addSet()
                    navController.navigateUp()
                }
            },
            enabled = isSetButtonEnabled,
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
        ) {
            Text(text = stringResource(id = R.string.button_add))
        }
        Divider()
        Text(text = "Добавить цикл")
        TextButton(
            onClick = { isCycleRepetitionsDialogOpened = true },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = "${stringResource(id = R.string.element_card_repetitions)}: ${viewModel.cycleRepetition}",
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
                isElementButtonEnabled = false
                isSetButtonEnabled = false
                if (isCycleButtonEnabled) {
                    viewModel.addCycle()
                    navController.navigateUp()
                }
            },
            enabled = isCycleButtonEnabled,
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
        ) {
            Text(text = stringResource(id = R.string.button_add))
        }
    }
}
