/*
package me.imadenigma.gangsplugin.commands;

import co.aikar.commands.*;
import me.imadenigma.gangsplugin.GangsPlugin;
import me.imadenigma.gangsplugin.gangs.Gang;
import me.imadenigma.gangsplugin.gangs.GangManager;
import me.imadenigma.gangsplugin.user.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandManager {
    private final PaperCommandManager commandManager;

    public CommandManager() {
        this.commandManager = new PaperCommandManager(GangsPlugin.getSingleton());
        this.registerContexts();
        this.registerCommands();
    }

    private void registerCommands() {
        this.commandManager.registerCommand(new CreateCommand());
    }

    private void registerContexts() {
        CommandContexts<BukkitCommandExecutionContext> commandContexts = commandManager.getCommandContexts();
        commandContexts.registerIssuerAwareContext(Gang.class, c -> {
            final String tag = c.popFirstArg();
            final CommandSender sender = c.getSender();
            Gang gang = GangManager.getGangs().stream().filter(gang1 -> gang1.getName().equalsIgnoreCase(tag))
                    .findAny()
                    .orElse(null);
            if (gang == null) throw  new InvalidCommandArgument("could not find a gang with than name");

            return gang;
        });

        commandContexts.registerIssuerAwareContext(User.class, c -> {
            final boolean optional = c.isOptional();
            final CommandSender sender = c.getSender();
            final boolean isPlayer = sender instanceof Player;

            if(c.hasFlag("other")) {
                final String name = c.popFirstArg();
                if(name == null) {
                    if(optional) {
                        return null;
                    } else {
                        throw new InvalidCommandArgument();
                    }
                }

                final Player player = ACFBukkitUtil.findPlayerSmart(c.getIssuer(), name);
                if (player == null) {
                    if (optional) {
                        return null;
                    }
                    throw new InvalidCommandArgument(false);
                }
                return User.getFromBukkit(player);
            } else {
                final Player player = isPlayer ? (Player) sender : null;
                if(player == null && !optional) {
                    throw new InvalidCommandArgument("§cThis command is player only", false);
                }

                return User.getFromBukkit(player);
            }
        });

        commandContexts.registerIssuerAwareContext(User.class,c -> {
            final CommandSender sender = c.getSender();
            if (sender instanceof Player) return User.getFromBukkit((Player) sender);
            else return null;
        });
    }
}
*/
