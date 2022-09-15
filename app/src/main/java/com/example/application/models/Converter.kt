package com.example.application.models

import java.math.BigDecimal

abstract class Converter {
    abstract val unitsList: List<TitleAndCode>
    abstract fun convert(value: BigDecimal, from: String, to: String): BigDecimal
}