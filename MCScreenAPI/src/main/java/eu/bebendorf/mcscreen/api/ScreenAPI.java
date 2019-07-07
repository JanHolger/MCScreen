package eu.bebendorf.mcscreen.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

public interface ScreenAPI {

    Screen getScreen(int id);
    Screen getScreen(Player player);
    List<Screen> getScreens();
    void remove(Screen screen);
    void addListener(ScreenListener listener);

    static ScreenAPI getInstance(){
        Plugin plugin = Bukkit.getPluginManager().getPlugin("MCScreen");
        if(plugin == null)
            return null;
        if(!(plugin instanceof ScreenAPIPlugin))
            return null;
        ScreenAPIPlugin screenAPIPlugin = (ScreenAPIPlugin) plugin;
        return screenAPIPlugin.getAPI();
    }

}
