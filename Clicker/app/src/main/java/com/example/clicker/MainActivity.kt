package com.example.clicker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel:MainActivityViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tvCounter = findViewById<TextView>(R.id.tvCounter)

        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        viewModel.count.observe(this, Observer {
            tvCounter.text = it.toString()
        })

        findViewById<Button>(R.id.bClick).setOnClickListener {
            viewModel.updateCount()
        }
    }
}