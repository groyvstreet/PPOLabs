package com.example.calculator.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.calculator.viewModels.CalculatorViewModel
import kotlinx.coroutines.launch

@Composable
fun ScientificMode(viewModel: CalculatorViewModel) {
    val scope = rememberCoroutineScope()
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.142857f)
                .padding(4.dp)
        ) {
            CalculatorButton(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.2f)
                    .padding(4.dp),
                imageVector = Icons.Filled.ExposureZero,
                text = "2nd",
                withIcon = false,
                onClick = { viewModel.second = !viewModel.second },
                enabled = !viewModel.isCalculating
            )
            if (viewModel.second) {
                CalculatorButton(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.25f)
                        .padding(4.dp),
                    imageVector = Icons.Filled.ExposureZero,
                    text = "asin",
                    withIcon = false,
                    onClick = {
                        viewModel.addSymbolToInput("arcsin(")
                    },
                    enabled = !viewModel.isCalculating
                )
                CalculatorButton(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.33f)
                        .padding(4.dp),
                    imageVector = Icons.Filled.ExposureZero,
                    text = "acos",
                    withIcon = false,
                    onClick = {
                        viewModel.addSymbolToInput("arccos(")
                    },
                    enabled = !viewModel.isCalculating
                )
                CalculatorButton(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.5f)
                        .padding(4.dp),
                    imageVector = Icons.Filled.ExposureZero,
                    text = "atg",
                    withIcon = false,
                    onClick = {
                        viewModel.addSymbolToInput("arctg(")
                    },
                    enabled = !viewModel.isCalculating
                )
            } else {
                CalculatorButton(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.25f)
                        .padding(4.dp),
                    imageVector = Icons.Filled.ExposureZero,
                    text = "sin",
                    withIcon = false,
                    onClick = {
                        viewModel.addSymbolToInput("sin(")
                    },
                    enabled = !viewModel.isCalculating
                )
                CalculatorButton(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.33f)
                        .padding(4.dp),
                    imageVector = Icons.Filled.ExposureZero,
                    text = "cos",
                    withIcon = false,
                    onClick = {
                        viewModel.addSymbolToInput("cos(")
                    },
                    enabled = !viewModel.isCalculating
                )
                CalculatorButton(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.5f)
                        .padding(4.dp),
                    imageVector = Icons.Filled.ExposureZero,
                    text = "tg",
                    withIcon = false,
                    onClick = {
                        viewModel.addSymbolToInput("tg(")
                    },
                    enabled = !viewModel.isCalculating
                )
            }
            CalculatorButton(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                imageVector = Icons.Filled.Calculate,
                onClick = {
                    scope.launch { viewModel.calculateAsync() }
                },
                enabled = viewModel.input.isNotEmpty() && !viewModel.isCalculating
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.16f)
                .padding(4.dp)
        ) {
            CalculatorButton(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.2f)
                    .padding(4.dp),
                imageVector = Icons.Filled.ExposureZero,
                text = "X^Y",
                withIcon = false,
                onClick = {
                    viewModel.addSymbolToInput("^(")
                },
                enabled = !viewModel.isCalculating
            )
            CalculatorButton(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.25f)
                    .padding(4.dp),
                imageVector = Icons.Filled.ExposureZero,
                text = "pi",
                withIcon = false,
                onClick = {
                    viewModel.addSymbolToInput("pi")
                },
                enabled = !viewModel.isCalculating
            )
            CalculatorButton(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.33f)
                    .padding(4.dp),
                imageVector = Icons.Filled.ExposureZero,
                text = "e",
                withIcon = false,
                onClick = {
                    viewModel.addSymbolToInput("e")
                },
                enabled = !viewModel.isCalculating
            )
            CalculatorButton(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.5f)
                    .padding(4.dp),
                imageVector = Icons.Filled.ExposureZero,
                text = "lg",
                withIcon = false,
                onClick = {
                    viewModel.addSymbolToInput("lg(")
                },
                enabled = !viewModel.isCalculating
            )
            CalculatorButton(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                imageVector = Icons.Filled.ExposureZero,
                text = "ln",
                withIcon = false,
                onClick = {
                    viewModel.addSymbolToInput("ln(")
                },
                enabled = !viewModel.isCalculating
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.2f)
                .padding(4.dp)
        ) {
            CalculatorButton(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.2f)
                    .padding(4.dp),
                imageVector = Icons.Filled.ExposureZero,
                text = "X!",
                withIcon = false,
                onClick = {
                    viewModel.addSymbolToInput("!")
                },
                enabled = !viewModel.isCalculating
            )
            CalculatorButton(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.25f)
                    .padding(4.dp),
                imageVector = Icons.Filled.ExposureZero,
                text = "(",
                withIcon = false,
                onClick = {
                    viewModel.addSymbolToInput("(")
                },
                enabled = !viewModel.isCalculating
            )
            CalculatorButton(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.33f)
                    .padding(4.dp),
                imageVector = Icons.Filled.ExposureZero,
                text = ")",
                withIcon = false,
                onClick = {
                    viewModel.addSymbolToInput(")")
                },
                enabled = !viewModel.isCalculating
            )
            CalculatorButton(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.5f)
                    .padding(4.dp),
                imageVector = Icons.Filled.ExposureZero,
                text = "AC",
                withIcon = false,
                onClick = { viewModel.clear() },
                enabled = !viewModel.isCalculating
            )
            CalculatorButton(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                imageVector = Icons.Filled.Backspace,
                onClick = { viewModel.removeSymbol() },
                enabled = !viewModel.isCalculating
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.25f)
        ) {
            CalculatorButton(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.25f)
                    .padding(4.dp),
                imageVector = Icons.Filled.ExposureZero,
                text = "7",
                withIcon = false,
                onClick = {
                    viewModel.addSymbolToInput("7")
                },
                enabled = !viewModel.isCalculating
            )
            CalculatorButton(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.33f)
                    .padding(4.dp),
                imageVector = Icons.Filled.ExposureZero,
                text = "8",
                withIcon = false,
                onClick = {
                    viewModel.addSymbolToInput("8")
                },
                enabled = !viewModel.isCalculating
            )
            CalculatorButton(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.5f)
                    .padding(4.dp),
                imageVector = Icons.Filled.ExposureZero,
                text = "9",
                withIcon = false,
                onClick = {
                    viewModel.addSymbolToInput("9")
                },
                enabled = !viewModel.isCalculating
            )
            CalculatorButton(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                imageVector = Icons.Filled.Add,
                onClick = {
                    viewModel.addSymbolToInput("+")
                },
                enabled = !viewModel.isCalculating
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.33f)
        ) {
            CalculatorButton(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.25f)
                    .padding(4.dp),
                imageVector = Icons.Filled.ExposureZero,
                text = "4",
                withIcon = false,
                onClick = {
                    viewModel.addSymbolToInput("4")
                },
                enabled = !viewModel.isCalculating
            )
            CalculatorButton(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.33f)
                    .padding(4.dp),
                imageVector = Icons.Filled.ExposureZero,
                text = "5",
                withIcon = false,
                onClick = {
                    viewModel.addSymbolToInput("5")
                },
                enabled = !viewModel.isCalculating
            )
            CalculatorButton(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.5f)
                    .padding(4.dp),
                imageVector = Icons.Filled.ExposureZero,
                text = "6",
                withIcon = false,
                onClick = {
                    viewModel.addSymbolToInput("6")
                },
                enabled = !viewModel.isCalculating
            )
            CalculatorButton(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                imageVector = Icons.Filled.Remove,
                onClick = {
                    viewModel.addSymbolToInput("-")
                },
                enabled = !viewModel.isCalculating
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
        ) {
            CalculatorButton(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.25f)
                    .padding(4.dp),
                imageVector = Icons.Filled.ExposureZero,
                text = "1",
                withIcon = false,
                onClick = {
                    viewModel.addSymbolToInput("1")
                },
                enabled = !viewModel.isCalculating
            )
            CalculatorButton(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.33f)
                    .padding(4.dp),
                imageVector = Icons.Filled.ExposureZero,
                text = "2",
                withIcon = false,
                onClick = {
                    viewModel.addSymbolToInput("2")
                },
                enabled = !viewModel.isCalculating
            )
            CalculatorButton(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.5f)
                    .padding(4.dp),
                imageVector = Icons.Filled.ExposureZero,
                text = "3",
                withIcon = false,
                onClick = {
                    viewModel.addSymbolToInput("3")
                },
                enabled = !viewModel.isCalculating
            )
            CalculatorButton(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                imageVector = Icons.Filled.Clear,
                onClick = {
                    viewModel.addSymbolToInput("x")
                },
                enabled = !viewModel.isCalculating
            )
        }
        Row(
            modifier = Modifier
                .fillMaxSize()
        ) {
            CalculatorButton(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.25f)
                    .padding(4.dp),
                imageVector = Icons.Filled.SwapHoriz,
                onClick = { CalculatorViewModel.scientificMode = false },
                enabled = !(LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE && CalculatorViewModel.scientificMode) && !viewModel.isCalculating
            )
            CalculatorButton(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.33f)
                    .padding(4.dp),
                imageVector = Icons.Filled.ExposureZero,
                text = "0",
                withIcon = false,
                onClick = {
                    viewModel.addSymbolToInput("0")
                },
                enabled = !viewModel.isCalculating
            )
            CalculatorButton(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.5f)
                    .padding(4.dp),
                imageVector = Icons.Filled.ExposureZero,
                text = ",",
                withIcon = false,
                onClick = {
                    viewModel.addSymbolToInput(",")
                },
                enabled = !viewModel.isCalculating
            )
            CalculatorButton(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                imageVector = Icons.Filled.ExposureZero,
                text = "/",
                withIcon = false,
                onClick = {
                    viewModel.addSymbolToInput("/")
                },
                enabled = !viewModel.isCalculating
            )
        }
    }
}
