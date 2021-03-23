package me.imadenigma.gangsplugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.imadenigma.gangsplugin.gangs.Gang;
import me.imadenigma.gangsplugin.gangs.GangImpl;
import me.imadenigma.gangsplugin.user.User;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.annotations.WrongUsage;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.entity.Player;

@Command("gang")
public class CreateCommand extends CommandBase {

    @SubCommand("create") @Permission("gang.create")
    @WrongUsage("&c/gang create <name>")
    public void create(final Player player,final String name) {
        User user = User.getFromBukkit(player);
        if (user.hasGang()) {
            user.msgC("user","create","failed");
            return;
        }
        Gang gang = new GangImpl(name,user);
        gang.addMember(user);
        user.setGang(gang);

    }

}
