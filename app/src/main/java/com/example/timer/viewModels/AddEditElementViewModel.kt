package com.example.timer.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timer.R
import com.example.timer.data.ElementRepository
import com.example.timer.data.SequenceRepository
import com.example.timer.models.Element
import com.example.timer.utils.ResourcesProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditElementViewModel @Inject constructor(
    private val repository: ElementRepository,
    private val sequenceRepository: SequenceRepository,
    private val resourcesProvider: ResourcesProvider,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var element by mutableStateOf<Element?>(null)
        private set

    var title by mutableStateOf("")
        private set

    var description by mutableStateOf("")
        private set

    var hours by mutableStateOf(0)
        private set

    var minutes by mutableStateOf(0)
        private set

    var seconds by mutableStateOf(0)
        private set

    var repetition by mutableStateOf(0)
        private set

    var setRepetition by mutableStateOf(0)
        private set

    var cycleRepetition by mutableStateOf(0)
        private set

    init {
        val elementId = savedStateHandle.get<String>("elementId")
        val sequenceId = savedStateHandle.get<String>("sequenceId")
        if (elementId != null) {
            viewModelScope.launch {
                repository.getElementById(elementId)?.let { element ->
                    title = element.title
                    description = element.description
                    hours = element.time / 1000 / 3600
                    minutes = (element.time / 1000 - hours * 3600) / 60
                    seconds = element.time / 1000 - hours * 3600 - minutes * 60
                    repetition = element.repetition
                    this@AddEditElementViewModel.element = element
                }
            }
        } else {
            element = Element(parentSequenceId = sequenceId!!)
        }
    }

    fun updateTitle(title: String) {
        this.title = title
    }

    fun updateDescription(description: String) {
        this.description = description
    }

    fun updateHours(hours: Int) {
        this.hours = hours
    }

    fun updateMinutes(minutes: Int) {
        this.minutes = minutes
    }

    fun updateSeconds(seconds: Int) {
        this.seconds = seconds
    }

    fun updateRepetition(repetition: Int) {
        this.repetition = repetition
    }

    fun updateSetRepetition(repetition: Int) {
        this.setRepetition = repetition
    }

    fun updateCycleRepetition(repetition: Int) {
        this.cycleRepetition = repetition
    }

    fun addElement() {
        val temp = Element(
            title = title.trim(),
            description = description.trim(),
            time = (hours * 3600 + minutes * 60 + seconds) * 1000,
            repetition = repetition,
            parentSequenceId = element!!.parentSequenceId
        )
        viewModelScope.launch {
            repository.addElement(temp)
            sequenceRepository.getSequenceById(temp.parentSequenceId)?.let { sequence ->
                sequence.elementAmount += 1
                sequenceRepository.updateSequence(sequence)
            }
        }
    }

    fun updateElement() {
        val temp = Element(
            id = element!!.id,
            title = title.trim(),
            description = description.trim(),
            time = (hours * 3600 + minutes * 60 + seconds) * 1000,
            repetition = repetition,
            parentSequenceId = element!!.parentSequenceId
        )
        viewModelScope.launch {
            repository.updateElement(temp)
        }
    }

    fun addSet() {
        val temp = Element(
            title = "{!set!}${resourcesProvider.getString(R.string.set_title)}",
            description = resourcesProvider.getString(R.string.set_description),
            time = 0,
            repetition = setRepetition,
            parentSequenceId = element!!.parentSequenceId
        )
        viewModelScope.launch {
            repository.addElement(temp)
        }
    }

    fun addCycle() {
        val temp = Element(
            title = "{!cycle!}${resourcesProvider.getString(R.string.cycle_title)}",
            description = resourcesProvider.getString(R.string.cycle_description),
            time = 0,
            repetition = cycleRepetition,
            parentSequenceId = element!!.parentSequenceId
        )
        viewModelScope.launch {
            repository.addElement(temp)
        }
    }

    fun isTitleInvalid(): Boolean {
        return title.isBlank()
    }

    fun isTimeInvalid(): Boolean {
        return hours * 3600 + minutes * 60 + seconds < 1
    }

    fun isFieldsValid(): Boolean {
        return !isTitleInvalid() && !isTimeInvalid()
    }
}
