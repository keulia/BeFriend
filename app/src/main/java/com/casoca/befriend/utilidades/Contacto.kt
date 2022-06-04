package com.casoca.befriend.utilidades

data class Contacto(
    var idContact:String?,
    var name:String?,
    var birthday:String?,
    var img:String?,
    var notes:String?,
    var number:String?,
    var temasConvo:Boolean?,
    var time:String?,
    var fechaCrear:String?
    ) {
    constructor():this(null,null,null,null,null,null, null, null, null)

}