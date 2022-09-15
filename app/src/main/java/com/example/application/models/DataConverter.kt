package com.example.application.models

import java.math.BigDecimal

class DataConverter : Converter() {
    override val unitsList = listOf(
        TitleAndCode("Byte", "B"),
        TitleAndCode("Kilobyte", "KB"),
        TitleAndCode("Megabyte", "MB")
    )

    override fun convert(value: BigDecimal, from: String, to: String): BigDecimal {
        return when (to) {
            "B" -> toBytes(value, from)
            "KB" -> toKilobytes(value, from)
            "MB" -> toMegabytes(value, from)
            else -> value
        }
    }

    private fun toBytes(value: BigDecimal, code: String): BigDecimal {
        return when (code) {
            "B" -> value
            "KB" -> value.times(BigDecimal("1024"))
            "MB" -> value.times(BigDecimal("1048576"))
            else -> value
        }
    }

    private fun toKilobytes(value: BigDecimal, code: String): BigDecimal {
        return when (code) {
            "B" -> value.times(BigDecimal("0.0009765625"))
            "KB" -> value
            "MB" -> value.times(BigDecimal("1024"))
            else -> value
        }
    }

    private fun toMegabytes(value: BigDecimal, code: String): BigDecimal {
        return when (code) {
            "B" -> value.times(BigDecimal("0.00000095367431640625"))
            "KB" -> value.times(BigDecimal("0.0009765625"))
            "MB" -> value
            else -> value
        }
    }
}