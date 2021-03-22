package me.imadenigma.gangsplugin.gangs;

import me.imadenigma.gangsplugin.user.User;
import me.lucko.helper.gson.GsonSerializable;

import java.util.Set;
import java.util.UUID;

public interface Gang extends GsonSerializable {

    UUID getUniqueID();

    String getName();

    void setName(final String name);

    Set<User> getMembers();

    void kickMember(final User user);

    void addMember(final User user);

    long getMinedBlocks();

    long getBalance();

    void withdrawBalance(final int value);

    void depositBalance(final int value);

    void destroy();

}
