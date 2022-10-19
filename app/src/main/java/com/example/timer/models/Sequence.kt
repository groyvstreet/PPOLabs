package com.example.timer.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Sequence(
    @PrimaryKey
    @ColumnInfo(name = "sequenceId")
    var id: String = UUID.randomUUID().toString(),

    var title: String = "",
    var color: String = "FFFFFF",
    var elementAmount: Int = 0
)
