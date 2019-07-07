package eu.bebendorf.mcscreen.command;

import eu.bebendorf.mcscreen.MCScreen;
import eu.bebendorf.mcscreen.ScreenImplementation;
import eu.bebendorf.mcscreen.api.Screen;
import lombok.AllArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class MCScreenCommand implements CommandExecutor {

    private MCScreen plugin;

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        if(cmd.getName().equalsIgnoreCase("mcscreen")){
            if(args.length > 0){
                if(args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("delete")){
                    if(args.length == 1){
                        if(!sender.hasPermission("mcscreen.remove")){
                            plugin.sendPrefixed(player, "§cYou are not allowed to do that!");
                            return true;
                        }
                        ScreenImplementation screen = (ScreenImplementation) plugin.getScreenManager().getScreen(player);
                        if(screen != null){
                            if(player.hasPermission("mcscreen.remove.admin") || screen.removalCheck(player)){
                                plugin.getAPI().remove(screen);
                                plugin.sendPrefixed(player, "The screen §8(§e"+screen.getId()+"§8) §7has been removed!");
                            }else{
                                plugin.sendPrefixed(player, "§cYou are not allowed to do that!");
                            }
                        }else{
                            plugin.sendPrefixed(player, "§cThere was no screen found in your line of sight!");
                        }
                        return true;
                    }
                    if(args.length == 2){
                        if(!sender.hasPermission("mcscreen.remove")){
                            plugin.sendPrefixed(player, "§cYou are not allowed to do that!");
                            return true;
                        }
                        ScreenImplementation screen = (ScreenImplementation) plugin.getScreenManager().getScreen(Integer.parseInt(args[1]));
                        if(screen != null){
                            if(player.hasPermission("mcscreen.remove.admin") || screen.removalCheck(player)){
                                plugin.getAPI().remove(screen);
                                plugin.sendPrefixed(player, "The screen §8(§e"+screen.getId()+"§8) §7has been removed!");
                            }else{
                                plugin.sendPrefixed(player, "§cYou are not allowed to do that!");
                            }
                        }else{
                            plugin.sendPrefixed(player, "§cThere was no screen found with that id!");
                        }
                        return true;
                    }
                }
                if(args[0].equalsIgnoreCase("tools")){
                    if(args.length == 1){
                        if(!sender.hasPermission("mcscreen.tools")){
                            plugin.sendPrefixed(player, "§cYou are not allowed to do that!");
                            return true;
                        }
                        player.getInventory().addItem(plugin.getTools());
                        plugin.sendPrefixed(player, "You received the screen creation tools!");
                        return true;
                    }
                }
                if(args[0].equalsIgnoreCase("list")){
                    if(args.length == 1){
                        if(!sender.hasPermission("mcscreen.list")){
                            plugin.sendPrefixed(player, "§cYou are not allowed to do that!");
                            return true;
                        }
                        StringBuilder sb = new StringBuilder();
                        for(Screen screen : plugin.getScreenManager().getScreens()){
                            if(sb.length() > 0)
                                sb.append("§8, §e");
                            sb.append(screen.getId());
                        }
                        plugin.sendPrefixed(player, "Screens§8: §e"+sb.toString());
                        return true;
                    }
                }
                if(args[0].equalsIgnoreCase("teleport") || args[0].equalsIgnoreCase("tp")){
                    if(args.length == 2){
                        if(!sender.hasPermission("mcscreen.teleport")){
                            plugin.sendPrefixed(player, "§cYou are not allowed to do that!");
                            return true;
                        }
                        Screen screen = plugin.getScreenManager().getScreen(Integer.parseInt(args[1]));
                        if(screen != null){
                            player.teleport(screen.getLocation());
                            plugin.sendPrefixed(player, "You were teleported to the screen §8(§e"+screen.getId()+"§8)§7!");
                        }else{
                            plugin.sendPrefixed(player, "§cThere was no screen found with that id!");
                        }
                        return true;
                    }
                }
            }
            player.sendMessage("§e/mcscreen §blist");
            player.sendMessage("§e/mcscreen §bteleport|tp <id>");
            player.sendMessage("§e/mcscreen §bdelete|remove [id]");
            player.sendMessage("§e/mcscreen §btools");
            return true;
        }
        return false;
    }
}
