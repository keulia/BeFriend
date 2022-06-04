package com.casoca.befriend.utilidades

data class User(
    var email:String?,
    var idDoc:String?,
    var nombre:String?,
    var frases:MutableList<String>?
){
    constructor():this(null,null,null,null)
}