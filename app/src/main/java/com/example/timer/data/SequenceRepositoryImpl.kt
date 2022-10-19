package com.example.timer.data

import com.example.timer.models.Sequence
import kotlinx.coroutines.flow.Flow

class SequenceRepositoryImpl(
    private val dao: SequenceDao
) : SequenceRepository {

    override suspend fun addSequence(sequence: Sequence) {
        dao.addSequence(sequence)
    }

    override suspend fun updateSequence(sequence: Sequence) {
        dao.updateSequence(sequence)
    }

    override suspend fun deleteSequence(sequence: Sequence) {
        dao.deleteSequence(sequence)
    }

    override suspend fun getSequenceById(id: String): Sequence? {
        return dao.getSequenceById(id)
    }

    override fun getSequences(): Flow<List<Sequence>> {
        return dao.getSequences()
    }
}
