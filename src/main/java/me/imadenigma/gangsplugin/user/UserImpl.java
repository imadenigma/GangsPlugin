package me.imadenigma.gangsplugin.user;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import me.imadenigma.gangsplugin.Configuration;
import me.imadenigma.gangsplugin.economy.EconomyManager;
import me.imadenigma.gangsplugin.gangs.Gang;
import me.imadenigma.gangsplugin.utils.MessagesHandler;
import me.lucko.helper.gson.JsonBuilder;
import me.lucko.helper.text3.TextComponent;
import me.lucko.helper.text3.event.ClickEvent;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class UserImpl extends EconomyManager implements User {

    private final UUID uniqueID;
    private String name;
    private Optional<Gang> gang;
    private UUID lastGang;
    private final OfflinePlayer offlinePlayer;
    private Rank rank;
    private boolean chatEnabled;


    public UserImpl(final String name, final Gang gang, OfflinePlayer offlinePlayer) {
        this.uniqueID = offlinePlayer.getUniqueId();
        this.name = name;
        this.offlinePlayer = offlinePlayer;
        if (gang != null) {
            this.gang = Optional.of(gang);
            this.lastGang = gang.getUniqueID();
        }
    }


    @Override
    public double getBalance() {
        return this.economylib.getBalance(this.offlinePlayer);
    }

    @Override
    public double withdrawBalance(long value) {
        return this.economylib.withdrawPlayer(this.offlinePlayer,value).amount;
    }

    @Override
    public double depositBalance(long value) {
        return this.economylib.depositPlayer(this.offlinePlayer,value).amount;
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
        return this.gang;
    }

    @Override
    public boolean hasGang() {
        return this.gang.isPresent();
    }

    @Override
    public void setGang(Gang gang) {
        Preconditions.checkNotNull(gang,"Gang may not be null");
        if (gang == null) {
            this.rank = null;
            this.lastGang = null;
            this.chatEnabled = false;
            return;
        }

        if (this.gang.isPresent()) {
            this.gang.get().kickMember(this);
            this.gang.get().msgC("gang","playerquit");
        }

        this.rank = Rank.MEMBER;
        this.lastGang = gang.getUniqueID();
        this.gang = Optional.of(gang);

    }

    @Override
    public UUID getLastGang() {
        return this.lastGang;
    }

    @Override
    public void updateLastGang() {
        if (!this.gang.isPresent()) {
            lastGang = null;
            return;
        }
        this.lastGang = this.gang.get().getUniqueID();
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
        if (this.rank.getLevel() == 1) return; //A normal member can't invite people
        TextComponent message = TextComponent.builder()
                .content(Configuration.getLanguage().getNode("invite-msg").getString("&3Default message"))
                .clickEvent(
                        ClickEvent.runCommand("JoInGaNg") //a simple helper command just to accept ... it will not be saw by any player
                )
                .build();
        user.msg(message.content());


    }

    @Override
    public void setRank(Rank rank) {
        this.rank = rank;
    }

    @Override
    public void increaseRank() {
        if (this.rank == Rank.OWNER || this.rank == Rank.CoLeader) return;
        this.rank = Arrays.stream(Rank.values()).filter(rank -> rank.getLevel() == this.rank.getLevel() + 1).findAny().get();
    }

    @Override
    public void decreaseRank() {
        if (this.rank == Rank.OWNER) return;
        this.rank = Arrays.stream(Rank.values()).filter(rank -> rank.getLevel() == this.rank.getLevel() - 1).findAny().get();

    }


    @NotNull
    @Override
    public JsonElement serialize() {
        return JsonBuilder.object()
                .add("uuid",this.uniqueID.toString())
                .add("name",this.name)
                .add("gang", this.gang.map(value -> value.getUniqueID().toString()).orElse(null))
                .add("rank",this.rank == null ? null : this.rank.name())
                .build();
    }

    @Override
    public void msg(@NotNull String msg) {
        if (!this.offlinePlayer.isOnline()) return;
        this.offlinePlayer.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',msg));
    }

    @Override
    public void msgC(@NotNull String... path) {
        if (!this.offlinePlayer.isOnline()) return;
        final String message = Configuration.getLanguage().getNode((Object[]) path).getString("default message");
        if (message.equalsIgnoreCase("")) return;
        this.offlinePlayer.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',message));
    }

    @Override
    public void msgH(@NotNull String msg, @NotNull Object... replacements) {
        if (!this.offlinePlayer.isOnline()) return;
        final String message = MessagesHandler.INSTANCE.handleMessage(msg,replacements);
        this.offlinePlayer.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',message));
    }
}
