package me.imadenigma.gangsplugin;

import me.imadenigma.gangsplugin.gangs.GangManager;
import me.imadenigma.gangsplugin.user.UserManager;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.lucko.helper.plugin.ap.Plugin;
import me.lucko.helper.plugin.ap.PluginDependency;


@Plugin(
        name = "GangsPlugin",
        version = "1.13",
        apiVersion = "1.13",
        depends = @PluginDependency("vault"),
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

    }

    @Override
    public void disable() {
        // Plugin shutdown logic
        this.userManager.saveUsers();
        this.gangManager.saveGangs();
    }


    public static GangsPlugin getSingleton() {
        return singleton;
    }
}
