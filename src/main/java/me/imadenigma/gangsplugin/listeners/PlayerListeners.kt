package me.imadenigma.gangsplugin.listeners

import me.imadenigma.gangsplugin.gangs.GangManager
import me.imadenigma.gangsplugin.user.User
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerListeners : Listener {

    @EventHandler
    fun onJoinEvent(e: PlayerJoinEvent) {
        val user = User.getFromBukkit(e.player)
        if (user.hasGang()) {
            if (GangManager.getGangs().contains(user.presentGang)) return
            GangManager.build().loadGang(user.gang.get().toString())
        }
    }

}