package eu.bebendorf.mcscreen;

import eu.bebendorf.mcscreen.api.ScreenAPI;
import eu.bebendorf.mcscreen.api.ScreenAPIPlugin;
import eu.bebendorf.mcscreen.command.MCScreenCommand;
import eu.bebendorf.mcscreen.listener.ItemFrameListener;
import eu.bebendorf.mcscreen.listener.PlaceListener;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class MCScreen extends JavaPlugin implements Listener, ScreenAPIPlugin {
    @Getter
    ScreenAPIImplementation screenManager;

    @Getter
    private static MCScreen instance;

    public void onEnable(){
        instance = this;
        Bukkit.getPluginManager().registerEvents(new PlaceListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ItemFrameListener(this), this);
        if(!getDataFolder().exists())
            getDataFolder().mkdir();
        screenManager = new ScreenAPIImplementation(new File(getDataFolder(), "screens.json"));
        getCommand("mcscreen").setExecutor(new MCScreenCommand(this));
    }

    public ScreenAPI getAPI() {
        return screenManager;
    }

    public void sendPrefixed(Player player, String message){
        player.sendMessage("§8[§cScreen§8] "+(!message.startsWith("§")?"§7":"")+message);
    }

    public ItemStack[] getTools(){
        ItemStack tool = new ItemStack(Material.REDSTONE_BLOCK, 1);
        ItemMeta meta = tool.getItemMeta();
        meta.setDisplayName("§cScreen Placer");
        meta.setLore(new ArrayList<String>(){{
            add(" ");
            add("§7Left-click one corner,");
            add("§7then right-click the other.");
            add(" ");
        }});
        tool.setItemMeta(meta);
        return new ItemStack[]{tool};
    }

}
