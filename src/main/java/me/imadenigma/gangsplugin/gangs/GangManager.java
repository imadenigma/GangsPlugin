package me.imadenigma.gangsplugin.gangs;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.imadenigma.gangsplugin.GangsPlugin;
import me.lucko.helper.gson.GsonProvider;
import me.lucko.helper.serialize.GsonStorageHandler;
import me.lucko.helper.utils.Log;
import java.io.*;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

public class GangManager {

    private static final Set<Gang> gangs = Sets.newHashSet();
    private static GangManager instance;
    private static final Set<String> gangsNames = Sets.newHashSet();
    private final File gangsFolder;
    private final File namesFile;

    public GangManager() {
        this.gangsFolder = new File(GangsPlugin.getSingleton().getDataFolder() + "/gangs");
        namesFile = new File(this.gangsFolder,"names.json");
        if (!namesFile.exists()) {
            namesFile.getParentFile().mkdirs();
            try {
                namesFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        instance = this;
    }

    public void loadNames() throws IOException {
        Gson gson = GsonProvider.standard();
        Set<String> strings = gson.fromJson(new FileReader(namesFile),new TypeToken<Set<String>>(){}.getType());
        if (strings == null) return;
        gangsNames.addAll(strings);
    }

    public void saveNames() throws IOException {
        gangsNames.addAll(
                gangs.stream().map(Gang::getName).collect(Collectors.toSet())
        );
        Gson gson = GsonProvider.standard();
        FileWriter writer = new FileWriter(namesFile);
        gson.toJson(gangsNames,new TypeToken<Set<String>>(){}.getType(),writer);
        writer.close();
    }



    public Gang loadGang(final String uniqueID) {
        if (Arrays.stream(gangsFolder.listFiles()).noneMatch(file -> file.getName().equalsIgnoreCase(uniqueID))) return null;

        try {
            return  Gang.deserialize(GsonProvider.parser().parse(new FileReader(new File(gangsFolder,uniqueID + ".json"))));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;

    }

    public void saveGangs() {
        for (final Gang gang : gangs) {
            File file = new File(gangsFolder,gang.getUniqueID().toString() + ".json");
            checkFile(file);
            try {
                FileWriter writer = new FileWriter(file);
                GsonProvider.writeElementPretty(writer,gang.serialize());
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Set<Gang> getGangs() {
        return gangs;
    }

    private static void checkFile(final File file) {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static GangManager build() {
        return instance;
    }

    public File getGangsFolder() {
        return gangsFolder;
    }


}
