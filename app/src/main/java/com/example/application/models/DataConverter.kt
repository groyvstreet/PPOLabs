package com.example.application.models

class DataConverter : Converter() {
    override val unitsList = listOf(
        TitleAndCode("Byte", "B"),
        TitleAndCode("Kilobyte", "KB"),
        TitleAndCode("Megabyte", "MB")
    )
    private fun toBytes(value: Double, code: String): Double {
        return when (code) {
            "B" -> value
            "KB" -> value * 1024
            "MB" -> value * 1024 * 1024
            else -> value
        }
    }

    private fun toKiloBytes(value: Double, code: String): Double {
        return when (code) {
            "B" -> value / 1024
            "KB" -> value
            "MB" -> value * 1024
            else -> value
        }
    }

    private fun toMegaBytes(value: Double, code: String): Double {
        return when (code) {
            "B" -> value / 1024 / 1024
            "KB" -> value / 1024
            "MB" -> value
            else -> value
        }
    }

    override fun convert(value: Double, from: String, to: String): Double {
        return when (to) {
            "B" -> toBytes(value, from)
            "KB" -> toKiloBytes(value, from)
            "MB" -> toMegaBytes(value, from)
            else -> value
        }
    }
}