package me.imadenigma.gangsplugin.gangs;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.imadenigma.gangsplugin.GangsPlugin;
import me.lucko.helper.gson.GsonProvider;
import me.lucko.helper.gson.JsonBuilder;
import me.lucko.helper.serialize.GsonStorageHandler;
import me.lucko.helper.utils.Log;

import java.io.*;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Set;

public class GangManager {

    private static final Set<Gang> gangs = Sets.newHashSet();

    private final Set<String> gangsNames = Sets.newHashSet();
    private final File gangsFolder;

    public GangManager() {
        this.gangsFolder = new File(GangsPlugin.getSingleton().getDataFolder() + File.pathSeparator + "gangs");
    }

    public void loadNames() throws FileNotFoundException {
        final File namesFile = new File(this.gangsFolder,"names.json");
        Gson gson = GsonProvider.standard();
        gangsNames.addAll(
                gson.fromJson(new FileReader(namesFile),new TypeToken<Set<String>>(){}.getType())
        );
    }

    public void saveNames() throws IOException {
        final File namesFile = new File(this.gangsFolder,"names.json");
        Gson gson = GsonProvider.standard();
        gson.toJson(gangsNames,new TypeToken<Set<String>>(){}.getType(),new FileWriter(namesFile));
    }



    public Gang loadGang(final String uniqueID) {
        if (Arrays.stream(gangsFolder.listFiles()).noneMatch(file -> file.getName().equalsIgnoreCase(uniqueID))) return null;

        GsonStorageHandler<Gang> gang = new GsonStorageHandler<>(uniqueID, ".json", gangsFolder, Gang.class, GsonProvider.standard());
        try {
            return  gang.load().get();
        }catch (NoSuchElementException e) {
            Log.severe("Can't get a gang from config file , its uuid is " + uniqueID);
        }
        return null;

    }

    public void saveGangs() {
        for (final Gang gang : gangs) {
            final GsonStorageHandler<Gang> storageHandler = new GsonStorageHandler<>(gang.getUniqueID().toString(),
                    ".json",
                    gangsFolder, Gang.class,
                    GsonProvider.standard());
            storageHandler.save(gang);
        }
    }

    public static Set<Gang> getGangs() {
        return gangs;
    }


}
