package me.imadenigma.gangsplugin.utils

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object Utils {
    fun copyContent(source: File, destination: File) {
        val reader = FileInputStream(source)
        val write = FileOutputStream(destination)
        reader.copyTo(write)
    }
}