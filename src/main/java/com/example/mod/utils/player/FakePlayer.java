package com.example.mod.utils.player;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

import static com.example.mod.client.GameAccessor.mc;

public class FakePlayer extends OtherClientPlayerEntity {
    private final PlayerEntity player;
    private final int entityId;

    public FakePlayer(PlayerEntity player, boolean cloneInventory) {
        // player.getGameProfile()
        super(mc.world, new GameProfile(UUID.randomUUID(), player.getGameProfile().getName()));

        this.player = player;
        this.entityId = player.getId() + this.hashCode();
        this.setId(this.entityId);
        this.setHealth(player.getHealth());
        this.setAbsorptionAmount(player.getAbsorptionAmount());
        this.getAttributes().setFrom(player.getAttributes());
        this.setPose(player.getPose());

/*
        this.limbAnimator.setSpeed(player.limbAnimator.getSpeed());
        ((LimbAnimatorAccessor) this.limbAnimator).setPos(player.limbAnimator.getPos());

 */
        this.copyPositionAndRotation(player);
        this.setPrevPositionAndAngles(new Vec3d(player.prevX, player.prevY, player.prevZ), player.prevYaw, player.prevPitch);

        this.setHeadYaw(player.getHeadYaw());
        this.prevHeadYaw = player.prevHeadYaw;
        this.setBodyYaw(player.getBodyYaw());
        this.prevBodyYaw = player.prevBodyYaw;

        if (cloneInventory) {
            this.getInventory().clone(player.getInventory());
        }

        mc.world.addEntity(this);
    }

  /*
    @Override
    public void updateLimbs(boolean flutter) {}
   */

    public void remove() {
        mc.world.removeEntity(this.entityId, RemovalReason.DISCARDED);
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public int getEntityId() {
        return entityId;
    }
}
