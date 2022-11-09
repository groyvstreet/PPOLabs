package com.example.calculator

import android.app.ActivityManager
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.getSystemService
import com.example.calculator.components.DefaultMode
import com.example.calculator.components.ScientificMode
import com.example.calculator.ui.theme.CalculatorTheme
import com.example.calculator.viewModels.CalculatorViewModel
import com.jakewharton.processphoenix.ProcessPhoenix

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorTheme {
                val viewModel: CalculatorViewModel by viewModels()
                viewModel.toast = Toast.makeText(LocalContext.current, "", Toast.LENGTH_LONG)
                viewModel.activityManager = this.getSystemService()!!
                viewModel.memoryInfo = ActivityManager.MemoryInfo()
                viewModel.activityManager.getMemoryInfo(viewModel.memoryInfo)
                viewModel.context = LocalContext.current

                val preferences = getSharedPreferences("input", Context.MODE_PRIVATE)
                val input = preferences.getString("input", "") ?: ""

                if (!viewModel.isStarted && input.isNotEmpty()) {
                    viewModel.input = input
                    viewModel.cursor = input.length
                    viewModel.defineNumbers()
                    viewModel.isStarted = true
                }

                fun restart() {
                    ProcessPhoenix.triggerRebirth(viewModel.context)
                }

                viewModel.restart = ::restart

                val clipboardManager = LocalClipboardManager.current
                val scroll1 = rememberScrollState(0)
                val scroll2 = rememberScrollState(0)

                if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    if (BuildConfig.IS_PRO) {
                        CalculatorViewModel.scientificMode = true
                    }
                }

                var menu1 by remember { mutableStateOf(false) }
                var menu2 by remember { mutableStateOf(false) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Box {
                                Text(
                                    text = viewModel.getInputWithCursor(),
                                    fontSize = 24.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(0.2f)
                                        .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                                        .horizontalScroll(scroll1)
                                        .combinedClickable(
                                            onLongClick = {
                                                menu1 = true
                                            },
                                            onDoubleClick = {},
                                            onClick = {}
                                        )
                                )
                                DropdownMenu(
                                    expanded = menu1,
                                    onDismissRequest = { menu1 = false },
                                    offset = DpOffset(x = 0.dp, y = (-104).dp)
                                ) {
                                    DropdownMenuItem(
                                        onClick = {
                                            menu1 = false
                                            viewModel.insert(
                                                clipboardManager.getText().toString()
                                            )
                                        }
                                    ) {
                                        Text(text = "Вставить")
                                    }
                                }
                            }
                            Box {
                                Text(
                                    text = if (viewModel.isCalculating) {
                                        "Идёт вычисление..."
                                    } else {
                                        viewModel.output
                                    },
                                    fontSize = 24.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(0.25f)
                                        .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                                        .verticalScroll(scroll2)
                                        .combinedClickable(
                                            onLongClick = {
                                                menu2 = true
                                            },
                                            onDoubleClick = {},
                                            onClick = {}
                                        )
                                )
                                DropdownMenu(
                                    expanded = menu2,
                                    onDismissRequest = { menu2 = false },
                                    offset = DpOffset(x = 0.dp, y = (-104).dp)
                                ) {
                                    DropdownMenuItem(
                                        onClick = {
                                            menu2 = false
                                            clipboardManager.setText(
                                                AnnotatedString(viewModel.output)
                                            )
                                            viewModel.toast.cancel()
                                            viewModel.toast.setText("Скопировано")
                                            viewModel.toast.show()
                                        }
                                    ) {
                                        Text(text = "Скопировать")
                                    }
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.16f)
                            ) {
                                IconButton(
                                    onClick = {
                                        viewModel.cursorToLeft()
                                    },
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(0.5f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ArrowBackIosNew,
                                        contentDescription = ""
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        viewModel.cursorToRight()
                                    },
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ArrowForwardIos,
                                        contentDescription = ""
                                    )
                                }
                            }
                            if (CalculatorViewModel.scientificMode ||
                                (LocalConfiguration.current.orientation ==
                                        Configuration.ORIENTATION_LANDSCAPE && BuildConfig.IS_PRO)
                            ) {
                                ScientificMode(viewModel)
                            } else {
                                DefaultMode(viewModel)
                            }
                        }
                    } else {
                        Row(modifier = Modifier.fillMaxSize()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(0.5f)
                            ) {
                                Box {
                                    Text(
                                        text = viewModel.getInputWithCursor(),
                                        fontSize = 24.sp,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight(0.4f)
                                            .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                                            .horizontalScroll(scroll1)
                                            .combinedClickable(
                                                onLongClick = {
                                                    menu1 = true
                                                },
                                                onDoubleClick = {},
                                                onClick = {}
                                            )
                                    )
                                    DropdownMenu(
                                        expanded = menu1,
                                        onDismissRequest = { menu1 = false },
                                        offset = DpOffset(x = 0.dp, y = (-104).dp)
                                    ) {
                                        DropdownMenuItem(
                                            onClick = {
                                                menu1 = false
                                                viewModel.insert(
                                                    clipboardManager.getText().toString()
                                                )
                                            }
                                        ) {
                                            Text(text = "Вставить")
                                        }
                                    }
                                }
                                Box {
                                    Text(
                                        text = viewModel.output,
                                        fontSize = 24.sp,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight(0.67f)
                                            .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                                            .verticalScroll(scroll2)
                                            .combinedClickable(
                                                onLongClick = {
                                                    menu2 = true
                                                },
                                                onDoubleClick = {},
                                                onClick = {}
                                            )
                                    )
                                    DropdownMenu(
                                        expanded = menu2,
                                        onDismissRequest = { menu2 = false },
                                        offset = DpOffset(x = 0.dp, y = (-104).dp)
                                    ) {
                                        DropdownMenuItem(
                                            onClick = {
                                                menu2 = false
                                                clipboardManager.setText(
                                                    AnnotatedString(viewModel.output)
                                                )
                                                viewModel.toast.cancel()
                                                viewModel.toast.setText("Скопировано")
                                                viewModel.toast.show()
                                            }
                                        ) {
                                            Text(text = "Скопировать")
                                        }
                                    }
                                }
                                Row(modifier = Modifier.fillMaxSize()) {
                                    IconButton(
                                        onClick = { viewModel.cursorToLeft() },
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(0.5f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.ArrowBackIosNew,
                                            contentDescription = ""
                                        )
                                    }
                                    IconButton(
                                        onClick = { viewModel.cursorToRight() },
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.ArrowForwardIos,
                                            contentDescription = ""
                                        )
                                    }
                                }
                            }
                            Column(modifier = Modifier.fillMaxSize()) {
                                if (CalculatorViewModel.scientificMode) {
                                    ScientificMode(viewModel)
                                } else {
                                    DefaultMode(viewModel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
