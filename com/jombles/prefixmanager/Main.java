package com.jombles.prefixmanager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;

public class Main  extends JavaPlugin {

    public ConfigManager cm;
    public ReadPlayers rp;
    public HashMap<String, PlayerPrefixes> playerData;

    @Override
    public void onEnable(){
        this.loadConfig();
        rp = new ReadPlayers(this);
        playerData = rp.getData();

        this.getCommand("prefix").setExecutor(new Prefix(playerData, this));
        this.getServer().getPluginManager().registerEvents(new Join(this, playerData), this);

        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&3PrefixManager&7] &aEnabled"));
    }

    @Override
    public void onDisable(){
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&3PrefixManager&7] &4Disabled"));
    }

    private void loadConfig(){
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        this.cm = new ConfigManager(this);
        this.cm.setupConfig();
    }

    public void updateConfig(){
        this.cm.saveConfig();
        this.saveConfig();
    }

    public void refreshConfig(){
        this.cm.reloadConfig();
    }

    public HashMap<String, PlayerPrefixes> getData(){
        return playerData;
    }

    public void updateData(HashMap<String, PlayerPrefixes> data){
        this.playerData = data;
    }
}
