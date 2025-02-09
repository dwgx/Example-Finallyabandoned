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
    private static float distanceTo(double x, double x2, double y, double y2, double z, double z2) {
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

    public static List<Position> getNearbyPositions(BlockPos center) {
        if (center == null) {
            throw new IllegalArgumentException("Center position must not be null");
        }

        List<Position> nearbyPositions = new ArrayList<>();
        for (int x = -3; x <= 3; x++) {
            for (int y = -1; y >= -3; y--) {
                for (int z = -3; z <= 3; z++) {
                    if (x == 0 && y == 0 && z == 0) {
                        continue;
                    }
                    nearbyPositions.add(new Position(center.add(x, y, z)));
                }
            }
        }
        return nearbyPositions;
    }

    public static List<Position> filterPositions(List<Position> positions, Predicate<Position> predicate) {
        if (positions == null || predicate == null) {
            throw new IllegalArgumentException("Positions and predicate must not be null.");
        }

        List<Position> filtered = new ArrayList<>();
        for (Position pos : positions) {
            if (predicate.test(pos)) {
                filtered.add(pos);
            }
        }
        return filtered;
    }

    public static List<Position> findClosest(Position self, List<Position> candidates) {
        if (self == null || candidates == null || candidates.isEmpty()) {
            throw new IllegalArgumentException("Self and candidates must not be null or empty");
        }

        Filter<Position> filter = new Filter<>();

        // filter.priorityComparator(Comparator.comparingDouble(a -> a.distanceTo(self)));
        filter.priorityComparator((a, b) -> Double.compare(a.distanceTo(self), b.distanceTo(self)));

        filter.limit(1);

        return filter.filter(candidates);
    }

    public static BlockPos findClosestInRange(BlockPos self, List<BlockPos> candidates) {
        if (candidates.isEmpty()) {
            return null;
        }

        return candidates.stream()
                .min(Comparator.comparingDouble(a -> a.getSquaredDistance(self)))
                .orElse(null); // 防止为空
    }
}
