package su.nightexpress.sunlight.utils;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class Point3D {

    private final double x;
    private final double y;
    private final double z;

    public Point3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @NotNull
    public static Point3D of(@NotNull Location location) {
        return new Point3D(location.getX(), location.getY(), location.getZ());
    }

    public boolean isDifferent(@NotNull Point3D other) {
        return this.getX() != other.getX() || this.getY() != other.getY() || this.getZ() != other.getZ();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}
