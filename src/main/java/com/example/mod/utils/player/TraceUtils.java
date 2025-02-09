package com.example.mod.utils.player;

import com.example.mod.datatypes.QAngle;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.example.mod.client.GameAccessor.mc;

public class TraceUtils {
    private static BlockHitResult traceBase(Vec3d startPos, Vec3d endPos, RaycastContext.ShapeType shapeType, RaycastContext.FluidHandling fluidHandling, Entity entity) {
        RaycastContext context = new RaycastContext(startPos, endPos, shapeType, fluidHandling, entity);

        return mc.world.raycast(context);
    }

    private static BlockHitResult trace(QAngle angle, double range, RaycastContext.ShapeType shapeType, RaycastContext.FluidHandling fluidHandling, float tickDelta) {
        Entity entity = mc.cameraEntity;
        if (entity == null) {
            return null;
        }

        Vec3d startPos = entity.getCameraPosVec(tickDelta);
        Vec3d endPos = startPos.add(angle.getVec().multiply(range));
        return traceBase(startPos, endPos, shapeType, fluidHandling, entity);
    }

    public static BlockHitResult trace(QAngle angle, double range, RaycastContext.FluidHandling fluidHandling, float tickDelta) {
        return trace(angle, range, RaycastContext.ShapeType.OUTLINE, fluidHandling, tickDelta);
    }

    public static BlockHitResult trace(QAngle angle, double range, float tickDelta) {
        return trace(angle, range, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, tickDelta);
    }

    public static List<BlockHitResult> trace(QAngle[] angles, double range, RaycastContext.ShapeType shapeType, RaycastContext.FluidHandling fluidHandling, float tickDelta) {
        return java.util.Arrays.stream(angles)
                .map(angle -> trace(angle, range, shapeType, fluidHandling, tickDelta))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
