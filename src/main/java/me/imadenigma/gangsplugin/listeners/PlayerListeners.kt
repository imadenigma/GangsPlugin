package me.imadenigma.gangsplugin.listeners

import me.imadenigma.gangsplugin.gangs.Gang
import me.imadenigma.gangsplugin.user.User
import me.lucko.helper.utils.Log
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import java.util.stream.Collectors
import kotlin.streams.toList

class PlayerListeners : Listener {

    @EventHandler
    fun onJoinEvent(e: PlayerJoinEvent) {
        val user = User.getFromBukkit(e.player)
        if (user.hasGang()) {
            if (user.gang.isPresent) return
            Gang.getByName(user.lastKnownGang)
            user.msg("&7your gang is loaded !")
            Log.info("the first member of the gang joined ! The gang is loaded , name: ${user.lastKnownGang}")
        }
    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        val user = User.getFromBukkit(e.player)
        if (user.hasGang()) {
            user.gang.get().increaseMinedBlocks()
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun textingEvent(e: AsyncPlayerChatEvent) {
        val user = User.getFromBukkit(e.player)
        if (!user.hasGang()) return
        if (!user.isChatEnabled) return
        e.recipients.clear()
        e.recipients.addAll(
            user.gang.get().members.stream()
                .filter(User::isChatEnabled)
                .filter { user -> user.offlinePlayer.isOnline }
                .map { user -> user.offlinePlayer.player }
                .collect(Collectors.toSet())
        )
    }

}