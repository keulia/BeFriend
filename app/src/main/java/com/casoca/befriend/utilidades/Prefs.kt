package com.casoca.befriend.utilidades

import android.content.Context

class Prefs(val context:Context) {
    val SharedName = "recuerdame"
    val SharedRememberMe = "recordar"
    val SharedUsername = "usuario"
    val SharedPass = "pass"
    val Storage = context.getSharedPreferences(SharedName,0)

    fun saveRecordar(name:Boolean) {
        Storage.edit().putBoolean(SharedRememberMe,name).apply()
    }

    fun getRecordar():Boolean{
        return Storage.getBoolean(SharedRememberMe,false)
    }

    fun saveUser(user:String){
        Storage.edit().putString(SharedUsername, user).apply()
    }

    fun getUser():String{
        return Storage.getString(SharedUsername,"")?:""
    }

    fun savePass(pass:String){
        Storage.edit().putString(SharedPass, pass).apply()
    }

    fun getPass():String{
        return Storage.getString(SharedPass,"")?:""
    }

}