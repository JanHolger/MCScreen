package eu.bebendorf.mcscreen.intersection;

import lombok.Getter;
import lombok.Setter;

public class Vector3D {
    @Getter
    @Setter
    private double x, y, z;
    Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    Vector3D plus(Vector3D v) {
        return new Vector3D(x + v.x, y + v.y, z + v.z);
    }
    Vector3D minus(Vector3D v) {
        return new Vector3D(x - v.x, y - v.y, z - v.z);
    }
    Vector3D times(double s) {
        return new Vector3D(s * x, s * y, s * z);
    }
    double dot(Vector3D v) {
        return x * v.x + y * v.y + z * v.z;
    }
    public static Vector3D intersectPoint(Vector3D rayVector, Vector3D rayPoint, Vector3D planeNormal, Vector3D planePoint) {
        Vector3D diff = rayPoint.minus(planePoint);
        double prod1 = diff.dot(planeNormal);
        double prod2 = rayVector.dot(planeNormal);
        double prod3 = prod1 / prod2;
        return rayPoint.minus(rayVector.times(prod3));
    }
}
