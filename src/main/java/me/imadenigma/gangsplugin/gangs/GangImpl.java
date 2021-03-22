package me.imadenigma.gangsplugin.gangs;

import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import me.imadenigma.gangsplugin.Configuration;
import me.imadenigma.gangsplugin.user.User;
import me.imadenigma.gangsplugin.utils.MessagesHandler;
import me.lucko.helper.gson.JsonBuilder;
import org.jetbrains.annotations.NotNull;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class GangImpl implements Gang {
    private final UUID uniqueID;
    private String name;
    private Set<User> members = Sets.newHashSet();
    private long balance;
    private long minedBlocks;
    private User leader;
    public GangImpl(String name,User leader) {
        this.uniqueID = UUID.randomUUID();
        this.name = name;
        this.balance = 0L;
        this.minedBlocks = 0L;
        this.leader = leader;
    }



    @Override
    public UUID getUniqueID() {
        return this.uniqueID;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public Set<User> getMembers() {
        return this.members;
    }

    @Override
    public void kickMember(final User user) {
        this.members.remove(user);
    }

    @Override
    public void addMember(final User user) {
        if (this.members.size() >= Configuration.getConfig().getNode("gangs","max-members").getInt(10)) return;
        this.members.add(user);
        this.msgC("gang","join-msg");
    }

    @Override
    public long getMinedBlocks() {
        return this.minedBlocks;
    }

    @Override
    public long getBalance() {
        return this.balance;
    }

    @Override
    public void withdrawBalance(int value) {
        this.balance -= value;
    }

    @Override
    public void depositBalance(int value) {
        this.balance += value;
    }

    @Override
    public void destroy() {
        this.getMembers().forEach(member -> {
            member.setGang(null);
            member.updateLastGang();
            member.disableChat();
            member.msgC("messages","gang","destroy-msg");
        });
        this.members = null;
        this.minedBlocks = 0L;
        this.name = null;
        this.balance = 0L;
        GangManager.getGangs().remove(this);

    }

    @NotNull
    @Override
    public JsonElement serialize() {
        JsonArray array = JsonBuilder.array().build();
        for (String member : this.members.stream().map(User::getUniqueID).map(UUID::toString).collect(Collectors.toSet())) {
            array.add(member);
        }
        return JsonBuilder.object()
                .add("uuid",this.uniqueID.toString())
                .add("name",this.name)
                .add("balance",this.balance)
                .add("minedBlocks",this.minedBlocks)
                .add("members",array)
                .build();
    }

    @Override
    public void msg(@NotNull String msg) {
        for (User user : this.members) {
            user.msg(msg);
        }
    }

    @Override
    public void msgC(String... path) {
        final String message = Configuration.getLanguage().getNode((Object[]) path).getString("default message");
        if (message.equalsIgnoreCase("")) return;
        for (User user : this.members) {
            user.msg(message);
        }
    }

    @Override
    public void msgH(@NotNull String msg, @NotNull Object... replacements) {
        final String message = MessagesHandler.INSTANCE.handleMessage(msg,replacements);
        for (User user : this.members) {
            user.msg(message);
        }
    }
}
