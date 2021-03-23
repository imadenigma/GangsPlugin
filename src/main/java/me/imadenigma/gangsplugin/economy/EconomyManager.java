package me.imadenigma.gangsplugin.economy;

import me.imadenigma.gangsplugin.exceptions.VaultNotFound;
import me.lucko.helper.Helper;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;


public class EconomyManager {

    public static EconomyManager INSTANCE;


    private Economy economylib;

    public EconomyManager() {
        setupEconomy();
        INSTANCE = this;
    }


    private void setupEconomy() {
        if(Helper.server().getPluginManager().isPluginEnabled("Vault")) {
            final RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> rsp = Helper
                    .services()
                    .getRegistration(net.milkbowl.vault.economy.Economy.class);

            if (rsp == null) throw new VaultNotFound();
            economylib = rsp.getProvider();
            if(economylib == null) throw new VaultNotFound();
        } else {
            throw new VaultNotFound();
        }
    }

    public Economy getEconomylib() {
        return economylib;
    }
}
