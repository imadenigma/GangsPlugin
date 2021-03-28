package me.imadenigma.gangsplugin.user;

import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import me.imadenigma.gangsplugin.GangsPlugin;
import me.lucko.helper.gson.GsonProvider;
import me.lucko.helper.utils.Log;
import java.io.*;
import java.util.Set;

public class UserManager {
    private static final Set<User> users = Sets.newHashSet();

    private final File file = new File(GangsPlugin.getSingleton().getDataFolder(), "users.json");

    public void loadUsers() {
        long ms = System.currentTimeMillis();
        try {
            for (JsonElement element :
                    GsonProvider.parser().parse(new FileReader(file)).getAsJsonArray()) {
                User.deserialize(element);
            }
            Log.info(
                    "§aLoading took §4 "
                            + (System.currentTimeMillis() - ms)
                            + " §ams"); // calculate how much time passed in milliseconds
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void saveUsers() {
        long ms = System.currentTimeMillis();
        JsonArray array = new JsonArray();
        for (User user : users) {
            array.add(user.serialize());
        }
        try {
            FileWriter writer = new FileWriter(file);
            GsonProvider.writeElementPretty(writer, array);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.info("§aSaving took §4 " + (System.currentTimeMillis() - ms) + " §ams");
    }

    public static Set<User> getUsers() {
        return users;
    }
}
