package com.jombles.prefixmanager;

import java.util.HashMap;
import java.util.List;

public class ReadPlayers {

    public Main plugin;
    public List<String> playerNames;
    public HashMap<String, PlayerPrefixes> players;

    public ReadPlayers(Main plugin){
        this.plugin = plugin;
        this.playerNames = this.plugin.getConfig().getStringList("players");
        players = new HashMap();

        for (String name : playerNames){
            players.put(name, new PlayerPrefixes(this.plugin.getConfig().getStringList("data." + name)));
        }
    }

    public List<String> getPrefixesForPlayer(String name){
        return players.get(name).getPrefixes();
    }

    public HashMap<String, PlayerPrefixes> getData(){
        return players;
    }
}
