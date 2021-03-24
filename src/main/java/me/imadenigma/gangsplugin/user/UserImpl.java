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

public class UserImpl implements User {

    private final UUID uniqueID;
    private String name;
    private Optional<UUID> gang;
    private UUID lastGang;
    private final OfflinePlayer offlinePlayer;
    private Rank rank;
    private boolean chatEnabled;
    private Gang presentGang;

    public UserImpl(final String name, final UUID gang, OfflinePlayer offlinePlayer) {
        this.uniqueID = offlinePlayer.getUniqueId();
        this.name = name;
        this.offlinePlayer = offlinePlayer;
        this.gang = Optional.empty();
        UserManager.getUsers().add(this);
        if (gang != null) {
            this.gang = Optional.of(gang);
            this.lastGang = gang;
            this.presentGang = Gang.getFromUUID(gang);
        }
    }


    @Override
    public double getBalance() {
        return EconomyManager.INSTANCE.getEconomylib().getBalance(this.offlinePlayer);
    }

    @Override
    public double withdrawBalance(long value) {
        return EconomyManager.INSTANCE.getEconomylib().withdrawPlayer(this.offlinePlayer,value).amount;
    }

    @Override
    public double depositBalance(long value) {
        return EconomyManager.INSTANCE.getEconomylib().depositPlayer(this.offlinePlayer,value).amount;    }

    @Override
    public UUID getUniqueID() {
        return this.uniqueID;
    }

    @Override
    public OfflinePlayer getOfflinePlayer() {
        return this.offlinePlayer;
    }

    @Override
    public Optional<UUID> getGang() {
        return this.gang;
    }

    @Override
    public boolean hasGang() {
        return this.gang.isPresent();
    }

    @Override
    public void setGang(final Gang gang) {
        Preconditions.checkNotNull(gang,"Gang may not be null");
        if (this.gang.isPresent()) {
            this.presentGang.kickMember(this);
            this.presentGang.msgC("gang","playerquit");
        }
        if (gang == null) {
            this.rank = null;
            this.lastGang = null;
            this.chatEnabled = false;
            this.presentGang = null;
            return;
        }


        this.rank = Rank.MEMBER;
        this.lastGang = gang.getUniqueID();
        this.gang = Optional.of(gang.getUniqueID());
        this.presentGang = gang;

    }

    @Override
    public UUID getLastGang() {
        return this.lastGang;
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
        if (this.rank == Rank.MEMBER) return;
        this.rank = Arrays.stream(Rank.values()).filter(rank -> rank.getLevel() == this.rank.getLevel() - 1).findAny().get();

    }

    @Override
    public Rank getRank() {
        return this.rank;
    }

    @Override
    public Gang getPresentGang() {
        return this.presentGang;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setPresentGang(Gang gang) {
        this.presentGang = gang;
    }


    @NotNull
    @Override
    public JsonElement serialize() {
        return JsonBuilder.object()
                .add("uuid",this.uniqueID.toString())
                .add("name",this.name)
                .add("gang", this.gang.toString())
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

    @Override
    public void msgCH(@NotNull String[] path, @NotNull Object[] replacements) {
        if (!this.offlinePlayer.isOnline()) return;
        final String message = MessagesHandler.INSTANCE.handleMessage(Configuration.getLanguage().getNode((Object[]) path).getString(),replacements);
        this.offlinePlayer.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',message));
    }
}
