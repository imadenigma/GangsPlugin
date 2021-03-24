package me.imadenigma.gangsplugin.commands;

import me.imadenigma.gangsplugin.GangsPlugin;
import me.imadenigma.gangsplugin.economy.Baltop;
import me.imadenigma.gangsplugin.gangs.Gang;
import me.imadenigma.gangsplugin.gangs.GangImpl;
import me.imadenigma.gangsplugin.user.Invite;
import me.imadenigma.gangsplugin.user.Rank;
import me.imadenigma.gangsplugin.user.User;
import me.imadenigma.gangsplugin.user.UserManager;
import me.lucko.helper.Commands;
import me.mattstudios.mf.annotations.*;
import me.mattstudios.mf.base.CommandBase;
import me.mattstudios.mf.base.CommandManager;
import me.mattstudios.mf.base.components.TypeResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.stream.Collectors;

@Command("gang")
public class GangCommands extends CommandBase {

    public GangCommands() {
        Commands.create()
                .assertPlayer()
                .handler(
                        c -> {
                            User user = User.getFromBukkit(c.sender());
                            if (Invite.Companion.getAvailable().get(user) != null) {
                                Invite.acceptInv(user, Invite.Companion.getAvailable().get(user));
                            }
                        })
                .register("accept");
        final CommandManager manager = new CommandManager(GangsPlugin.getSingleton());
        manager.getCompletionHandler()
                .register(
                        "#gang-players",
                        input ->
                                UserManager.getUsers().stream().map(User::getName).collect(Collectors.toList()));
        manager.getParameterHandler()
                .register(
                        User.class,
                        argument -> {
                            final User user = User.getFromBukkit(Bukkit.getPlayer(argument.toString()));
                            if (user == null) return new TypeResult(argument);
                            return new TypeResult(user, argument);
                        });
        manager.register(this);
    }

    @SubCommand("create")
    @Permission("gang.create")
    @WrongUsage("&c/gang &3create &c<name>")
    public void create(final Player player, final String name) {
        final User user = User.getFromBukkit(player);
        if (user.hasGang()) {
            user.msgC("user", "create", "failed");
            return;
        }
        final Gang gang = new GangImpl(name, user);
        gang.addMember(user);
        user.setGang(gang);
    }

    @SubCommand("invite")
    @Permission("gang.invite")
    @WrongUsage("&c/gang &3invite &c<player>")
    @Completion("#players")
    public void invite(final Player player, final User target) {
        if (Invite.Companion.getAvailable().containsKey(target)) return;
        final User user = User.getFromBukkit(player);
        if (!user.hasGang()) return;
        new Invite(user, target);
    }

    @SubCommand("kick")
    @Permission("gang.kick")
    @WrongUsage("&c/gang &3kick &c<player>")
    @Completion("#gang-players")
    public void kick(final Player player, final User target) {
        final User user = User.getFromBukkit(player);
        if (!user.hasGang()) return;
        if (!target.hasGang()) return;
        if (!user.getGang().get().equals(target.getGang().get())) return;
        if (target.getRank().getLevel() >= user.getRank().getLevel()) return;

        user.getPresentGang().kickMember(target);
    }

    @SubCommand("demote")
    @Permission("gang.demote")
    @WrongUsage("&c/gang &3demote &c<player>")
    @Completion("#gang-players")
    public void demote(final Player player, final User target) {
        final User user = User.getFromBukkit(player);
        if (!user.hasGang()) return;
        if (!target.hasGang()) return;
        if (!user.getGang().get().equals(target.getGang().get())) return;
        if (target.getRank().getLevel() > user.getRank().getLevel()) return;

        target.decreaseRank();
    }

    @SubCommand("promote")
    @Permission("gang.promote")
    @WrongUsage("&c/gang &3demote &c<player>")
    @Completion("#gang-players")
    public void promote(final Player player, final User target) {
        final User user = User.getFromBukkit(player);
        if (!user.hasGang()) return;
        if (!target.hasGang()) return;
        if (!user.getGang().get().equals(target.getGang().get())) return;
        if (target.getRank().getLevel() > user.getRank().getLevel()) return;

        target.increaseRank();
    }

    @SubCommand("deposit")
    @Permission("gang.deposit")
    @WrongUsage("&c/gang &3deposit <amount>")
    @Completion("#range:1-99999")
    public void deposit(final Player player, final Integer amount) {
        final User user = User.getFromBukkit(player);
        if (!user.hasGang()) return;
        if (user.getBalance() < amount) return;
        user.withdrawBalance(amount);
        user.getPresentGang().depositBalance(amount);
    }

    @SubCommand("withdraw")
    @Permission("gang.withdraw")
    @WrongUsage("&c/gang &3withdraw <amount>")
    @Completion("#range:1-99999")
    public void withdraw(final Player player, final Integer amount) {
        final User user = User.getFromBukkit(player);
        if (!user.hasGang()) return;
        if (user.getPresentGang().getBalance() < amount) return;
        user.getPresentGang().withdrawBalance(amount);
        user.depositBalance(amount);
    }

    @SubCommand("top")
    @Permission("gang.top")
    @WrongUsage("&c/gang &3top|leaderboard")
    @Alias({"top", "leaderboard"})
    public void top(final Player player) {
        player.teleport(Baltop.INSTANCE.getLocation());
    }

    @SubCommand("balance")
    @Permission("gang.balance")
    @WrongUsage("&c/gang &3balance")
    public void balance(final Player player) {
        final User user = User.getFromBukkit(player);
        if (user.hasGang())
            user.msgCH(new String[] {""}, new Object[] {user.getPresentGang().getBalance()});
    }

    @SubCommand("rank")
    @Permission("gang.rank")
    @WrongUsage("&c/gang &3rank")
    public void rank(final Player player) {
        final User user = User.getFromBukkit(player);
        if (user.hasGang()) user.msgCH(new String[]{"user","rank"},new Object[]{user.getRank()});
    }

    @SubCommand("chat")
    @Permission("gang.chat")
    @WrongUsage("&c/gang &3chat <on/off>")
    @Completion("#enum")
    public void chat(final Player player,final Toggle toggle) {
        final User user = User.getFromBukkit(player);
        if (toggle.b) user.enableChat();
        else user.disableChat();
    }



    enum Toggle {
        on(true),off(false);
        private final boolean b;
        Toggle(boolean b) {
            this.b = b;
        }

    }
}
