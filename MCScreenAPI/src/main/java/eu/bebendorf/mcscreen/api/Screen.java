package eu.bebendorf.mcscreen.api;

import eu.bebendorf.mcscreen.api.helper.ImageWrapper;
import eu.bebendorf.mcscreen.api.helper.ScreenPixel;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public interface Screen {

    int getId();
    void render(ImageWrapper wrapper);
    ScreenPixel getPixel(Player player);
    Location getLocation();
    BlockFace getDirection();

}
