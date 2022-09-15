package com.example.application.models

import java.math.BigDecimal

class MassConverter : Converter() {
    override val unitsList = listOf(
        TitleAndCode("Gram", "G"),
        TitleAndCode("Carat", "CT"),
        TitleAndCode("Ounce", "OZ")
    )

    override fun convert(value: BigDecimal, from: String, to: String): BigDecimal {
        return when (to) {
            "G" -> toGrams(value, from)
            "CT" -> toCarats(value, from)
            "OZ" -> toOunce(value, from)
            else -> value
        }
    }

    private fun toGrams(value: BigDecimal, code: String): BigDecimal {
        return when (code) {
            "G" -> value
            "CT" -> value.times(BigDecimal("0.2"))
            "OZ" -> value.times(BigDecimal("28.3495231"))
            else -> value
        }
    }

    private fun toCarats(value: BigDecimal, code: String): BigDecimal {
        return when (code) {
            "G" -> value.times(BigDecimal("5"))
            "CT" -> value
            "OZ" -> value.times(BigDecimal("141.747616"))
            else -> value
        }
    }

    private fun toOunce(value: BigDecimal, code: String): BigDecimal {
        return when (code) {
            "G" -> value.times(BigDecimal("0.0352739619"))
            "CT" -> value.times(BigDecimal("0.00705479239"))
            "OZ" -> value
            else -> value
        }
    }
}