package com.jombles.prefixmanager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager {

    public Main plugin;
    public File mainConfigFile;
    public FileConfiguration mainConfig;

    public ConfigManager(Main plugin){
        this.plugin = plugin;
    }

    public void setupConfig(){
        this.mainConfigFile = new File(this.plugin.getDataFolder(), "config.yml");

        if (!this.mainConfigFile.exists()){
            try {
                this.mainConfigFile.createNewFile();
                System.out.println("[Prefix Manager] Config file created (config.yml)");
            } catch (Exception e) {
                System.out.println("[Prefix Manager] Could not create config file: " + e.getMessage());
            }
        }
        reloadConfig();
    }

    public void reloadConfig(){
        this.mainConfig = (FileConfiguration) YamlConfiguration.loadConfiguration(this.mainConfigFile);
        System.out.println("[Prefix Manager] Reloaded config file");
    }

    public void saveConfig(){
        try{
            this.mainConfig.save(this.mainConfigFile);
            System.out.println("[Prefix Manager] Config file saved");
        } catch (IOException e) {
            System.out.println("[Prefix Manager] Could not save config file: " + e.getMessage());
        }
    }
}
