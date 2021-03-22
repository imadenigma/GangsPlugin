package me.imadenigma.gangsplugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.imadenigma.gangsplugin.gangs.Gang;
import me.imadenigma.gangsplugin.gangs.GangImpl;
import me.imadenigma.gangsplugin.user.User;


@CommandAlias("gang")
public class CreateCommand extends BaseCommand {

    @Subcommand("create") @CommandPermission("gangs.create")
    @Syntax("/gang create <name>")
    public void createCommand(final User user, final String name) {
        if (user.hasGang()) {
            user.msgC("user","create","failed");
            return;
        }
        Gang gang = new GangImpl(name,user);
        gang.addMember(user);
        user.setGang(gang);

    }

}
