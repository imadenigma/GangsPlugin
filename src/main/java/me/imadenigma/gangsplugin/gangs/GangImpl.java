package me.imadenigma.gangsplugin.gangs;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import me.imadenigma.gangsplugin.Configuration;
import me.imadenigma.gangsplugin.user.Rank;
import me.imadenigma.gangsplugin.user.User;
import me.imadenigma.gangsplugin.utils.MessagesHandler;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.gson.JsonBuilder;
import me.lucko.helper.terminable.Terminable;
import me.lucko.helper.terminable.TerminableConsumer;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class GangImpl implements Gang {

    private String name;
    private long balance;
    private long minedBlocks;
    private User leader;
    private final Map<User, Rank> members = Maps.newHashMap();

    public GangImpl(String name, User leader) {
        this(
                name,
                0L,
                0L,
                leader.getUniqueID(),
                Maps.newHashMap()
        );
    }

    public GangImpl(
            final String name,
            final long balance,
            final long minedBlocks,
            final UUID leader,
            final Map<User, Rank> members) {
        this.name = name;
        this.balance = balance;
        this.minedBlocks = minedBlocks;
        this.leader = User.getFromUUID(leader);
        this.members.clear();
        this.members.putAll(members);
        GangManager.getGangs().add(this);
        GangManager.getGangsBalance().put(this.name,this.balance);

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
        return this.members.keySet();
    }

    @Override
    public void kickMember(final User user) {
        if (this.members.get(user) == null) return;
        if (this.members.get(user) == Rank.OWNER) return;
        this.members.remove(user);
        user.setGang(null);
    }

    @Override
    public void addMember(final User user) {
        if (this.members.size() >= Configuration.getConfig().getNode("gangs", "max-members").getInt(10))
            return;
        if (this.members.containsKey(user)) return;
        this.members.put(user, Rank.MEMBER);
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
        GangManager.getGangsBalance().replace(this.name,this.balance);
    }

    @Override
    public void depositBalance(int value) {
        this.balance += value;
        GangManager.getGangsBalance().replace(this.name,this.balance);
    }

    @Override
    public void destroy() {
        this.getMembers()
                .forEach(
                        member -> {
                            member.setGang(null);
                            member.disableChat();
                            member.msgC("messages", "gang", "destroy-msg");
                        });
        this.members.clear();
        this.minedBlocks = 0L;
        this.name = null;
        this.balance = 0L;
        new File(GangManager.build().getGangsFolder(), this.name + ".json").delete();
        GangManager.getGangs().remove(this);
    }

    @Override
    public User getLeader() {
        return this.leader;
    }

    @Override
    public void increaseRank(User user) {
        if (this.members.get(user) != null && user.getRank() != Rank.CoLeader) {
            this.members.replace(user, Arrays.stream(Rank.values()).filter(a -> a.getLevel() == this.members.get(user).getLevel() + 1).findAny().get());
        }
    }

    @Override
    public void setRank(User user, Rank rank) {
        if (this.members.get(user) != null) {
            this.members.replace(user,rank);
        }
    }

    @Override
    public void decreaseRank(User user) {
        if (this.members.get(user) != null && user.getRank() != Rank.CoLeader && user.getRank() != Rank.MEMBER) {
            this.members.replace(user, Arrays.stream(Rank.values()).filter(a -> a.getLevel() == this.members.get(user).getLevel() - 1).findAny().get());
        }
    }

    @Override
    public void increaseMinedBlocks() {
        this.minedBlocks += 1;
    }

    @Override
    public Rank getRank(final User user) {
        return this.members.get(user);
    }

    @NotNull
    @Override
    public JsonElement serialize() {
        JsonArray array = JsonBuilder.array().build();
        this.members
                .forEach((key, value) -> array.add(
                        JsonBuilder.object()
                                .add("uuid", key.getUniqueID().toString())
                                .add("rank", value.name())
                                .build()));
        return JsonBuilder.object()
                .add("name", this.name)
                .add("balance", this.balance)
                .add("minedBlocks", this.minedBlocks)
                .add("leader", this.leader.getUniqueID().toString())
                .add("members", array)
                .build();
    }

    @Override
    public void msg(@NotNull String msg) {
        for (User user : this.members.keySet()) {
            user.msg(msg);
        }
    }

    @Override
    public void msgC(String... path) {
        ConfigurationNode node = Configuration.getLanguage().getNode();
        for (String string : path) {
            node = node.getNode(string);
        }
        final String message = node.getString(Arrays.toString(path) + " Path not found");
        for (User user : this.members.keySet()) {
            user.msg(message);
        }
    }

    @Override
    public void msgH(@NotNull String msg, @NotNull Object... replacements) {
        final String message = MessagesHandler.INSTANCE.handle(msg, replacements);
        for (User user : this.members.keySet()) {
            user.msg(message);
        }
    }

    @Override
    public void msgCH(@NotNull String[] path, @NotNull Object[] replacements) {
        String message =
                Configuration.getLanguage().getNode((Object[]) path).getString("default message");
        message = MessagesHandler.INSTANCE.handle(message, replacements);
        if (message.equalsIgnoreCase("")) return;
        for (User user : this.members.keySet()) {
            user.msg(message);
        }
    }
}
