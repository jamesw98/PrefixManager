package com.jombles.prefixmanager;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Prefix implements CommandExecutor {

    public HashMap<String, PlayerPrefixes> data;
    public Main plugin;
    public String defaultPrefix;

    public Prefix(HashMap<String, PlayerPrefixes> data, Main plugin){
        this.data = data;
        this.plugin = plugin;

        defaultPrefix = this.plugin.getConfig().getString("default");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        // player execution
        if (sender instanceof Player){
            Player user = (Player) sender;

            // user checking their available prefixes
            if (args.length == 0) {
                checkPrefixesPlayer(user, true);
            }

            // incorrect usage for set
            else if (args.length == 1 && args[0].equals("set")) {
                user.sendMessage("§2Proper Usage: §a/prefix set §l<number>");
            }
            // /prefix set <number>
            else if (args.length == 2 && args[0].equals("set")) {
                List<String> prefixesForUser = checkPrefixesPlayer(user, false);

                int max = prefixesForUser.size() - 1;
                int selection = 0;
                try{
                    selection = Integer.parseInt(args[1]);
                } catch (Exception e) {
                     user.sendMessage("§2Invalid input");
                     return false;
                }

                if (selection > max || selection < 0){
                    user.sendMessage("§2Invalid input");
                    return false;
                }

                String manager = "";
                if (this.plugin.getConfig().getBoolean("permissionManager.luckperms")){
                    manager = "luck";
                }
                else if (this.plugin.getConfig().getBoolean("permissionManager.pex")){
                    manager = "pex";
                }
                else {
                    manager = "nick";
                }

                setPrefixPlayer(user, prefixesForUser.get(selection), manager);
                user.sendMessage("§2Your prefix as been set to: " + prefixesForUser.get(selection));
            }
            // incorrect usage for add
            else if ((args.length == 1 || args.length == 2)  && args[0].equals("add")){
                if (!user.hasPermission("prefixmanager.add")){
                    user.sendMessage("§2You do not have permission for this command. If you feel this is an error, contact a server administrator");
                    return false;
                }
                user.sendMessage("§2Proper Usage: §a/prefix add §l<user> §l<prefix>");
            }
            else if (args.length == 3 && args[0].equals("set")){
                if (!user.hasPermission("prefixmanager.set.other")){
                    user.sendMessage("§2You do not have permission for this command. If you feel this is an error, contact a server administrator");
                    return false;
                }

                int selection = checkSelection(args[2]);
                String userName = args[1];

                if (Bukkit.getPlayer(args[1]) == null){
                    user.sendMessage("§2User: §a " + userName + " §2does not exist");
                    return false;
                }

                int max = checkPrefixesPlayer(Bukkit.getPlayer(userName), false).size() - 1;

                if (selection > max || selection < 0){
                    user.sendMessage("§2Invalid input");
                    return false;
                }

                String manager = "";
                if (this.plugin.getConfig().getBoolean("permissionManager.luckperms")){
                    manager = "luck";
                }
                else if (this.plugin.getConfig().getBoolean("permissionManager.pex")){
                    manager = "pex";
                }
                else {
                    manager = "nick";
                }

                List<String> prefixesForUser = checkPrefixesPlayer(user, false);
                setPrefixPlayer(Bukkit.getPlayer(userName), prefixesForUser.get(selection), manager);
            }
            // /prefix add <user> <prefix string>
            else if (args.length == 3 && args[0].equals("add")){
                if (!user.hasPermission("prefixmanager.add")){
                    user.sendMessage("§2You do not have permission for this command. If you feel this is an error, contact a server administrator");
                    return false;
                }
                String userName = args[1];

                if (Bukkit.getPlayer(userName) == null){
                    user.sendMessage("§2User: §a " + userName + " §2does not exist");
                    return false;
                }

                if (!data.containsKey(userName)) {
                    String newPrefix = args[2];
                    newPrefix = newPrefix.replace('&', '§');
                    newPrefix = newPrefix.replace('_', ' ');

                    if (data.get(userName).getPrefixes().contains(newPrefix)){
                        user.sendMessage("§2User: §a" + userName + " §2already has that prefix");
                        return false;
                    }

                    System.out.println("[Prefix Manager] New player: " + userName + " registered.");

                    ArrayList<String> tempNames = new ArrayList<String>();
                    tempNames.add(newPrefix);

                    data.put(userName, new PlayerPrefixes(tempNames));

                    List<String> currentPlayers = plugin.getConfig().getStringList("players");
                    currentPlayers.add(userName);

                    this.plugin.getConfig().set("players", currentPlayers);
                    this.plugin.getConfig().set("data." + userName, new String[]{newPrefix});
                    this.plugin.updateConfig();

                    user.sendMessage("§2Prefix successfully added!");

                    System.out.println("[Prefix Manager] Added new prefix: " + newPrefix + " §2to user: " + userName);
                }
                else {
                    String newPrefix = args[2];
                    newPrefix = newPrefix.replace('&', '§');
                    newPrefix = newPrefix.replace('_', ' ');

                    if (data.get(userName).getPrefixes().contains(newPrefix)){
                        user.sendMessage("§2User: §a" + userName + " §2already has that prefix");
                        return false;
                    }

                    data.get(userName).addPrefix(newPrefix);

                    String[] currentPrefixes = new String[data.get(userName).getPrefixes().size()];
                    data.get(userName).getPrefixes().toArray(currentPrefixes);

                    this.plugin.getConfig().set("data." + userName, currentPrefixes);
                    this.plugin.updateConfig();

                    user.sendMessage("§2Prefix successfully added!");

                    System.out.println("[Prefix Manager] Added new prefix: " + newPrefix + " to user: " + userName);
                }
            }
            // /prefix remove <number>
            else if (args.length == 2 && args[0].equals("remove")){
                String userName = user.getName();
                List<String> prefixesForUser = checkPrefixesPlayer(user, false);

                int max = prefixesForUser.size() - 1;
                int selection = 0;
                try{
                    selection = Integer.parseInt(args[1]);
                } catch (Exception e) {
                    user.sendMessage("§2Invalid input");
                    return false;
                }

                if (selection == 0){
                    user.sendMessage("§2You can't remove the default prefix");
                    return false;
                }
                else if (selection > max || selection < 1){
                    user.sendMessage("§2Invalid input");
                    return false;
                }

                removePrefix(userName, prefixesForUser, selection);

                user.sendMessage("§2Removed prefix");
            }
            // /prefix remove <player> <number>
            else if (args.length == 3 && args[0].equals("remove")){
                if (user.hasPermission("prefixmanager.remove.other")){
                    String userName = args[1];

                    if (Bukkit.getPlayer(userName) == null){
                        user.sendMessage("§2User: §a " + userName + " §2does not exist");
                        return false;
                    }

                    List<String> prefixesForUser = checkPrefixesPlayer(Bukkit.getPlayer(userName), false);

                    int selection = checkSelection(args[1]);

                    if (selection == -1 || selection > prefixesForUser.size() - 1 || selection < 1) {
                        user.sendMessage("§2Invalid input");
                        return false;
                    }

                    if (selection == 0){
                        user.sendMessage("§2You can't remove the default prefix");
                        return false;
                    }

                    removePrefix(userName, prefixesForUser, selection);

                    user.sendMessage("§2Removed prefix for player §a" + userName);
                }
                else {
                    user.sendMessage("§2You do not have permission for this command. If you feel this is an error, contact a server administrator");
                    return false;
                }
            }
            else if (args.length == 1 && args[0].equals("help")){
                user.sendMessage("§2Prefix Manager Commands:");
                user.sendMessage("§2/prefix §a- lists the prefixes available to you");
                user.sendMessage("§2/prefix set <number> §a- sets your prefix to one in your list");
                user.sendMessage("§2/prefix remove <number> §a- removes a prefix from your list");
                user.sendMessage("§2/prefix list <user> §a- lists the prefixes for another user §c(admin command)");
                user.sendMessage("§2/prefix add <user> <prefix> §a- adds a prefix to a user §c(admin command)");
                user.sendMessage("§2/prefix remove <user> <number> §a- removes a prefix from a player's list §c(admin command)");
            }
            else if (args.length == 2 && args[0].equals("list")){
                if (user.hasPermission("prefixmanager.list.other")){
                    Player otherUser = Bukkit.getPlayer(args[1]);

                    if (otherUser == null){
                        user.sendMessage("§aPlayer not found or offline");
                        return false;
                    }

                    checkPrefixesPlayer(otherUser, true, user);
                }
                else {
                    user.sendMessage("§2You do not have permission for this command. If you feel this is an error, contact a server administrator");
                    return false;
                }
            }
            else {
                user.sendMessage("§2Unrecognized command, type §a/prefix help §2for a list of commands");
            }
        }
        // console execution
        else {
            if (args.length == 3 && args[0].equals("add")){
                String userName = args[1];

                if (!data.containsKey(userName)) {
                    String newPrefix = convert(args[2]);

                    if (data.get(userName).getPrefixes().contains(newPrefix)){
                        System.out.println("§2User: §a" + userName + " §2already has that prefix");
                        return false;
                    }

                    System.out.println("[Prefix Manager] New player: " + userName + " registered.");

                    ArrayList<String> tempNames = new ArrayList<String>();
                    tempNames.add(newPrefix);

                    data.put(userName, new PlayerPrefixes(tempNames));

                    List<String> currentPlayers = plugin.getConfig().getStringList("players");
                    currentPlayers.add(userName);

                    addToConfigNewPlayer(currentPlayers, userName, newPrefix);

                    System.out.println("[Prefix Manager] Added new prefix: " + newPrefix + " to user: " + userName);
                }
                else {
                    String newPrefix = convert(args[2]);

                    if (data.get(userName).getPrefixes().contains(newPrefix)){
                        System.out.println("§2User: §a" + userName + " §2already has that prefix");
                        return false;
                    }

                    data.get(userName).addPrefix(newPrefix);

                    String[] currentPrefixes = new String[data.get(userName).getPrefixes().size()];
                    data.get(userName).getPrefixes().toArray(currentPrefixes);

                    addToConfig(currentPrefixes, userName);

                    System.out.println("[Prefix Manager] Added new prefix: " + newPrefix + " to user: " + userName);
                }
            }
        }
        return true;
    }

    private void addToConfigNewPlayer(List<String> currentPlayers, String userName, String newPrefix){
        this.plugin.getConfig().set("players", currentPlayers);
        this.plugin.getConfig().set("data." + userName, new String[]{newPrefix});
        this.plugin.updateConfig();
    }

    private void addToConfig(String[] currentPrefixes, String userName){
        this.plugin.getConfig().set("data." + userName, currentPrefixes);
        this.plugin.updateConfig();
    }

    /**
     * converts an entered prefix to the proper format for the config file
     * @param newPrefix the new prefix
     * @return the converted prefix
     */
    private String convert(String newPrefix){
        newPrefix = newPrefix.replace('&', '§');
        newPrefix = newPrefix.replace('_', ' ');
        return newPrefix;
    }

    /**
     * checks if a selection is valid
     * @param selection the selection
     * @return 0-inf if valid, -1 if invalid
     */
    private int checkSelection(String selection){
        int temp = 0;
        try{
            temp = Integer.parseInt(selection);
        } catch (Exception e) {
            return -1;
        }
        return temp;
    }

    /**
     * removes a prefix from a user
     * @param userName the user to remove a prefix from
     * @param prefixesForUser a list of prefixes for the user
     * @param selection the index of the prefix to remove
     */
    private void removePrefix(String userName, List<String> prefixesForUser, int selection){
        data.get(userName).removePrefix(prefixesForUser.get(selection));
        this.plugin.getConfig().set("data." + userName, data.get(userName).getPrefixes());
        this.plugin.updateConfig();
    }

    /**
     * sets a prefix for a user
     * @param user the user to set a prefix for
     * @param newPrefix the new prefix
     * @param manager a string representing which permission manager the server is using
     */
    private void setPrefixPlayer(Player user, String newPrefix, String manager){
        if (manager.equals("luck")){
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + user.getName() + " meta clear prefixes");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + user.getName() + " meta addprefix 2 " + "\"" + newPrefix + "\"");
        }
        else if (manager.equals("pex")){
            user.sendMessage("§2PEX Support Coming Soon");
        }
        else {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "nick " + user.getName() + " " + newPrefix + user.getName());
        }
    }



    /**
     * returns a list of prefixes for a specific player
     * @param user the user to check
     * @param print whether or not to print out the list
     * @return a list of prefixes
     */
    private List<String> checkPrefixesPlayer(Player user, boolean print){
        this.data = this.plugin.getData();

        String name = user.getName();

        // if the player isn't already in the system, add them to the system with the default prefix
        if (!data.containsKey(name)){
            System.out.println("[Prefix Manager] New player: " + name + " registered.");

            ArrayList<String> tempNames = new ArrayList<String>();
            tempNames.add(defaultPrefix);

            data.put(name, new PlayerPrefixes(tempNames));

            List<String> currentPlayers = plugin.getConfig().getStringList("players");
            currentPlayers.add(name);

            this.plugin.getConfig().set("players", currentPlayers);
            this.plugin.getConfig().set("data." + name, new String[]{defaultPrefix});
            this.plugin.updateConfig();
        }

        List<String> prefixes = data.get(name).getPrefixes();

        if (print){
            user.sendMessage("§a--- §2Prefixes Available §a---");

            int count = 0;
            for(String i : prefixes){
                user.sendMessage("§a" + count++ + ": " + i);
            }

            user.sendMessage("§2To set a prefix: §a/prefix set <number>");
        }

        return prefixes;
    }

    /**
     * returns a list of prefixes for a specific player and prints it to another player
     *
     * @param user the user to check
     * @param print whether or not to print out the list
     * @param sendTo the user to send the message too
     * @return a list of prefixes
     */
    private List<String> checkPrefixesPlayer(Player user, boolean print, Player sendTo){
        this.data = this.plugin.getData();

        String name = user.getName();

        // if the player isn't already in the system, add them to the system with the default prefix
        if (!data.containsKey(name)){
            System.out.println("[Prefix Manager] New player: " + name + " registered.");

            ArrayList<String> tempNames = new ArrayList<String>();
            tempNames.add(defaultPrefix);

            data.put(name, new PlayerPrefixes(tempNames));

            List<String> currentPlayers = plugin.getConfig().getStringList("players");
            currentPlayers.add(name);

            this.plugin.getConfig().set("players", currentPlayers);
            this.plugin.getConfig().set("data." + name, new String[]{defaultPrefix});
            this.plugin.updateConfig();
        }

        List<String> prefixes = data.get(name).getPrefixes();

        if (print){
            sendTo.sendMessage("§a--- §2Prefixes Available for: §l" + name + "§r §a---");

            int count = 0;
            for(String i : prefixes){
                sendTo.sendMessage("§a" + count++ + ": " + i);
            }

            sendTo.sendMessage("§2To set a prefix: §a/prefix set <number>");
        }

        return prefixes;
    }
}
