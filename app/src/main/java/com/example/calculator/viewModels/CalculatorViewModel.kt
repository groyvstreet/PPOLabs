package com.example.calculator.viewModels

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.os.AsyncTask
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import ch.obermuhlner.math.big.BigDecimalMath
import com.example.calculator.utils.E
import kotlinx.coroutines.coroutineScope
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.concurrent.fixedRateTimer

val operationPriority = mapOf(
    "(" to 0,
    ")" to 0,
    "+" to 1,
    "-" to 1,
    "x" to 2,
    "/" to 2,
    "^" to 4,
    "!" to 4,
    "g" to 5,
    "n" to 5,
    "s" to 5,
    "c" to 5,
    "t" to 5,
    "q" to 5,
    "w" to 5,
    "d" to 5
)

class CalculatorViewModel : ViewModel() {

    var input by mutableStateOf("")

    var output by mutableStateOf("")
        private set

    var second by mutableStateOf(false)

    private var numbers by mutableStateOf(listOf<String>())

    private var number by mutableStateOf("")

    var cursor by mutableStateOf(0)

    companion object {
        var scientificMode by mutableStateOf(false)
    }

    private val mathContext = MathContext(200)

    lateinit var toast: Toast

    lateinit var activityManager: ActivityManager

    lateinit var memoryInfo: ActivityManager.MemoryInfo

    @SuppressLint("StaticFieldLeak")
    lateinit var context: Context

    lateinit var restart: () -> Unit

    private var timer = Timer()

    var isStarted = false

    var isCalculating by mutableStateOf(false)
        private set

    var calculatingTime by mutableStateOf(0)
        private set

    fun addSymbolToInput(symbol: String): Boolean {
        val temp = addSymbol(input, symbol)

        if (temp == "error") {
            if (input.isEmpty()) {
                toast.cancel()
                toast.setText("Ожидается число, открывающая скобка или функция")
                toast.show()
            } else if (cursor == 0) {
                toast.cancel()
                toast.setText("Ожидается число, открывающая скобка или функция")
                toast.show()
            } else if (input[cursor - 1] == ',') {
                toast.cancel()
                toast.setText("Ожидается цифра")
                toast.show()
            } else if (isOperationOrOpenBracket(input[cursor - 1].toString())) {
                toast.cancel()
                toast.setText("Ожидается число, открывающая скобка или функция")
                toast.show()
            } else if (isNumberOrCloseBracket(input[cursor - 1].toString()) || input[cursor - 1] == '!') {
                toast.cancel()
                toast.setText("Ожидается операция, знак факториала или возведение в степень")
                toast.show()
            }
            return false
        }

        input = temp

        if (cursor > input.length) {
            cursor = input.length
        }

        //defineNumbers()

        while (true) {
            defineNumbers()

            if (number.isNotEmpty() && number.first() == '0' && (!number.contains(',') || (number.split(
                    ','
                )[0] != "0")) && number != "0"
            ) {
                if (cursor >= 1 && input[cursor - 1] == '0') {
                    input = StringBuilder(input).removeRange(cursor - 1, cursor).toString()
                    cursor -= 1
                } else {
                    input = StringBuilder(input).removeRange(cursor, cursor + 1).toString()
                }
            } else {
                break
            }
        }

        val preferences = context.getSharedPreferences("input", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("input", input)
        editor.apply()

        return true
    }

    private fun addSymbol(string: String, symbol: String): String {
        var result = string

        if (symbol == "+" || symbol == "x" || symbol == "/") {
            if (cursor == 0) {
                return "error"
            }
            if (result.isNotEmpty()) {
                if (isNumberOrCloseBracket(result[cursor - 1].toString()) || result[cursor - 1] == '!') {
                    result = StringBuilder(result).insert(cursor, symbol).toString()
                    cursor += 1

                    if (result.length > cursor && result[cursor] == ',') {
                        result = StringBuilder(result).insert(cursor, "0").toString()
                    }
                } else if (result[cursor - 1] == ',') {
                    result = StringBuilder(result).insert(cursor, "0$symbol").toString()
                    cursor += 2
                } else {
                    return "error"
                }
            } else {
                return "error"
            }
        } else if (symbol == "-") {
            if (cursor == 0) {
                result = StringBuilder(result).insert(cursor, "($symbol").toString()
                cursor += 2
            } else {
                if (result.isNotEmpty()) {
                    if (isNumberOrCloseBracket(result[cursor - 1].toString()) || result[cursor - 1] == '!') {
                        result = StringBuilder(result).insert(cursor, symbol).toString()
                        cursor += 1

                        if (result.length > cursor && result[cursor] == ',') {
                            result = StringBuilder(result).insert(cursor, "0").toString()
                        }
                    } else if (isOperationOrOpenBracket(result[cursor - 1].toString())) {
                        if (result[cursor - 1] == '(') {
                            result = StringBuilder(result).insert(cursor, symbol).toString()
                            cursor += 1
                        } else {
                            result = StringBuilder(result).insert(cursor, "($symbol").toString()
                            cursor += 2
                        }
                    } else if (result[cursor - 1] == ',') {
                        result = StringBuilder(result).insert(cursor, "0$symbol").toString()
                        cursor += 2
                    } else {
                        return "error"
                    }
                } else {
                    result += "($symbol"
                    cursor += 2
                }
            }
        } else if (symbol == ",") {
            if (cursor == 0) {
                if (number.isNotEmpty()) {
                    if (!number.contains(",")) {
                        result = StringBuilder(result).insert(cursor, "0$symbol").toString()
                        cursor += 2
                    } else {
                        return "error"
                    }
                } else {
                    result = StringBuilder(result).insert(cursor, "0$symbol").toString()
                    cursor += 2
                }
            } else {
                if (number.isNotEmpty()) {
                    if (!number.contains(",") && isDigit(input[cursor - 1].toString())) {
                        result = StringBuilder(result).insert(cursor, symbol).toString()
                        cursor += 1
                    } else {
                        return "error"
                    }
                } else if (isOperationOrOpenBracket(result[cursor - 1].toString())) {
                    result = StringBuilder(result).insert(cursor, "0$symbol").toString()
                    cursor += 2
                } else {
                    return "error"
                }
            }
        } else if (symbol == "(" || symbol == "pi" || symbol == "e" || symbol == "lg(" ||
            symbol == "ln(" || symbol == "sin(" || symbol == "cos(" || symbol == "tg(" ||
            symbol == "arcsin(" || symbol == "arccos(" || symbol == "arctg("
        ) {
            if (cursor == 0) {
                result = StringBuilder(result).insert(cursor, symbol).toString()
                cursor += symbol.length
            } else {
                if (result.isEmpty() || isOperationOrOpenBracket(result[cursor - 1].toString())) {
                    result = StringBuilder(result).insert(cursor, symbol).toString()
                    cursor += symbol.length
                } else if (symbol != "(") {
                    if (number == "0" && result[cursor - 1] == '0') {
                        if (symbol == "pi") {
                            result =
                                StringBuilder(result).replace(cursor - 1, cursor, symbol).toString()
                            cursor += symbol.length - 1
                        } else if (symbol == "e") {
                            result =
                                StringBuilder(result).replace(cursor - 1, cursor, symbol).toString()
                        } else if (symbol == "lg(" || symbol == "ln(") {
                            result =
                                StringBuilder(result).replace(cursor - 1, cursor, "${symbol}0)")
                                    .toString()
                            cursor += symbol.length
                        } else if (symbol == "sin(") {
                            result =
                                StringBuilder(result).replace(cursor - 1, cursor, "${symbol}0)")
                                    .toString()
                            cursor += symbol.length
                        } else if (symbol == "cos(") {
                            result =
                                StringBuilder(result).replace(cursor - 1, cursor, "${symbol}0)")
                                    .toString()
                            cursor += symbol.length
                        } else if (symbol == "tg(") {
                            result =
                                StringBuilder(result).replace(cursor - 1, cursor, "${symbol}0)")
                                    .toString()
                            cursor += symbol.length
                        } else if (symbol == "arcsin(") {
                            result =
                                StringBuilder(result).replace(cursor - 1, cursor, "${symbol}0)")
                                    .toString()
                            cursor += symbol.length
                        } else if (symbol == "arccos(") {
                            result =
                                StringBuilder(result).replace(cursor - 1, cursor, "${symbol}0)")
                                    .toString()
                            cursor += symbol.length
                        } else if (symbol == "arctg(") {
                            result =
                                StringBuilder(result).replace(cursor - 1, cursor, "${symbol}0)")
                                    .toString()
                            cursor += symbol.length
                        }
                    }
                } else {
                    return "error"
                }
            }
        } else if (symbol == ")") {
            if (cursor == 0) {
                return "error"
            } else {
                if (result.count { it == '(' } > result.count { it == ')' } && result.isNotEmpty()
                    && (isNumberOrCloseBracket(result[cursor - 1].toString()) || result[cursor - 1] == '!')) {
                    result = StringBuilder(result).insert(cursor, symbol).toString()
                    cursor += 1

                    if (result.length > cursor && result[cursor] == ',') {
                        result = StringBuilder(result).insert(cursor, "0").toString()
                    }
                } else {
                    return "error"
                }
            }
        } else if (symbol == "!") {
            if (cursor == 0) {
                return "error"
            } else {
                if (result.isNotEmpty() && (isNumberOrCloseBracket(result[cursor - 1].toString()) ||
                            result[cursor - 1] == '!')
                ) {
                    result = StringBuilder(result).insert(cursor, symbol).toString()
                    cursor += 1

                    if (result.length > cursor && result[cursor] == ',') {
                        result = StringBuilder(result).insert(cursor, "0").toString()
                    }
                } else {
                    return "error"
                }
            }
        } else if (symbol == "^(") {
            if (cursor == 0) {
                return "error"
            } else {
                if (result.isNotEmpty() &&
                    (isNumberOrCloseBracket(result[cursor - 1].toString()) || result[cursor - 1] == '!')
                ) {
                    result = StringBuilder(result).insert(cursor, symbol).toString()
                    cursor += 2

                    if (result.length > cursor && result[cursor] == ',') {
                        result = StringBuilder(result).insert(cursor, "0").toString()
                    }
                } else {
                    return "error"
                }
            }
        } else {
            if (cursor == 0) {
                if (symbol != "0" || (result.length >= 2 && result[cursor] == ',')) {
                    result = StringBuilder(result).insert(cursor, symbol).toString()
                    cursor += 1
                } else {
                    if (result.isEmpty()) {
                        result = StringBuilder(result).insert(cursor, symbol).toString()
                        cursor += 1
                    } else {
                        return "error"
                    }
                }
            } else {
                if (result.isEmpty()) {
                    if (isDigit(result[cursor - 1].toString()) ||
                        isOperationOrOpenBracket(result[cursor - 1].toString()) || result[cursor - 1] == ','
                    ) {
                        if (number == "0") {
                            result =
                                StringBuilder(result).replace(cursor - 1, cursor, symbol).toString()
                        } else {
                            result = StringBuilder(result).insert(cursor, symbol).toString()
                            cursor += 1
                        }
                    }
                } else {
                    if (number == "0") {
                        if (result[cursor - 1] == '0') {
                            result =
                                StringBuilder(result).replace(cursor - 1, cursor, symbol).toString()
                        } else {
                            if (symbol != "0") {
                                result = StringBuilder(result).insert(cursor, symbol).toString()
                                cursor += 1
                            }
                        }
                    } else if (number.split(',')[0] == "0") {
                        if (result[cursor - 1] == '0' && result.length > cursor && result[cursor] == ',') {
                            result =
                                StringBuilder(result).replace(cursor - 1, cursor, symbol).toString()
                        } else {
                            result = StringBuilder(result).insert(cursor, symbol).toString()
                            cursor += 1
                        }
                    } else {
                        if (cursor > result.indexOfFirst { it == ',' } && number.isNotEmpty()) {
                            result = StringBuilder(result).insert(cursor, symbol).toString()
                            cursor += 1
                        } else /*if (number.split(',')[0] != "0")*/ {
                            if (isDigit(result[cursor - 1].toString()) ||
                                isOperationOrOpenBracket(result[cursor - 1].toString()) || result[cursor - 1] == ','
                            ) {
                                if (number == "0") {
                                    result =
                                        StringBuilder(result).replace(cursor - 1, cursor, symbol)
                                            .toString()
                                } else {
                                    result = StringBuilder(result).insert(cursor, symbol).toString()
                                    cursor += 1
                                }
                            }
                        }
                    }
                }
            }
        }

        return result
    }

    fun clear() {
        numbers = listOf()
        number = ""
        input = ""
        output = ""
        cursor = 0

        val preferences = context.getSharedPreferences("input", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("input", input)
        editor.apply()
    }

    fun removeSymbol() {
        if (cursor == 0) {
            return
        }

        /*if (input.isNotEmpty() && (isDigit(input[cursor - 1].toString()) || input[cursor - 1] == ',')) {
            var temp = getInputWithCursor()
            temp = temp.replace("arcsin", " ")
            temp = temp.replace("arccos", " ")
            temp = temp.replace("arctg", " ")
            temp = temp.replace("sin", " ")
            temp = temp.replace("cos", " ")
            temp = temp.replace("tg", " ")
            temp = temp.replace("lg", " ")
            temp = temp.replace("ln", " ")
            temp = temp.replace("pi", " ")
            temp = temp.replace("e", " ")
            temp = temp.replace("(", " ")
            temp = temp.replace(")", " ")
            temp = temp.replace("^", " ")
            temp = temp.replace("!", " ")
            temp = temp.replace("+", " ")
            temp = temp.replace("-", " ")
            temp = temp.replace("x", " ")
            temp = temp.replace("/", " ")
            temp = temp.trim()
            val list = temp.split("\\s+".toRegex())
            val index = list.indexOfFirst { it.contains('<') }
            number = list[index]
            val cursorIndex = number.indexOf('<')
            number = StringBuilder(number).removeRange(cursorIndex - 1, cursorIndex + 1).toString()
            if (index != list.size - 1) {
                if (number.isNotEmpty()) {
                    numbers = numbers.subList(0, index) + number + numbers.subList(
                        index + 1,
                        numbers.size
                    )
                } else {
                    numbers = numbers.subList(0, index) + numbers.subList(index + 1, numbers.size)
                }
            }
        }*/

        if (input.length >= 7 && cursor >= 7 && (input.subSequence(
                cursor - 7,
                cursor
            ) == "arcsin(" ||
                    input.subSequence(cursor - 7, cursor) == "arccos(")
        ) {
            input = StringBuilder(input).removeRange(cursor - 7, cursor).toString()
            cursor -= 7
            removeCloseBracket()
        } else if (input.length >= 6 && cursor >= 6 && input.subSequence(
                cursor - 6,
                cursor
            ) == "arctg("
        ) {
            input = StringBuilder(input).removeRange(cursor - 6, cursor).toString()
            cursor -= 6
            removeCloseBracket()
        } else if (input.length >= 4 && cursor >= 4 && (input.subSequence(
                cursor - 4,
                cursor
            ) == "sin(" ||
                    input.subSequence(cursor - 4, cursor) == "cos(")
        ) {
            input = StringBuilder(input).removeRange(cursor - 4, cursor).toString()
            cursor -= 4
            removeCloseBracket()
        } else if (input.length >= 3 && cursor >= 3 && (input.subSequence(
                cursor - 3,
                cursor
            ) == "tg(" ||
                    input.subSequence(cursor - 3, cursor) == "lg(" ||
                    input.subSequence(cursor - 3, cursor) == "ln(")
        ) {
            input = StringBuilder(input).removeRange(cursor - 3, cursor).toString()
            cursor -= 3
            removeCloseBracket()
        } else if (input.length >= 2 && cursor >= 2 && (input.subSequence(
                cursor - 2,
                cursor
            ) == "^(" ||
                    input.subSequence(cursor - 2, cursor) == "pi")
        ) {
            if (input[cursor - 1] == '(') {
                removeCloseBracket()
            }
            if (input.length >= 4) {
                if (cursor == input.length) {
                    input = StringBuilder(input).removeRange(cursor - 2, cursor).toString()
                    cursor -= 2
                } else if (cursor >= 3) {
                    if ((input[cursor - 3] == '(' || input[cursor - 3] == '+' || input[cursor - 3] == '-' || input[cursor - 3] == 'x' || input[cursor - 3] == '/') &&
                        (input[cursor] == ')' || input[cursor] == '+' || input[cursor] == '-' || input[cursor] == 'x' || input[cursor] == '/' || input[cursor] == '^' || input[cursor] == '!')
                    ) {
                        input = StringBuilder(input).replace(cursor - 2, cursor, "0").toString()
                        cursor -= 1
                    } else {
                        input = StringBuilder(input).removeRange(cursor - 2, cursor).toString()
                        cursor -= 2
                    }
                } else if (cursor == 2) {
                    if (input[cursor] == '+' || input[cursor] == '-' || input[cursor] == 'x' || input[cursor] == '/' || input[cursor] == '^' || input[cursor] == '!') {
                        input = StringBuilder(input).replace(cursor - 2, cursor, "0").toString()
                        cursor -= 1
                    } else {
                        input = StringBuilder(input).removeRange(cursor - 2, cursor).toString()
                        cursor -= 2
                    }
                }
            } else if (input.length == 3) {
                if (cursor == input.length) {
                    input = StringBuilder(input).removeRange(cursor - 2, cursor).toString()
                    cursor -= 2
                } else {
                    if (input[cursor] == '+' || input[cursor] == '-' || input[cursor] == 'x' || input[cursor] == '/' || input[cursor] == '^' || input[cursor] == '!') {
                        input = StringBuilder(input).replace(cursor - 2, cursor, "0").toString()
                        cursor -= 1
                    } else {
                        input = StringBuilder(input).removeRange(cursor - 2, cursor).toString()
                        cursor -= 2
                    }
                }
            } else {
                input = StringBuilder(input).removeRange(cursor - 2, cursor).toString()
                cursor -= 2
            }
        } else if (input.isNotEmpty() && cursor >= 1) {
            if (input[cursor - 1] == '(') {
                removeCloseBracket()
            }

            if (input[cursor - 1] == ',') {
                input = StringBuilder(input).removeRange(cursor - 1, cursor).toString()
                cursor -= 1
            } else {
                if (input.length >= 3) {
                    if (cursor == input.length) {
                        input = StringBuilder(input).removeRange(cursor - 1, cursor).toString()
                        cursor -= 1
                    } else if (cursor >= 2) {
                        if ((input[cursor - 2] == '(' || input[cursor - 2] == '+' || input[cursor - 2] == '-' || input[cursor - 2] == 'x' || input[cursor - 2] == '/') &&
                            (input[cursor] == ')' || input[cursor] == '+' || input[cursor] == '-' || input[cursor] == 'x' || input[cursor] == '/' || input[cursor] == '^' || input[cursor] == '!' || input[cursor] == ',')
                        ) {
                            if (input[cursor - 1] == '0') {
                                toast.cancel()
                                toast.setText("Введите число, функцию, операцию или знак факториала")
                                toast.show()
                            } else {
                                input =
                                    StringBuilder(input).replace(cursor - 1, cursor, "0").toString()
                            }
                        } else {
                            input = StringBuilder(input).removeRange(cursor - 1, cursor).toString()
                            cursor -= 1
                        }
                    } else if (cursor == 1) {
                        if (input[cursor] == '+' || input[cursor] == '-' || input[cursor] == 'x' || input[cursor] == '/' || input[cursor] == '^' || input[cursor] == '!' || input[cursor] == ',') {
                            if (input[cursor - 1] == '0') {
                                toast.cancel()
                                toast.setText("Введите число, функцию, операцию или знак факториала")
                                toast.show()
                            } else {
                                input =
                                    StringBuilder(input).replace(cursor - 1, cursor, "0").toString()
                            }
                        } else {
                            input = StringBuilder(input).removeRange(cursor - 1, cursor).toString()
                            cursor -= 1
                        }
                    }
                } else if (input.length == 2) {
                    if (cursor == input.length) {
                        input = StringBuilder(input).removeRange(cursor - 1, cursor).toString()
                        cursor -= 1
                    } else {
                        if (input[cursor] == '+' || input[cursor] == '-' || input[cursor] == 'x' || input[cursor] == '/' || input[cursor] == '^' || input[cursor] == '!' || input[cursor] == ',') {
                            if (input[cursor - 1] == '0') {
                                toast.cancel()
                                toast.setText("Введите число, функцию, операцию или знак факториала")
                                toast.show()
                            } else {
                                input =
                                    StringBuilder(input).replace(cursor - 1, cursor, "0").toString()
                            }
                        } else {
                            input = StringBuilder(input).removeRange(cursor - 1, cursor).toString()
                            cursor -= 1
                        }
                    }
                } else {
                    input = StringBuilder(input).removeRange(cursor - 1, cursor).toString()
                    cursor -= 1
                }
            }
        }

        while (true) {
            defineNumbers()

            if (number.isNotEmpty() && number.first() == '0' && (!number.contains(',') || (number.split(
                    ','
                )[0] != "0")) && number != "0"
            ) {
                if (cursor >= 1 && input[cursor - 1] == '0') {
                    input = StringBuilder(input).removeRange(cursor - 1, cursor).toString()
                    cursor -= 1
                } else {
                    input = StringBuilder(input).removeRange(cursor, cursor + 1).toString()
                }
            } else {
                break
            }
        }

        if (cursor < 0) {
            cursor = 0
        }

        val preferences = context.getSharedPreferences("input", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("input", input)
        editor.apply()
    }

    private fun isDigit(symbol: String): Boolean {
        return symbol == "0" || symbol == "1" || symbol == "2" || symbol == "3" || symbol == "4" ||
                symbol == "5" || symbol == "6" || symbol == "7" || symbol == "8" || symbol == "9"
    }

    private fun isOperationOrOpenBracket(symbol: String): Boolean {
        return symbol == "+" || symbol == "-" || symbol == "x" || symbol == "/" || symbol == "("
    }

    private fun isNumberOrCloseBracket(symbol: String): Boolean {
        return isDigit(symbol) || symbol == "i" || symbol == "e" || symbol == ")"
    }

    suspend fun calculateAsync() = coroutineScope {

        AsyncTask.execute {
            calculate()
        }

        var isRestarted = false
        calculatingTime = 0
        timer.cancel()

        timer = fixedRateTimer(initialDelay = 100L, period = 100L) {
            activityManager.getMemoryInfo(memoryInfo)
            val availMem = memoryInfo.availMem.toDouble()
            val totalMem = memoryInfo.totalMem.toDouble()

            if (availMem / totalMem <= 0.2 && !isRestarted) {
                isRestarted = true
                calculatingTime = 0
                toast.cancel()
                toast.setText("Нехватка памяти! Ожидайте перезагрузку приложения")
                toast.show()
                restart()
            }

            if (calculatingTime >= 30000 && isCalculating) {
                isRestarted = true
                calculatingTime = 0
                toast.cancel()
                toast.setText("Время ожидания истекло! Ожидайте перезагрузку приложения")
                toast.show()
                restart()
            }

            calculatingTime += 100
        }
    }

    private fun calculate() {
        isCalculating = true

        val rpn = toRPN().replace(",", ".").dropLast(1)

        if (rpn == "error") {
            isCalculating = false
            return
        }

        val stack = ArrayDeque<BigDecimal>()

        if (rpn.isNotEmpty()) {
            val expression = rpn.split(' ')
            for (i in expression) {
                if (isDigit(i[0].toString()) || i[0] == '.') {
                    stack.addLast(BigDecimal(i))
                } else if (i[0] == '+' || i[0] == '-' || i[0] == 'x' || i[0] == '/' || i[0] == '^') {
                    if (stack.size >= 2) {
                        val num2 = stack.removeLast()
                        val num1 = stack.removeLast()
                        val resNum = when (i) {
                            "+" -> num1 + num2
                            "-" -> num1 - num2
                            "x" -> num1.times(num2)
                            "/" -> if (num2 == BigDecimal.ZERO) {
                                output = "Ошибка: деление на 0"
                                isCalculating = false
                                return
                            } else {
                                num1.divide(num2, mathContext)
                            }
                            "^" -> {
                                try {
                                    BigDecimalMath.pow(num1, num2, mathContext)
                                } catch (e: ArithmeticException) {
                                    output = "Некорректный аргумент: x >= 0"
                                    isCalculating = false
                                    return
                                }
                            }
                            else -> BigDecimal(0)
                        }
                        stack.addLast(resNum)
                    }
                } else {
                    if (stack.isNotEmpty()) {
                        val num = stack.removeLast()
                        val resNum = when (i) {
                            "!" -> {
                                try {
                                    BigDecimalMath.factorial(num, mathContext)
                                } catch (e: ArithmeticException) {
                                    if (e.message == "Rounding necessary") {
                                        output = "Слишком большое число"
                                    } else {
                                        output = "Некорректный аргумент: x >= 0"
                                    }
                                    isCalculating = false
                                    return
                                }
                            }
                            "g" -> {
                                try {
                                    BigDecimalMath.log10(num, mathContext)
                                } catch (e: ArithmeticException) {
                                    output = "Некорректный аргумент: x >= 1 или 0 < x < 1"
                                    isCalculating = false
                                    return
                                }
                            }
                            "n" -> {
                                if (num >= BigDecimal.ONE || (BigDecimal.ZERO < num && num < BigDecimal.ONE)) {
                                    BigDecimalMath.log(num, mathContext)
                                } else {
                                    output = "Некорректный аргумент: x >= 1 или 0 < x < 1"
                                    isCalculating = false
                                    return
                                }
                            }
                            "s" -> BigDecimalMath.sin(num, mathContext)
                            "c" -> {
                                var number = num.times(BigDecimal("2"))
                                    .divide(BigDecimalMath.pi(mathContext), mathContext)
                                    .toPlainString()

                                if (number.contains('.') && number.last() == '0') {
                                    number = number.dropLastWhile { it == '0' }
                                    number = number.dropLast(1)
                                }

                                val digits = number.length

                                if (!number.contains('.')) {
                                    BigDecimalMath.cos(num, mathContext)
                                        .setScale(199 - (digits - 1), RoundingMode.HALF_EVEN)
                                } else {
                                    BigDecimalMath.cos(num, mathContext)
                                }
                            }
                            "t" -> {
                                var number = num.times(BigDecimal("2"))
                                    .divide(BigDecimalMath.pi(mathContext), mathContext)
                                    .toPlainString()

                                if (number.contains('.') && number.last() == '0') {
                                    number = number.dropLastWhile { it == '0' }
                                    number = number.dropLast(1)
                                }

                                val digits = number.length

                                var cos = if (!number.contains('.')) {
                                    BigDecimalMath.cos(num, mathContext)
                                        .setScale(199 - (digits - 1), RoundingMode.HALF_EVEN)
                                } else {
                                    BigDecimalMath.cos(num, mathContext)
                                }.toPlainString()

                                if (cos.contains('.') && cos.last() == '0') {
                                    cos = cos.dropLastWhile { it == '0' }
                                    cos = cos.dropLast(1)
                                }

                                if (cos.toBigDecimal() == BigDecimal.ZERO) {
                                    output = "Ошибка: деление на 0. tg = sin/cos, cos($num) = $cos"
                                    isCalculating = false
                                    return
                                } else {
                                    BigDecimalMath.tan(num, mathContext)
                                }
                            }
                            "q" -> {
                                try {
                                    BigDecimalMath.asin(num, mathContext)
                                } catch (e: ArithmeticException) {
                                    output = "Некорректный аргумент: -1 <= x <= 1"
                                    isCalculating = false
                                    return
                                }
                            }
                            "w" -> {
                                try {
                                    BigDecimalMath.acos(num, mathContext)
                                } catch (e: ArithmeticException) {
                                    output = "Некорректный аргумент: -1 <= x <= 1"
                                    isCalculating = false
                                    return
                                }
                            }
                            "d" -> BigDecimalMath.atan(num, mathContext)
                            else -> BigDecimal(0)
                        }
                        stack.addLast(resNum)
                    }
                }
            }
        }

        if (stack.isNotEmpty()) {
            if (isRational()) {
                output = stack.last().toPlainString().replace('.', ',')

                if (output.contains(',') && output.last() == '0') {
                    output = output.dropLastWhile { it == '0' }

                    if (output.last() == ',') {
                        output = output.dropLast(1)
                    }
                }

                val temp1 = output.split(",")
                if (temp1.size > 1) {
                    if (temp1[1].length >= 199) {
                        val temp2 = temp1[1]
                        var index1 = 0
                        var index2 = 1
                        var period = ""
                        while (true) {
                            if (index1 < index2) {
                                val test = temp2.substring(index1, index2)
                                val temp3 = temp2.replace(test, "*")
                                if (temp3.contains("**") && test.length > temp3.replace(
                                        "*",
                                        ""
                                    ).length
                                ) {
                                    period = test
                                    break
                                }
                            }

                            index2 += 1
                            if (index2 >= temp2.length) {
                                index1 += 1
                                index2 = 1
                            }

                            if (index1 == temp2.length) {
                                index1 = -1
                                break
                            }
                        }
                        if (index1 != -1) {
                            val str = output.replace(period, " ").dropLastWhile { it != ' ' }.trim()
                            output = "${str}(${period})"
                        }
                    }
                }
            } else {
                output = stack.last().setScale(199, RoundingMode.HALF_EVEN).toPlainString()
                    .replace('.', ',')

                /*val temp = stack.last().toPlainString().split('.')
                if (temp.size == 1) {
                    output = temp[0]
                } else {
                    var counter = 0
                    for (i in temp[1].indices) {
                        if (temp[1][i] == '0') {
                            counter += 1
                        } else {
                            break
                        }
                    }
                    output = if (counter >= 199) {
                        if (temp[0] == "-0") {
                            "0"
                        } else {
                            temp[0]
                        }
                    } else {
                        "${temp[0]},${temp[1]}"
                    }
                }*/

                if (output.contains(',') && output.last() == '0') {
                    output = output.dropLastWhile { it == '0' }

                    if (output.last() == ',') {
                        output = output.dropLast(1)
                    }
                }
            }
        } else {
            output = "Ошибка: ожидается число"
        }

        isCalculating = false
    }

    private fun toRPN(): String {
        var tempInput = input

        if (tempInput.isNotEmpty() && isOperationOrOpenBracket(tempInput.last().toString())) {
            tempInput = tempInput.dropLastWhile { isOperationOrOpenBracket(it.toString()) }
        }

        for (i in 0..tempInput.length - 2) {
            if (isDigit(tempInput[i].toString()) && !isDigit(tempInput[i + 1].toString()) &&
                tempInput[i + 1] != '+' && tempInput[i + 1] != '-' && tempInput[i + 1] != 'x' &&
                tempInput[i + 1] != '/' && tempInput[i + 1] != ',' && tempInput[i + 1] != '!' &&
                tempInput[i + 1] != '^' && tempInput[i + 1] != ')'
            ) {
                output =
                    "Ошибка: после цифры ожидается запятая, операция, возведение в степень, знак факториала или закрывающая скобка"
                return "error "
            }

            if (tempInput[i] == '(' && !isDigit(tempInput[i + 1].toString()) &&
                tempInput[i + 1] != 'p' && tempInput[i + 1] != 'e' &&
                tempInput[i + 1] != '(' && tempInput[i + 1] != 'a' && tempInput[i + 1] != 'l' &&
                tempInput[i + 1] != 't' && tempInput[i + 1] != 's' && tempInput[i + 1] != 'c' &&
                tempInput[i + 1] != '-'
            ) {
                output =
                    "Ошибка: после открывающей скобки ожидается число, функция или открывающая скобка"
                return "error "
            }

            if ((tempInput[i] == ')' || tempInput[i] == 'i' || tempInput[i] == 'e') &&
                tempInput[i + 1] != '!' && tempInput[i + 1] != '^' && tempInput[i + 1] != ')' &&
                tempInput[i + 1] != '+' && tempInput[i + 1] != '-' && tempInput[i + 1] != 'x' &&
                tempInput[i + 1] != '/' && i >= 1 && tempInput[i - 1] != 's' && tempInput[i] == 'i'
                && tempInput[i + 1] != 'n'
            ) {
                output =
                    "Ошибка: после закрывающей скобки/числа ожидается операция, возведение в степень, знак факториала или закрывающая скобка"
                return "error "
            }

            if ((tempInput[i] == ')' || tempInput[i] == 'i' || tempInput[i] == 'e') && isDigit(
                    tempInput[i + 1].toString()
                )
            ) {
                output =
                    "Ошибка: после закрывающей скобки/числа ожидается операция, возведение в степень, знак факториала или закрывающая скобка"
                return "error "
            }

            if ((tempInput[i] == '+' || tempInput[i] == '-' || tempInput[i] == 'x' ||
                        tempInput[i] == '/') && !isDigit(tempInput[i + 1].toString()) &&
                tempInput[i + 1] != '(' && tempInput[i + 1] != 'a' && tempInput[i + 1] != 'l' &&
                tempInput[i + 1] != 't' && tempInput[i + 1] != 's' && tempInput[i + 1] != 'c' &&
                tempInput[i + 1] != 'p' && tempInput[i + 1] != 'e'
            ) {
                output = "Ошибка: после операции ожидается число, функция или открывающая скобка"
                return "error "
            }

            /*if (tempInput[i] == ',' && !isDigit(tempInput[i + 1].toString())) {
                output = "Ошибка: после запятой ожидается цифра"
                return "error "
            }*/

            if (tempInput.count { it == '(' } < tempInput.count { it == ')' }) {
                output = "Ошибка: закрывающих скобок больше чем открывающих"
                return "error "
            }
        }

        tempInput = tempInput.replace("arcsin", "q")
        tempInput = tempInput.replace("arccos", "w")
        tempInput = tempInput.replace("arctg", "d")
        tempInput = tempInput.replace("sin", "s")
        tempInput = tempInput.replace("cos", "c")
        tempInput = tempInput.replace("tg", "t")
        tempInput = tempInput.replace("lg", "g")
        tempInput = tempInput.replace("ln", "n")
        tempInput = tempInput.replace("pi", "i")

        if (isOperationOrOpenBracket(tempInput.last().toString())) {
            output = "Ошибка: ожидается число"
            return "error "
        }

        if (tempInput.count { it == '(' } > tempInput.count { it == ')' } &&
            tempInput.isNotEmpty()) {
            repeat(tempInput.count { it == '(' } - tempInput.count { it == ')' }) {
                tempInput = tempInput.plus(')')
            }
        }

        repeat(input.length) {
            for (i in tempInput.indices) {
                if (i != tempInput.length - 1) {
                    if ((tempInput[i] != tempInput[i + 1] &&
                                (!isDigit(tempInput[i].toString()) ||
                                        !isDigit(tempInput[i + 1].toString())) &&
                                tempInput[i] != ' ' && tempInput[i + 1] != ' ' &&
                                tempInput[i] != ',' && tempInput[i + 1] != ',') ||
                        (tempInput[i] == tempInput[i + 1] && (
                                (tempInput[i] == '(' || tempInput[i] == ')') ||
                                        tempInput[i] == '!')) ||
                        (tempInput[i] == ',' && (tempInput[i + 1] == '+' ||
                                tempInput[i + 1] == '-' || tempInput[i + 1] == 'x' ||
                                tempInput[i + 1] == '/' || tempInput[i + 1] == ')'))
                    ) {
                        tempInput = if (tempInput[i] == '(' && tempInput[i + 1] == '-') {
                            StringBuilder(tempInput).insert(i + 1, " 0 ").toString()
                        } else {
                            StringBuilder(tempInput).insert(i + 1, ' ').toString()
                        }
                    }
                }
            }
        }

        val stack = ArrayDeque<String>()
        var tempOutput = ""

        if (tempInput.isNotEmpty()) {
            val expression = tempInput.split(' ')

            for (i in expression) {
                if (isDigit(i[0].toString())) {
                    tempOutput += "$i "
                } else if (i == "i") {
                    tempOutput += "${BigDecimalMath.pi(mathContext)} "
                } else if (i == "e") {
                    tempOutput += "$E "
                } else {
                    when (i) {
                        "(" -> stack.addLast(i)
                        ")" -> {
                            while (stack.last() != "(") {
                                tempOutput += "${stack.last()} "
                                stack.removeLast()
                            }
                            stack.removeLast()
                            if (stack.isNotEmpty()) {
                                if (stack.last() == "g" || stack.last() == "n" ||
                                    stack.last() == "s" || stack.last() == "c" || stack.last() == "t" ||
                                    stack.last() == "q" || stack.last() == "w" || stack.last() == "d"
                                ) {
                                    tempOutput += "${stack.last()} "
                                    stack.removeLast()
                                }
                            }
                        }
                        else -> {
                            if (stack.isNotEmpty()) {
                                while (stack.isNotEmpty() &&
                                    operationPriority[stack.last()]!! >= operationPriority[i]!!
                                ) {
                                    tempOutput += "${stack.last()} "
                                    stack.removeLast()
                                }
                            }
                            stack.addLast(i)
                        }
                    }
                }
            }
        }

        while (stack.isNotEmpty()) {
            tempOutput += "${stack.last()} "
            stack.removeLast()
        }
        return tempOutput
    }

    fun cursorToLeft() {
        if (input.length >= 7 && cursor >= 7 && (input.subSequence(
                cursor - 7,
                cursor
            ) == "arcsin(" ||
                    input.subSequence(cursor - 7, cursor) == "arccos(")
        ) {
            cursor -= 7
        } else if (input.length >= 6 && cursor >= 6 && input.subSequence(
                cursor - 6,
                cursor
            ) == "arctg("
        ) {
            cursor -= 6
        } else if (input.length >= 4 && cursor >= 4 && (input.subSequence(
                cursor - 4,
                cursor
            ) == "sin(" ||
                    input.subSequence(cursor - 4, cursor) == "cos(")
        ) {
            cursor -= 4
        } else if (input.length >= 3 && cursor >= 3 && (input.subSequence(
                cursor - 3,
                cursor
            ) == "tg(" ||
                    input.subSequence(cursor - 3, cursor) == "lg(" ||
                    input.subSequence(cursor - 3, cursor) == "ln(")
        ) {
            cursor -= 3
        } else if (input.length >= 2 && cursor >= 2 && (input.subSequence(
                cursor - 2,
                cursor
            ) == "^(" ||
                    input.subSequence(cursor - 2, cursor) == "pi")
        ) {
            cursor -= 2
        } else if (input.isNotEmpty() && cursor >= 1) {
            cursor -= 1
        }

        if (cursor < 0) {
            cursor = 0
        }

        defineNumber()
    }

    fun cursorToRight() {
        if (input.length >= cursor + 7 && (input.subSequence(cursor, cursor + 7) == "arcsin(" ||
                    input.subSequence(cursor, cursor + 7) == "arccos(")
        ) {
            cursor += 7
        } else if (input.length >= cursor + 6 && input.subSequence(
                cursor,
                cursor + 6
            ) == "arctg("
        ) {
            cursor += 6
        } else if (input.length >= cursor + 4 && (input.subSequence(cursor, cursor + 4) == "sin(" ||
                    input.subSequence(cursor, cursor + 4) == "cos(")
        ) {
            cursor += 4
        } else if (input.length >= cursor + 3 && (input.subSequence(cursor, cursor + 3) == "tg(" ||
                    input.subSequence(cursor, cursor + 3) == "lg(" ||
                    input.subSequence(cursor, cursor + 3) == "ln(")
        ) {
            cursor += 3
        } else if (input.length >= cursor + 2 && (input.subSequence(cursor, cursor + 2) == "^(" ||
                    input.subSequence(cursor, cursor + 2) == "pi")
        ) {
            cursor += 2
        } else if (input.isNotEmpty()) {
            cursor += 1
        }

        if (cursor > input.length) {
            cursor = input.length
        }

        defineNumber()
    }

    fun getInputWithCursor(): String {
        return StringBuilder(input).insert(cursor, "<").toString()
    }

    private fun defineNumber() {
        var temp = getInputWithCursor()
        temp = temp.replace("arcsin", " ")
        temp = temp.replace("arccos", " ")
        temp = temp.replace("arctg", " ")
        temp = temp.replace("sin", " ")
        temp = temp.replace("cos", " ")
        temp = temp.replace("tg", " ")
        temp = temp.replace("lg", " ")
        temp = temp.replace("ln", " ")
        temp = temp.replace("pi", " ")
        temp = temp.replace("e", " ")
        temp = temp.replace("(", " ")
        temp = temp.replace(")", " ")
        temp = temp.replace("^", " ")
        temp = temp.replace("!", " ")
        temp = temp.replace("+", " ")
        temp = temp.replace("-", " ")
        temp = temp.replace("x", " ")
        temp = temp.replace("/", " ")
        temp = temp.trim()
        val list = temp.split("\\s+".toRegex())
        val index = list.indexOfFirst { it.contains('<') }

        number = list[index].replace("<", "")
    }

    fun defineNumbers() {
        var temp = getInputWithCursor()
        temp = temp.replace("arcsin", " ")
        temp = temp.replace("arccos", " ")
        temp = temp.replace("arctg", " ")
        temp = temp.replace("sin", " ")
        temp = temp.replace("cos", " ")
        temp = temp.replace("tg", " ")
        temp = temp.replace("lg", " ")
        temp = temp.replace("ln", " ")
        temp = temp.replace("pi", " ")
        temp = temp.replace("e", " ")
        temp = temp.replace("(", " ")
        temp = temp.replace(")", " ")
        temp = temp.replace("^", " ")
        temp = temp.replace("!", " ")
        temp = temp.replace("+", " ")
        temp = temp.replace("-", " ")
        temp = temp.replace("x", " ")
        temp = temp.replace("/", " ")
        temp = temp.trim()
        val list = temp.split("\\s+".toRegex())
        val index = list.indexOfFirst { it.contains('<') }
        number = list[index].replace("<", "")
        numbers = temp.replace("<", "").trim().split("\\s+".toRegex())

        if (input.isNotEmpty() && (isDigit(input.last().toString()) || input.last() == ',')) {
            numbers = numbers.dropLast(1)
        }
    }

    fun insert(buffer: String) {
        var tempInput = buffer.replace(" ", "")
            .replace("(-", "(0-")
            .replace(".", ",")
        while (tempInput.isNotEmpty()) {
            if (tempInput.length >= 7 && (tempInput.subSequence(0, 7).toString()
                    .toLowerCase() == "arcsin(" ||
                        tempInput.subSequence(0, 7).toString().toLowerCase() == "arccos(")
            ) {
                if (!addSymbolToInput(tempInput.subSequence(0, 7).toString().toLowerCase())) {
                    return
                }
            } else if (tempInput.length >= 6 && tempInput.subSequence(0, 6).toString()
                    .toLowerCase() == "arctg("
            ) {
                if (!addSymbolToInput(tempInput.subSequence(0, 6).toString().toLowerCase())) {
                    return
                }
            } else if (tempInput.length >= 4 && (tempInput.subSequence(0, 4).toString()
                    .toLowerCase() == "sin(" ||
                        tempInput.subSequence(0, 4).toString().toLowerCase() == "cos(")
            ) {
                if (!addSymbolToInput(tempInput.subSequence(0, 4).toString().toLowerCase())) {
                    return
                }
            } else if (tempInput.length >= 3 && (tempInput.subSequence(0, 3).toString()
                    .toLowerCase() == "tg(" ||
                        tempInput.subSequence(0, 3).toString().toLowerCase() == "lg(" ||
                        tempInput.subSequence(0, 3).toString().toLowerCase() == "ln(")
            ) {
                if (!addSymbolToInput(tempInput.subSequence(0, 3).toString().toLowerCase())) {
                    return
                }
            } else if (tempInput.length >= 2 && (tempInput.subSequence(0, 2) == "^(" ||
                        tempInput.subSequence(0, 2).toString().toLowerCase() == "pi")
            ) {
                if (!addSymbolToInput(tempInput.subSequence(0, 2).toString().toLowerCase())) {
                    return
                }
            } else if (tempInput.isNotEmpty()) {
                if (tempInput.first() != '+' && tempInput.first() != '-' && tempInput.first() != 'x' &&
                    tempInput.first() != '/' && tempInput.first() != '!' && tempInput.first() != '(' &&
                    tempInput.first() != ')' && tempInput.first() != 'e' && tempInput.first() != 'E' &&
                    !isDigit(tempInput.first().toString()) && tempInput.first() != ','
                ) {
                    toast.cancel()
                    toast.setText("Вставка не завершена! Неизвестный символ: ${tempInput.first()}")
                    toast.show()
                    return
                } else {
                    if (!addSymbolToInput(tempInput.first().toString().toLowerCase())) {
                        return
                    }
                }
            }

            tempInput = tempInput.drop(1)
        }
    }

    private fun isRational(): Boolean {
        return !input.contains("pi") && !input.contains("e") &&
                !input.contains("^") && !input.contains("sin") &&
                !input.contains("cos") && !input.contains("tg") &&
                !input.contains("arcsin") && !input.contains("arccos") &&
                !input.contains("arctg") && !input.contains("lg") &&
                !input.contains("ln")
    }

    private fun removeCloseBracket() {
        var counter = 0
        for (i in cursor until input.length) {
            if (input[i] == '(') {
                counter += 1
            }
            if (input[i] == ')') {
                counter -= 1
            }
            if (counter == -1) {
                input = StringBuilder(input).removeRange(i, i + 1).toString()
                break
            }
        }
    }
}
