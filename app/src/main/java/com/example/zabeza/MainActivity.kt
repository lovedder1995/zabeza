package com.example.zabeza

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var number1TextView: TextView
    private lateinit var number2TextView: TextView
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        number1TextView = findViewById(R.id.number1)
        number2TextView = findViewById(R.id.number2)
        resultTextView = findViewById(R.id.result)
        showResultButton = findViewById(R.id.show_result_button)
        correctButton = findViewById(R.id.correct_button)
        incorrectButton = findViewById(R.id.incorrect_button)
        progressBar = findViewById(R.id.progress_bar)
        rangeTextView = findViewById(R.id.max_number_text)

        setupNewGame()

        showResultButton.setOnClickListener {
            showResult()
        }

        correctButton.setOnClickListener {
            handleCorrectAnswer()
        }

        incorrectButton.setOnClickListener {
            handleIncorrectAnswer()
        }
    }

    private fun setupNewGame() {
        progressBar.max = answersToLevelUp
        updateRangeText()
        generateNumbers()
    }

    private fun generateNumbers() {
        val num1 = Random.nextInt(minNumber, maxNumber + 1)
        val num2 = Random.nextInt(minNumber, maxNumber + 1)

        number1TextView.text = num1.toString()
        number2TextView.text = num2.toString()
    }

    private fun showResult() {
        val num1 = number1TextView.text.toString().toInt()
        val num2 = number2TextView.text.toString().toInt()
        val sum = num1 + num2

        resultTextView.text = sum.toString()
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
}