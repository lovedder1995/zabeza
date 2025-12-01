package com.example.zabeza

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class PracticeActivity : AppCompatActivity() {

    private lateinit var number1TextView: TextView
    private lateinit var number2TextView: TextView
    private lateinit var operatorTextView: TextView
    private lateinit var resultTextView: TextView
    private lateinit var showResultButton: Button
    private lateinit var correctButton: Button
    private lateinit var incorrectButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var rangeTextView: TextView

    private var minNumber = 1
    private var maxNumber = 6
    private var correctAnswers = 0
    private var levelsCompleted = 0
    private val answersToLevelUp = 6

    private lateinit var operationType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        operationType = intent.getStringExtra("operation") ?: "sum"

        loadProgress()

        number1TextView = findViewById(R.id.number1)
        number2TextView = findViewById(R.id.number2)
        operatorTextView = findViewById(R.id.plus_sign)
        resultTextView = findViewById(R.id.result)
        showResultButton = findViewById(R.id.show_result_button)
        correctButton = findViewById(R.id.correct_button)
        incorrectButton = findViewById(R.id.incorrect_button)
        progressBar = findViewById(R.id.progress_bar)
        rangeTextView = findViewById(R.id.max_number_text)

        setupNewGame()

        showResultButton.setOnClickListener { showResult() }
        correctButton.setOnClickListener { handleCorrectAnswer() }
        incorrectButton.setOnClickListener { handleIncorrectAnswer() }
    }

    override fun onPause() {
        super.onPause()
        saveProgress()
    }

    private fun setupNewGame() {
        progressBar.max = answersToLevelUp
        operatorTextView.text = when (operationType) {
            "sum" -> "+"
            "subtract" -> "-"
            "multiply" -> "x"
            "divide" -> "÷"
            else -> "+"
        }
        updateRangeText()
        updateProgressBar()
        generateNumbers()
    }

    private fun generateNumbers() {
        var num1: Int
        var num2: Int

        if (operationType == "divide") {
            // Generate a problem that results in a whole number
            val tempResult = Random.nextInt(minNumber, maxNumber + 1)
            num2 = Random.nextInt(minNumber, maxNumber + 1)
            num1 = num2 * tempResult
        } else {
            num1 = Random.nextInt(minNumber, maxNumber + 1)
            num2 = Random.nextInt(minNumber, maxNumber + 1)
        }

        if (operationType == "subtract" && num1 < num2) {
            // Swap numbers to avoid negative results
            val temp = num1
            num1 = num2
            num2 = temp
        }

        number1TextView.text = num1.toString()
        number2TextView.text = num2.toString()
    }

    private fun showResult() {
        val num1 = number1TextView.text.toString().toInt()
        val num2 = number2TextView.text.toString().toInt()

        val result = when (operationType) {
            "sum" -> num1 + num2
            "subtract" -> num1 - num2
            "multiply" -> num1 * num2
            "divide" -> num1 / num2
            else -> 0
        }

        resultTextView.text = result.toString()
        correctButton.visibility = View.VISIBLE
        incorrectButton.visibility = View.VISIBLE
    }

    private fun handleCorrectAnswer() {
        correctAnswers++
        if (correctAnswers >= answersToLevelUp) {
            levelsCompleted++
            maxNumber++
            if (levelsCompleted > 0 && levelsCompleted % 3 == 0) {
                minNumber++
            }
            correctAnswers = 0
            updateRangeText()
        }
        updateProgressBar()
        nextOperation()
    }

    private fun handleIncorrectAnswer() {
        correctAnswers = 0
        updateProgressBar()
        nextOperation()
    }

    private fun nextOperation() {
        correctButton.visibility = View.GONE
        incorrectButton.visibility = View.GONE
        resultTextView.text = ""
        generateNumbers()
    }

    private fun updateRangeText() {
        rangeTextView.text = "Rango: $minNumber - $maxNumber"
    }

    private fun updateProgressBar() {
        progressBar.progress = correctAnswers
    }

    private fun saveProgress() {
        val prefs = getSharedPreferences("game_progress", Context.MODE_PRIVATE).edit()
        prefs.putInt("${operationType}_minNumber", minNumber)
        prefs.putInt("${operationType}_maxNumber", maxNumber)
        prefs.putInt("${operationType}_correctAnswers", correctAnswers)
        prefs.putInt("${operationType}_levelsCompleted", levelsCompleted)
        prefs.apply()
    }

    private fun loadProgress() {
        val prefs = getSharedPreferences("game_progress", Context.MODE_PRIVATE)
        minNumber = prefs.getInt("${operationType}_minNumber", 1)
        maxNumber = prefs.getInt("${operationType}_maxNumber", 6)
        correctAnswers = prefs.getInt("${operationType}_correctAnswers", 0)
        levelsCompleted = prefs.getInt("${operationType}_levelsCompleted", 0)
    }
}