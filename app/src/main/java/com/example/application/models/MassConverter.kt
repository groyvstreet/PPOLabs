package com.example.application.models

class MassConverter : Converter() {
    override val unitsList = listOf(
        TitleAndCode("Gram", "G"),
        TitleAndCode("Carat", "CT"),
        TitleAndCode("Ounce", "OZ")
    )

    override fun convert(value: Double, from: String, to: String): Double {
        return when (to) {
            "G" -> toGrams(value, from)
            "CT" -> toCarats(value, from)
            "OZ" -> toOunce(value, from)
            else -> value
        }
    }

    private fun toGrams(value: Double, code: String): Double {
        return when (code) {
            "G" -> value
            "CT" -> value / 5
            "OZ" -> value * 28.3495231
            else -> value
        }
    }

    private fun toCarats(value: Double, code: String): Double {
        return when (code) {
            "G" -> value * 5
            "CT" -> value
            "OZ" -> value * 141.747616
            else -> value
        }
    }

    private fun toOunce(value: Double, code: String): Double {
        return when (code) {
            "G" -> value / 28.3495231
            "CT" -> value / 141.747616
            "OZ" -> value
            else -> value
        }
    }
}