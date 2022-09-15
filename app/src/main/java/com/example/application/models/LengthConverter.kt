package com.example.application.models

import java.math.BigDecimal

class LengthConverter : Converter() {
    override val unitsList = listOf(
        TitleAndCode("Kilometer", "KM"),
        TitleAndCode("Mile", "MI"),
        TitleAndCode("Foot", "FT")
    )

    override fun convert(value: BigDecimal, from: String, to: String): BigDecimal {
        return when (to) {
            "KM" -> toKilometers(value, from)
            "MI" -> toMiles(value, from)
            "FT" -> toFeet(value, from)
            else -> value
        }
    }

    private fun toKilometers(value: BigDecimal, code: String): BigDecimal {
        return when (code) {
            "KM" -> value
            "MI" -> value.times(BigDecimal("1.609344"))
            "FT" -> value.times(BigDecimal("0.0003048"))
            else -> value
        }
    }

    private fun toMiles(value: BigDecimal, code: String): BigDecimal {
        return when (code) {
            "KM" -> value.times(BigDecimal("0.621371192"))
            "MI" -> value
            "FT" -> value.times(BigDecimal("0.000189393939"))
            else -> value
        }
    }

    private fun toFeet(value: BigDecimal, code: String): BigDecimal {
        return when (code) {
            "KM" -> value.times(BigDecimal("3280"))
            "MI" -> value.times(BigDecimal("5280"))
            "FT" -> value
            else -> value
        }
    }
}