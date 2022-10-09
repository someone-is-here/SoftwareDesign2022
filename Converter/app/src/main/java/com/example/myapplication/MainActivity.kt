package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private var tvInput:TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvInput = findViewById(R.id.tvInput)
    }

    fun onDigit(view: View){
        tvInput?.append((view as Button).text)
    }

    fun onClear(view: View){
        tvInput!!.text=""
    }

    fun onDot(view: View){
        val digit:CharSequence = (view as Button).text

        if(!tvInput!!.text.contains(digit)){
            tvInput?.append(digit)
        } else{
            Toast.makeText(this, "Your number already decimal", Toast.LENGTH_SHORT).show()
        }
    }
}