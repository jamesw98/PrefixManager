package com.jombles.prefixmanager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Join implements Listener {

    public Main plugin;
    public HashMap<String, PlayerPrefixes> data;
    public String defaultPrefix;

    public Join(Main plugin, HashMap<String, PlayerPrefixes> data){
        this.plugin = plugin;
        this.data = data;
        this.defaultPrefix = this.plugin.getConfig().getString("default");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        String userName = event.getPlayer().getName();

        if (!data.containsKey(userName)){
            ArrayList<String> temp = new ArrayList<>();
            temp.add(defaultPrefix);

            data.put(userName, new PlayerPrefixes(temp));

            List<String> players = plugin.getConfig().getStringList("players");
            players.add(userName);

            this.plugin.getConfig().set("players", players);
            this.plugin.getConfig().set("data." + userName, new String[]{this.defaultPrefix});
            this.plugin.updateConfig();

            this.plugin.updateData(this.data);

            System.out.println("[Prefix Manager] Player: " + userName + " added to the config");
        }
    }
}
