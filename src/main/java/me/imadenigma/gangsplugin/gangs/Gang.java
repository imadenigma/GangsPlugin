package me.imadenigma.gangsplugin.gangs;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.imadenigma.gangsplugin.exceptions.GangNotFoundException;
import me.imadenigma.gangsplugin.user.Rank;
import me.imadenigma.gangsplugin.user.User;
import me.imadenigma.gangsplugin.utils.Messenger;
import me.lucko.helper.gson.GsonSerializable;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface Gang extends GsonSerializable, Messenger {

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

    User getLeader();

    void increaseRank(final User user);

    void setRank(final User user, final Rank rank);

    void decreaseRank(final User user);

    void increaseMinedBlocks();

    static Gang deserialize(final JsonElement jsonElement) {
        JsonObject object = jsonElement.getAsJsonObject();
        final String name = object.get("name").getAsString();
        final long balance = object.get("balance").getAsLong();
        final long minedBlocks = object.get("minedBlocks").getAsLong();
        final UUID leader = UUID.fromString(object.get("leader").getAsString());
        final Map<User, Rank> members = Maps.newHashMap();
        JsonArray array = object.get("members").getAsJsonArray();
        for (JsonElement element : array) {
            JsonObject object1 = element.getAsJsonObject();
            members.put(
                    User.getFromUUID(UUID.fromString(object1.get("uuid").getAsString())),
                    Rank.valueOf(object1.get("rank").getAsString()));
        }
        return new GangImpl(name, balance, minedBlocks, leader, members);
    }

    static Gang getByName(final String name) throws FileNotFoundException {
        if (GangManager.getGangs().stream().anyMatch(gang -> gang.getName().equalsIgnoreCase(name))) {
            return GangManager.getGangs().stream()
                    .filter(gang -> gang.getName().equalsIgnoreCase(name))
                    .findAny()
                    .get();
        }
        System.out.println(name);
        Gang gang = GangManager.build().loadGang(name);
        if (gang == null) {
            gang = new GangManager().loadGang(name);
            if (gang == null) throw new GangNotFoundException(name);
        }
        return gang;
    }

    Rank getRank(User user);

}
