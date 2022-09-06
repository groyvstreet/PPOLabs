package com.example.application.screens

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.application.utils.addSymbol
import com.example.application.components.NumPadButton
import com.example.application.utils.formatAndToDouble
import com.example.application.models.Converter
import com.example.application.utils.removeSymbol
import com.example.application.utils.toStringAndFormat
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun MainScreen(converter: Converter) {
    var selectedField by remember { mutableStateOf(true) }
    var firstField by remember { mutableStateOf("0") }
    var secondField by remember { mutableStateOf("0") }
    var selectedLabel by remember { mutableStateOf(true) }
    var firstLabel by remember { mutableStateOf(converter.unitsList[1].code) }
    var secondLabel by remember { mutableStateOf(converter.unitsList[0].code) }
    val state = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    val orientation = LocalConfiguration.current.orientation

    fun numPadButtonOnClick(string: String) {
        if (selectedField) {
            firstField = addSymbol(firstField, string)
            secondField = toStringAndFormat(
                converter.convert(
                    formatAndToDouble(firstField),
                    firstLabel,
                    secondLabel
                )
            )
        } else {
            secondField = addSymbol(secondField, string)
            firstField = toStringAndFormat(
                converter.convert(
                    formatAndToDouble(secondField),
                    secondLabel,
                    firstLabel
                )
            )
        }
    }

    fun getPadding(): Dp {
        return if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            80.dp
        } else {
            0.dp
        }
    }

    ModalBottomSheetLayout(
        sheetState = state,
        sheetContent = {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    ListItem(
                        text = {
                            Text(
                                text = "Select unit",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    )
                }
                items(converter.unitsList) { item ->
                    ListItem(
                        text = {
                            TextButton(
                                onClick = {
                                    scope.launch {
                                        if (selectedLabel) {
                                            firstLabel = item.code
                                        } else {
                                            secondLabel = item.code
                                        }
                                        if (selectedField) {
                                            secondField = toStringAndFormat(
                                                converter.convert(
                                                    formatAndToDouble(firstField),
                                                    firstLabel,
                                                    secondLabel
                                                )
                                            )
                                        } else {
                                            firstField = toStringAndFormat(
                                                converter.convert(
                                                    formatAndToDouble(secondField),
                                                    secondLabel,
                                                    firstLabel
                                                )
                                            )
                                        }
                                        state.hide()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = "${item.title} ${item.code}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    )
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.25f)
            ) {
                Text(
                    text = firstLabel,
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.2f)
                        .padding(horizontal = 20.dp)
                        .padding(top = getPadding())
                        .wrapContentSize(align = Alignment.CenterStart)
                        .clickable { selectedLabel = true; scope.launch { state.show() } },
                    fontSize = 24.sp
                )
                Text(
                    text = firstField,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                        .padding(top = getPadding())
                        .wrapContentSize(align = Alignment.CenterEnd)
                        .clickable { selectedField = true },
                    fontSize = if (firstField.length < 20) 26.sp else if (firstField.length < 26) 22.sp else 19.sp,
                    color = if (selectedField) {
                        Color.Blue
                    } else {
                        Color.Black
                    }
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.33f)
            ) {
                Text(
                    text = secondLabel,
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.2f)
                        .padding(horizontal = 20.dp)
                        .padding(bottom = getPadding())
                        .wrapContentSize(align = Alignment.CenterStart)
                        .clickable { selectedLabel = false; scope.launch { state.show() } },
                    fontSize = 24.sp
                )
                Text(
                    text = secondField,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = getPadding())
                        .wrapContentSize(align = Alignment.CenterEnd)
                        .clickable { selectedField = false },
                    fontSize = if (secondField.length < 20) 26.sp else if (secondField.length < 26) 22.sp else 19.sp,
                    color = if (selectedField) {
                        Color.Black
                    } else {
                        Color.Blue
                    }
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.75f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxHeight(0.25f)
                    ) {
                        NumPadButton(
                            text = "7",
                            onClick = { numPadButtonOnClick("7") },
                            width = 0.33f
                        )
                        NumPadButton(
                            text = "8",
                            onClick = { numPadButtonOnClick("8") },
                            width = 0.5f
                        )
                        NumPadButton(
                            text = "9",
                            onClick = { numPadButtonOnClick("9") })
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxHeight(0.33f)
                    ) {
                        NumPadButton(
                            text = "4",
                            onClick = { numPadButtonOnClick("4") },
                            width = 0.33f
                        )
                        NumPadButton(
                            text = "5",
                            onClick = { numPadButtonOnClick("5") },
                            width = 0.5f
                        )
                        NumPadButton(
                            text = "6",
                            onClick = { numPadButtonOnClick("6") })
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxHeight(0.5f)
                    ) {
                        NumPadButton(
                            text = "1",
                            onClick = { numPadButtonOnClick("1") },
                            width = 0.33f
                        )
                        NumPadButton(
                            text = "2",
                            onClick = { numPadButtonOnClick("2") },
                            width = 0.5f
                        )
                        NumPadButton(
                            text = "3",
                            onClick = { numPadButtonOnClick("3") })
                    }
                    Row {
                        Surface(modifier = Modifier.fillMaxWidth(0.33f)) {}
                        NumPadButton(
                            text = "0",
                            onClick = { numPadButtonOnClick("0") },
                            width = 0.5f
                        )
                        NumPadButton(
                            text = ",",
                            onClick = { numPadButtonOnClick(",") }
                        )
                    }
                }
                Column {
                    NumPadButton(
                        text = "AC",
                        onClick = { firstField = "0"; secondField = "0" },
                        height = 0.5f
                    )
                    NumPadButton(
                        text = "<",
                        onClick = {
                            if (selectedField) {
                                firstField = removeSymbol(firstField)
                                secondField = toStringAndFormat(
                                    converter.convert(
                                        formatAndToDouble(firstField),
                                        firstLabel,
                                        secondLabel
                                    )
                                )
                            } else {
                                secondField = removeSymbol(secondField)
                                firstField = toStringAndFormat(
                                    converter.convert(
                                        formatAndToDouble(secondField),
                                        secondLabel,
                                        firstLabel
                                    )
                                )
                            }
                        })
                }
            }
        }
    }
}