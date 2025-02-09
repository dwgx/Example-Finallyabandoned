package com.example.mod.features.module.rage;

import com.example.mod.enums.ModuleCategory;
import com.example.mod.events.entity.LivingEntityTickEvent;
import com.example.mod.features.module.AbstractModule;
import com.example.mod.storage.PacketStorage;
import com.example.mod.utils.player.ChatUtils;
import com.example.mod.utils.player.FakePlayer;
import com.example.utils.AbstractCallbackImpl;
import com.example.utils.Stopwatch;
import com.example.utils.pattern.Singleton;
import net.engio.mbassy.listener.Handler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.text.Text;

public class ModuleFakeLag extends AbstractModule {
    public ModuleFakeLag() {
        super("FakeLag", "lag !!", ModuleCategory.RAGE);
    }

    private FakePlayer fakePlayer;
    private Stopwatch stopwatch = new Stopwatch();

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
                    UpdateSelectedSlotC2SPacket.class
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

                            // fakePlayer.updatePosition(x, y, z);
                            fakePlayer.updateTrackedPositionAndAngles(x, y, z, fakePlayer.getLerpTargetYaw(), fakePlayer.getLerpTargetPitch(), 5);
                            // fakePlayer.updatePosition(MathHelper.lerp(tickDelta, fakePlayer.prevX, x), MathHelper.lerp(tickDelta, fakePlayer.prevY, y), MathHelper.lerp(tickDelta, fakePlayer.prevZ, z));
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
    public void onLivingEntityTick(LivingEntityTickEvent event) {
        if (!event.isLocalPlayer()) {
            return;
        }

        if (!this.stopwatch.isRunning()) {
            this.stopwatch.start();
        }

        if (this.stopwatch.hasElapsed(50)) {
            PacketStorage.getInstance().release(1, false);
            ChatUtils.display(Text.literal(String.valueOf(PacketStorage.getInstance().getPacketCount())), this.hashCode());
            this.stopwatch.reset();
        }
    }

    public static ModuleFakeLag getInstance() {
        return Singleton.getInstance(ModuleFakeLag.class);
    }
}
