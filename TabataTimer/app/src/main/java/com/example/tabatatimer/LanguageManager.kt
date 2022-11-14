package com.example.tabatatimer

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.core.content.edit
import java.util.*

class LanguageManager(private val context:Context,
                      private var sharedPreferences: SharedPreferences =
                          context.getSharedPreferences("LANG", Context.MODE_PRIVATE)) {
    fun updateResource(code:String){
        val locale = Locale(code)
        Locale.setDefault(locale)
        val resources = context.resources
        val configuration = resources.configuration
        configuration.locale = locale
        resources.updateConfiguration(configuration, resources.displayMetrics)
        setLanguage(code)
    }
    private fun setLanguage(code:String){
        sharedPreferences.edit {
            putString("lang", code)
        }
    }
    fun getLanguage(): String {
        return sharedPreferences.getString("lang", "en").toString()
    }
}

class FontManager(private val context:Context,
                  private var sharedPreferences: SharedPreferences =
    context.getSharedPreferences("FONT", Context.MODE_PRIVATE)) {

    fun updateResource(size: Float){
        val resources = context.resources
        val configuration = resources.configuration
        configuration.fontScale = size
        resources.updateConfiguration(configuration, resources.displayMetrics)
        setFont(size)
    }
    private fun setFont(size: Float){
        sharedPreferences.edit {
            putFloat("font", size)
        }
    }
    fun getFont(): Float {
        return sharedPreferences.getFloat("font", 1.0f)
    }
}

