package com.example.recycleview

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    val listWithValues = listOf<Fruit>(
        Fruit("Mango","smth"),
        Fruit("Apple","smth"),
        Fruit("Banana","smth"),
        Fruit("Orange","smth"),
        Fruit("Melon","smth"),
        Fruit("Pear","smth"),
        Fruit("Watermelon","smth")
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.setBackgroundColor(Color.BLACK)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = MyRecyclerViewAdapter(listWithValues)
    }
}