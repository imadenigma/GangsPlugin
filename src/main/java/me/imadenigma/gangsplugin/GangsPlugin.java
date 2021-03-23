package me.imadenigma.gangsplugin;

import me.imadenigma.gangsplugin.commands.CreateCommand;
import me.imadenigma.gangsplugin.economy.EconomyManager;
import me.imadenigma.gangsplugin.gangs.GangManager;
import me.imadenigma.gangsplugin.listeners.PlayerListeners;
import me.imadenigma.gangsplugin.user.UserImpl;
import me.imadenigma.gangsplugin.user.UserManager;
import me.lucko.helper.Commands;
import me.lucko.helper.Helper;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.lucko.helper.plugin.ap.Plugin;
import me.lucko.helper.plugin.ap.PluginDependency;
import org.bukkit.Bukkit;
import org.bukkit.command.defaults.BukkitCommand;

import java.io.FileNotFoundException;
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
    private Configuration configuration;

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
            this.configuration = new Configuration();
        } catch (IOException e) {
            e.printStackTrace();
        }

        registerListener(new PlayerListeners());
        new EconomyManager();
        new me.mattstudios.mf.base.CommandManager(this).register(new CreateCommand());
        Commands.create().handler(c -> {
            new UserImpl("7assan",null, Bukkit.getOfflinePlayer("JohanLiebert1"));
        }).register("hassan");

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
