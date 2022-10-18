package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private var tvInput:TextView? = null
    lateinit var bottomNav : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<LinearLayout>(R.id.mainLayout).isVisible = false;
        tvInput = findViewById(R.id.tvInput)

        loadFragment(HomeFragment())
        bottomNav = findViewById(R.id.bottomNav) as BottomNavigationView
        bottomNav.setOnNavigationItemReselectedListener {
            when (it.itemId) {
                R.id.home -> {
                    loadFragment(HomeFragment())
                    findViewById<LinearLayout>(R.id.mainLayout).isVisible = false;
                    return@setOnNavigationItemReselectedListener
                }
                R.id.currency -> {
                    loadFragment(CurrencyFragment())
                    setChooseMenu(resources.getStringArray(R.array.currency))
                    findViewById<LinearLayout>(R.id.mainLayout).isVisible = true;
                    return@setOnNavigationItemReselectedListener
                }
                R.id.distance -> {
                    loadFragment(DistanceFragment())
                    setChooseMenu(resources.getStringArray(R.array.distance))
                    findViewById<LinearLayout>(R.id.mainLayout).isVisible = true;
                    return@setOnNavigationItemReselectedListener
                }
                R.id.weight -> {
                    loadFragment(WeightFragment())
                    setChooseMenu(resources.getStringArray(R.array.weight))
                    findViewById<LinearLayout>(R.id.mainLayout).isVisible = true;
                    return@setOnNavigationItemReselectedListener
                }
                R.id.settings -> {
                    loadFragment(SettingsFragment())
                    findViewById<LinearLayout>(R.id.mainLayout).isVisible = false;
                    return@setOnNavigationItemReselectedListener
                }
            }
        }
    }

    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
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
    public fun setChooseMenu(languages: Array<String?>){

//        // get reference to the string array that we just created
//        val languages = resources.getStringArray(R.array.currency)
        // create an array adapter and pass the required parameter
        // in our case pass the context, drop down layout , and array.
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, languages)
        // get reference to the autocomplete text view
        val autocompleteTV = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        val autocompleteTV2 = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView2)
        // set adapter to the autocomplete tv to the arrayAdapter
        autocompleteTV.setAdapter(arrayAdapter)
        autocompleteTV2.setAdapter(arrayAdapter)
    }
}