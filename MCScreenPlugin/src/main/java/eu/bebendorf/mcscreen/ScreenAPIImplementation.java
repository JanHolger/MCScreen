package eu.bebendorf.mcscreen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.bebendorf.mcscreen.api.Screen;
import eu.bebendorf.mcscreen.api.ScreenAPI;
import eu.bebendorf.mcscreen.api.ScreenListener;
import eu.bebendorf.mcscreen.api.helper.MouseButton;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScreenAPIImplementation implements ScreenAPI {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private List<ScreenImplementation> screens = new ArrayList<>();
    private File saveFile;
    private List<ScreenListener> listeners = new ArrayList<>();

    public ScreenAPIImplementation(){
        this(null);
    }

    public ScreenAPIImplementation(File saveFile){
        this.load(saveFile);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(MCScreen.getInstance(), () -> screens.forEach(ScreenImplementation::renderSync), 10L, 1L);
    }

    List<ScreenListener> getListeners(){
        return new ArrayList<>(listeners);
    }

    public Screen getScreen(int id){
        for(ScreenImplementation screen : screens) {
            if (screen.getId() == id)
                return screen;
        }
        return null;
    }

    public Screen getScreen(Player player){
        ScreenImplementation current = null;
        Location currentPos = null;
        for(ScreenImplementation screen : screens){
            if(!screen.getLocation().getWorld().equals(player.getWorld()))
                continue;
            Location lookPos = screen.getLookLocation(player);
            if(lookPos != null){
                if(current == null || lookPos.distance(player.getEyeLocation()) < currentPos.distance(player.getEyeLocation())){
                    current = screen;
                    currentPos = lookPos;
                }
            }
        }
        return current;
    }

    public void addListener(ScreenListener screenListener){
        listeners.add(screenListener);
    }

    public void load(File saveFile){
        this.saveFile = saveFile;
        screens.clear();
        if(saveFile == null)
            return;
        if(!saveFile.exists()){
            this.save(saveFile);
            return;
        }
        JsonObject json = gson.fromJson(readFile(saveFile), JsonObject.class);
        for(Map.Entry<String, JsonElement> entry : json.entrySet()){
            screens.add(new ScreenImplementation(Integer.parseInt(entry.getKey()), entry.getValue().getAsJsonObject()));
        }
    }

    public List<Screen> getScreens(){
        return new ArrayList<>(screens);
    }

    public void save(File saveFile){
        JsonObject json = new JsonObject();
        for(ScreenImplementation screen : screens){
            json.add(String.valueOf(screen.getId()), screen.toJson());
        }
        writeFile(saveFile, gson.toJson(json));
    }

    private String readFile(File file){
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream fis = new FileInputStream(file);
            while (fis.available() > 0){
                byte[] data = new byte[Math.min(fis.available(), 4096)];
                fis.read(data);
                sb.append(new String(data, StandardCharsets.UTF_8));
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private void writeFile(File file, String content){
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(content.getBytes(StandardCharsets.UTF_8));
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Screen createScreen(Location l1, Location l2, BlockFace direction){
        int id = getFreeId();
        ScreenImplementation screen = new ScreenImplementation(id, l1, l2, direction);
        screens.add(screen);
        if(saveFile != null)
            this.save(saveFile);
        listeners.forEach(l -> l.onCreate(screen));
        return screen;
    }

    public void remove(Screen screen){
        ScreenImplementation screenImplementation = (ScreenImplementation) screen;
        listeners.forEach(l -> l.onCreate(screen));
        screens.remove(screen);
        save(saveFile);
        screenImplementation.remove();
    }

    private int getFreeId(){
        for(int i=0; i < Integer.MAX_VALUE; i++){
            if(getScreen(i) == null)
                return i;
        }
        return -1;
    }

    public ScreenImplementation findScreenByMapId(short mapId){
        for(ScreenImplementation screen : screens){
            if(screen.isMapOfScreen(mapId))
                return screen;
        }
        return null;
    }

}
