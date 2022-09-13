package com.example.application.viewModels

import androidx.lifecycle.ViewModel
import com.example.application.models.Converter
import com.example.application.models.DataConverter
import com.example.application.uiStates.ConverterUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ConverterViewModel : ViewModel() {
    var converter: Converter = DataConverter()
        set(value) {
            field = value
            _uiState.value = ConverterUiState(
                converter.unitsList[1].code,
                converter.unitsList[0].code,
                _uiState.value.value1,
                toStringAndFormat(
                    converter.convert(
                        formatAndToDouble(_uiState.value.value1),
                        _uiState.value.unit1,
                        _uiState.value.unit2
                    )
                )
            )
        }

    private val _uiState = MutableStateFlow(ConverterUiState())
    val uiState: StateFlow<ConverterUiState> = _uiState.asStateFlow()

    fun clear() {
        _uiState.value = ConverterUiState(_uiState.value.unit1, _uiState.value.unit2)
    }

    fun swap() {
        _uiState.value = ConverterUiState(
            _uiState.value.unit2,
            _uiState.value.unit1,
            _uiState.value.value2,
            _uiState.value.value1
        )
    }

    fun addSymbolTo(symbol: String, isFirstSelected: Boolean) {
        if (isFirstSelected) {
            val value1 = addSymbol(_uiState.value.value1, symbol)
            _uiState.value = ConverterUiState(
                _uiState.value.unit1,
                _uiState.value.unit2,
                value1,
                toStringAndFormat(
                    converter.convert(
                        formatAndToDouble(value1),
                        _uiState.value.unit1,
                        _uiState.value.unit2
                    )
                )
            )
        } else {
            val value2 = addSymbol(_uiState.value.value2, symbol)
            _uiState.value = ConverterUiState(
                _uiState.value.unit1,
                _uiState.value.unit2,
                toStringAndFormat(
                    converter.convert(
                        formatAndToDouble(value2),
                        _uiState.value.unit2,
                        _uiState.value.unit1
                    )
                ),
                value2
            )
        }
    }

    fun selectUnit(unit: String, isFirstUnitSelected: Boolean, isFirstValueSelected: Boolean) {
        if (isFirstUnitSelected) {
            _uiState.value = ConverterUiState(
                unit,
                _uiState.value.unit2,
                _uiState.value.value1,
                _uiState.value.value2,
            )
        } else {
            _uiState.value = ConverterUiState(
                _uiState.value.unit1,
                unit,
                _uiState.value.value1,
                _uiState.value.value2,
            )
        }
        if (isFirstValueSelected) {
            _uiState.value = ConverterUiState(
                _uiState.value.unit1,
                _uiState.value.unit2,
                _uiState.value.value1,
                toStringAndFormat(
                    converter.convert(
                        formatAndToDouble(_uiState.value.value1),
                        _uiState.value.unit1,
                        _uiState.value.unit2
                    )
                )
            )
        } else {
            _uiState.value = ConverterUiState(
                _uiState.value.unit1,
                _uiState.value.unit2,
                toStringAndFormat(
                    converter.convert(
                        formatAndToDouble(_uiState.value.value2),
                        _uiState.value.unit2,
                        _uiState.value.unit1
                    )
                ),
                _uiState.value.value2
            )
        }
    }

    fun removeSymbolFrom(isFirstSelected: Boolean) {
        if (isFirstSelected) {
            val value1 = removeSymbol(_uiState.value.value1)
            _uiState.value = ConverterUiState(
                _uiState.value.unit1,
                _uiState.value.unit2,
                value1,
                toStringAndFormat(
                    converter.convert(
                        formatAndToDouble(value1),
                        _uiState.value.unit1,
                        _uiState.value.unit2
                    )
                )
            )
        } else {
            val value2 = removeSymbol(_uiState.value.value2)
            _uiState.value = ConverterUiState(
                _uiState.value.unit1,
                _uiState.value.unit2,
                toStringAndFormat(
                    converter.convert(
                        formatAndToDouble(value2),
                        _uiState.value.unit2,
                        _uiState.value.unit1
                    )
                ),
                value2
            )
        }
    }

    private fun insertSpaces(string: String): String {
        var result = string.replace(" ", "")
        val list = result.split(",")
        result = ""
        val length = list[0].length
        for (i in 0 until length) {
            if (i != 0 && i % 3 == 0) {
                result += " ${list[0][length - 1 - i]}"
            } else {
                result += list[0][length - 1 - i]
            }
        }
        result = result.reversed()
        if (list.count() == 2) {
            result += ",${list[1]}"
        }
        return result
    }

    private fun addSymbol(string: String, symbol: String): String {
        var result = string
        if ((result.replace(" ", "").length < 15 && "," !in result) ||
            (result.replace(" ", "").length < 20 && "," in result) ||
            (result.replace(" ", "").length < 16 && symbol == ",")
        ) {
            if (result == "0") {
                if (symbol == ",") {
                    result += symbol
                } else {
                    result = symbol
                }
            } else {
                if (!(symbol == "," && "," in result)) {
                    result += symbol
                }
            }
            result = insertSpaces(result)
        }
        return result
    }

    private fun removeSymbol(string: String): String {
        var result = string
        if (result.length == 1) {
            result = "0"
        } else {
            result = result.dropLast(1)
            result = insertSpaces(result)
        }
        return result
    }

    private fun formatAndToDouble(string: String): Double {
        var result = string.replace(" ", "").replace(",", ".")
        if (!(result.last().isDigit()) && result.last() != '.') {
            result = "0"
        }
        return result.toDouble()
    }

    private fun toStringAndFormat(number: Double): String {
        var result = number.toBigDecimal().toPlainString()
        result = result.replace(".", ",")
        result = insertSpaces(result)
        if (result.endsWith(",0")) {
            result = result.dropLast(2)
        }
        return result
    }
}