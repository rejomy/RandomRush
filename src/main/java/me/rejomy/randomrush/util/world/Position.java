package me.rejomy.randomrush.util.world;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;

@Getter
@Setter
public class Position {
    double x, y, z;
    float yaw, pitch;

    public Position() {}

    public Position(Location location) {
        this(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public Position (double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Position (double x, double y, double z, float yaw, float pitch) {
        this(x, y, z);

        this.yaw = yaw;
        this.pitch = pitch;
    }

    public static Location toLocation(World world, Position position) {
        return new Location(world, position.x, position.y, position.z, position.yaw, position.pitch);
    }
}
