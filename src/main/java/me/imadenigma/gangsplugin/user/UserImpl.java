package me.imadenigma.gangsplugin.user;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import me.imadenigma.gangsplugin.economy.EconomyManager;
import me.imadenigma.gangsplugin.gangs.Gang;
import me.lucko.helper.gson.JsonBuilder;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
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
        if (this.gang.isPresent()) {
            // TODO: 22‏/3‏/2021 delete the player from the gang
        }
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
        if (this.rank)
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
}
