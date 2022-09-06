package com.example.application.models

abstract class Converter {
    abstract val unitsList: List<TitleAndCode>
    abstract fun convert(value: Double, from: String, to: String): Double
}