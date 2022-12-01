package com.example.calculator

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Selection.setSelection
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.calculator.databinding.FragmentOperationsBinding
import com.fathzer.soft.javaluator.DoubleEvaluator
import java.math.BigDecimal
import kotlin.math.exp
import kotlin.math.sqrt

class OperationsFragment : Fragment() {
    private lateinit var binding:FragmentOperationsBinding

    private lateinit var tvResult: EditText
    private lateinit var tvSolution: EditText
    private val evaluator = DoubleEvaluator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        tvResult = requireActivity().findViewById<EditText>(R.id.tvResult)
        tvSolution = requireActivity().findViewById<EditText>(R.id.tvSolution)
        tvResult.showSoftInputOnFocus = false
        tvSolution.showSoftInputOnFocus = false
        tvResult.setOnClickListener{
            var i = tvResult.selectionStart - 1
            val string = tvResult.text
            if(i >= 0 && (string[i].isLetter() || string[i] == '(')){
                Log.i("CHeck", string[i].toString())
                while( i + 1 < string.length && string[i].isLetter()){
                    Log.i("CHeck", string[i].toString())
                    i++
                }
                if(string[i] == '(') {
                    Log.i("HERE", string[i].toString())
                    tvResult.setSelection(i+1)
                } else {
                    Log.i("HET", string[i].toString())
                    tvResult.setSelection(i)
                }
            }
        }
        tvResult.isLongClickable = false
        binding = FragmentOperationsBinding.inflate(inflater, container, false)

        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            initClickListenersChange()
            initButtons()
        }

        return binding.root
    }

    private fun initButtons() {
        initButtonsPow()
        initButtonsConstants()
        initButtonsLog()
        initTrigonometricButtons()
        initButtonRandom()
        initRootButton()
        initFactorialButton()
    }

    private fun initFactorialButton() {
        binding.apply {
            btnFactorial!!.setOnClickListener{
                val expression = tvResult.text.toString()
                if((expression.isNotEmpty() && (expression[expression.length-1].isDigit() ||
                            expression[expression.length-1] == ')'))){
                    addToExpression("!",1)
                }
            }
        }
    }

    private fun initRootButton() {
        binding.apply {
            btnRoot!!.setOnClickListener{
                val expression = tvResult.text.toString()
                if((expression.isNotEmpty() && (!expression[expression.length-1].isLetterOrDigit() ||
                            expression[expression.length-1] == '(') || tvResult.selectionStart == 0) || expression.isEmpty()){
                    addToExpression("√")
                }
            }
        }
    }

    private fun initButtonRandom() {
        binding.apply {
            btnRand!!.setOnClickListener{
                val expression = tvResult.text.toString()
                if((expression.isNotEmpty() && (!expression[expression.length-1].isLetterOrDigit() ||
                            expression[expression.length-1] == '(') || tvResult.selectionStart == 0) || expression.isEmpty()){
                    addToExpression("random()", 8)
                }
            }
        }
    }
    private fun initTrigonometricButtons() {
        binding.apply {
            btnSin!!.setOnClickListener {
                val expression = tvResult.text.toString()
                if ((expression.isNotEmpty() && (!expression[expression.length - 1].isLetterOrDigit() ||
                            expression[expression.length - 1] == '(') || tvResult.selectionStart == 0) || expression.isEmpty()
                ) {
                    addToExpression("sin(", 4)
                }
            }
            btnCos!!.setOnClickListener {
                val expression = tvResult.text.toString()
                if ((expression.isNotEmpty() && (!expression[expression.length - 1].isLetterOrDigit() ||
                            expression[expression.length - 1] == '(') || tvResult.selectionStart == 0) || expression.isEmpty()
                ) {
                    addToExpression("cos(", 4)
                }
            }
            btnTan!!.setOnClickListener {
                val expression = tvResult.text.toString()
                if ((expression.isNotEmpty() && (!expression[expression.length - 1].isLetterOrDigit() ||
                            expression[expression.length - 1] == '(') || tvResult.selectionStart == 0) || expression.isEmpty()
                ) {
                    addToExpression("tan(", 4)
                }
            }
            btnSinH!!.setOnClickListener {
                val expression = tvResult.text.toString()
                if ((expression.isNotEmpty() && (!expression[expression.length - 1].isLetterOrDigit() ||
                            expression[expression.length - 1] == '(') || tvResult.selectionStart == 0) || expression.isEmpty()
                ) {
                    addToExpression("sinh(", 5)
                }
            }
            btnCosH!!.setOnClickListener {
                val expression = tvResult.text.toString()
                if ((expression.isNotEmpty() && (!expression[expression.length - 1].isLetterOrDigit() ||
                            expression[expression.length - 1] == '(') || tvResult.selectionStart == 0) || expression.isEmpty()
                ) {
                    addToExpression("cosh(", 5)
                }
            }
            btnTanH!!.setOnClickListener {
                val expression = tvResult.text.toString()
                if ((expression.isNotEmpty() && (!expression[expression.length - 1].isLetterOrDigit() ||
                            expression[expression.length - 1] == '(') || tvResult.selectionStart == 0) || expression.isEmpty()
                ) {
                    addToExpression("tanh(", 5)
                }
            }
        }
    }
    private fun initButtonsLog() {
        binding.apply {
            btnLg!!.setOnClickListener{
                val expression = tvResult.text.toString()
                if((expression.isNotEmpty() && (!expression[expression.length-1].isLetterOrDigit() ||
                            expression[expression.length-1] == '(') || tvResult.selectionStart == 0) || expression.isEmpty()){
                    addToExpression("log(",4)
                }
            }
            btnLn!!.setOnClickListener{
                val expression = tvResult.text.toString()
                if((expression.isNotEmpty() && (!expression[expression.length-1].isLetterOrDigit() ||
                            expression[expression.length-1] == '(') || tvResult.selectionStart == 0) || expression.isEmpty()) {
                    addToExpression("ln(", 3)
                }
            }
        }
    }
    private fun initButtonsConstants() {
        binding.apply {
            btnPi!!.setOnClickListener{
                val expression = tvResult.text.toString()
                if(expression.isNotEmpty() && !expression[expression.length - 1].isDigit() &&
                    (expression[expression.length - 1] != ')' ||
                            expression[expression.length - 1] != '.')){
                        addToExpression("pi",2)
                    }else if(expression.isEmpty()){
                        addToExpression("pi",2)
                    }
            }
            btnExp!!.setOnClickListener{
                val expression = tvResult.text.toString()
                if(expression.isNotEmpty() && !expression[expression.length - 1].isDigit() &&
                    (expression[expression.length - 1] != ')' ||
                            expression[expression.length - 1] != '.')){
                    addToExpression("e")
                }else if (expression.isEmpty()){
                    addToExpression("e")
                }
            }
        }
    }
    private fun initButtonsPow() {
        binding.apply {
            btnPow!!.setOnClickListener{
                val expression = tvResult.text.toString()
                if((expression.isNotEmpty() && (expression[expression.length-1].isLetterOrDigit() ||
                            expression[expression.length-1] == ')')) || expression.isEmpty()){
                    addToExpression("^")
                }
            }
            btnPow10!!.setOnClickListener{
                val expression = tvResult.text.toString()
                if(expression.isNotEmpty() && ((!expression[expression.length-1].isLetterOrDigit() &&
                            expression[expression.length-1] != ')')
                            || expression[expression.length-1] == '(')
                    || tvResult.selectionStart == 0
                    || expression.isEmpty()){
                    addToExpression("10^", 3)
                } else {
                    addToExpression("*10^",4)
                }
            }
            btnSquare!!.setOnClickListener{
                val expression = tvResult.text.toString()
                if((expression.isNotEmpty() && (expression[expression.length-1].isLetterOrDigit() ||
                            expression[expression.length-1] == ')')) || expression.isEmpty()){
                    addToExpression("^2", 2)
                }
            }
            btnThirdPow!!.setOnClickListener{
                val expression = tvResult.text.toString()
                if((expression.isNotEmpty() && (expression[expression.length-1].isLetterOrDigit() ||
                            expression[expression.length-1] == ')')) || expression.isEmpty()){
                    addToExpression("^3", 2)
                }
            }
            btnPowExp!!.setOnClickListener{
                val expression = tvResult.text.toString()
                if((expression.isNotEmpty() && (!expression[expression.length-1].isLetterOrDigit() ||
                                expression[expression.length-1] == '(') || tvResult.selectionStart == 0) || expression.isEmpty()){
                    addToExpression("e^", 2)
                }
            }
            btn2Pow!!.setOnClickListener{
                val expression = tvResult.text.toString()
                if((expression.isNotEmpty() && (!expression[expression.length-1].isLetterOrDigit() ||
                            expression[expression.length-1] == '(') || tvResult.selectionStart == 0) || expression.isEmpty()){
                    addToExpression("2^", 2)
                }
            }
            btnInverse!!.setOnClickListener{
                val expression = tvResult.text.toString()
                if((expression.isNotEmpty() && (expression[expression.length-1].isLetterOrDigit() ||
                            expression[expression.length-1] == ')'))){
                    addToExpression("^(-1)", 5)
                }
            }
        }
    }
    private fun addToExpression(text: CharSequence, moveCursor: Int = 1){
        val res = tvResult.selectionStart
        var stringWithExpression = tvResult.text.toString()
        stringWithExpression = StringBuilder(stringWithExpression).insert(res, text).toString()
        tvResult.setText(stringWithExpression)
        tvResult.setSelection(res + moveCursor)
        calculateResult()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun calculateResult() {
        var expression = tvResult.text.toString()
        if(expression.contains("!")) {
            expression = calculateFactorial(expression)
        }
        if(expression.contains("√")) {
            expression = calculateRoot(expression)
        }
        try {
            Log.i("Result", expression)
            if(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    expression.chars().allMatch { Character.isDigit(it) }
                } else {
                    Log.i("Wrong","SDK")
                    false
                }
            ){
                tvSolution.setText(expression.toBigDecimal().toPlainString())
            }else {
                val result = evaluator.evaluate(expression)
                Log.i("Result00", result.toString().toBigDecimal().toPlainString())
                Log.i("Result001", result.toString())
                tvSolution.setText(result.toBigDecimal().toPlainString())
            }
        }catch (e: IllegalArgumentException){
            if(e.message == "Parentheses mismatched"){
                Toast.makeText(binding.root.context, e.message.toString(), Toast.LENGTH_SHORT).show()
            }
            Log.i("MYTAG", e.message.toString())
            Log.i("MYTAG", e.cause.toString())
            Log.i("MYTAG", e.javaClass.kotlin.simpleName.toString())
            tvSolution.setText("Error")
        }
    }

    fun Double.round(decimals: Int = 2): Double = "%.${decimals}f".format(this).toDouble()
    private fun checkMismatch(expression:String):Boolean{
        var brackets = 0
        for(ch in expression){
            if(ch == '('){
                brackets += 1
            }
            if(ch == ')'){
                brackets -= 1
            }
        }
        if(brackets == 0){
            return false
        }
        return true
    }
    private fun calculateRoot(expression: String): String {
        var result = expression
        while(result.contains("√")){
            while (!checkMismatch(expression) && result.contains("√(") && result.contains(")")){
                Log.i("result", expression.toString())
                val index_left = result.indexOf("√(")
                var index_copy=index_left
                var bracketCounter = 1

                while(index_copy + 1 < result.length){
                    index_copy += 1
                    if(result[index_copy]=='('){
                        bracketCounter+=1
                    }
                    if(result[index_copy]==')'){
                        bracketCounter-=1
                    }
                    if(bracketCounter==0){
                        break
                    }
                }
                val index_right = index_copy
                Log.i("result", index_left.toString()+" "+index_right.toString())
                val subRes = result.subSequence(index_left + 2,
                    index_right)
                Log.i("result", subRes.toString())
                result = result.removeRange(index_left, index_right + 1)
                Log.i("result", result)
                Log.i("sub", subRes.toString())
                var subResCalc = calculateSubString(subRes.toString())

                if(subResCalc ==  "Error"){
                    result = "Error"
                }else {
                    Log.i("subResCalc1", Math.round(subResCalc.toDouble()).toString())

                    result = StringBuilder(result).insert(
                        index_left,
                        sqrt(subResCalc.toDouble()).toString()
                    ).toString()
                    Log.i("subResCalc13", result)
                }
            }
            if(!result.contains("√")) {
                return result
            }

            val index = result.indexOf("√")
            result = result.removeRange(index, index+1)
            Log.i("SQRT", result)

            if(index >= 0 && index<result.length && result[index].isDigit()){
                var i = index
                while(i+1 < result.length && result[i+1].isDigit()){
                    i++
                }
                val stringWithValue = sqrt(result.subSequence(index, i+1).toString().toDouble())
                    .toString()

                result = result.removeRange(index, i+1)
                result = StringBuilder(result).insert(index, stringWithValue).toString()
            }
        }
        return result
    }
    private fun calculateFactorial(expression: String):String {
        var result = expression
        while(result.contains("!")){
            while (!checkMismatch(expression) && result.contains('(') && result.contains(")!")){
                val index_right = result.indexOf(")!")
                val index_left = result.lastIndexOf('(', index_right)
                val subRes = result.subSequence(index_left + 1,
                    index_right)
                result = result.removeRange(index_left, index_right+2)
                Log.i("result", result)
                Log.i("sub", subRes.toString())
                var subResCalc = calculateSubString(subRes.toString())
                if(subResCalc ==  "Error"){
                    result="Error"
                }else {
                    Log.i("subResCalc1", Math.round(subResCalc.toDouble()).toString())

                    result = StringBuilder(result).insert(
                        index_left,
                        factorial(Math.round(subResCalc.toDouble()).toInt()).toString()
                    ).toString()
                    Log.i("subResCalc13", result)
                }
            }
            if(!result.contains("!")) {
                return result
            }
            val index = result.lastIndexOf("!") - 1
            Log.i("FACT2", result)
            result = result.removeRange(index + 1, index + 2)
            if(result[index].isDigit()){
                var i = index
                while(i-1 >= 0 && result[i-1].isDigit()){
                    i--
                }
                val stringWithValue:String = if(result.subSequence(i, index + 1).toString().toInt()<=200) {
                    factorial((result.subSequence(i, index + 1).toString().toInt())).toString()
                }else{
                    "Error"
                }
                result = result.removeRange(i, index+1)
                result = StringBuilder(result).insert(i, stringWithValue).toString()
                Log.i("FACTORIAL", result)
            }
        }
        return result
    }
    private fun calculateSubString(string: String): String{
        var expression = string
        if(expression.contains("!")) {
            expression = calculateFactorial(expression)
        }
        if(expression.contains("√")) {
            expression = calculateRoot(expression)
        }
        var result:String
        try {
            result = evaluator.evaluate(expression).toString()
            Log.i("resCalcSub", result)
            // tvSolution.setText(result.toString())
        }catch (e: IllegalArgumentException){
            if(e.message == "Parentheses mismatched"){
                Toast.makeText(binding.root.context, e.message.toString(), Toast.LENGTH_SHORT).show()
            }
            Log.i("MYTAG", e.message.toString())
            Log.i("MYTAG", e.cause.toString())
            Log.i("MYTAG", e.javaClass.kotlin.simpleName.toString())
            result = "Error"
        }
        return result
    }
    private fun factorial(num: Int): BigDecimal{
        var factorialRes = BigDecimal(1)
        for (i in 1..num) {
            factorialRes *= i.toBigDecimal()
        }
        return factorialRes
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun initClickListenersChange(){
        binding.apply {
            btnPow!!.text = Html.fromHtml("y<sup>x</sup>", Html.FROM_HTML_MODE_LEGACY)
            btnSquare!!.text = Html.fromHtml("x<sup>2</sup>", Html.FROM_HTML_MODE_LEGACY)
            btnThirdPow!!.text = Html.fromHtml("x<sup>3</sup>", Html.FROM_HTML_MODE_LEGACY)
            btnPow10!!.text = Html.fromHtml("10<sup>x</sup>", Html.FROM_HTML_MODE_LEGACY)
            btnPowExp!!.text = Html.fromHtml("e<sup>x</sup>", Html.FROM_HTML_MODE_LEGACY)
            btn2Pow!!.text = Html.fromHtml("2<sup>x</sup>", Html.FROM_HTML_MODE_LEGACY)
        }
    }
}