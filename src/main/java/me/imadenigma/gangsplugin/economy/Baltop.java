package me.imadenigma.gangsplugin.economy;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.imadenigma.gangsplugin.gangs.Gang;
import me.imadenigma.gangsplugin.gangs.GangManager;
import me.imadenigma.gangsplugin.utils.MessagesHandler;
import me.lucko.helper.Helper;
import me.lucko.helper.Schedulers;
import org.bukkit.Location;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

public class Baltop {
    public static Baltop INSTANCE;
    private final Location location;
    private static final int duration = 10;
    private final Hologram hologram;
    private final String format;

    public Baltop(final Location location, final String format) {
        this.location = location;
        this.format = format;
        this.hologram = HologramsAPI.createHologram(Helper.hostPlugin(),location);
        Schedulers.sync()
                .runRepeating(this::reloadCache, duration, TimeUnit.SECONDS, duration, TimeUnit.SECONDS);
        INSTANCE = this;
    }


    private void reloadCache() {
        this.hologram.clearLines();
        this.hologram.appendTextLine("§8§m----------------------------------------");
        GangManager.getGangs().stream()
                .sorted(Comparator.comparingLong(Gang::getBalance).reversed())
                .forEachOrdered(
                        gang ->
                                this.hologram.appendTextLine(
                                        MessagesHandler.INSTANCE.handle(
                                                this.format, gang.getName(), gang.getBalance())));

        this.hologram.appendTextLine("§8§m----------------------------------------");

    }

    public Hologram getHologram() {
        return hologram;
    }

    public Location getLocation() {
        return location;
    }
}
