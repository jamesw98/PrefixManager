package com.jombles.prefixmanager;

import java.util.List;

public class PlayerPrefixes {

    public List<String> prefixes;

    public PlayerPrefixes(List<String> prefixes){
        this.prefixes = prefixes;
    }

    public List<String> getPrefixes(){
        return this.prefixes;
    }

    public void addPrefix(String newPre){
        this.prefixes.add(newPre);
    }

    public void removePrefix(String toRemove){
        this.prefixes.remove(toRemove);
    }
}
