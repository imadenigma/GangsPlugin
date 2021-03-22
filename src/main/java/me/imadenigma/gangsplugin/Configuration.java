package me.imadenigma.gangsplugin;

import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import java.io.File;
import java.io.IOException;

public class Configuration {

    private static ConfigurationNode language;
    private static ConfigurationNode config;

    public Configuration() throws IOException {
        final YAMLConfigurationLoader languageLoader = YAMLConfigurationLoader.builder().setFile(new File(GangsPlugin.getSingleton().getDataFolder(),"language.yml")).build();
        final YAMLConfigurationLoader configLoader = YAMLConfigurationLoader.builder().setFile(new File(GangsPlugin.getSingleton().getDataFolder(),"config.yml")).build();

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
