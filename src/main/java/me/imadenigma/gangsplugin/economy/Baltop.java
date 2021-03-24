package me.imadenigma.gangsplugin.economy;

import com.google.common.collect.Comparators;
import com.google.common.collect.Lists;
import me.imadenigma.gangsplugin.gangs.GangManager;
import me.lucko.helper.Schedulers;
import me.lucko.helper.hologram.Hologram;
import me.lucko.helper.serialize.Position;
import org.bukkit.Location;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Baltop {
    public static Baltop INSTANCE;
    private final Location location;
    private static final int duration = 10;
    private final List<String> sortedLines = Lists.newLinkedList();
    private final Hologram hologram;


    public Baltop(Location location) {
        this.location = location;
        reloadCache();
        this.hologram = Hologram.create(Position.of(location), sortedLines);
        display();
        Schedulers.sync()
                .runRepeating(() -> {
                    reloadCache();
                    display();
                },duration, TimeUnit.SECONDS,duration,TimeUnit.SECONDS);
        INSTANCE = this;
    }

    private void display() {
        if (!hologram.isSpawned()) {
            this.hologram.spawn();
        }
        this.hologram.updateLines(this.sortedLines);
    }

    private void reloadCache() {
        this.sortedLines.add("§8§m----------------------------------------");
        GangManager.getGangsBalance().entrySet().stream().sorted(Map.Entry.comparingByValue()).forEachOrdered(entry -> {
            this.sortedLines.add("Gang : " + entry.getKey() + ";;; balance: " + entry.getValue());
        });
        this.sortedLines.add("§8§m----------------------------------------");

    }

    public Hologram getHologram() {
        return hologram;
    }

    public Location getLocation() {
        return location;
    }
}
