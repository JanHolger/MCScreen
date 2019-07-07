package eu.bebendorf.mcscreen.api;

import eu.bebendorf.mcscreen.api.helper.MouseButton;
import eu.bebendorf.mcscreen.api.helper.ScreenPixel;
import org.bukkit.entity.Player;

public interface ScreenListener {

    void onClick(Player player, Screen screen, MouseButton button, ScreenPixel pixel);
    void onRemove(Screen screen);
    void onCreate(Screen screen);
    default boolean canRemove(Player player, Screen screen){
        return true;
    }

}
