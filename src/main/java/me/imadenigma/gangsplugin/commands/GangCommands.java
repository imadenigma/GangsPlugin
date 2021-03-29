package me.imadenigma.gangsplugin.commands;

import me.imadenigma.gangsplugin.Configuration;
import me.imadenigma.gangsplugin.GangsPlugin;
import me.imadenigma.gangsplugin.economy.Baltop;
import me.imadenigma.gangsplugin.gangs.Gang;
import me.imadenigma.gangsplugin.gangs.GangImpl;
import me.imadenigma.gangsplugin.gangs.GangManager;
import me.imadenigma.gangsplugin.user.Invite;
import me.imadenigma.gangsplugin.user.Rank;
import me.imadenigma.gangsplugin.user.User;
import me.imadenigma.gangsplugin.user.UserManager;
import me.imadenigma.gangsplugin.utils.MessagesHandler;
import me.lucko.helper.Commands;
import me.mattstudios.mf.annotations.*;
import me.mattstudios.mf.base.CommandBase;
import me.mattstudios.mf.base.CommandManager;
import me.mattstudios.mf.base.components.TypeResult;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Arrays;
import java.util.List;
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
        manager
                .getCompletionHandler()
                .register(
                        "#gang-players",
                        input ->
                                UserManager.getUsers().stream().map(User::getName).collect(Collectors.toList()));
        manager
                .getParameterHandler()
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
        if (user.hasGang()
                || Arrays.stream(GangManager.build().getGangsFolder().listFiles())
                .anyMatch(
                        file -> file.getName().equalsIgnoreCase(name) || file.getName().contains(name))) {
            user.msg(getFailMessage("create"));
            return;
        }
        final Gang gang = new GangImpl(name, user);
        gang.addMember(user);
        gang.setRank(user, Rank.OWNER);
        user.setGang(gang);
        user.msg(getSuccessMessage("create"));
    }

    @SubCommand("invite")
    @Permission("gang.invite")
    @WrongUsage("&c/gang &3invite &c<player>")
    @Completion("#players")
    public void invite(final Player player, final User target) {
        final User user = User.getFromBukkit(player);
        if (Invite.Companion.getAvailable().containsKey(target)) {
            user.msgC("user", "invite", "wait");
            return;
        }
        if (!user.hasGang()) {
            user.msgC("user", "invite", "has-no-gang");
            return;
        }
        new Invite(user, target);
        user.msgH(getSuccessMessage("invite"), target.getName());
    }

    @SubCommand("kick")
    @Permission("gang.kick")
    @WrongUsage("&c/gang &3kick &c<player>")
    @Completion("#gang-players")
    public void kick(final Player player, final User target) {
        final User user = User.getFromBukkit(player);
        if (!user.hasGang()) {
            user.msgC("user", "kick", "user-notGangMember");
            return;
        }
        if (!target.hasGang()) {
            user.msgC("user", "kick", "target-notGangMember");
            return;
        }
        if (!user.getGang().get().equals(target.getGang().get())) {
            user.msgC("user", "kick", "not-sameGang");
            return;
        }
        if (user.getGang().get().getLeader().equals(target)) {
            user.msgC("user", "kick", "leader");
            return;
        }
        user.msgH(getSuccessMessage("kick"), target.getName());
        user.getGang().ifPresent(gang -> gang.kickMember(user));
    }

    @SubCommand("demote")
    @Permission("gang.demote")
    @WrongUsage("&c/gang &3demote &c<player>")
    @Completion("#gang-players")
    public void demote(final Player player, final User target) {
        final User user = User.getFromBukkit(player);

        if (!user.hasGang()) user.msgC("user", "demote", "user-has-no-gang");
        else if (!target.hasGang()) user.msgC("user", "demote", "target-user-notsame-gang");
        else if (!user.getGang().get().equals(target.getGang().get()))
            user.msgC("user", "demote", "target-user-notsame-gang");
        else if (target.getRank().getLevel() > user.getRank().getLevel())
            user.msgC("user", "demote", "no-permission");
        else {
            target.decreaseRank();
            user.msgH(getSuccessMessage("demote"), target.getName());
        }
    }

    @SubCommand("promote")
    @Permission("gang.promote")
    @WrongUsage("&c/gang &3demote &c<player>")
    @Completion("#gang-players")
    public void promote(final Player player, final User target) {
        final User user = User.getFromBukkit(player);
        if (!user.hasGang()) user.msgC("user", "promote", "user-has-no-gang");
        else if (!target.hasGang()) user.msgC("user", "promote", "target-user-notsame-gang");
        else if (!user.getGang().get().equals(target.getGang().get()))
            user.msgC("user", "promote", "target-user-notsame-gang");
        else if (target.getRank().getLevel() > user.getRank().getLevel())
            user.msgC("user", "promote", "no-permission");
        else target.increaseRank();
    }

    @SubCommand("deposit")
    @Permission("gang.deposit")
    @WrongUsage("&c/gang &3deposit <amount>")
    @Completion("#range:1-1000")
    public void deposit(final Player player, final Integer amount) {
        final User user = User.getFromBukkit(player);
        if (!user.hasGang()) {
            user.msgC("user", "deposit", "no-gang");
        } else if (user.getBalance() < amount) {
            user.msgC("user", "deposit", "not-enough-balance");
        } else {

            user.getGang().get().depositBalance(amount);
            user.withdrawBalance(amount);
            user.msg("&7your balance now : &3" + user.getBalance());
            user.msg("&7your Gang's balance now : &3" + user.getGang().get().getBalance());

        }
    }

    @SubCommand("withdraw")
    @Permission("gang.withdraw")
    @WrongUsage("&c/gang &3withdraw <amount>")
    @Completion("#range:1-1000")
    public void withdraw(final Player player, final Integer amount) {
        final User user = User.getFromBukkit(player);
        if (!user.hasGang()) {
            user.msgC("user", "withdraw", "no-gang");
        } else if (user.getGang().get().getBalance() < amount) {
            user.msgC("user", "withdraw", "not-enough-balance");
        } else {
            user.getGang().get().withdrawBalance(amount);
            user.depositBalance(amount);
            user.msg("&7your balance now : &3" + user.getBalance());
            user.msg("&7your Gang's balance now : &3" + user.getGang().get().getBalance());
        }
    }

    @SubCommand("top")
    @Permission("gang.top")
    @WrongUsage("&c/gang &3top|leaderboard")
    @Alias({"top", "leaderboard"})
    public void top(final Player player) {
        player.teleport(Baltop.INSTANCE.getLocation());
        player.sendMessage(
                ChatColor.translateAlternateColorCodes('&', getSuccessMessage("leaderboard")));
    }

    @SubCommand("balance")
    @Permission("gang.balance")
    @WrongUsage("&c/gang &3balance")
    public void balance(final Player player) {
        final User user = User.getFromBukkit(player);
        if (user.hasGang()) {
            user.msgH(getSuccessMessage("balance"), user.getGang().get().getBalance());
        } else user.msg(getFailMessage("balance"));
    }

    @SubCommand("rank")
    @Permission("gang.rank")
    @WrongUsage("&c/gang &3rank")
    public void rank(final Player player) {
        final User user = User.getFromBukkit(player);
        if (user.hasGang()) {
            user.msg(MessagesHandler.INSTANCE.handle(getSuccessMessage("rank"), user.getRank().name()));
        } else user.msg(getFailMessage("rank"));
    }

    @SubCommand("chat")
    @Permission("gang.chat")
    @WrongUsage("&c/gang &3chat <on/off>")
    public void chat(final Player player, final String toggle) {
        final User user = User.getFromBukkit(player);
        if (!user.hasGang()) {
            user.msg(getFailMessage("chat"));
        }
        if (toggle.equalsIgnoreCase("on")) {
            user.enableChat();
            user.msgC("user","chat","on");
        }
        else if (toggle.equalsIgnoreCase("off")) {
            user.disableChat();
            user.msgC("user","chat","off");
        }
    }

    @SubCommand("rename")
    @Permission("gang.rename")
    @WrongUsage("&c/gang &3rename <new name>")
    public void rename(final Player player, final String newName) {
        final User user = User.getFromBukkit(player);
        if (!user.hasGang()) {
            user.msg(getFailMessage("rename"));
            return;
        }
        if (Arrays.stream(GangManager.build().getGangsFolder().listFiles())
                .anyMatch(
                        file -> file.getName().equalsIgnoreCase(newName) || file.getName().contains(newName) || newName.contains(file.getName()))) {
            user.msg(getFailMessage("rename"));
            return;
        }
        user.msgH(getSuccessMessage("rename"), newName);
        Arrays.stream(GangManager.build().getGangsFolder().listFiles())
                .filter(file -> file.getName().equalsIgnoreCase(user.getLastKnownGang()))
                .findAny()
                .ifPresent(fi ->
                fi.renameTo(new File(GangManager.build().getGangsFolder(), newName + ".json"))
        );
        user.getGang().get().setName(newName);
    }

    @SubCommand("name")
    @Permission("gang.name")
    @WrongUsage("&c/gang &3name")
    public void name(final Player player) {
        final User user = User.getFromBukkit(player);
        if (!user.hasGang()) {
            user.msg(getFailMessage("name"));
            return;
        }
        user.msgH(getSuccessMessage("name"), user.getGang().get().getName());
        user.msg("1");
        player.sendMessage("2");
    }

    @SubCommand("destroy")
    @Permission("gang.destroy")
    @WrongUsage("&c/gang &3destroy <sure>")
    public void destroy(final Player player, final Boolean bool) {
        final User user = User.getFromBukkit(player);
        if (!user.hasGang()) {
            user.msgC("user", "destroy", "no-gang");
            return;
        }
        if (user.getGang().get().getRank(user) != Rank.OWNER) {
            user.msgC("user", "destroy", "not-owner");
            return;
        }
        if (!bool) {
            user.msgC("user", "destroy", "not-sure");
            return;
        }
        user.msg(getSuccessMessage("destroy"));
        user.getGang().get().destroy();
    }

    // Command Utils

    @CompleteFor("chat")
    public List<String> commandCompletionChat(final List<String> completionArg, final Player player) {
        return Arrays.asList("on", "off");
    }

    @CompleteFor("destroy")
    public List<String> commandCompletionDestroy(
            final List<String> completionArg, final Player player) {
        return Arrays.asList("true", "false");
    }

    private static String getFailMessage(final String command) {
        return Configuration.getLanguage().getNode("user", command, "failed").getString("null");
    }

    private static String getSuccessMessage(final String command) {
        return Configuration.getLanguage().getNode("user", command, "success").getString("abc");
    }
}
