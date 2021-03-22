package me.imadenigma.gangsplugin.economy

interface Economy {

    fun getBalance(): Double

    fun withdrawBalance(value: Long): Double

    fun depositBalance(value: Long): Double
}