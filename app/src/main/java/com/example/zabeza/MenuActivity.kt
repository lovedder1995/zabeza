package com.example.zabeza

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        findViewById<Button>(R.id.sum_button).setOnClickListener {
            launchMathActivity("sum")
        }

        findViewById<Button>(R.id.subtract_button).setOnClickListener {
            launchMathActivity("subtract")
        }

        findViewById<Button>(R.id.multiply_button).setOnClickListener {
            launchMathActivity("multiply")
        }

        findViewById<Button>(R.id.divide_button).setOnClickListener {
            launchMathActivity("divide")
        }

        findViewById<Button>(R.id.bones_button).setOnClickListener {
            launchQuizActivity("huesos_del_cuerpo_humano")
        }

        findViewById<Button>(R.id.flags_button).setOnClickListener {
            launchQuizActivity("banderas")
        }
    }

    private fun launchMathActivity(operation: String) {
        val intent = Intent(this, PracticeActivity::class.java)
        intent.putExtra("operation", operation)
        startActivity(intent)
    }

    private fun launchQuizActivity(quizName: String) {
        val intent = Intent(this, QuizActivity::class.java)
        intent.putExtra("quiz_name", quizName)
        startActivity(intent)
    }
}