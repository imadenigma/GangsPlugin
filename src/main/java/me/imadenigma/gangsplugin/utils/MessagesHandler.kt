package me.imadenigma.gangsplugin.utils

import org.apache.commons.lang.StringUtils

object MessagesHandler {

    fun handleMessage(msg: String,vararg replacements: Any): String {
        if (replacements.isEmpty()) return msg
        val tore = StringUtils.substringBetween(msg, "{", "}")
        if (tore.isEmpty()) return msg

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

}