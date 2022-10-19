package com.example.timer.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.timer.viewModels.ElementListViewModel

@Composable
fun ElementList(
    viewModel: ElementListViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val elements = viewModel.elements.collectAsState(initial = emptyList()).value
    LazyColumn(
        modifier = Modifier.padding(
            end = if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) {
                0.dp
            } else {
                16.dp
            }
        )
    ) {
        items(elements) { element ->
            ElementCard(element, { viewModel.deleteElement(element.id) }, navController)
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
