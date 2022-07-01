package com.eundmswlji.tacoling

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesUtil(context: Context) {

    private val sp : SharedPreferences= context.getSharedPreferences("tacoling", Context.MODE_PRIVATE)

    fun getString(key: String, defValue: String): String {
        return sp.getString(key, defValue).toString()
    }

    fun setString(key: String, str: String) {
        sp.edit().putString(key, str).apply()
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return sp.getBoolean(key, defValue)
    }

    fun setBoolean(key: String, boolean: Boolean) {
        sp.edit().putBoolean(key, boolean).apply()
    }

    fun clear() {
        sp.edit().clear().apply()
    }

}
