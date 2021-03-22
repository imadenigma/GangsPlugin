package me.imadenigma.gangsplugin;

import org.bukkit.plugin.java.JavaPlugin;

public final class GangsPlugin extends JavaPlugin {

    private static GangsPlugin singleton;

    @Override
    public void onEnable() {
        // Plugin startup logic
        singleton = this;

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    public static GangsPlugin getSingleton() {
        return singleton;
    }
}
