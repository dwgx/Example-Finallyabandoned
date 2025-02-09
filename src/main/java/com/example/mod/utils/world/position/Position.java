package com.example.mod.utils.world.position;

import com.example.entity.PositionEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import static com.example.mod.client.GameAccessor.mc;

public class Position extends PositionEntity {
    public Position(Vec3d vec3d) {
        super(vec3d);
    }

    public Position(BlockPos pos) {
        this(new Vec3d(pos));
    }

    public double distanceTo(Position other) {
        if (mc.player == null) {
            return Double.MIN_VALUE;
        }

        return PositionUtils.distanceTo(other, this);
    }

    public double distanceTo(BlockPos other) {
        return this.distanceTo(new Position(other));
    }

    public double distanceTo(Vec3d other) {
        return this.distanceTo(new Position(other));
    }

    public double distance() {
        return this.distanceTo(mc.player.getPos());
    }
}
