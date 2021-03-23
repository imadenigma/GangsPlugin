package me.imadenigma.gangsplugin;

import me.imadenigma.gangsplugin.commands.GangCommands;
import me.imadenigma.gangsplugin.economy.EconomyManager;
import me.imadenigma.gangsplugin.gangs.GangManager;
import me.imadenigma.gangsplugin.listeners.PlayerListeners;
import me.imadenigma.gangsplugin.user.UserManager;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.lucko.helper.plugin.ap.Plugin;
import me.lucko.helper.plugin.ap.PluginDependency;
import me.mattstudios.mf.base.CommandManager;

import java.io.IOException;


@Plugin(
        name = "GangsPlugin",
        version = "1.13",
        apiVersion = "1.13",
        depends = @PluginDependency("Vault"),
        authors = "Johan Liebert"
)
public final class GangsPlugin extends ExtendedJavaPlugin {

    private static GangsPlugin singleton;

    private UserManager userManager;
    private GangManager gangManager;

    @Override
    public void enable() {
        // Plugin startup logic
        singleton = this;
        //Loading users
        this.userManager = new UserManager();
        this.userManager.loadUsers();
        //Create gangManager's instance
        this.gangManager = new GangManager();
        try {
            this.gangManager.loadNames();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            new Configuration();
        } catch (IOException e) {
            e.printStackTrace();
        }

        registerListener(new PlayerListeners());
        new EconomyManager();
        new CommandManager(this).register(new GangCommands());

    }

    @Override
    public void disable() {
        // Plugin shutdown logic
        this.userManager.saveUsers();
        this.gangManager.saveGangs();
        try {
            this.gangManager.saveNames();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static GangsPlugin getSingleton() {
        return singleton;
    }



}
