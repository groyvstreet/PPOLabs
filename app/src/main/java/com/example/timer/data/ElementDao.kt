package com.example.timer.data

import androidx.room.*
import com.example.timer.models.Element
import kotlinx.coroutines.flow.Flow

@Dao
interface ElementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addElement(element: Element)

    @Update
    suspend fun updateElement(element: Element)

    @Delete
    suspend fun deleteElement(element: Element)

    @Query("SELECT * FROM element WHERE id = :id")
    suspend fun getElementById(id: String): Element?

    @Query("SELECT * FROM element WHERE parentSequenceId = :id")
    fun getElements(id: String): Flow<List<Element>>
}
