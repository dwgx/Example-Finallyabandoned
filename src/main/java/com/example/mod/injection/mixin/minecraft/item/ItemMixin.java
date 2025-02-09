package com.example.mod.injection.mixin.minecraft.item;

import com.example.mod.enums.cmd.CmdType;
import com.example.mod.managers.RotationManager;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static com.example.mod.client.GameAccessor.mc;

@Mixin(Item.class)
public class ItemMixin {
    @ModifyExpressionValue(
            method = "raycast",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getRotationVector(FF)Lnet/minecraft/util/math/Vec3d;"
            )
    )
    private static Vec3d onRaycastRotationVector(Vec3d original, World world, PlayerEntity player, RaycastContext.FluidHandling fluidHandling) {
        if (!player.equals(mc.player) || (!RotationManager.getInstance().isNetwork() && !RotationManager.getInstance().getCmdType().has(CmdType.RENDER)) || !RotationManager.getInstance().getWorkingRotation().isTrace()) {
            return original;
        }

        return RotationManager.getInstance().getWorkingRotation().getAngle().getVec();
    }
}
