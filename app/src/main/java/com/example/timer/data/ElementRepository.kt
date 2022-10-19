package com.example.timer.data

import com.example.timer.models.Element
import kotlinx.coroutines.flow.Flow

interface ElementRepository {

    suspend fun addElement(element: Element)

    suspend fun updateElement(element: Element)

    suspend fun deleteElement(element: Element)

    suspend fun getElementById(id: String): Element?

    fun getElements(id: String): Flow<List<Element>>
}
