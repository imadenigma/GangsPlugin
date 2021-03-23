package me.imadenigma.gangsplugin.utils

interface Messenger {

    fun msg(msg: String) //A normal Message

    fun msgC(vararg path: String) //a message from configFile

    fun msgH(msg: String, vararg replacements: Any) //message that contains placeholders

    fun msgCH(vararg path: String, replacements: Array<Any>)


}