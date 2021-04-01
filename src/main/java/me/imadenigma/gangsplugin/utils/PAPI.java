package me.imadenigma.gangsplugin.utils;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.imadenigma.gangsplugin.user.User;
import me.lucko.helper.Helper;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PAPI extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return Helper.hostPlugin().getDescription().getAuthors().get(0);
    }

    @Override
    public @NotNull String getAuthor() {
        return "Johan Liebert";
    }

    @Override
    public @NotNull String getVersion() {
        return Helper.hostPlugin().getDescription().getVersion();
    }



    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return "";
        final User user = User.getFromBukkit(player);
        if (!user.hasGang()) return "";
        if (params.equalsIgnoreCase("name")) return user.getLastKnownGang();
        if (params.equalsIgnoreCase("balance")) return String.valueOf(user.getGang().get().getBalance());
        if (params.equalsIgnoreCase("minedblocks")) return String.valueOf(user.getGang().get().getMinedBlocks());
        else return "";
    }

    @Override
    public boolean register() {
        return super.register();
    }
}
