package eu.bebendorf.mcscreen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import eu.bebendorf.mcscreen.api.Screen;
import eu.bebendorf.mcscreen.api.ScreenListener;
import eu.bebendorf.mcscreen.api.helper.ImageWrapper;
import eu.bebendorf.mcscreen.api.helper.MouseButton;
import eu.bebendorf.mcscreen.api.helper.ScreenPixel;
import eu.bebendorf.mcscreen.intersection.IntersectionHelper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.io.File;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScreenImplementation implements Screen {

    @Getter
    int id;
    @Getter
    Location location;
    short[][] maps;
    @Getter
    BlockFace direction;

    public ScreenImplementation(int id, Location l1, Location l2, BlockFace direction){
        this.id = id;
        int xChange = Math.max(l1.getBlockX(), l2.getBlockX()) - Math.min(l1.getBlockX(), l2.getBlockX());
        int zChange = Math.max(l1.getBlockZ(), l2.getBlockZ()) - Math.min(l1.getBlockZ(), l2.getBlockZ());
        int width = (xChange + zChange) + 1;
        int height = (Math.max(l1.getBlockY(), l2.getBlockY()) - Math.min(l1.getBlockY(), l2.getBlockY())) + 1;
        maps = new short[width][height];
        if(direction == BlockFace.WEST || direction == BlockFace.SOUTH){
            this.location = new Location(l1.getWorld(), Math.min(l1.getBlockX(), l2.getBlockX()), Math.min(l1.getBlockY(), l2.getBlockY()), Math.min(l1.getBlockZ(), l2.getBlockZ()));
        }else{
            this.location = new Location(l1.getWorld(), Math.max(l1.getBlockX(), l2.getBlockX()), Math.min(l1.getBlockY(), l2.getBlockY()), Math.max(l1.getBlockZ(), l2.getBlockZ()));
        }
        this.direction = direction;
        for(int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Location frameLocation = getBlockLocation(x, y);
                if(direction == BlockFace.NORTH){
                    frameLocation.setYaw(180);
                }
                if(direction == BlockFace.SOUTH){
                    frameLocation.setYaw(0);
                }
                if(direction == BlockFace.EAST){
                    frameLocation.setYaw(-90);
                }
                ItemFrame itemFrame = frameLocation.getWorld().spawn(frameLocation, ItemFrame.class);
                itemFrame.setFacingDirection(direction);
                itemFrame.setRotation(Rotation.NONE);
                MapView mapView = Bukkit.createMap(frameLocation.getWorld());
                for(MapRenderer renderer : mapView.getRenderers())
                    mapView.removeRenderer(renderer);
                itemFrame.setItem(new ItemStack(Material.MAP, 1, mapView.getId()));
                maps[x][y] = mapView.getId();
            }
        }
    }

    public ScreenImplementation(int id, JsonObject json){
        this.id = id;
        JsonObject loc = json.getAsJsonObject("location");
        this.location = new Location(Bukkit.getWorld(loc.get("world").getAsString()), loc.get("x").getAsInt(), loc.get("y").getAsInt(), loc.get("z").getAsInt());
        this.direction = BlockFace.valueOf(json.get("direction").getAsString());
        int width = json.get("maps").getAsJsonArray().size();
        int height = json.get("maps").getAsJsonArray().get(0).getAsJsonArray().size();
        this.maps = new short[width][height];
        JsonArray jsonMaps = json.getAsJsonArray("maps");
        for(int x = 0; x < width; x++){
            JsonArray jsonMapsRow = jsonMaps.get(x).getAsJsonArray();
            for(int y = 0; y < height; y++){
                maps[x][y] = jsonMapsRow.get(y).getAsShort();
                MapView mapView = Bukkit.getMap(maps[x][y]);
                for(MapRenderer renderer : mapView.getRenderers())
                    mapView.removeRenderer(renderer);
            }
        }
    }

    public boolean isMapOfScreen(short mapId){
        for(int x = 0; x < maps.length; x++){
            for(int y = 0; y < maps[x].length; y++){
                if(maps[x][y] == mapId)
                    return true;
            }
        }
        return false;
    }

    public JsonObject toJson(){
        JsonObject json = new JsonObject();
        JsonObject loc = new JsonObject();
        loc.addProperty("world", location.getWorld().getName());
        loc.addProperty("x", location.getBlockX());
        loc.addProperty("y", location.getBlockY());
        loc.addProperty("z", location.getBlockZ());
        json.add("location", loc);
        json.addProperty("direction", direction.name());
        JsonArray jsonMaps = new JsonArray();
        for(int x = 0; x < getWidth(); x++){
            JsonArray jsonMapsRow = new JsonArray();
            for(int y = 0; y < getHeight(); y++){
                jsonMapsRow.add(new JsonPrimitive(maps[x][y]));
            }
            jsonMaps.add(jsonMapsRow);
        }
        json.add("maps", jsonMaps);
        return json;
    }

    public int getHeight(){
        return maps[0].length;
    }

    public int getWidth(){
        return maps.length;
    }

    public int getPixelHeight() {
        return getHeight() * 128;
    }

    public int getPixelWidth(){
        return getWidth() * 128;
    }

    public void clicked(Player player, MouseButton button, ScreenPixel pixel){
        MCScreen.getInstance().getScreenManager().getListeners().forEach(l -> l.onClick(player, this, button, pixel));
    }

    public ScreenPixel getPixel(Player player) {
        Location lookPos = getLookLocation(player);
        if(lookPos != null){
            return getPixel(lookPos);
        }
        return null;
    }

    public Location getBlockLocation(int x, int y){
        switch (direction){
            case NORTH:
                return new Location(location.getWorld(), location.getX() - x, location.getY() + y, location.getZ());
            case SOUTH:
                return new Location(location.getWorld(), location.getX() + x, location.getY() + y, location.getZ());
            case EAST:
                return new Location(location.getWorld(), location.getX(), location.getY() + y, location.getZ() - x);
            case WEST:
                return new Location(location.getWorld(), location.getX(), location.getY() + y, location.getZ() + x);
        }
        return this.location;
    }

    public void render(ImageWrapper source){
        for(int blockX = 0; blockX < getWidth(); blockX++){
            for(int blockY = 0; blockY < getHeight(); blockY++){
                ImageWrapper image = new ImageWrapper(128, 128);
                for(int x = 0; x < 128; x++){
                    for(int y = 0; y < 128; y++){
                        ImageWrapper.WrappedPixel pixel = source.getPixel((blockX * 128) + x, (source.getHeight() - ((blockY + 1) * 128)) + y);
                        image.getPixel(x, y).setRGBA(pixel.getRed(), pixel.getGreen(), pixel.getBlue(), pixel.getAlpha());
                    }
                }
                image.write(new File("tile-"+blockX+"-"+blockY+".png"));
                render(blockX, blockY, image);
            }
        }
    }

    private void render(int x, int y, ImageWrapper image){
        MapView map = Bukkit.getMap(maps[x][y]);
        for(MapRenderer renderer : map.getRenderers())
            map.removeRenderer(renderer);
        map.addRenderer(new MapRenderer() {
            public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
                mapCanvas.drawImage(0, 0, image.getImage());
            }
        });
    }

    public Location getPlaneLocation(){
        switch (direction){
            case WEST:
                return new Location(location.getWorld(), location.getX() + (15d/16d), location.getY(), location.getZ());
            case EAST:
                return new Location(location.getWorld(), location.getX() + (1d/16d), location.getY(), location.getZ());
            case NORTH:
                return new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() + (15d/16d));
            case SOUTH:
                return new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() + (1d/16d));
        }
        return location;
    }

    public ScreenPixel getPixel(Location location){
        double x = 0;
        if(direction == BlockFace.SOUTH){
            x = location.getX() - this.location.getX();
        }
        if(direction == BlockFace.NORTH){
            x = this.location.getX() - location.getX() + 1;
        }
        if(direction == BlockFace.WEST){
            x = location.getZ() - this.location.getZ();
        }
        if(direction == BlockFace.EAST){
            x = this.location.getZ() - location.getZ() + 1;
        }
        if(x < 0)
            x = 0;
        if(x > getWidth())
            x = getWidth();
        int pixelX = (int)((x / getWidth()) * getPixelWidth());
        double y = location.getY() - this.location.getY();
        if(y < 0)
            y = 0;
        if(y > getHeight())
            y = getHeight();
        int pixelY = (int)((y / getHeight()) * getPixelHeight());
        pixelY = getPixelHeight() - pixelY;
        return new ScreenPixel(pixelX, pixelY);
    }

    public void clicked(Player player, MouseButton button, Location location){
        ScreenPixel pixel = getPixel(location);
        clicked(player, button, pixel);
    }

    public Location getLookLocation(Player player){
        if(!player.getWorld().equals(location.getWorld()))
            return null;
        Location planeLocation = getPlaneLocation();
        Location intersection = IntersectionHelper.getIntersection(planeLocation, direction, player.getEyeLocation());
        if(intersection == null)
            return null;
        if(intersection.getY() < location.getY() || intersection.getY() >= location.getY() + getHeight())
            return null;
        switch (direction){
            case EAST:
                if(intersection.getZ() > location.getZ() + 1 || intersection.getZ() <= location.getZ() + 1 - getWidth())
                    return null;
                if(player.getLocation().getX() < planeLocation.getX())
                    return null;
                break;
            case WEST:
                if(intersection.getZ() < location.getZ() || intersection.getZ() >= location.getZ() + getWidth())
                    return null;
                if(player.getLocation().getX() > planeLocation.getX())
                    return null;
                break;
            case SOUTH:
                if(intersection.getX() < location.getX() || intersection.getX() >= location.getX() + getWidth())
                    return null;
                if(player.getLocation().getZ() < planeLocation.getZ())
                    return null;
                break;
            case NORTH:
                if(intersection.getX() > location.getX() + 1 || intersection.getX() <= location.getX() + 1 - getWidth())
                    return null;
                if(player.getLocation().getZ() > planeLocation.getZ())
                    return null;
                break;
        }
        if(intersection.distance(player.getEyeLocation()) > 7)
            return null;
        return intersection;
    }

    public boolean removalCheck(Player player){
        boolean remove = true;
        for(ScreenListener listener : MCScreen.getInstance().getScreenManager().getListeners()){
            remove = listener.canRemove(player, this);
        }
        return remove;
    }

    public void remove(){
        for(Entity frameEntity : location.getWorld().getEntitiesByClasses(ItemFrame.class)){
            ItemFrame frame = (ItemFrame) frameEntity;
            if(frame.getItem() == null)
                continue;
            if(frame.getItem().getType() != Material.MAP)
                continue;
            if(!isMapOfScreen(frame.getItem().getDurability()))
                continue;
            frame.remove();
        }
    }

}
