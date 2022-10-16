package com.example.converter

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    lateinit var bottomNav : BottomNavigationView

    lateinit var convertFrom: String
    lateinit var convertTo: String

    private var inputFrom: String = ""
    private var inputTo: String = ""

    private lateinit var input: TextView
    private lateinit var output: TextView

    private var isActiveInputFrom = false
    private var isActiveInputTo = false

    private var savedCopy: String = ""
    private var profMode: Boolean = false

    private var nightMode: Boolean = false

    private val convertibleValues:Map<String, Double> = mapOf(
        "EUR" to 2.45996, "USD" to 2.53473,
        "GBP" to 2.81348, "BLR" to 1.0,
        "CNY" to 0.353272, "CAD" to 1.83530,
        "m" to 1.0, "dm" to 0.1, "cm" to 0.01,
        "km" to 1000.0, "mi" to 1609.344,
        "yd" to 0.9144, "ft" to 0.3048,
        "kg" to 1.0, "gr" to 0.001,
        "t" to 1000.0, "q" to 100.0,
        "st" to 6.3503, "lb" to 0.4536
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        bottomNav.setOnNavigationItemSelectedListener {
            Log.i("mko","bottomNavListener" + it.itemId.toString())
            when (it.itemId) {
                R.id.currency -> {
                    loadFragment(CurrencyFragment())
                    setChooseMenu(R.array.currency)
                    Log.i("mko", "Load chosen currency")
                    initInput()
                    return@setOnNavigationItemSelectedListener true
                    //return@setOnNavigationItemReselectedListener
                }
                R.id.distance -> {
                    loadFragment(DistanceFragment())
                    setChooseMenu(R.array.distance)
                    Log.i("mko", "Load chosen distance")
                    initInput()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.weight -> {
                    loadFragment(WeightFragment())
                    setChooseMenu(R.array.weight)
                    Log.i("mko", "Load chosen weight")
                    initInput()
                    return@setOnNavigationItemSelectedListener true
                }
                else -> {
                    return@setOnNavigationItemSelectedListener false
                }
            }
        }

        setButtonsListeners()

        loadFragment(CurrencyFragment())
        setChooseMenu(R.array.currency)

        Log.i("mko", "Load default currency")

        if(savedInstanceState != null){
            Log.i("mko", "NOT NULL STATE" + savedInstanceState.getInt("opened_fragment"))
            bottomNav.selectedItemId = savedInstanceState.getInt("opened_fragment")
        }

        initInput()
    }

    private fun setButtonsListeners(){
        findViewById<Button>(R.id.btnPaste1).visibility = View.INVISIBLE
        findViewById<Button>(R.id.btnCopy1).visibility = View.INVISIBLE
        findViewById<Button>(R.id.btnPaste).visibility = View.INVISIBLE
        findViewById<Button>(R.id.btnCopy).visibility = View.INVISIBLE
        findViewById<Button>(R.id.btnSwap1).visibility = View.INVISIBLE
        findViewById<Button>(R.id.btnNightMode).visibility = View.INVISIBLE

        findViewById<Button>(R.id.btnAdditionalMode).setOnClickListener {
            profMode = true
            Log.i("ProfMode", "SerProfMode!")
            findViewById<Button>(R.id.btnSwap1).visibility = View.VISIBLE

            findViewById<Button>(R.id.btnCopy1).visibility = View.VISIBLE
            findViewById<Button>(R.id.btnCopy1).setOnClickListener {
                val res = findViewById<EditText>(R.id.etInput).text.toString()
                if(res.isNotEmpty()){
                    savedCopy = res
                }
            }

            findViewById<Button>(R.id.btnPaste1).visibility = View.VISIBLE
            findViewById<Button>(R.id.btnPaste1).setOnClickListener {
                inputFrom = savedCopy
                input.setText(inputFrom, TextView.BufferType.EDITABLE)
                isActiveInputFrom = true
                (input as EditText).setSelection(inputFrom.length)
                update()
            }

            findViewById<Button>(R.id.btnCopy).visibility = View.VISIBLE
            findViewById<Button>(R.id.btnCopy).setOnClickListener {
                val res = findViewById<EditText>(R.id.etOutput).text.toString()
                if(res.isNotEmpty()){
                    savedCopy = res
                }
            }

            findViewById<Button>(R.id.btnPaste).visibility = View.VISIBLE
            findViewById<Button>(R.id.btnPaste).setOnClickListener {
                inputTo = savedCopy
                output.setText(inputTo, TextView.BufferType.EDITABLE)
                isActiveInputTo = true
                (output as EditText).setSelection(inputTo.length)
            }

            findViewById<Button>(R.id.btnNightMode).visibility = View.VISIBLE
            findViewById<Button>(R.id.btnNightMode).setOnClickListener {
                nightMode = !nightMode
                if(nightMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }
    }
    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    private fun initInput(){
        val etInput = findViewById<TextView>(R.id.etInput)
        etInput.hint = "0.0"
        etInput.text = ""
        etInput.showSoftInputOnFocus = false
        input = etInput
        inputFrom = ""
        etInput.setOnFocusChangeListener { view, hasFocus -> if (hasFocus) {
            isActiveInputTo = false
            isActiveInputFrom = true
        }}

        val etOutput = findViewById<TextView>(R.id.etOutput)
        etOutput.hint = "0.0"
        etOutput.text = ""
        etOutput.showSoftInputOnFocus = false
        inputTo = ""
        etOutput.setOnFocusChangeListener { view, hasFocus -> if (hasFocus) {
            isActiveInputTo = true
            isActiveInputFrom = false
        }}
        output = etOutput

        isActiveInputFrom = true
    }
    override fun onResume() {
        super.onResume()

        if(profMode) findViewById<Button>(R.id.btnAdditionalMode).performClick()
        if(nightMode) findViewById<Button>(R.id.btnNightMode).performClick()
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)

        val sConvertFrom = findViewById<Spinner>(R.id.sInputFrom)
        val sConvertTo = findViewById<Spinner>(R.id.sInputTo)

        sConvertFrom.setSelection(savedInstanceState.getInt("convertFrom"))
        sConvertTo.setSelection(savedInstanceState.getInt("convertTo"))

        convertFrom = sConvertFrom.selectedItem.toString()
        convertTo = sConvertTo.selectedItem.toString()

        inputFrom = savedInstanceState.getString("inputFrom").toString()
        inputTo = savedInstanceState.getString("inputTo").toString()

        input.setText(inputFrom, TextView.BufferType.EDITABLE)
        output.setText(inputTo, TextView.BufferType.EDITABLE)

        savedCopy = savedInstanceState.getString("savedCopy").toString()
        profMode = savedInstanceState.getBoolean("profMode")
    }
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putInt("convertFrom", findViewById<Spinner>(R.id.sInputFrom).selectedItemPosition)
        savedInstanceState.putInt("convertTo", findViewById<Spinner>(R.id.sInputTo).selectedItemPosition)

        savedInstanceState.putString("inputFrom", inputFrom)
        savedInstanceState.putString("inputTo", inputTo)

        savedInstanceState.putString("savedCopy", savedCopy)
        savedInstanceState.putBoolean("profMode", profMode)

        savedInstanceState.putInt("opened_fragment", bottomNav.selectedItemId)

        super.onSaveInstanceState(savedInstanceState);
    }
    private fun setChooseMenu(arr: Int){
        val arrayAdapter = ArrayAdapter.createFromResource(this, arr, android.R.layout.simple_spinner_item)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val spinnerFrom = findViewById<Spinner>(R.id.sInputFrom)
        val spinnerTo = findViewById<Spinner>(R.id.sInputTo)

        spinnerFrom.adapter = arrayAdapter
        spinnerTo.adapter = arrayAdapter

        spinnerFrom.onItemSelectedListener = object : AdapterView
        .OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
               convertFrom = spinnerFrom.selectedItem.toString()
                Log.i("CONVERTER", convertFrom)
                update()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                convertFrom = spinnerTo.selectedItem.toString()
            }
        }
        spinnerTo.onItemSelectedListener = object : AdapterView
        .OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                convertTo = spinnerTo.selectedItem.toString()
                update()
                Log.i("CONVERTER", convertTo)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                convertTo = spinnerTo.selectedItem.toString()
            }
        }
    }
    private fun update(){
        if(inputFrom.isNotEmpty() && isActiveInputFrom){
            val res = (inputFrom.toDouble() * convertibleValues[convertFrom]!!) / (convertibleValues[convertTo]!!)
            inputTo = res.toString()
            output.text = inputTo
            if(res >= 10000000){
                Toast.makeText(this, "Possible loss of accuracy", LENGTH_LONG).show()
            }
        }
    }
    fun onDigit(view: View) {
        if(isActiveInputFrom){
            val res = (input as EditText).selectionStart
            inputFrom = StringBuilder(inputFrom).insert(res, (view as Button).text.toString()).toString()

            input.text = inputFrom
            (input as EditText).setSelection(res + 1)
        }
        if(isActiveInputTo){
            val res = (output as EditText).selectionStart
            inputTo = StringBuilder(inputTo).insert(res, (view as Button).text.toString()).toString()

            output.text = inputTo
            (output as EditText).setSelection(res + 1)
        }
        update()
    }
    fun onDot(view: View) {
        if(isActiveInputFrom && !inputFrom.contains(".")){
            inputFrom = inputFrom.dropWhile { ch -> ch == '0' }

            if (inputFrom.isEmpty()){
                inputFrom = "0"
                input.text = "0"
                (input as EditText).setSelection(inputFrom.length)
            }

            val res = (input as EditText).selectionStart
            inputFrom = StringBuilder(inputFrom).insert(res, (view as Button).text.toString()).toString()

            input.text = inputFrom
            (input as EditText).setSelection(inputFrom.length)
        } else if(isActiveInputFrom) {
            Toast.makeText(this, "Your number is already decimal", LENGTH_SHORT).show()
        }

        if(isActiveInputTo && !inputTo.contains(".")){
            inputTo = inputTo.dropWhile { ch -> ch == '0' }

            if (inputTo.isEmpty()){
                inputTo = "0"
                output.text = "0"
                (output as EditText).setSelection(inputTo.length)
            }

            val res = (output as EditText).selectionStart
            inputTo = StringBuilder(inputTo).insert(res, (view as Button).text.toString()).toString()

            output.text = inputTo
            (output as EditText).setSelection(inputTo.length)

        } else if(isActiveInputTo) {
            Toast.makeText(this, "Your number is already decimal", LENGTH_SHORT).show()
        }
        update()
    }
    fun onClear(view: View) {
        if(isActiveInputFrom){
            inputFrom = ""
            input.text = inputFrom
            inputTo = ""
            output.text = inputTo
        }
        if(isActiveInputTo){
            inputTo = ""
            output.text = inputTo
        }
    }
    fun onDelete(view: View) {
        if(isActiveInputFrom && inputFrom.isNotEmpty()){
            val res = (input as EditText).selectionStart
            Log.i("My_TAG", res.toString()+" ${inputFrom.length}")
            inputFrom = StringBuilder(inputFrom).deleteRange(res - 1, res).toString()
            input.text = inputFrom
            (input as EditText).setSelection(res - 1)
        }
        if(isActiveInputTo && inputTo.isNotEmpty()){
            val res = (output as EditText).selectionStart
            inputTo = StringBuilder(inputTo).deleteRange(res - 1, res).toString()
            output.text = inputTo
            (output as EditText).setSelection(res - 1)
        }
        update()
    }
    fun onSwap(view: View) {
        val res = inputFrom

        inputFrom = inputTo
        inputTo = res

        input.text = inputFrom
        output.text = inputTo

        if(isActiveInputFrom){
            (input as EditText).setSelection(inputFrom.length)
        }
        if(isActiveInputTo){
            (output as EditText).setSelection(inputTo.length)
        }

        update()
    }
}

// TODO: Restriction on digits?
