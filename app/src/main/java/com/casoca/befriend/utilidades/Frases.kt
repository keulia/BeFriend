package com.casoca.befriend.utilidades

import android.util.Log
import kotlin.random.Random


object Frases {
    fun generateRandom(lista:MutableList<String>) = lista[(0 until lista.size).shuffled().first()]
}