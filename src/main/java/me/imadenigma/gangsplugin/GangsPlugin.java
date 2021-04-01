package me.imadenigma.gangsplugin;

import me.imadenigma.gangsplugin.commands.GangCommands;
import me.imadenigma.gangsplugin.economy.Baltop;
import me.imadenigma.gangsplugin.economy.EconomyManager;
import me.imadenigma.gangsplugin.gangs.Gang;
import me.imadenigma.gangsplugin.gangs.GangManager;
import me.imadenigma.gangsplugin.listeners.PlayerListeners;
import me.imadenigma.gangsplugin.user.User;
import me.imadenigma.gangsplugin.user.UserManager;
import me.imadenigma.gangsplugin.utils.PAPI;
import me.lucko.helper.Helper;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.lucko.helper.plugin.ap.Plugin;
import me.lucko.helper.plugin.ap.PluginDependency;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import java.io.FileNotFoundException;
import java.io.IOException;

@Plugin(
        name = "GangsPlugin",
        version = "1.13",
        apiVersion = "1.13",
        depends = {
                @PluginDependency("Vault"),
                @PluginDependency(value = "Essentials"),
                @PluginDependency(value = "HolographicDisplays",soft = true),
                @PluginDependency(value = "PlaceholderAPI",soft = true)
        },
        authors = "Johan Liebert")
public final class GangsPlugin extends ExtendedJavaPlugin {

    private static GangsPlugin singleton;

    private UserManager userManager;
    private GangManager gangManager;
    private Baltop balanceTop;

    @Override
    public void enable() {
        // Plugin startup logic
        singleton = this;
        new EconomyManager();
        // Create gangManager's instance
        try {
            this.gangManager = new GangManager();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Loading users
        this.userManager = new UserManager();
        this.userManager.loadUsers();
        try {

            new Configuration();
        } catch (IOException e) {
            e.printStackTrace();
        }
        new GangCommands();
        registerListener(new PlayerListeners());
        createHolo();
        reload();
        new PAPI().register();
    }

    @Override
    public void disable() {
        // Plugin shutdown logic
        this.userManager.saveUsers();
        this.gangManager.saveGangs();
        this.balanceTop.getHologram().isDeleted();
    }

    public static GangsPlugin getSingleton() {
        return singleton;
    }

    public void createHolo() {
        final ConfigurationNode node = Configuration.getConfig().getNode("holo-location");
        final int x = node.getNode("x").getInt(0);
        final int y = node.getNode("y").getInt(0);
        final int z = node.getNode("z").getInt(0);
        final World world = Helper.worldNullable(node.getNode("world").getString("world"));
        this.balanceTop =
                new Baltop(
                        new Location(world, x, y, z),
                        node.getParent().getNode("holo-format").getString("null"));
    }

    public void reload() {
        for (Player player : Helper.server().getOnlinePlayers()) {
            User user = User.getFromBukkit(player);
            if (user.hasGang()) {
                if (user.getGang().isPresent()) continue;
                try {
                    Gang.getByName(user.getLastKnownGang());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
