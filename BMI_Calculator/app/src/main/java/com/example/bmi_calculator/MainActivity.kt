package com.example.bmi_calculator

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val weightText = findViewById<EditText>(R.id.etWeight)
        val heightText = findViewById<EditText>(R.id.etHeight)
        val calcButton = findViewById<Button>(R.id.btnCalculate)

        calcButton.setOnClickListener{
            val weight = weightText.text.toString()
            val height = heightText.text.toString()
            if (validateInput(weight, height)) {
                val bmi = weight.toFloat() / ((height.toFloat() / 100) * (height.toFloat() / 100))
                val bmi2Digits = String.format("%.2f", bmi).toFloat()

                displayInfo(bmi2Digits)
            }
        }
    }

    private fun validateInput(weight:String?, height:String?):Boolean{
        return when {
            weight.isNullOrEmpty() -> {
                Toast.makeText(this, "Weight is empty", Toast.LENGTH_LONG).show()
                return false
            }
            height.isNullOrEmpty() -> {
                Toast.makeText(this, "Height is empty", Toast.LENGTH_LONG).show()
                return false
            }
            else -> return true
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayInfo(bmi: Float){
        findViewById<TextView>(R.id.tvIndex).text = bmi.toString()
        findViewById<TextView>(R.id.tvInfo).text = "(Normal range is 18.5 - 24.9)"

        var resColor = 0
        var resultExpression:String = ""

        when{
            bmi < 18.5 -> {
                resultExpression = "Underweight"
                resColor = R.color.under_weight
            }
            bmi in 18.50..24.99 -> {
                resultExpression = "Health"
                resColor = R.color.normal
            }
            bmi in 25.00..29.99 -> {
                resultExpression = "Overweight"
                resColor = R.color.over_weight
            }
            bmi > 29.99 -> {
                resultExpression = "Obese"
                resColor = R.color.obese
            }
        }

        val tvRes = findViewById<TextView>(R.id.tvResult)

        tvRes.text = resultExpression
        tvRes.setTextColor(ContextCompat.getColor(this, resColor))
    }
}