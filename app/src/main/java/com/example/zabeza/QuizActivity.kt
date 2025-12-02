package com.example.zabeza

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import org.json.JSONArray
import java.io.InputStream

data class Question(val imagenes: List<String>?, val pregunta: String?, val respuesta: String)

class QuizActivity : AppCompatActivity() {

    private lateinit var questionTextView: TextView
    private lateinit var imageContainer: LinearLayout
    private lateinit var resultTextView: TextView
    private lateinit var showResultButton: Button
    private lateinit var correctButton: Button
    private lateinit var incorrectButton: Button
    private lateinit var progressBar: ProgressBar

    private var questionList = listOf<Question>()
    private var currentQuestion: Question? = null
    private var correctAnswers = 0
    private val answersToLevelUp = 6

    private lateinit var quizName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        quizName = intent.getStringExtra("quiz_name") ?: "default_quiz"

        loadProgress()
        loadQuestions()

        questionTextView = findViewById(R.id.question_text)
        imageContainer = findViewById(R.id.image_container)
        resultTextView = findViewById(R.id.result)
        showResultButton = findViewById(R.id.show_result_button)
        correctButton = findViewById(R.id.correct_button)
        incorrectButton = findViewById(R.id.incorrect_button)
        progressBar = findViewById(R.id.progress_bar)

        setupNewGame()

        showResultButton.setOnClickListener { showResult() }
        correctButton.setOnClickListener { handleCorrectAnswer() }
        incorrectButton.setOnClickListener { handleIncorrectAnswer() }
    }

    override fun onPause() {
        super.onPause()
        saveProgress()
    }

    private fun loadQuestions() {
        try {
            val resourceId = resources.getIdentifier(quizName, "raw", packageName)
            val inputStream: InputStream = resources.openRawResource(resourceId)
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            val questions = mutableListOf<Question>()
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

                questions.add(
                    Question(
                        imagenes = imageList,
                        pregunta = jsonObject.optString("pregunta").takeIf { it.isNotEmpty() },
                        respuesta = jsonObject.getString("respuesta")
                    )
                )
            }
            questionList = questions
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupNewGame() {
        progressBar.max = answersToLevelUp
        updateProgressBar()
        generateNewQuestion()
    }

    private fun generateNewQuestion() {
        if (questionList.isEmpty()) return

        currentQuestion = questionList.random()

        val hasImages = !currentQuestion?.imagenes.isNullOrEmpty()
        val hasText = currentQuestion?.pregunta != null

        imageContainer.visibility = if (hasImages) View.VISIBLE else View.GONE
        questionTextView.visibility = if (hasText) View.VISIBLE else View.GONE

        if (hasImages) {
            loadImages(currentQuestion?.imagenes)
        }
        if (hasText) {
            questionTextView.text = currentQuestion?.pregunta
        }
    }

    private fun loadImages(imagePaths: List<String>?) {
        imageContainer.removeAllViews()
        if (imagePaths.isNullOrEmpty()) return

        for (imagePath in imagePaths) {
            val imageView = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0, 
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1f
                ).also { it.setMargins(8, 8, 8, 8) }
            }
            imageContainer.addView(imageView)

            Glide.with(this)
                .load("file:///android_asset/$imagePath")
                .error(R.drawable.placeholder_image)
                .into(imageView)
        }
    }

    private fun showResult() {
        resultTextView.text = currentQuestion?.respuesta
        correctButton.visibility = View.VISIBLE
        incorrectButton.visibility = View.VISIBLE
    }

    private fun handleCorrectAnswer() {
        correctAnswers++
        if (correctAnswers >= answersToLevelUp) {
            correctAnswers = 0
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
        generateNewQuestion()
    }

    private fun updateProgressBar() {
        progressBar.progress = correctAnswers
    }

    private fun saveProgress() {
        val prefs = getSharedPreferences("game_progress", Context.MODE_PRIVATE).edit()
        prefs.putInt("${quizName}_correctAnswers", correctAnswers)
        prefs.apply()
    }

    private fun loadProgress() {
        val prefs = getSharedPreferences("game_progress", Context.MODE_PRIVATE)
        correctAnswers = prefs.getInt("${quizName}_correctAnswers", 0)
    }
}