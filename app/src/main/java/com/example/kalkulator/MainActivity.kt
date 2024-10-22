package com.example.kalkulator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    CalculatorScreen()
                }
            }
        }
    }
}

@Composable
fun CalculatorScreen() {
    var currentNumber by remember { mutableStateOf("") }
    var operator by remember { mutableStateOf("") }
    var firstNumber by remember { mutableStateOf(0.0) }
    var isNewNumber by remember { mutableStateOf(true) }
    var expressionHistory by remember { mutableStateOf("") }
    // Tambahkan state untuk menandai input angka kedua
    var isSecondNumber by remember { mutableStateOf(false) }

    val purpleColor = Color(0xFF8B00FF)
    val darkGray = Color(0xFF1E1E1E)
    val lightGray = Color(0xFF363636)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        // Expression History Display
        Text(
            text = expressionHistory,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            fontSize = 24.sp,
            textAlign = TextAlign.End,
            color = Color.Gray
        )

        // Current Number Display
        Text(
            text = if (isNewNumber && isSecondNumber) "0" else currentNumber.ifEmpty { "0" },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            fontSize = 48.sp,
            textAlign = TextAlign.End,
            color = Color.White
        )

        // Special functions row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("C", "÷", "×", "⌫").forEach { symbol ->
                Button(
                    onClick = {
                        when (symbol) {
                            "C" -> {
                                currentNumber = ""
                                operator = ""
                                firstNumber = 0.0
                                isNewNumber = true
                                isSecondNumber = false
                                expressionHistory = ""
                            }
                            "÷" -> {
                                if (currentNumber.isNotEmpty()) {
                                    if (operator.isEmpty()) {
                                        firstNumber = currentNumber.toDouble()
                                        operator = "/"
                                        expressionHistory = "$currentNumber ÷"
                                        isNewNumber = true
                                        isSecondNumber = true
                                    } else {
                                        // Jika sudah ada operator sebelumnya
                                        val secondNumber = currentNumber.toDouble()
                                        val result = calculateResult(firstNumber, secondNumber, operator)
                                        firstNumber = result
                                        currentNumber = result.toString()
                                        operator = "/"
                                        expressionHistory = "$result ÷"
                                        isNewNumber = true
                                        isSecondNumber = true
                                    }
                                }
                            }
                            "×" -> {
                                if (currentNumber.isNotEmpty()) {
                                    if (operator.isEmpty()) {
                                        firstNumber = currentNumber.toDouble()
                                        operator = "*"
                                        expressionHistory = "$currentNumber ×"
                                        isNewNumber = true
                                        isSecondNumber = true
                                    } else {
                                        // Jika sudah ada operator sebelumnya
                                        val secondNumber = currentNumber.toDouble()
                                        val result = calculateResult(firstNumber, secondNumber, operator)
                                        firstNumber = result
                                        currentNumber = result.toString()
                                        operator = "*"
                                        expressionHistory = "$result ×"
                                        isNewNumber = true
                                        isSecondNumber = true
                                    }
                                }
                            }
                            "⌫" -> {
                                if (currentNumber.isNotEmpty()) {
                                    currentNumber = currentNumber.dropLast(1)
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = lightGray,
                        contentColor = if (symbol == "C") purpleColor else Color.White
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(text = symbol, fontSize = 24.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Numbers and operators grid
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // First two rows (7-9 and 4-6 with operators)
            listOf(
                listOf("7", "8", "9", "-"),
                listOf("4", "5", "6", "+")
            ).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { symbol ->
                        Button(
                            onClick = {
                                when {
                                    symbol in "0123456789" -> {
                                        if (isNewNumber) {
                                            currentNumber = symbol
                                            isNewNumber = false
                                        } else {
                                            currentNumber += symbol
                                        }
                                    }
                                    symbol in "+-" -> {
                                        if (currentNumber.isNotEmpty()) {
                                            if (operator.isEmpty()) {
                                                firstNumber = currentNumber.toDouble()
                                                operator = symbol
                                                expressionHistory = "$currentNumber $symbol"
                                                isNewNumber = true
                                                isSecondNumber = true
                                            } else {
                                                // Jika sudah ada operator sebelumnya
                                                val secondNumber = currentNumber.toDouble()
                                                val result = calculateResult(firstNumber, secondNumber, operator)
                                                firstNumber = result
                                                currentNumber = result.toString()
                                                operator = symbol
                                                expressionHistory = "$result $symbol"
                                                isNewNumber = true
                                                isSecondNumber = true
                                            }
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = darkGray,
                                contentColor = if (symbol in "+-") purpleColor else Color.White
                            ),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(text = symbol, fontSize = 24.sp)
                        }
                    }
                }
            }

            // Last two rows with equals button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Left section (1-3, %, 0, .)
                Column(
                    modifier = Modifier.weight(3f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Numbers 1-3
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("1", "2", "3").forEach { symbol ->
                            Button(
                                onClick = {
                                    if (isNewNumber) {
                                        currentNumber = symbol
                                        isNewNumber = false
                                    } else {
                                        currentNumber += symbol
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = darkGray,
                                    contentColor = Color.White
                                ),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Text(text = symbol, fontSize = 24.sp)
                            }
                        }
                    }

                    // Bottom row (%, 0, .)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("%", "0", ".").forEach { symbol ->
                            Button(
                                onClick = {
                                    when (symbol) {
                                        "0" -> {
                                            if (isNewNumber) {
                                                currentNumber = symbol
                                                isNewNumber = false
                                            } else {
                                                currentNumber += symbol
                                            }
                                        }
                                        "." -> {
                                            if (!currentNumber.contains(".")) {
                                                currentNumber += if (currentNumber.isEmpty()) "0." else "."
                                            }
                                        }
                                        "%" -> {
                                            if (currentNumber.isNotEmpty()) {
                                                val number = currentNumber.toDouble()
                                                currentNumber = (number / 100).toString()
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = darkGray,
                                    contentColor = Color.White
                                ),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Text(text = symbol, fontSize = 24.sp)
                            }
                        }
                    }
                }

                // Right section (equals button)
                Button(
                    onClick = {
                        if (currentNumber.isNotEmpty() && operator.isNotEmpty()) {
                            val secondNumber = currentNumber.toDouble()
                            expressionHistory = "$firstNumber $operator $secondNumber ="
                            val result = calculateResult(firstNumber, secondNumber, operator)
                            currentNumber = if (result.isNaN()) "Error" else result.toString()
                            operator = ""
                            isNewNumber = true
                            isSecondNumber = false
                        }
                    },
                    modifier = Modifier
                        .width(75.dp)
                        .height(162.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = purpleColor,
                        contentColor = Color.White
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(text = "=", fontSize = 24.sp)
                }
            }
        }
    }
}

private fun calculateResult(firstNumber: Double, secondNumber: Double, operator: String): Double {
    return when (operator) {
        "+" -> firstNumber + secondNumber
        "-" -> firstNumber - secondNumber
        "*" -> firstNumber * secondNumber
        "/" -> if (secondNumber != 0.0) firstNumber / secondNumber else Double.NaN
        else -> 0.0
    }
}