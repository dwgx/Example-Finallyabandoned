package com.example.mod.utils.player;

import com.example.mod.events.entity.VelocityUpdateEvent;
import net.minecraft.util.math.Vec3d;

public class VelocityUtils {
    public static Vec3d movementInputToVelocity(Vec3d movementInput, double speed, double yaw) {
        double lengthSquared = movementInput.lengthSquared();

        if (lengthSquared < 1.0E-7) {
            return Vec3d.ZERO;
        }

        Vec3d direction = (lengthSquared > 1.0) ? movementInput.normalize() : movementInput;

        Vec3d velocity = direction.multiply(speed);

        return velocity.rotateY((float) -Math.toRadians(yaw));
    }

    public static Vec3d movementInputToVelocity(VelocityUpdateEvent event, double yaw) {
        return movementInputToVelocity(event.getMovementInput(), event.getSpeed(), yaw);
    }
}
