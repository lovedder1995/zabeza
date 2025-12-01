package com.example.zabeza

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val sumButton: Button = findViewById(R.id.sum_button)
        val subtractButton: Button = findViewById(R.id.subtract_button)
        val multiplyButton: Button = findViewById(R.id.multiply_button)
        val divideButton: Button = findViewById(R.id.divide_button)

        sumButton.setOnClickListener {
            launchPracticeActivity("sum")
        }

        subtractButton.setOnClickListener {
            launchPracticeActivity("subtract")
        }

        multiplyButton.setOnClickListener {
            launchPracticeActivity("multiply")
        }

        divideButton.setOnClickListener {
            launchPracticeActivity("divide")
        }
    }

    private fun launchPracticeActivity(operation: String) {
        val intent = Intent(this, PracticeActivity::class.java)
        intent.putExtra("operation", operation)
        startActivity(intent)
    }
}