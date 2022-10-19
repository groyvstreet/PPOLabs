package com.example.timer.data

import com.example.timer.models.Sequence
import kotlinx.coroutines.flow.Flow

interface SequenceRepository {

    suspend fun addSequence(sequence: Sequence)

    suspend fun updateSequence(sequence: Sequence)

    suspend fun deleteSequence(sequence: Sequence)

    suspend fun getSequenceById(id: String): Sequence?

    fun getSequences(): Flow<List<Sequence>>
}
