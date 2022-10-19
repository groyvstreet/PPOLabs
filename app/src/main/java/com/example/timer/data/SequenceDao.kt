package com.example.timer.data

import androidx.room.*
import com.example.timer.models.Sequence
import kotlinx.coroutines.flow.Flow

@Dao
interface SequenceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSequence(sequence: Sequence)

    @Update
    suspend fun updateSequence(sequence: Sequence)

    @Delete
    suspend fun deleteSequence(sequence: Sequence)

    @Query("SELECT * FROM sequence WHERE sequenceId = :id")
    suspend fun getSequenceById(id: String): Sequence?

    @Query("SELECT * FROM sequence")
    fun getSequences(): Flow<List<Sequence>>
}
