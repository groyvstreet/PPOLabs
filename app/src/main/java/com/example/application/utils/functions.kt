package com.example.application.utils

fun insertSpaces(string: String): String {
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

fun addSymbol(string: String, symbol: String): String {
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

fun removeSymbol(string: String): String {
    var result = string
    if (result.length == 1) {
        result = "0"
    } else {
        result = result.dropLast(1)
        result = insertSpaces(result)
    }
    return result
}

fun formatAndToDouble(string: String): Double {
    var result = string.replace(" ", "").replace(",", ".")
    if (!(result.last().isDigit()) && result.last() != '.') {
        result = "0"
    }
    return result.toDouble()
}

fun toStringAndFormat(number: Double): String {
    var result = number.toBigDecimal().toPlainString()
    result = result.replace(".", ",")
    result = insertSpaces(result)
    if (result.endsWith(",0")) {
        result = result.dropLast(2)
    }
    return result
}