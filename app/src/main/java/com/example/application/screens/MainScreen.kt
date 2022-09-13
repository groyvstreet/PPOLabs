package com.example.application.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.application.BuildConfig
import com.example.application.R
import com.example.application.components.NumPadButton
import com.example.application.models.Converter
import com.example.application.viewModels.ConverterViewModel
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun MainScreen(
    converter: Converter,
    converterViewModel: ConverterViewModel
) {
    converterViewModel.converter = converter
    val converterUiState by converterViewModel.uiState.collectAsState()
    var selectedLabel by remember { mutableStateOf(true) }
    var selectedField by remember { mutableStateOf(true) }
    val state = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    ModalBottomSheetLayout(
        sheetState = state,
        sheetContent = {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    ListItem(
                        text = {
                            Text(
                                text = stringResource(R.string.select_unit),
                                fontSize = 22.sp,
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
                                        converterViewModel.selectUnit(
                                            item.code,
                                            selectedLabel,
                                            selectedField
                                        )
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
        Column {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.5f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.5f)
                    ) {
                        Text(
                            text = converterUiState.unit1,
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(0.2f)
                                .padding(horizontal = 20.dp)
                                .wrapContentSize(align = Alignment.BottomStart)
                                .clickable { selectedLabel = true; scope.launch { state.show() } },
                            fontSize = 24.sp
                        )
                        Text(
                            text = converterUiState.value1,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp)
                                .wrapContentSize(align = Alignment.BottomEnd)
                                .clickable { selectedField = true },
                            fontSize = if (converterUiState.value1.length < 20) 26.sp else if (converterUiState.value1.length < 26) 22.sp else 19.sp,
                            color = if (selectedField) {
                                MaterialTheme.colors.primary
                            } else {
                                MaterialTheme.colors.onSurface
                            }
                        )
                    }
                    if (BuildConfig.IS_PRO) {
                        Text(
                            text = stringResource(R.string.copy),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp)
                                .wrapContentSize(align = Alignment.TopEnd)
                                .clickable {
                                    clipboardManager.setText(
                                        AnnotatedString(
                                            converterUiState.value1
                                        )
                                    )
                                }
                        )
                    } else {
                        Surface(modifier = Modifier.fillMaxSize()) {}
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.5f)
                    ) {
                        Text(
                            text = converterUiState.unit2,
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(0.2f)
                                .padding(horizontal = 20.dp)
                                .wrapContentSize(align = Alignment.BottomStart)
                                .clickable { selectedLabel = false; scope.launch { state.show() } },
                            fontSize = 24.sp
                        )
                        Text(
                            text = converterUiState.value2,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp)
                                .wrapContentSize(align = Alignment.BottomEnd)
                                .clickable { selectedField = false },
                            fontSize = if (converterUiState.value2.length < 20) 26.sp else if (converterUiState.value2.length < 26) 22.sp else 19.sp,
                            color = if (selectedField) {
                                MaterialTheme.colors.onSurface
                            } else {
                                MaterialTheme.colors.primary
                            }
                        )
                    }
                    if (BuildConfig.IS_PRO) {
                        Text(
                            text = stringResource(R.string.copy),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp)
                                .wrapContentSize(align = Alignment.TopEnd)
                                .clickable {
                                    clipboardManager.setText(
                                        AnnotatedString(
                                            converterUiState.value2
                                        )
                                    )
                                }
                        )
                    } else {
                        Surface(modifier = Modifier.fillMaxSize()) {}
                    }
                }
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
                            onClick = { converterViewModel.addSymbolTo("7", selectedField) },
                            width = 0.33f
                        )
                        NumPadButton(
                            text = "8",
                            onClick = { converterViewModel.addSymbolTo("8", selectedField) },
                            width = 0.5f
                        )
                        NumPadButton(
                            text = "9",
                            onClick = { converterViewModel.addSymbolTo("9", selectedField) })
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxHeight(0.33f)
                    ) {
                        NumPadButton(
                            text = "4",
                            onClick = { converterViewModel.addSymbolTo("4", selectedField) },
                            width = 0.33f
                        )
                        NumPadButton(
                            text = "5",
                            onClick = { converterViewModel.addSymbolTo("5", selectedField) },
                            width = 0.5f
                        )
                        NumPadButton(
                            text = "6",
                            onClick = { converterViewModel.addSymbolTo("6", selectedField) })
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxHeight(0.5f)
                    ) {
                        NumPadButton(
                            text = "1",
                            onClick = { converterViewModel.addSymbolTo("1", selectedField) },
                            width = 0.33f
                        )
                        NumPadButton(
                            text = "2",
                            onClick = { converterViewModel.addSymbolTo("2", selectedField) },
                            width = 0.5f
                        )
                        NumPadButton(
                            text = "3",
                            onClick = { converterViewModel.addSymbolTo("3", selectedField) })
                    }
                    Row {
                        if (BuildConfig.IS_PRO) {
                            NumPadButton(
                                text = "S",
                                onClick = { converterViewModel.swap() },
                                width = 0.33f
                            )
                        } else {
                            Surface(modifier = Modifier.fillMaxWidth(0.33f)) {}
                        }
                        NumPadButton(
                            text = "0",
                            onClick = { converterViewModel.addSymbolTo("0", selectedField) },
                            width = 0.5f
                        )
                        NumPadButton(
                            text = ",",
                            onClick = { converterViewModel.addSymbolTo(",", selectedField) }
                        )
                    }
                }
                Column {
                    NumPadButton(
                        text = "AC",
                        onClick = { converterViewModel.clear() },
                        height = 0.5f
                    )
                    NumPadButton(
                        text = "<",
                        onClick = { converterViewModel.removeSymbolFrom(selectedField) }
                    )
                }
            }
        }
    }
}