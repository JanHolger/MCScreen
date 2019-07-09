package eu.bebendorf.mcscreen;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CustomPacketSender {

    private Method renderMethod;
    private Field bufferField;
    private Method getHandleMethod;
    private Field playerConnectionField;
    private Method sendPacketMethod;

    private Constructor<?> packetV1Constructor;

    public CustomPacketSender() throws NoSuchMethodException, NoSuchFieldException {
        Class<?> craftMapViewClass = getCBClass("map.CraftMapView");
        Class<?> craftPlayerClass = getCBClass("entity.CraftPlayer");
        renderMethod = craftMapViewClass.getDeclaredMethod("render", craftPlayerClass);
        bufferField = renderMethod.getReturnType().getDeclaredField("buffer");
        getHandleMethod = craftPlayerClass.getDeclaredMethod("getHandle");
        playerConnectionField = getHandleMethod.getReturnType().getDeclaredField("playerConnection");
        sendPacketMethod = playerConnectionField.getType().getDeclaredMethod("sendPacket", getNMSClass("Packet"));

        Class<?> packetClass = getNMSClass("PacketPlayOutMap");
        for(Constructor<?> c : packetClass.getDeclaredConstructors()){
            if(c.getParameterCount() > 0)
                packetV1Constructor = c;
        }
    }

    public void send(Player player, MapView mapView){
        try {
            byte[] data = (byte[]) bufferField.get(renderMethod.invoke(mapView, player));
            Object packet = packetV1(mapView.getId(), mapView.getScale().getValue(), data);
            sendPacket(player, packet);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    private Object packetV1(short id, byte scale, byte[] data) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        List<?> icons = new ArrayList<>();
        return packetV1Constructor.newInstance((int) id, scale, icons, data, 0, 0, 128, 128);
    }

    private void sendPacket(Player player, Object packet) {
        try {
            Object playerConnection = playerConnectionField.get(getHandleMethod.invoke(player));
            sendPacketMethod.invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Class<?> getNMSClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Class<?> getCBClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("org.bukkit.craftbukkit." + version + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


}
