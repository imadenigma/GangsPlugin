package me.imadenigma.gangsplugin.user;

import com.google.common.base.Preconditions;
import me.imadenigma.gangsplugin.gangs.Gang;
import me.lucko.helper.gson.GsonSerializable;
import me.lucko.helper.utils.Players;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public interface User extends GsonSerializable {

    default UUID getUniqueID() {
        return this.getOfflinePlayer().getUniqueId();
    }

    OfflinePlayer getOfflinePlayer();

    Optional<Gang> getGang();

    default boolean hasGang() {
        return this.getGang().isPresent();
    }

    void setGang(Gang gang);

    UUID getLastGang();

    void updateLastGang();

    boolean isChatEnabled();

    void enableChat();

    void disableChat();

    void invite(User user);

    static User getFromBukkit(final Player player) {
        Preconditions.checkNotNull(player, "Player may not be null");
        final Optional<User> optional = UserManager.getUsers().stream().filter(user -> user.getUniqueID().equals(player.getUniqueId())).findAny();
        return optional.orElseGet(() -> new UserImpl(player.getDisplayName(), null, Players.getOffline(player.getUniqueId()).get()));
    }

}
