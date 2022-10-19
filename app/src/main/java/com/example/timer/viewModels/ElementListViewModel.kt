package com.example.timer.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timer.data.ElementRepository
import com.example.timer.data.SequenceRepository
import com.example.timer.models.Element
import com.example.timer.models.Sequence
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ElementListViewModel @Inject constructor(
    private val elementRepository: ElementRepository,
    private val sequenceRepository: SequenceRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var elements: Flow<List<Element>> = flow {}
        private set

    var sequence by mutableStateOf(Sequence(elementAmount = 0))
        private set

    init {
        val sequenceId = savedStateHandle.get<String>("sequenceId")
        if (sequenceId != null) {
            elements = elementRepository.getElements(sequenceId)
            viewModelScope.launch {
                sequenceRepository.getSequenceById(sequenceId)?.let { sequence ->
                    this@ElementListViewModel.sequence = sequence
                }
            }
        }
    }

    fun deleteElement(id: String) {
        viewModelScope.launch {
            val element = elementRepository.getElementById(id)
            elementRepository.deleteElement(element!!)
            if (element.title.contains("{!set!}") || element.title.contains("{!cycle!}")) {
                sequence.elementAmount += 1
            }
            sequence.elementAmount -= 1
            sequenceRepository.updateSequence(sequence)
        }
    }
}
