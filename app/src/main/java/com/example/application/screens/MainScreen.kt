package com.example.application.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
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
@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
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
    val context = LocalContext.current
    val scroll1 = rememberScrollState()
    val scroll2 = rememberScrollState()
    val toast = Toast.makeText(context, "", Toast.LENGTH_SHORT)

    fun onLongClick(isFirstSelected: Boolean) {
        selectedField = isFirstSelected
        val isCorrect =
            converterViewModel.pasteTo(clipboardManager.getText().toString(), selectedField)
        if (isCorrect) {
            toast.setText(R.string.pasted)
            toast.show()
        } else {
            toast.setText(R.string.invalid_input)
            toast.show()
        }
    }

    fun onNumPadButtonClick(symbol: String) {
        converterViewModel.addSymbolTo(symbol, selectedField)
        if (selectedField) {
            if (converterUiState.value1.replace(" ", "").length >= 101) {
                toast.cancel()
                toast.setText(R.string.limit)
                toast.show()
            } else {
                scope.launch { scroll1.animateScrollBy(10000f) }
            }
        } else {
            if (converterUiState.value2.replace(" ", "").length >= 101) {
                toast.cancel()
                toast.setText(R.string.limit)
                toast.show()
            } else {
                scope.launch { scroll2.animateScrollBy(10000f) }
            }
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
                                .horizontalScroll(scroll1)
                                .combinedClickable(
                                    onLongClick = { onLongClick(true) },
                                    onDoubleClick = {},
                                    onClick = { selectedField = true }
                                ),
                            fontSize = 24.sp,
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
                                    toast.cancel()
                                    toast.setText(R.string.copied)
                                    toast.show()
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
                                .horizontalScroll(scroll2)
                                .combinedClickable(
                                    onLongClick = { onLongClick(false) },
                                    onDoubleClick = {},
                                    onClick = { selectedField = false }
                                ),
                            fontSize = 24.sp,
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
                                    toast.cancel()
                                    toast.setText(R.string.copied)
                                    toast.show()
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
                            onClick = { onNumPadButtonClick("7") },
                            width = 0.33f
                        )
                        NumPadButton(
                            text = "8",
                            onClick = { onNumPadButtonClick("8") },
                            width = 0.5f
                        )
                        NumPadButton(
                            text = "9",
                            onClick = { onNumPadButtonClick("9") }
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxHeight(0.33f)
                    ) {
                        NumPadButton(
                            text = "4",
                            onClick = { onNumPadButtonClick("4") },
                            width = 0.33f
                        )
                        NumPadButton(
                            text = "5",
                            onClick = { onNumPadButtonClick("5") },
                            width = 0.5f
                        )
                        NumPadButton(
                            text = "6",
                            onClick = { onNumPadButtonClick("6") }
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxHeight(0.5f)
                    ) {
                        NumPadButton(
                            text = "1",
                            onClick = { onNumPadButtonClick("1") },
                            width = 0.33f
                        )
                        NumPadButton(
                            text = "2",
                            onClick = { onNumPadButtonClick("2") },
                            width = 0.5f
                        )
                        NumPadButton(
                            text = "3",
                            onClick = { onNumPadButtonClick("3") }
                        )
                    }
                    Row {
                        if (BuildConfig.IS_PRO) {
                            NumPadButton(
                                text = "S",
                                onClick = {
                                    converterViewModel.swap()
                                    val temp = scroll1.value
                                    scope.launch { scroll1.scrollTo(scroll2.value) }
                                    scope.launch { scroll2.scrollTo(temp) }
                                },
                                width = 0.33f
                            )
                        } else {
                            Surface(modifier = Modifier.fillMaxWidth(0.33f)) {}
                        }
                        NumPadButton(
                            text = "0",
                            onClick = { onNumPadButtonClick("0") },
                            width = 0.5f
                        )
                        NumPadButton(
                            text = ",",
                            onClick = {
                                converterViewModel.addSymbolTo(",", selectedField)
                                if (selectedField) {
                                    scope.launch { scroll1.animateScrollBy(10000f) }
                                } else {
                                    scope.launch { scroll2.animateScrollBy(10000f) }
                                }
                            }
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