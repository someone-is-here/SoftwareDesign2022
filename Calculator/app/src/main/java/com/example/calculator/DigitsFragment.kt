package com.example.calculator

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.calculator.databinding.FragmentDigitsBinding
import com.fathzer.soft.javaluator.DoubleEvaluator
import com.google.android.material.button.MaterialButton
import java.lang.Error
import java.math.BigDecimal
import kotlin.reflect.typeOf
import java.lang.Math
import java.math.RoundingMode
import kotlin.math.exp
import kotlin.math.sqrt

//import com.fathzer.soft.javaluator


class DigitsFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentDigitsBinding

    private lateinit var tvResult: EditText
    private lateinit var tvSolution: EditText
    private val evaluator = DoubleEvaluator()

    private var stringWithExpression = ""
    private val listWithConstantsFour:List<String> = listOf("log(", "sin(","cos(", "tan(")
    private val listWithConstantsFive:List<String> = listOf("^(-1)", "sinh(","cosh(", "tanh(")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        tvResult = requireActivity().findViewById<EditText>(R.id.tvResult)
        tvSolution = requireActivity().findViewById<EditText>(R.id.tvSolution)
        tvResult.showSoftInputOnFocus = false
        tvSolution.showSoftInputOnFocus = false
        tvResult.isLongClickable = false
        binding = FragmentDigitsBinding.inflate(inflater, container, false)

        initButtons()
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

        return binding.root
    }

    private val listener = View.OnClickListener {
        stringWithExpression = tvResult.text.toString()
        val i = tvResult.selectionStart
        if(i > 0 &&
            (stringWithExpression[i - 1] == ')' ||
                    stringWithExpression[i - 1].isLetterOrDigit() ||
            stringWithExpression[i - 1] == '!')){
            addToExpression((it as MaterialButton).text)
        } else if((it as MaterialButton).text == "-"){
            addToExpression((it as MaterialButton).text)
        }
    }

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
                Log.i("result", expression)
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
                //val index_right = result.lastIndexOf(')', index_left)
                Log.i("result", index_left.toString()+" "+index_right.toString())
                val subRes = result.subSequence(index_left + 2,
                    index_right)
                Log.i("result", subRes.toString())
                result = result.removeRange(index_left, index_right+1)
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
            Log.i("Result", expression)
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

    private fun initDigitsButtons(){
        binding.apply {
            btnOne.setOnClickListener(this@DigitsFragment)
            btnTwo.setOnClickListener(this@DigitsFragment)
            btnThree.setOnClickListener(this@DigitsFragment)
            btnFour.setOnClickListener(this@DigitsFragment)
            btnFive.setOnClickListener(this@DigitsFragment)
            btnSix.setOnClickListener(this@DigitsFragment)
            btnSeven.setOnClickListener(this@DigitsFragment)
            btnEight.setOnClickListener(this@DigitsFragment)
            btnNine.setOnClickListener(this@DigitsFragment)
            btnZero.setOnClickListener(this@DigitsFragment)
        }
    }
    private fun initClearButtons(){
        binding.apply {
            btnAC.setOnClickListener {
                stringWithExpression = ""
                tvResult.setText(stringWithExpression)
                tvResult.setSelection(0)
                tvSolution.setText("")
            }
            btnClear.setOnClickListener{
                stringWithExpression = tvResult.text.toString()
                val res = tvResult.selectionStart
                Log.i("MYTAG", res.toString())
                if(res != 0){
                    var removed = false
                    if(res >= 7 && stringWithExpression.subSequence(res - 7, res) == "random("){
                        Log.i("MYTAG", stringWithExpression.subSequence(res - 7, res).toString())
                        stringWithExpression =
                            StringBuilder(stringWithExpression).deleteRange(res - 7, res).toString()
                        tvResult.setText(stringWithExpression)
                        if(res - 7 >= 0) {
                            tvResult.setSelection(res - 7)
                        }
                        removed = true
                    }
                    if(!removed && res >= 5 && listWithConstantsFive.contains(stringWithExpression.subSequence(res - 5, res).toString())){
                        Log.i("MYTAG", stringWithExpression.subSequence(res - 5, res).toString())
                        stringWithExpression =
                            StringBuilder(stringWithExpression).deleteRange(res - 5, res).toString()
                        tvResult.setText(stringWithExpression)
                        if(res - 5 >= 0) {
                            tvResult.setSelection(res - 5)
                        }
                        removed = true
                    }
                    if(!removed && res >= 4 && listWithConstantsFour.contains(stringWithExpression.subSequence(res - 4, res).toString())){
                        Log.i("MYTAG", stringWithExpression.subSequence(res - 4, res).toString())
                        stringWithExpression =
                                StringBuilder(stringWithExpression).deleteRange(res - 4, res).toString()
                            tvResult.setText(stringWithExpression)
                        if(res - 4 >= 0) {
                            tvResult.setSelection(res - 4)
                        }
                        removed = true
                    }

                    if(!removed && res >= 3 && stringWithExpression.subSequence(res - 3,res).toString() == "ln("){
                        Log.i("MYTAG", stringWithExpression.subSequence(res - 3, res).toString())
                        stringWithExpression =
                            StringBuilder(stringWithExpression).deleteRange(res - 3, res).toString()
                        tvResult.setText(stringWithExpression)
                        if(res - 3 >= 0) {
                            tvResult.setSelection(res - 3)
                        }
                        removed = true
                    }
                    if(!removed){
                        stringWithExpression =
                            StringBuilder(stringWithExpression).deleteRange(res - 1, res).toString()
                        tvResult.setText(stringWithExpression)
                        if(res - 1 >= 0) {
                            tvResult.setSelection(res - 1)
                        }
                    }
                    if(stringWithExpression.isEmpty()){
                        tvSolution.setText("")
                    }
                    calculateResult()
                }
            }
        }
    }
    private fun initDotButton(){
        binding.apply {
            btnDot.setOnClickListener{
                var res = tvResult.selectionStart
                Log.i("MYTAG", res.toString())
                Log.i("MYTAG", (res-1).toString())
                stringWithExpression = tvResult.text.toString()
                if(stringWithExpression.isNotEmpty() && res - 1 >= 0 &&
                    stringWithExpression[res - 1].isDigit()){

                    var check = false

                    var i = res - 1

                    while(i > 0 && stringWithExpression[i].isDigit()){
                        i--
//                        Log.i("MYTAG", i.toString())
                    }

                    if(stringWithExpression[i] == '.'){
                        check = true
                    }
                    res -= 1
//                    Log.i("MYTAG", res.toString()+"lp")
                    while(res >= 0 && res + 1 != stringWithExpression.length && stringWithExpression[res].isDigit()){
                        res += 1
//                        Log.i("MYTAG", res.toString()+"res")
                    }
                    if(stringWithExpression[res] == '.'){
                        check = true
                    }

                    if(!check){
                        addToExpression(".")
                    }

                }
            }
        }
    }
    private fun initOperationsButtons(){
        binding.apply {
            btnPlus.setOnClickListener(listener)
            btnMinus.setOnClickListener(listener)
            btnMultiply.setOnClickListener(listener)
            btnDivide.setOnClickListener(listener)
        }
    }
    private fun initBracketsButtons(){
        binding.apply {
            btnOpenBracket.setOnClickListener(this@DigitsFragment)
            btnCloseBracket.setOnClickListener(this@DigitsFragment)
        }
    }
    private fun initButtons() {
        initDigitsButtons()
        initClearButtons()
        initDotButton()
        initOperationsButtons()
        initBracketsButtons()
        binding.btnEquals.setOnClickListener{
            val res = tvSolution.text.toString()
            if(res != "Error" && res != "Infinity") {
                tvResult.setText(res)
                tvSolution.setText("")
                tvResult.setSelection(tvResult.selectionStart)
            }
        }
    }

    override fun onClick(view: View?) {
        binding.apply {
            addToExpression((view as MaterialButton).text)
        }
    }
    private fun addToExpression(text: CharSequence){
        val res = tvResult.selectionStart
        stringWithExpression = tvResult.text.toString()
        stringWithExpression = StringBuilder(stringWithExpression).insert(res, text).toString()
        tvResult.setText(stringWithExpression)
        tvResult.setSelection(res + 1)
        calculateResult()
    }
}