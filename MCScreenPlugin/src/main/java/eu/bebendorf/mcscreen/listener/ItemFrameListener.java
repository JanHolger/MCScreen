package eu.bebendorf.mcscreen.listener;

import eu.bebendorf.mcscreen.MCScreen;
import eu.bebendorf.mcscreen.ScreenImplementation;
import eu.bebendorf.mcscreen.api.Screen;
import eu.bebendorf.mcscreen.api.helper.MouseButton;
import eu.bebendorf.mcscreen.api.helper.ScreenPixel;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ItemFrameListener implements Listener {

    private MCScreen plugin;

    public ItemFrameListener(MCScreen plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e){
        if(e.getAction() == Action.PHYSICAL)
            return;
        ScreenImplementation screen = (ScreenImplementation) plugin.getScreenManager().getScreen(e.getPlayer());
        if(screen != null){
            MouseButton button = e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK ? MouseButton.LEFT : MouseButton.RIGHT;
            ScreenPixel pixel = screen.getPixel(e.getPlayer());
            screen.clicked(e.getPlayer(), button, pixel);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        ScreenImplementation screen = (ScreenImplementation) plugin.getScreenManager().getScreen(e.getPlayer());
        if(screen != null){
            ScreenPixel pixel = screen.getPixel(e.getPlayer());
            screen.clicked(e.getPlayer(), MouseButton.LEFT, pixel);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent e){
        ScreenImplementation screen = getEntityScreen(e.getRightClicked());
        if(screen != null){
            Location lookPos = screen.getLookLocation(e.getPlayer());
            if(lookPos != null){
                screen.clicked(e.getPlayer(), MouseButton.RIGHT, lookPos);
            }
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onHangingBreak(HangingBreakEvent e){
        ScreenImplementation screen = getEntityScreen(e.getEntity());
        if(screen != null){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onHangingBreakByPlayer(HangingBreakByEntityEvent e){
        if(e.getRemover().getType() != EntityType.PLAYER)
            return;
        Player player = (Player) e.getRemover();
        ScreenImplementation screen = getEntityScreen(e.getEntity());
        if(screen != null){
            Location lookPos = screen.getLookLocation(player);
            if(lookPos != null){
                screen.clicked(player, MouseButton.LEFT, lookPos);
            }
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamageByPlayer(EntityDamageByEntityEvent e){
        if(e.getDamager().getType() != EntityType.PLAYER)
            return;
        Player player = (Player) e.getDamager();
        ScreenImplementation screen = getEntityScreen(e.getEntity());
        if(screen != null){
            Location lookPos = screen.getLookLocation(player);
            if(lookPos != null){
                screen.clicked(player, MouseButton.LEFT, lookPos);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e){
        Screen screen = getEntityScreen(e.getEntity());
        if(screen != null){
            e.setCancelled(true);
        }
    }

    private ScreenImplementation getEntityScreen(Entity entity){
        if(entity.getType() != EntityType.ITEM_FRAME)
            return null;
        ItemFrame frame = (ItemFrame) entity;
        if(frame.getItem() == null)
            return null;
        if(frame.getItem().getType() != Material.MAP)
            return null;
        return plugin.getScreenManager().findScreenByMapId(frame.getItem().getDurability());
    }

}
