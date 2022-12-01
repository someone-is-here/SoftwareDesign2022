package com.example.calculator

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.calculator.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var orientation = "h"
    companion object Mode {
        private var isScientific=false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        initEditText()

        val transaction = supportFragmentManager.beginTransaction()

        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.add(R.id.containerWithDigits, DigitsFragment(), "DigitsFragment")

        transaction.commit()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        initButtons()
        Log.i("IsScientific", isScientific.toString())
    }

    private fun initButtons() {
        Log.i("initButtons", isScientific.toString())
        binding.apply {
            btnMode.setOnClickListener{
                Log.i("Orientation", isScientific.toString())
                if(orientation=="h"){
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    orientation="v"
                }else {
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    orientation="h"
                }
            }
            btnPaid.setOnClickListener{
                Log.i("IsScientificChange", isScientific.toString())
                isScientific = !isScientific
                val transaction = supportFragmentManager.beginTransaction()

                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)

                if(isScientific) {
                    transaction.add(
                        R.id.containerWithScientificOperations,
                        OperationsFragment(),
                        "OperationsFragment"
                    )
                } else {
                    val fragment = supportFragmentManager.findFragmentById(R.id.containerWithScientificOperations)
                    if (fragment != null) {
                        transaction.remove(fragment)
                    }
                    transaction.addToBackStack(null)
                }
                transaction.commit()
            }
        }
    }

    private fun initEditText() {
        binding.apply {
            forbidActions(tvResult)
            forbidActions(tvSolution)
        }
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
                return false
            }

            override fun onDestroyActionMode(p0: ActionMode?) {}
        }
    }
}