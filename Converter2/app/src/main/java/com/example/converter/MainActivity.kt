package com.example.converter

import android.content.ClipData
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.ClipboardManager
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

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

    private lateinit var clipboardManager: android.content.ClipboardManager
    lateinit var clipData: ClipData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        bottomNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.currency -> {
                    loadFragment(CurrencyFragment())
                    setChooseMenu(R.array.currency)
                    initInput()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.distance -> {
                    loadFragment(DistanceFragment())
                    setChooseMenu(R.array.distance)
                    initInput()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.weight -> {
                    loadFragment(WeightFragment())
                    setChooseMenu(R.array.weight)
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

        if(savedInstanceState != null){
            bottomNav.selectedItemId = savedInstanceState.getInt("opened_fragment")
        }

        initInput()

        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
    }

    private fun forbidActions(mEditText: TextView){
        mEditText.customSelectionActionModeCallback = object : ActionMode.Callback{
            override fun onCreateActionMode(p0: ActionMode?, p1: Menu?): Boolean {
                return false
            }

            override fun onPrepareActionMode(p0: ActionMode?, p1: Menu?): Boolean {
                return false
            }

            override fun onActionItemClicked(p0: ActionMode?, p1: MenuItem?): Boolean {
                return true
            }

            override fun onDestroyActionMode(p0: ActionMode?) {}
        }
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
                    clipData = ClipData.newPlainText("saved",res)
                    clipboardManager.setPrimaryClip(clipData)
                    Toast.makeText(this, "Copied", LENGTH_SHORT).show()
                }
            }

            findViewById<Button>(R.id.btnPaste1).visibility = View.VISIBLE
            findViewById<Button>(R.id.btnPaste1).setOnClickListener {
                val saved = clipboardManager.primaryClip
                val item = saved?.getItemAt(0)

                inputFrom = item?.text.toString()
                input.text = inputFrom

                isActiveInputFrom = true
                (input as EditText).setSelection(inputFrom.length)
                update()

                Log.i("Paste", item?.text.toString())
            }

            findViewById<Button>(R.id.btnCopy).visibility = View.VISIBLE
            findViewById<Button>(R.id.btnCopy).setOnClickListener {
                val res = findViewById<EditText>(R.id.etOutput).text.toString()
                if(res.isNotEmpty()){
                    clipData = ClipData.newPlainText("saved", res)
                    clipboardManager.setPrimaryClip(clipData)
                    Toast.makeText(this, "Copied", LENGTH_SHORT).show()
                    Log.i("Copy", res)
                }
            }

            findViewById<Button>(R.id.btnPaste).visibility = View.VISIBLE
            findViewById<Button>(R.id.btnPaste).setOnClickListener {
                val saved = clipboardManager.primaryClip
                val item = saved?.getItemAt(0)

                inputTo = item?.text.toString()
                output.text = inputTo

                isActiveInputTo = true
                (output as EditText).setSelection(inputTo.length)

                Log.i("Paste", item?.text.toString())
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
        forbidActions(etInput)
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
        forbidActions(etOutput)
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
        (input as EditText).setSelection(inputFrom.length)

        output.setText(inputTo, TextView.BufferType.EDITABLE)
        (output as EditText).setSelection(inputTo.length)

        profMode = savedInstanceState.getBoolean("profMode")
    }
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putInt("convertFrom", findViewById<Spinner>(R.id.sInputFrom).selectedItemPosition)
        savedInstanceState.putInt("convertTo", findViewById<Spinner>(R.id.sInputTo).selectedItemPosition)

        savedInstanceState.putString("inputFrom", inputFrom)
        savedInstanceState.putString("inputTo", inputTo)

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
            Formatter.BigDecimalLayoutForm.SCIENTIFIC
            val res = (inputFrom.toBigDecimal()) * (convertibleValues[convertFrom]!! / convertibleValues[convertTo]!!).toBigDecimal()
            inputTo = res.toPlainString()
            output.text = inputTo
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
            isActiveInputFrom = true
            update()
            isActiveInputFrom = false
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
        isActiveInputFrom = true
        isActiveInputTo = false

        val res = inputFrom

        inputFrom = inputTo
        inputTo = res

        input.text = inputFrom
        output.text = inputTo

        if (isActiveInputFrom) {
            (input as EditText).setSelection(inputFrom.length)
        }
        if (isActiveInputTo) {
            (output as EditText).setSelection(inputTo.length)
        }

        update()
    }
}