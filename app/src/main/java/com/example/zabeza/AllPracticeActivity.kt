package com.example.zabeza

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import org.json.JSONArray
import java.io.InputStream
import java.text.DecimalFormat
import kotlin.random.Random

sealed class AllQuestion
data class MathQuestion(val operation: String, val num1: Int, val num2: Int, val answer: String) : AllQuestion()
data class QuizQuestion(val quizName: String, val question: Question) : AllQuestion()

class AllPracticeActivity : AppCompatActivity() {

    // Views
    private lateinit var mathLayout: View
    private lateinit var quizLayout: View
    private lateinit var number1TextView: TextView
    private lateinit var number2TextView: TextView
    private lateinit var operatorTextView: TextView
    private lateinit var questionTextView: TextView
    private lateinit var imageContainer: LinearLayout
    private lateinit var resultTextView: TextView
    private lateinit var showResultButton: Button
    private lateinit var correctButton: Button
    private lateinit var incorrectButton: Button

    private var allQuestions = mutableListOf<AllQuestion>()
    private var currentQuestion: AllQuestion? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_practice)
        bindViews()
        loadAllQuestions()
        setupNewGame()

        showResultButton.setOnClickListener { showResult() }
        correctButton.setOnClickListener { handleCorrectAnswer() }
        incorrectButton.setOnClickListener { handleIncorrectAnswer() }
    }

    private fun bindViews() {
        mathLayout = findViewById(R.id.math_layout)
        quizLayout = findViewById(R.id.quiz_layout)
        number1TextView = findViewById(R.id.number1)
        number2TextView = findViewById(R.id.number2)
        operatorTextView = findViewById(R.id.operator_sign)
        questionTextView = findViewById(R.id.question_text)
        imageContainer = findViewById(R.id.image_container)
        resultTextView = findViewById(R.id.result)
        showResultButton = findViewById(R.id.show_result_button)
        correctButton = findViewById(R.id.correct_button)
        incorrectButton = findViewById(R.id.incorrect_button)
    }

    private fun loadAllQuestions() {
        // Load math questions
        val mathOperations = listOf("sum", "subtract", "multiply", "divide")
        mathOperations.forEach { op ->
            val prefs = getSharedPreferences("game_progress", Context.MODE_PRIVATE)
            val min = prefs.getInt("${op}_minNumber", 1)
            val max = prefs.getInt("${op}_maxNumber", 6)
            // Add a representation of math questions
            repeat(10) { // Add 10 of each for balance
                 allQuestions.add(createMathQuestion(op, min, max))
            }
        }

        // Load quiz questions
        val quizFiles = listOf("huesos_del_cuerpo_humano", "banderas")
        quizFiles.forEach { quizName ->
            try {
                val resourceId = resources.getIdentifier(quizName, "raw", packageName)
                val inputStream: InputStream = resources.openRawResource(resourceId)
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                val jsonArray = JSONArray(jsonString)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val imagenesArray = jsonObject.optJSONArray("imágenes")
                    val imagenString = jsonObject.optString("imagen")
                    var imageList: List<String>? = null
                    if (imagenesArray != null) {
                         imageList = (0 until imagenesArray.length()).map { imagenesArray.getString(it) }
                    } else if (imagenString.isNotEmpty()) {
                        imageList = listOf(imagenString)
                    }
                    val question = Question(imageList, jsonObject.optString("pregunta").takeIf { it.isNotEmpty() }, jsonObject.getString("respuesta"))
                    allQuestions.add(QuizQuestion(quizName, question))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    private fun createMathQuestion(operation: String, min: Int, max: Int) : MathQuestion {
        var num1 = Random.nextInt(min, max + 1)
        var num2 = Random.nextInt(min, max + 1)

        if (operation == "divide") {
            while (num2 == 0) { num2 = Random.nextInt(min, max + 1) }
        }
        
        val result = when (operation) {
            "sum" -> (num1.toDouble() + num2.toDouble()).toString()
            "subtract" -> (num1.toDouble() - num2.toDouble()).toString()
            "multiply" -> (num1.toDouble() * num2.toDouble()).toString()
            "divide" -> (num1.toDouble() / num2.toDouble()).toString()
            else -> "0.0"
        }

        return MathQuestion(operation, num1, num2, formatResult(result.toDouble()))
    }

    private fun setupNewGame() {
        generateNewQuestion()
    }

    private fun generateNewQuestion() {
        if (allQuestions.isEmpty()) return

        currentQuestion = allQuestions.random()

        when (val question = currentQuestion) {
            is MathQuestion -> {
                quizLayout.visibility = View.GONE
                mathLayout.visibility = View.VISIBLE
                number1TextView.text = question.num1.toString()
                number2TextView.text = question.num2.toString()
                operatorTextView.text = when(question.operation) {
                    "sum" -> "+"
                    "subtract" -> "-"
                    "multiply" -> "x"
                    "divide" -> "÷"
                    else -> ""
                }
            }
            is QuizQuestion -> {
                mathLayout.visibility = View.GONE
                quizLayout.visibility = View.VISIBLE
                val hasImages = !question.question.imagenes.isNullOrEmpty()
                val hasText = question.question.pregunta != null

                imageContainer.visibility = if (hasImages) View.VISIBLE else View.GONE
                questionTextView.visibility = if (hasText) View.VISIBLE else View.GONE

                if (hasImages) loadImages(question.question.imagenes)
                if (hasText) questionTextView.text = question.question.pregunta
            }

            else -> {}
        }
    }

    private fun loadImages(imagePaths: List<String>?) {
        imageContainer.removeAllViews()
        if (imagePaths.isNullOrEmpty()) return

        for (imagePath in imagePaths) {
            val imageView = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                    .also { it.setMargins(8, 8, 8, 8) }
            }
            imageContainer.addView(imageView)

            Glide.with(this)
                .load("file:///android_asset/$imagePath")
                .error(R.drawable.placeholder_image)
                .into(imageView)
        }
    }

    private fun showResult() {
        resultTextView.text = when(val question = currentQuestion) {
            is MathQuestion -> question.answer
            is QuizQuestion -> question.question.respuesta
            else -> ""
        }
        correctButton.visibility = View.VISIBLE
        incorrectButton.visibility = View.VISIBLE
    }
    
    private fun formatResult(number: Double): String {
        return if (number % 1.0 == 0.0) {
            number.toInt().toString()
        } else {
            DecimalFormat("#.######").format(number)
        }
    }

    private fun handleCorrectAnswer() {
        // TODO: Implement individual progress saving
        nextOperation()
    }

    private fun handleIncorrectAnswer() {
        // TODO: Implement individual progress reset
        nextOperation()
    }

    private fun nextOperation() {
        correctButton.visibility = View.GONE
        incorrectButton.visibility = View.GONE
        resultTextView.text = ""
        generateNewQuestion()
    }
}