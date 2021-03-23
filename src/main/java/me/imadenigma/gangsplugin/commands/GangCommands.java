package me.imadenigma.gangsplugin.commands;

import me.imadenigma.gangsplugin.gangs.Gang;
import me.imadenigma.gangsplugin.gangs.GangImpl;
import me.imadenigma.gangsplugin.user.Invite;
import me.imadenigma.gangsplugin.user.Rank;
import me.imadenigma.gangsplugin.user.User;
import me.lucko.helper.Commands;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.annotations.WrongUsage;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.entity.Player;

@Command("gang")
public class GangCommands extends CommandBase {

    public GangCommands() {
        me.lucko.helper.Commands.create().assertPlayer().handler(c -> {
            User user = User.getFromBukkit(c.sender());
            if (Invite.Companion.getAvailable().get(user) != null ) {
                Invite.acceptInv(user,Invite.Companion.getAvailable().get(user));
            }
        }).register("accept");
    }

    @SubCommand("create") @Permission("gang.create")
    @WrongUsage("&c/gang &3create &c<name>")
    public void create(final Player player, final String name) {
        User user = User.getFromBukkit(player);
        if (user.hasGang()) {
            user.msgC("user","create","failed");
            return;
        }
        Gang gang = new GangImpl(name,user);
        gang.addMember(user);
        user.setGang(gang);

    }

    @SubCommand("invite") @Permission("gang.invite")
    @WrongUsage("&c/gang &3invite &c<player>")
    public void invite(final Player player, final Player target) {
        User user = User.getFromBukkit(player);
        User vtarget = User.getFromBukkit(target);
        if (user.getRank() == Rank.MEMBER ) return;
        new Invite(user,vtarget);
    }



}
