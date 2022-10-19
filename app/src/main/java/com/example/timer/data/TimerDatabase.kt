package com.example.timer.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.timer.models.Element
import com.example.timer.models.Sequence

@Database(
    entities = [Sequence::class, Element::class],
    version = 1,
)
abstract class TimerDatabase : RoomDatabase() {
    abstract val sequenceDao: SequenceDao
    abstract val elementDao: ElementDao
}
