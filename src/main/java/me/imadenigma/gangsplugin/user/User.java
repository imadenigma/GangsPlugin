package me.imadenigma.gangsplugin.user;

import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.imadenigma.gangsplugin.economy.Economy;
import me.imadenigma.gangsplugin.exceptions.UserNotFoundException;
import me.imadenigma.gangsplugin.gangs.Gang;
import me.imadenigma.gangsplugin.utils.Messenger;
import me.lucko.helper.gson.GsonSerializable;
import me.lucko.helper.utils.Players;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import java.util.Optional;
import java.util.UUID;

public interface User extends GsonSerializable, Messenger, Economy {

    default UUID getUniqueID() {
        return this.getOfflinePlayer().getUniqueId();
    }

    OfflinePlayer getOfflinePlayer();

    Optional<UUID> getGang();

    default boolean hasGang() {
        return this.getGang().isPresent() && this.getPresentGang() != null;
    }

    void setGang(Gang gang);

    UUID getLastGang();


    boolean isChatEnabled();

    void enableChat();

    void disableChat();

    void invite(User user);

    void setRank(Rank rank);

    void increaseRank();

    void decreaseRank();

    Rank getRank();

    Gang getPresentGang();

    String getName();

    void setPresentGang(final Gang gang);

    static User getFromBukkit(final Player player) {
        Preconditions.checkNotNull(player, "Player may not be null");
        final Optional<User> optional = UserManager.getUsers().stream().filter(user -> user.getUniqueID().equals(player.getUniqueId())).findAny();
        return optional.orElseGet(() -> new UserImpl(player.getDisplayName(), null, Players.getOffline(player.getUniqueId()).get()));
    }
    static User getFromUUID(final UUID uniqueID) {
        Preconditions.checkNotNull(uniqueID);
        final Optional<User> optional = UserManager.getUsers().stream().filter(user -> user.getUniqueID().equals(uniqueID)).findAny();
        return optional.orElseThrow(Suppliers.ofInstance(new UserNotFoundException()));
    }
    static User deserialize(final JsonElement element) {
        final JsonObject object = element.getAsJsonObject();
        final String name = object.get("name").getAsString();
        final UUID uuid = UUID.fromString(object.get("uuid").getAsString());
        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        return new UserImpl(name,null,offlinePlayer);
    }

}
