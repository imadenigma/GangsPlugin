package me.imadenigma.gangsplugin.user

import me.imadenigma.gangsplugin.gangs.Gang
import me.lucko.helper.Schedulers
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import java.util.concurrent.TimeUnit




class Invite(private val sender: User, private val receiver: User) {

    companion object {
        private const val duration = 30 //means 30 seconds
        val available = mutableMapOf<User,Gang>()

        @JvmStatic
        fun acceptInv(user: User, gang: Gang) {
            user.setGang(gang)
            gang.addMember(user)
        }
    }

    init {
        sendInvite()
        available[receiver] = sender.gang.get()
        Schedulers.sync()
            .runLater({
                      this.destroy()
            }, duration.toLong(),TimeUnit.SECONDS)
    }

    private fun destroy() {
        available.remove(this.receiver)
    }

    private fun sendInvite() {
        this.receiver.msg("${sender.name} has invited you to join the gang &6\"${sender.lastKnownGang}\"")
        val text = TextComponent("Click here to accept or skip it to decline")
        text.color = net.md_5.bungee.api.ChatColor.AQUA
        text.isBold = true
        text.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND,"/accept")

        Bukkit.getPlayer(this.receiver.uniqueID)?.spigot()?.sendMessage(text)
    }
}