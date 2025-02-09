package com.example.mod.features.module.rage;

import com.example.mod.enums.ModuleCategory;
import com.example.mod.events.client.network.AttackEntityEvent;
import com.example.mod.events.entity.LivingEntityTickEvent;
import com.example.mod.features.module.AbstractModule;
import com.example.mod.storage.PacketStorage;
import com.example.mod.utils.player.FakePlayer;
import com.example.utils.AbstractCallbackImpl;
import com.example.utils.pattern.Singleton;
import net.engio.mbassy.listener.Handler;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.c2s.play.*;

public class ModuleRag extends AbstractModule {
    private static final int MAX_ATTACKS = 2;  // Set the number of attacks to confirm
    private static final double MIN_AIR_Y_POSITION = 0.5;  // Minimum Y position to consider the player as "in the air"

    private FakePlayer fakePlayer;
    private Entity previousTarget;
    private int attackCount = 0;  // Counter to track number of attacks

    public ModuleRag() {
        super("Rag", "Rag with dog shit on it", ModuleCategory.RAGE);
    }

    @Override
    public void reset() {
        this.previousTarget = null;
        this.attackCount = 0;  // Reset attack count
    }

    @Override
    public void onEnable() {
        if (mc.player != null) {
            PacketStorage.getInstance().filter(
                    PlayerMoveC2SPacket.Full.class, PlayerMoveC2SPacket.OnGroundOnly.class, PlayerMoveC2SPacket.LookAndOnGround.class, PlayerMoveC2SPacket.PositionAndOnGround.class,
                    ClientCommandC2SPacket.class,
                    HandSwingC2SPacket.class,
                    PlayerInteractEntityC2SPacket.class,
                    PlayerActionC2SPacket.class,
                    PlayerInteractBlockC2SPacket.class,
                    PlayerInteractItemC2SPacket.class,
                    UpdateSelectedSlotC2SPacket.class,
                    CommonPongC2SPacket.class
            );

            PacketStorage.getInstance().releaseCallback(new AbstractCallbackImpl<>() {
                @Override
                public void onExecute(Packet<?> data) {
                    if (data instanceof PlayerMoveC2SPacket wrapper) {
                        if (wrapper.changesPosition() && wrapper.changesLook()) {
                            double x = wrapper.getX(0.0d);
                            double y = wrapper.getY(0.0d);
                            double z = wrapper.getZ(0.0d);

                            float yaw = wrapper.getYaw(0.0f);
                            float pitch = wrapper.getPitch(0.0f);

                            fakePlayer.updateTrackedPositionAndAngles(x, y, z, yaw, pitch, 5);
                        }

                        if (wrapper.changesPosition()) {
                            double x = wrapper.getX(0.0d);
                            double y = wrapper.getY(0.0d);
                            double z = wrapper.getZ(0.0d);

                            fakePlayer.updateTrackedPositionAndAngles(x, y, z, fakePlayer.getLerpTargetYaw(), fakePlayer.getLerpTargetPitch(), 5);
                        }

                        if (wrapper.changesLook()) {
                            float yaw = wrapper.getYaw(0.0f);
                            float pitch = wrapper.getPitch(0.0f);

                            fakePlayer.setHeadYaw(yaw);
                            fakePlayer.updateTrackedPositionAndAngles(fakePlayer.getLerpTargetX(), fakePlayer.getLerpTargetY(), fakePlayer.getLerpTargetZ(), yaw, pitch, 5);
                        }

                        fakePlayer.setOnGround(wrapper.isOnGround());
                    }

                    if (data instanceof ClientCommandC2SPacket wrapper) {
                        if (wrapper.getEntityId() == fakePlayer.getPlayer().getId()) {
                            switch (wrapper.getMode()) {
                                case START_SPRINTING -> fakePlayer.setSprinting(true);
                                case STOP_SPRINTING -> fakePlayer.setSprinting(false);
                                case PRESS_SHIFT_KEY -> fakePlayer.setSneaking(true);
                                case RELEASE_SHIFT_KEY -> fakePlayer.setSneaking(false);
                            }
                        }
                    }

                    if (data instanceof HandSwingC2SPacket wrapper) {
                        fakePlayer.swingHand(wrapper.getHand());
                    }

                    if (data instanceof UpdateSelectedSlotC2SPacket wrapper) {
                        fakePlayer.getInventory().selectedSlot = wrapper.getSelectedSlot();
                    }

                    fakePlayer.hurtTime = mc.player.hurtTime;
                }
            });

            if (PacketStorage.getInstance().storage(true)) {
                this.fakePlayer = new FakePlayer(mc.player, true);
            }
        }
    }

    @Override
    public void onDisable() {
        PacketStorage.getInstance().stop(true, false);

        if (this.fakePlayer != null) {
            this.fakePlayer.remove();
        }
    }

    @Handler
    public void onAttackEntity(AttackEntityEvent event) {
        if (event.getTarget().equals(this.fakePlayer)) {
            event.cancel();
        }

        if (event.getPlayer().equals(mc.player)) {
            this.previousTarget = event.getTarget();
            this.attackCount++;  // Increment attack count

            // Check if two attacks are made or the player is in the air
            if (this.attackCount >= MAX_ATTACKS || mc.player.getY() > MIN_AIR_Y_POSITION) {
                PacketStorage.getInstance().stop(true, false);

                if (this.fakePlayer != null) {
                    this.fakePlayer.remove();
                }
            }
        }
    }

    @Handler
    public void onLivingEntityTick(LivingEntityTickEvent event) {
        if (!event.isLocalPlayer()) {
            return;
        }

        if (this.fakePlayer != null && this.previousTarget != null) {
            double fakeDistance = this.fakePlayer.distanceTo(this.previousTarget);  // Virtual player to target
            double realPlayerDistance = mc.player.distanceTo(this.previousTarget);   // Real player to target

            if (realPlayerDistance < 3.0 && fakeDistance > 3.0) {
                PacketStorage.getInstance().releaseAll(false);

                double x = mc.player.getX();
                double y = mc.player.getY();
                double z = mc.player.getZ();

                this.fakePlayer.updateTrackedPositionAndAngles(x, y, z, fakePlayer.getLerpTargetYaw(), fakePlayer.getLerpTargetPitch(), 5);
            }
        }
    }

    public static ModuleRag getInstance() {
        return Singleton.getInstance(ModuleRag.class);
    }
}
