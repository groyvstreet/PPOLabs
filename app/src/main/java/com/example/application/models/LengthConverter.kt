package com.example.application.models

class LengthConverter : Converter() {
    override val unitsList = listOf(
        TitleAndCode("Kilometer", "KM"),
        TitleAndCode("Mile", "MI"),
        TitleAndCode("Foot", "FT")
    )

    override fun convert(value: Double, from: String, to: String): Double {
        return when (to) {
            "KM" -> toKilometers(value, from)
            "MI" -> toMiles(value, from)
            "FT" -> toFeet(value, from)
            else -> value
        }
    }

    private fun toKilometers(value: Double, code: String): Double {
        return when (code) {
            "KM" -> value
            "MI" -> value * 1.609344
            "FT" -> value * 0.0003048
            else -> value
        }
    }

    private fun toMiles(value: Double, code: String): Double {
        return when (code) {
            "KM" -> value / 1.609344
            "MI" -> value
            "FT" -> value / 5280
            else -> value
        }
    }

    private fun toFeet(value: Double, code: String): Double {
        return when (code) {
            "KM" -> value / 0.0003048
            "MI" -> value * 5280
            "FT" -> value
            else -> value
        }
    }
}