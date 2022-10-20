package com.example.timer.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.timer.components.SequenceCard
import com.example.timer.services.TimerService
import com.example.timer.viewModels.SequenceListViewModel

@Composable
fun SequenceListScreen(
    viewModel: SequenceListViewModel = hiltViewModel(),
    navController: NavHostController,
    timerService: TimerService
) {
    if (timerService.isServiceRunning.value) {
        timerService.clickPendingIntent()?.send()
    }
    val sequences = viewModel.sequences.collectAsState(initial = emptyList()).value
    LazyColumn {
        items(sequences) { sequence ->
            SequenceCard(sequence, { viewModel.deleteSequence(sequence.id) }, navController, timerService)
        }
        item {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )
        }
    }
}
