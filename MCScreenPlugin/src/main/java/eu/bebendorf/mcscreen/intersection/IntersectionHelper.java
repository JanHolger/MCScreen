package eu.bebendorf.mcscreen.intersection;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;

public class IntersectionHelper {

    public static Location getIntersection(Location screenLocation, BlockFace screenDirection, Location playerLook){
        if(!screenLocation.getWorld().equals(playerLook.getWorld()))
            return null;
        Vector3D planePoint = new Vector3D(screenLocation.getX(), screenLocation.getY(), screenLocation.getZ());
        Vector3D planeNormal;
        if(screenDirection == BlockFace.NORTH || screenDirection == BlockFace.SOUTH){
            planeNormal = new Vector3D(0, 0, 1);
        }else{
            planeNormal = new Vector3D(1, 0, 0);
        }
        Vector3D rayPoint = new Vector3D(playerLook.getX(), playerLook.getY(), playerLook.getZ());
        Vector3D rayVector = new Vector3D(playerLook.getDirection().getX(), playerLook.getDirection().getY(), playerLook.getDirection().getZ());
        Vector3D intersection = Vector3D.intersectPoint(rayVector, rayPoint, planeNormal, planePoint);
        if(intersection == null)
            return null;
        return new Location(screenLocation.getWorld(), intersection.getX(), intersection.getY(), intersection.getZ());
    }

}
