package me.imadenigma.gangsplugin.economy

import com.google.common.collect.Lists
import com.google.common.collect.Maps
import me.imadenigma.gangsplugin.gangs.Gang
import me.imadenigma.gangsplugin.gangs.GangManager
import me.lucko.helper.hologram.Hologram
import me.lucko.helper.serialize.Position
import org.bukkit.Location
import java.util.Map

class BalanceTop(private val loc: Location) {
    private val refreshDel = 10;
    private val sortedGangs = Lists.newLinkedList<Gang>()
    private val sortedLines = Lists.newLinkedList<String>()
    private lateinit var hologram: Hologram

    init {


    }

    private fun display() {
        this.hologram = Hologram.create(Position.of(loc), this.sortedLines)
        if (!hologram.isSpawned) {
            this.hologram.spawn()
        }
    }

    private fun reloadCache() {
        this.sortedLines.clear()
        this.sortedGangs.clear()
//        GangManager.getGangsBalance().entries.stream().sorted { a -> Comparator.comparingInt { a.value }}
    }


}