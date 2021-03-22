package me.imadenigma.gangsplugin.exceptions

import me.imadenigma.gangsplugin.GangsPlugin
import me.lucko.helper.Helper

class VaultNotFound : RuntimeException("No vault dependency found !") {
    init {
        Helper.server().pluginManager.disablePlugin(GangsPlugin.getSingleton())
    }
}