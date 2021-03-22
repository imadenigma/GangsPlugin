package me.imadenigma.gangsplugin.user;

import com.google.common.base.Suppliers;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import me.imadenigma.gangsplugin.GangsPlugin;
import me.lucko.helper.gson.GsonProvider;
import me.lucko.helper.serialize.GsonStorageHandler;
import me.lucko.helper.utils.Log;

import java.io.File;
import java.util.Set;

public class UserManager {
    private static final Set<User> users = Sets.newHashSet();

    private final File usersFolder;
    private final GsonStorageHandler<Set<User>> storageHandler;
    public UserManager() {
        this.usersFolder = new File(GangsPlugin.getSingleton().getDataFolder() + File.pathSeparator + "users");

        this.storageHandler = new GsonStorageHandler<>(
                "users",
                ".json",
                usersFolder,
                new TypeToken<Set<User>>() {
                }.getType(),
                GsonProvider.standard()
        );
    }

    public void loadUsers() {
        long ms = System.currentTimeMillis();
        users.addAll(
                this.storageHandler.load().orElseGet(Suppliers.ofInstance(Sets.newHashSet()))
        );
        Log.info("§aLoading took §4 " + (System.currentTimeMillis() - ms) + " §ams"); //calculate how much time passed in milliseconds
    }

    public void saveUsers() {
        long ms = System.currentTimeMillis();
        this.storageHandler.save(users);
        Log.info("§aSaving took §4 " + (System.currentTimeMillis() - ms) + " §ams");
    }


    public static Set<User> getUsers() {
        return users;
    }

}
