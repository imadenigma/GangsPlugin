package me.imadenigma.gangsplugin.gangs;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import me.imadenigma.gangsplugin.GangsPlugin;
import me.lucko.helper.gson.GsonProvider;
import java.io.*;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class GangManager {

    private static final Set<Gang> gangs = Sets.newHashSet();
    private static GangManager instance;
    private static final Map<String, Long> gangsBalance = Maps.newHashMap();
    private final File gangsFolder;

    public GangManager(final boolean force) throws FileNotFoundException {
        this.gangsFolder = new File(GangsPlugin.getSingleton().getDataFolder() + "/gangs");
        instance = this;
        try {
            loadBalances();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (File file : this.gangsFolder.listFiles()) {
            loadGang(file.getName().replace(".json",""));
        }
    }

    public GangManager() {
        this.gangsFolder = new File(GangsPlugin.getSingleton().getDataFolder() + "/gangs");
    }



    public Gang loadGang(final String name) {
        if (Arrays.stream(gangsFolder.listFiles())
                .noneMatch(file -> file.getName().equalsIgnoreCase(name))) return null;
        try {
            Gang gang = Gang.deserialize(
                    GsonProvider.parser()
                            .parse(new FileReader(new File(gangsFolder, name + ".json"))));
      gang.getMembers()
          .forEach(
              member -> {
                  member.forceGang(gang);
                  System.out.println(member);
              });
            gang.getLeader().setGang(gang);
            return gang;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveGangs() {
        for (final Gang gang : gangs) {
            File file = new File(gangsFolder, gang.getName() + ".json");
            checkFile(file);
            try {
                FileWriter writer = new FileWriter(file);
                GsonProvider.writeElementPretty(writer, gang.serialize());
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
        return new GangManager();
    }

    public File getGangsFolder() {
        return gangsFolder;
    }

    public void loadBalances() throws IOException {
        for (File file : gangsFolder.listFiles()) {
            Reader reader = new FileReader(file);
            JsonObject object = GsonProvider.parser().parse(reader).getAsJsonObject();
            reader.close();
            gangsBalance.put(object.get("name").getAsString(), object.get("balance").getAsLong());
        }
    }

    public static Map<String, Long> getGangsBalance() {
        return gangsBalance;
    }

}
