package me.imadenigma.gangsplugin.gangs;

import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import me.imadenigma.gangsplugin.user.User;
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

    public GangImpl(String name) {
        this.uniqueID = UUID.randomUUID();
        this.name = name;
        this.balance = 0L;
        this.minedBlocks = 0L;
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
        this.members.add(user);
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
}
