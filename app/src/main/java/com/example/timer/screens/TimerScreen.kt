package com.example.timer.screens

import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.timer.R
import com.example.timer.models.Element
import com.example.timer.services.TimerService
import com.example.timer.utils.foregroundStartService
import com.example.timer.viewModels.ElementListViewModel
import java.math.RoundingMode
import java.text.DecimalFormat

fun getPhases(items: List<Element>, baseIndex: Int = 0): List<Element> {
    var resultItems = listOf<Element>()
    var setPosition = -1
    for (i in items.indices) {
        for (j in 0..items[i].repetition) {
            var tempItems = listOf<Element>()
            if (items[i].title.contains("{!set!}")) {
                tempItems = getPhases(items.subList(setPosition + 1, i), baseIndex + resultItems.size)
            } else if (items[i].title.contains("{!cycle!}")) {
                tempItems = getPhases(items.subList(0, i), baseIndex + resultItems.size)
            } else {
                val temp = Element(
                    id = (baseIndex + resultItems.size).toString(),
                    title = items[i].title,
                    time = items[i].time
                )
                resultItems = resultItems.plus(temp)
            }
            resultItems = resultItems + tempItems
        }
        if (items[i].title.contains("{!set!}")) {
            setPosition = i
        }
    }
    return resultItems
}

@Composable
fun TimerScreen(
    viewModel: ElementListViewModel = hiltViewModel(),
    navController: NavHostController,
    timerService: TimerService
) {
    var isStarted by remember {mutableStateOf(false)}
    val elements = viewModel.elements.collectAsState(initial = emptyList()).value
    val phasesState: MutableState<List<Element>> = remember { mutableStateOf(listOf()) }
    if (elements.isNotEmpty() && phasesState.value.isEmpty()) {
        /*for (i in elements.indices) {
            for (j in 0..elements[i].repetition) {
                if (elements[i].title.contains("{!set!}")) {
                    for (k in i - 1 downTo 0 step 1) {

                    }
                }
                val temp = Element(
                    id = phasesState.value.size.toString(),
                    title = elements[i].title,
                    time = elements[i].time
                )
                val newList = mutableListOf<Element>()
                phasesState.value.forEach {
                    newList.add(it)
                }
                newList.add(temp)
                phasesState.value = newList
            }
        }*/
        phasesState.value = getPhases(elements)
        timerService.phases = phasesState.value
        timerService.current.value = phasesState.value[0]
        timerService.sequenceId.value = viewModel.sequence.id
        timerService.color.value =
            Color(android.graphics.Color.parseColor("#${viewModel.sequence.color}"))
        timerService.toast = Toast.makeText(LocalContext.current, "", Toast.LENGTH_SHORT)
        timerService.lastSecondsMP = MediaPlayer.create(LocalContext.current, R.raw.three_seconds)
        timerService.newPhaseMP = MediaPlayer.create(LocalContext.current, R.raw.new_phase)
        timerService.restartService()
        timerService.stringTimerStarted = stringResource(R.string.timer_started)
        timerService.stringTimerFinished = stringResource(R.string.timer_finished)
        timerService.stringTimerBack = stringResource(R.string.notification_back)
        timerService.stringTimerPause = stringResource(R.string.notification_pause)
        timerService.stringTimerResume = stringResource(R.string.notification_resume)
        timerService.stringTimerForward = stringResource(R.string.notification_forward)
        LocalContext.current.foregroundStartService("Start")
        isStarted = true
    }
    if (!isStarted) {
        val color = if (timerService.color.value.toArgb() < -8388608) {
            Color.White
        } else {
            Color.Black
        }
        val isTimerRunning by timerService.isTimerRunning
        val current by timerService.current
        val currentHours = current.time / 1000 / 3600
        val currentMinutes = (current.time / 1000 - currentHours * 3600) / 60
        val currentSeconds = current.time / 1000 - currentHours * 3600 - currentMinutes * 60
        val isBackEnabled by timerService.isBackEnabled
        val isForwardEnabled by timerService.isForwardEnabled
        val df = DecimalFormat("#")
        df.roundingMode = RoundingMode.UP
        Surface(
            color = timerService.color.value,
            modifier = Modifier.fillMaxSize()
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        //text = df.format(current.time.toFloat() / 1000).toInt().toString(),
                        text = "${
                            if (currentHours < 10) {
                                0
                            } else {
                                ""
                            }
                        }${currentHours}:${
                            if (currentMinutes < 10) {
                                0
                            } else {
                                ""
                            }
                        }${currentMinutes}:${
                            if (currentSeconds < 10) {
                                0
                            } else {
                                ""
                            }
                        }${currentSeconds}",
                        fontSize = 64.sp,
                        color = color
                    )
                }
                Divider()
                Row(modifier = Modifier.fillMaxWidth()) {
                    IconButton(
                        onClick = {
                            timerService.goBack()
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.33f)
                            .align(Alignment.CenterVertically)
                            .wrapContentWidth(align = Alignment.CenterHorizontally),
                        enabled = isBackEnabled
                    ) {
                        if (isBackEnabled) {
                            Icon(
                                Icons.Filled.SkipPrevious,
                                contentDescription = stringResource(id = R.string.content_description_back),
                                tint = color
                            )
                        }
                    }
                    if (isTimerRunning) {
                        IconButton(
                            onClick = {
                                timerService.pauseTimer()
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .align(Alignment.CenterVertically)
                                .wrapContentWidth(align = Alignment.CenterHorizontally)
                        ) {
                            Icon(
                                Icons.Filled.Pause,
                                contentDescription = stringResource(id = R.string.content_description_stop),
                                tint = color
                            )
                        }
                    } else {
                        IconButton(
                            onClick = {
                                timerService.resumeTimer()
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .align(Alignment.CenterVertically)
                                .wrapContentWidth(align = Alignment.CenterHorizontally),
                            enabled = current.id != "null"
                        ) {
                            if (current.id != "null") {
                                Icon(
                                    Icons.Filled.PlayArrow,
                                    contentDescription = stringResource(id = R.string.content_description_start),
                                    tint = color
                                )
                            }
                        }
                    }
                    IconButton(
                        onClick = {
                            timerService.goForward()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterVertically)
                            .wrapContentWidth(align = Alignment.CenterHorizontally),
                        enabled = isForwardEnabled
                    ) {
                        if (isForwardEnabled) {
                            Icon(
                                Icons.Filled.SkipNext,
                                contentDescription = stringResource(id = R.string.content_description_forward),
                                tint = color
                            )
                        }
                    }
                }
                Divider()
                LazyColumn {
                    //var staticIndexToSelect = -1
                    itemsIndexed(timerService.phases) { index, element ->
                        //staticIndexToSelect += 1
                        //val indexToSelect = staticIndexToSelect
                        val hours = element.time / 1000 / 3600
                        val minutes = (element.time / 1000 - hours * 3600) / 60
                        val seconds = element.time / 1000 - hours * 3600 - minutes * 60
                        TextButton(
                            onClick = { timerService.selectPhase(index) },
                            enabled = isBackEnabled || isForwardEnabled,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "${index + 1}) ${element.title}",
                                textAlign = TextAlign.Right,
                                fontWeight = if (current.id == element.id) {
                                    FontWeight.ExtraBold
                                } else {
                                    FontWeight.Normal
                                },
                                fontSize = if (current.id == element.id) {
                                    (MaterialTheme.typography.body1.fontSize.value + 4).sp
                                } else {
                                    MaterialTheme.typography.body1.fontSize
                                },
                                color = color,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.fillMaxWidth(0.7f)
                            )
                            Text(
                                text = ": ${
                                    if (hours < 10) {
                                        0
                                    } else {
                                        ""
                                    }
                                }${hours}:${
                                    if (minutes < 10) {
                                        0
                                    } else {
                                        ""
                                    }
                                }${minutes}:${
                                    if (seconds < 10) {
                                        0
                                    } else {
                                        ""
                                    }
                                }${seconds}",
                                textAlign = TextAlign.Left,
                                fontWeight = if (current.id == element.id) {
                                    FontWeight.ExtraBold
                                } else {
                                    FontWeight.Normal
                                },
                                fontSize = if (current.id == element.id) {
                                    (MaterialTheme.typography.body1.fontSize.value + 4).sp
                                } else {
                                    MaterialTheme.typography.body1.fontSize
                                },
                                color = color,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Divider()
                    }
                    item {
                        TextButton(
                            onClick = { timerService.selectPhase(timerService.phases.size) },
                            enabled = isBackEnabled || isForwardEnabled,
                            modifier = Modifier.fillMaxWidth()

                        ) {
                            Text(
                                text = stringResource(id = R.string.finish),
                                textAlign = TextAlign.Center,
                                fontWeight = if (current.id == "null") {
                                    FontWeight.ExtraBold
                                } else {
                                    FontWeight.Normal
                                },
                                fontSize = if (current.id == "null") {
                                    (MaterialTheme.typography.body1.fontSize.value + 4).sp
                                } else {
                                    MaterialTheme.typography.body1.fontSize
                                },
                                color = color
                            )
                        }
                        Divider()
                    }
                }
            }
        }
    }
}
