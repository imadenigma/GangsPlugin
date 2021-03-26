package me.imadenigma.gangsplugin;

import me.imadenigma.gangsplugin.utils.Utils;
import me.lucko.helper.Helper;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;

import javax.rmi.CORBA.Util;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

public class Configuration {

    private static ConfigurationNode language;
    private static ConfigurationNode config;

    public Configuration() throws IOException {
        final File languageFile = new File(GangsPlugin.getSingleton().getDataFolder(),"language.yml");
        final File configFile = new File(GangsPlugin.getSingleton().getDataFolder(),"config.yml");
        if (!languageFile.exists()) {
            languageFile.getParentFile().mkdirs();
            languageFile.createNewFile();
        }
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            configFile.createNewFile();
        }
        if (configFile.getUsableSpace() == 0) Utils.INSTANCE.copyContent(Helper.hostPlugin().getBundledFile("config.yml"),configFile);
        if (languageFile.length() == 0) Utils.INSTANCE.copyContent(Helper.hostPlugin().getBundledFile("language.yml"),languageFile);
    System.out.println(languageFile.length() + "&&");
        System.out.println(Helper.hostPlugin().getBundledFile("language.yml").getUsableSpace());


        final YAMLConfigurationLoader languageLoader = YAMLConfigurationLoader.builder().setFile(languageFile).build();
        final YAMLConfigurationLoader configLoader = YAMLConfigurationLoader.builder().setFile(configFile).build();

        if (languageLoader.canLoad()) language = languageLoader.load();
        if (configLoader.canLoad()) config = configLoader.load();
    }

    public static ConfigurationNode getConfig() {
        return config;
    }

    public static ConfigurationNode getLanguage() {
        return language;
    }
}
