package com.example.timer.data

import com.example.timer.models.Element
import kotlinx.coroutines.flow.Flow

class ElementRepositoryImpl(
    private val dao: ElementDao
) : ElementRepository {

    override suspend fun addElement(element: Element) {
        dao.addElement(element)
    }

    override suspend fun updateElement(element: Element) {
        dao.updateElement(element)
    }

    override suspend fun deleteElement(element: Element) {
        dao.deleteElement(element)
    }

    override suspend fun getElementById(id: String): Element? {
        return dao.getElementById(id)
    }

    override fun getElements(id: String): Flow<List<Element>> {
        return dao.getElements(id)
    }
}
