package me.imadenigma.gangsplugin.user;

import com.google.gson.JsonElement;
import me.imadenigma.gangsplugin.Configuration;
import me.imadenigma.gangsplugin.economy.EconomyManager;
import me.imadenigma.gangsplugin.gangs.Gang;
import me.imadenigma.gangsplugin.utils.MessagesHandler;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.gson.JsonBuilder;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;
import java.util.UUID;

public class UserImpl implements User {

    private final UUID uniqueID;
    private String name;
    private String lastKnownGang;
    private final OfflinePlayer offlinePlayer;
    private boolean chatEnabled;
    private Gang presentGang;

    public UserImpl(final String gangname, final OfflinePlayer offlinePlayer) {
        this.uniqueID = offlinePlayer.getUniqueId();
        this.name = offlinePlayer.getName();
        this.offlinePlayer = offlinePlayer;
        this.presentGang = null;
        this.lastKnownGang = gangname;
        UserManager.getUsers().add(this);
    }

    @Override
    public double getBalance() {
        return EconomyManager.INSTANCE.getEconomylib().getBalance(this.offlinePlayer);
    }

    @Override
    public double withdrawBalance(long value) {
        return EconomyManager.INSTANCE.getEconomylib().withdrawPlayer(this.offlinePlayer, value).amount;
    }

    @Override
    public double depositBalance(long value) {
        return EconomyManager.INSTANCE.getEconomylib().depositPlayer(this.offlinePlayer, value).amount;
    }

    @Override
    public UUID getUniqueID() {
        return this.uniqueID;
    }

    @Override
    public OfflinePlayer getOfflinePlayer() {
        return this.offlinePlayer;
    }

    @Override
    public Optional<Gang> getGang() {
        return Optional.ofNullable(this.presentGang);
    }

    @Override
    public boolean hasGang() {
        return this.lastKnownGang != null;
    }

    @Override
    public void setGang(final Gang gang) {
        if (this.getGang().isPresent()) {
            this.presentGang.kickMember(this);
            this.presentGang.msgC("gang", "playerquit");
        }
        if (gang == null) {
            this.chatEnabled = false;
            this.presentGang = null;
            this.lastKnownGang = null;
            return;
        }

        this.lastKnownGang = gang.getName();
        this.presentGang = gang;
    }

    @Override
    public void forceGang(Gang gang) {
        this.lastKnownGang = gang.getName();
        this.presentGang = gang;
    }

    @Override
    public boolean isChatEnabled() {
        return this.chatEnabled;
    }

    @Override
    public void enableChat() {
        this.chatEnabled = true;
    }

    @Override
    public void disableChat() {
        this.chatEnabled = false;
    }

    @Override
    public void invite(User user) {
        // A normal member can't invite people
        new Invite(this, user);
    }

    @Override
    public String getLastKnownGang() {
        return this.lastKnownGang;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Rank getRank() {
        if (this.presentGang == null) return null;
        return this.presentGang.getRank(this);
    }

    @Override
    public void decreaseRank() {
        if (this.presentGang != null) {
            this.presentGang.decreaseRank(this);
        }
    }

    @Override
    public void increaseRank() {
        if (this.presentGang != null) {
            this.presentGang.decreaseRank(this);
        }
    }

    @NotNull
    @Override
    public JsonElement serialize() {
        return JsonBuilder.object()
                .add("uuid", this.uniqueID.toString())
                .add("name", this.name)
                .add("gang", this.lastKnownGang)
                .build();
    }

    @Override
    public void msg(@NotNull String msg) {
        if (!this.offlinePlayer.isOnline()) return;
        this.offlinePlayer.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }

    @Override
    public void msgC(@NotNull String... path) {
        if (!this.offlinePlayer.isOnline()) return;
        final String message =
                Configuration.getLanguage().getNode((Object[]) path).getString("default message");
        if (message.equalsIgnoreCase("")) return;
        this.offlinePlayer
                .getPlayer()
                .sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    @Override
    public void msgH(@NotNull String msg, @NotNull Object... replacements) {
        if (!this.offlinePlayer.isOnline()) return;
        final String message = MessagesHandler.INSTANCE.handle(msg, replacements);
        this.offlinePlayer
                .getPlayer()
                .sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    @Override
    public void msgCH(@NotNull String[] path, @NotNull Object... replacements) {
        if (!this.offlinePlayer.isOnline()) return;
        ConfigurationNode node = Configuration.getLanguage();
        for (String word : path) {
            node = node.getNode(word);
        }
        final String message =
                MessagesHandler.INSTANCE.handle(
                        node.getString(), replacements
                );
        this.offlinePlayer
                .getPlayer()
                .sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
}
