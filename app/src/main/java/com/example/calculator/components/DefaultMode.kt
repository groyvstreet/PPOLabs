package com.example.calculator.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.calculator.BuildConfig
import com.example.calculator.viewModels.CalculatorViewModel
import kotlinx.coroutines.launch

@Composable
fun DefaultMode(viewModel: CalculatorViewModel) {
    val scope = rememberCoroutineScope()
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.2f)
                .padding(4.dp)
        ) {
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
                    .fillMaxHeight()
                    .fillMaxWidth(0.5f)
                    .padding(4.dp),
                imageVector = Icons.Filled.Backspace,
                onClick = { viewModel.removeSymbol() },
                enabled = !viewModel.isCalculating
            )
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
                onClick = {
                    if (BuildConfig.IS_PRO) {
                        CalculatorViewModel.scientificMode = true
                    } else {
                        viewModel.toast.cancel()
                        viewModel.toast.setText("Приобретите Pro-версию приложения, чтобы получить возможность использовать расширенный калькулятор")
                        viewModel.toast.show()
                    }
                },
                enabled = !viewModel.isCalculating
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
