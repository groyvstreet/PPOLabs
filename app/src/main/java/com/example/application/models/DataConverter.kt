package com.example.application.models

class DataConverter : Converter() {
    override val unitsList = listOf(
        TitleAndCode("Byte", "B"),
        TitleAndCode("Kilobyte", "KB"),
        TitleAndCode("Megabyte", "MB")
    )

    override fun convert(value: Double, from: String, to: String): Double {
        return when (to) {
            "B" -> toBytes(value, from)
            "KB" -> toKilobytes(value, from)
            "MB" -> toMegabytes(value, from)
            else -> value
        }
    }

    private fun toBytes(value: Double, code: String): Double {
        return when (code) {
            "B" -> value
            "KB" -> value * 1024
            "MB" -> value * 1024 * 1024
            else -> value
        }
    }

    private fun toKilobytes(value: Double, code: String): Double {
        return when (code) {
            "B" -> value / 1024
            "KB" -> value
            "MB" -> value * 1024
            else -> value
        }
    }

    private fun toMegabytes(value: Double, code: String): Double {
        return when (code) {
            "B" -> value / 1024 / 1024
            "KB" -> value / 1024
            "MB" -> value
            else -> value
        }
    }
}