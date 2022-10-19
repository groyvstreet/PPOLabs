package com.example.timer.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timer.data.SequenceRepository
import com.example.timer.models.Sequence
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SequenceListViewModel @Inject constructor(
    private val repository: SequenceRepository
) : ViewModel() {

    var isLoading = mutableStateOf(true)

    lateinit var sequences: Flow<List<Sequence>>

    init {
        viewModelScope.launch {
            sequences = repository.getSequences()
            isLoading.value = false
        }
    }

    fun deleteSequence(id: String) {
        viewModelScope.launch {
            val sequence = repository.getSequenceById(id)
            repository.deleteSequence(sequence!!)
        }
    }
}
