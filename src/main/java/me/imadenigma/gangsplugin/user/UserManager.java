package me.imadenigma.gangsplugin.user;

import com.google.common.collect.Sets;

import java.util.Set;

public class UserManager {

    private static final Set<User> users = Sets.newHashSet();

    public static Set<User> getUsers() {
        return users;
    }
}
