package com.example.mod.utils.world.position;

import com.example.entity.PositionEntity;
import com.example.utils.filter.Filter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class PositionUtils {
    public static float distanceTo(double x, double x2, double y, double y2, double z, double z2) {
        float f = (float)(x - x2);
        float g = (float)(y - y2);
        float h = (float)(z - z2);
        return MathHelper.sqrt(f * f + g * g + h * h);
    }

    public static float distanceTo(Vec3d start, Vec3d end) {
        return distanceTo(start.getX(), end.getX(), start.getY(), end.getY(), start.getZ(), end.getZ());
    }

    public static float distanceTo(BlockPos start, BlockPos end) {
        return distanceTo(start.getX(), end.getX(), start.getY(), end.getY(), start.getZ(), end.getZ());
    }

    public static float distanceTo(PositionEntity start, PositionEntity end) {
        return distanceTo(start.getPosition().getX(), end.getPosition().getX(), start.getPosition().getY(), end.getPosition().getY(), start.getPosition().getZ(), end.getPosition().getZ());
    }
}
