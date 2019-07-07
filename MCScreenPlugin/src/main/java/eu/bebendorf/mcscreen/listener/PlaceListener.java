package eu.bebendorf.mcscreen.listener;

import eu.bebendorf.mcscreen.MCScreen;
import eu.bebendorf.mcscreen.api.Screen;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlaceListener implements Listener {

    Map<Player, Location> loc1 = new HashMap<>();
    MCScreen plugin;

    public PlaceListener(MCScreen plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        if(loc1.containsKey(e.getPlayer()))
            loc1.remove(e.getPlayer());
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e){
        if(e.getBlockPlaced().getType() != Material.REDSTONE_BLOCK)
            return;
        if(!e.getItemInHand().hasItemMeta())
            return;
        ItemMeta meta = e.getItemInHand().getItemMeta();
        if(!meta.hasDisplayName())
            return;
        if(!meta.getDisplayName().equals("§cScreen Placer"))
            return;
        if(!e.getPlayer().hasPermission("mcscreen.create")){
            plugin.sendPrefixed(e.getPlayer(), "§cYou are not allowed to do that!");
            return;
        }
        Location loc1 = null;
        if(this.loc1.containsKey(e.getPlayer()))
            loc1 = this.loc1.get(e.getPlayer());
        e.getBlock().setType(Material.AIR);
        if(loc1 == null){
            plugin.sendPrefixed(e.getPlayer(), "Position 1 was set!");
            this.loc1.put(e.getPlayer(), e.getBlock().getLocation());
            return;
        }
        BlockFace dir = getBlockFace(e.getBlock(), e.getBlockAgainst());
        Screen screen = plugin.getScreenManager().createScreen(loc1, e.getBlock().getLocation(), dir);
        this.loc1.remove(e.getPlayer());
        plugin.sendPrefixed(e.getPlayer(), "Screen §8(§e"+screen.getId()+"§8)§7 was created successfully!");
        e.setBuild(false);
        e.setCancelled(true);
    }

    private BlockFace getBlockFace(Block block, Block against){
        if(block.getX() < against.getX())
            return BlockFace.WEST;
        if(block.getX() > against.getX())
            return BlockFace.EAST;
        if(block.getZ() < against.getZ())
            return BlockFace.NORTH;
        if(block.getZ() > against.getZ())
            return BlockFace.SOUTH;
        return BlockFace.UP;
    }

}
