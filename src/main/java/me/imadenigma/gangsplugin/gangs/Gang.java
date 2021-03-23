package me.imadenigma.gangsplugin.gangs;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.imadenigma.gangsplugin.exceptions.GangNotFoundException;
import me.imadenigma.gangsplugin.user.User;
import me.imadenigma.gangsplugin.utils.Messenger;
import me.lucko.helper.gson.GsonSerializable;

import java.io.File;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

public interface Gang extends GsonSerializable, Messenger {

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

    static Gang deserialize(final JsonElement jsonElement) {
        JsonObject object = jsonElement.getAsJsonObject();
        final UUID uuid = UUID.fromString(object.get("uuid").getAsString());
        final String name = object.get("name").getAsString();
        final long balance = object.get("balance").getAsLong();
        final long minedBlocks = object.get("minedBlocks").getAsLong();
        final UUID leader = UUID.fromString(object.get("leader").getAsString());
        final Set<User> members = Sets.newHashSet();
        object.get("members").getAsJsonArray().forEach(element -> {
            members.add(User.getFromUUID(UUID.fromString(element.getAsString())));
        });
        return new GangImpl(uuid,name,balance,minedBlocks,leader,members);
    }

    static Gang getFromUUID(final UUID uuid) {
        if (GangManager.getGangs().stream().anyMatch(gang -> gang.getUniqueID().equals(uuid))) {
            return GangManager.getGangs().stream().filter(gang -> gang.getUniqueID().equals(uuid)).findAny().get();
        }
        Gang gang = GangManager.build().loadGang(uuid.toString());
        if (gang == null)
            throw new GangNotFoundException(uuid.toString());
        return gang;
    }

}
