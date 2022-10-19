package com.example.timer.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    foreignKeys = [ForeignKey(
        entity = Sequence::class,
        parentColumns = arrayOf("sequenceId"),
        childColumns = arrayOf("parentSequenceId"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.RESTRICT
    )]
)
data class Element(
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),

    var title: String = "",
    var description: String = "",
    var time: Int = 1000,
    var repetition: Int = 0,

    val parentSequenceId: String = ""
)
