package me.imadenigma.gangsplugin.commands;

import co.aikar.commands.*;
import me.imadenigma.gangsplugin.GangsPlugin;
import me.imadenigma.gangsplugin.gangs.Gang;
import me.imadenigma.gangsplugin.gangs.GangManager;
import me.lucko.helper.command.context.CommandContext;
import org.bukkit.command.CommandSender;

public class CommandManager {
    private final PaperCommandManager commandManager;

    public CommandManager() {
        this.commandManager = new PaperCommandManager(GangsPlugin.getSingleton());
        this.registerCommands();
        this.registerContexts();
    }

    private void registerCommands() {

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
    }
}
