package me.imadenigma.gangsplugin.utils

import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.math.NumberUtils

object MessagesHandler {

    fun handleMessage(msg: String,vararg replacements: Any): String {
        if (msg == null) return ""
        val tore = StringUtils.substringBetween(msg, "{", "}")
        if (tore?.isEmpty() == true) return msg

        var toHandle = msg
        for (toRep in tore) {
            val index = toRep.toInt()
            try {
                val target = replacements[index]
                toHandle = StringUtils.replace(toHandle,"{$index}",target.toString())
            } catch (ignored: ArrayIndexOutOfBoundsException) {}
        }
        return toHandle
    }

    fun handle(string: String?, vararg replacements: Any): String? {
        if (replacements.isEmpty()) return string // no replacements
        val placeholders = StringUtils.substringsBetween(string, "{", "}")
        if (placeholders.isEmpty()) return string // no placeholders
        var handled = string
        for (possibleIndex in placeholders) { // possible match: we need to check if the string inside is an integer
            val index = NumberUtils.toInt(possibleIndex, -1)
            if (index == -1) continue
            try {
                val replacement = replacements[index]
                handled = StringUtils.replace(handled, "{$index}", replacement.toString())
            } catch (ignored: ArrayIndexOutOfBoundsException) {
            }
        }
        return handled
    }

}