package com.example.mod.utils.player;

import com.example.mod.datatypes.QAngle;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import static com.example.mod.client.GameAccessor.mc;

public class RotationUtils {
    public static QAngle getRotationTo(Vec3d start, Vec3d end) {
        Vec3d diff = end.subtract(start);

        double distance = Math.hypot(diff.x, diff.z);

        float yaw = (float) Math.toDegrees(MathHelper.atan2(diff.z, diff.x)) - 90.0f;
        float pitch = (float) -Math.toDegrees(MathHelper.atan2(diff.y, distance));

        return new QAngle(yaw, pitch);
    }

    public static QAngle getRotationTo(Vec3d end) {
        return getRotationTo(mc.player.getPos().add(new Vec3d(0.0, mc.player.getEyeHeight(mc.player.getPose()), 0.0)), end);
    }
}
