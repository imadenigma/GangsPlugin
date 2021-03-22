package me.imadenigma.gangsplugin.economy;

import me.imadenigma.gangsplugin.exceptions.VaultNotFound;
import me.lucko.helper.Helper;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

public abstract class EconomyManager implements me.imadenigma.gangsplugin.economy.Economy {

    protected final Economy economylib;

    public EconomyManager() {
        this.economylib = setupEconomy();
    }

    private Economy setupEconomy() {
        if (Helper.server().getPluginManager().getPlugin("Vault") == null) {
            throw new VaultNotFound();
        }
        RegisteredServiceProvider<Economy> rsp = Helper.server().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            throw new VaultNotFound();
        }
        return rsp.getProvider();
    }


}
