package me.imadenigma.gangsplugin.gangs;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.imadenigma.gangsplugin.GangsPlugin;
import me.lucko.helper.gson.GsonProvider;
import java.io.*;
import java.util.Map;
import java.util.Set;

public class GangManager {

    private static final Set<Gang> gangs = Sets.newHashSet();
    private static GangManager instance;
    private static final Map<String, Long> gangsBalance = Maps.newHashMap();
    private final File gangsFolder;

    public GangManager() throws FileNotFoundException {
        this.gangsFolder = new File(GangsPlugin.getSingleton().getDataFolder() + "/gangs");
        instance = this;
        if (!this.gangsFolder.exists()) {
            this.gangsFolder.mkdirs();
        }else setup();
    }
    public void setup() {
        for (File file : this.gangsFolder.listFiles()) {
            loadGang(file.getName().replace(".json",""));
        }

    }




    public Gang loadGang(final String name) {
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
        return instance;
    }

    public File getGangsFolder() {
        return gangsFolder;
    }



    public static Map<String, Long> getGangsBalance() {
        return gangsBalance;
    }



}
