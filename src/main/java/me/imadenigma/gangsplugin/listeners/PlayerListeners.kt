package me.imadenigma.gangsplugin.listeners

import me.imadenigma.gangsplugin.gangs.GangManager
import me.imadenigma.gangsplugin.user.User
import me.lucko.helper.utils.Log
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerListeners : Listener {

    @EventHandler
    fun onJoinEvent(e: PlayerJoinEvent) {
        val user = User.getFromBukkit(e.player)
        if (user.hasGang()) {
            if (user.gang.isPresent) return
            val gang = GangManager.build().loadGang(user.lastKnownGang)
            gang.members.forEach { member -> member.setPresentGang(gang) }
            Log.info("the first member of the gang joined ! The gang is loaded , name: ${user.lastKnownGang}")
        }
    }

}