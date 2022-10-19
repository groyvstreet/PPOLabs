package com.example.timer.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timer.data.SequenceRepository
import com.example.timer.models.Sequence
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditSequenceViewModel @Inject constructor(
    private val repository: SequenceRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var sequence by mutableStateOf<Sequence?>(null)
        private set

    var title by mutableStateOf("")
        private set

    var color by mutableStateOf("FFFFFF")
        private set

    init {
        val id = savedStateHandle.get<String>("sequenceId")
        if (id != null) {
            viewModelScope.launch {
                repository.getSequenceById(id)?.let { sequence ->
                    title = sequence.title
                    color = sequence.color
                    this@AddEditSequenceViewModel.sequence = sequence
                }
            }
        } else {
            sequence = Sequence()
        }
    }

    fun updateTitle(title: String) {
        this.title = title
    }

    fun updateColor(color: String) {
        this.color = color
    }

    fun addSequence() {
        val temp = Sequence(
            title = title.trim(),
            color = color
        )
        viewModelScope.launch {
            repository.addSequence(temp)
        }
    }

    fun updateSequence() {
        val temp = Sequence(
            id = sequence!!.id,
            title = title.trim(),
            color = color,
            elementAmount = sequence!!.elementAmount
        )
        viewModelScope.launch {
            repository.updateSequence(temp)
        }
    }

    fun isTitleInvalid(): Boolean {
        return title.isBlank()
    }
}
