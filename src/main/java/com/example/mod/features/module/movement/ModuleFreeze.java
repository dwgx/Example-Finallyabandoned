package com.example.mod.features.module.movement;

import com.example.event.Event;
import com.example.mod.datatypes.QAngle;
import com.example.mod.datatypes.Rotation;
import com.example.mod.enums.ModuleCategory;
import com.example.mod.enums.cmd.CmdType;
import com.example.mod.events.client.world.JoinWorldEvent;
import com.example.mod.events.network.LocalTickMovementEvent;
import com.example.mod.events.network.NetworkMovementEvent;
import com.example.mod.events.network.PacketEvent;
import com.example.mod.features.module.AbstractModule;
import com.example.mod.storage.PacketStorage;
import com.example.mod.utils.NetworkUtils;
import com.example.mod.utils.player.ChatUtils;
import com.example.mod.utils.player.TraceUtils;
import com.example.utils.pattern.Singleton;
import com.example.value.BasicValue;
import com.example.value.NumberValue;
import net.engio.mbassy.listener.Handler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.*;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public class ModuleFreeze extends AbstractModule {
    public ModuleFreeze() {
        super("Freeze", "fuck u movement.", ModuleCategory.MOVEMENT);
    }

    private final NumberValue<Integer> tickValue = new NumberValue<>("Tick", -1, 0, 20, 1);
    private final BasicValue<Boolean> cancelPacketValue = new BasicValue<>("Cancel Packet", true);

    private float serverYaw, serverPitch;
    private Vec3d velocity;
    private int tick, rotationCount, teleportId;
    private BlockHitResult blockHitResult;
    private boolean acceptTeleport, needUpdate, needReset;

    @Override
    public void onEnable() {
        if (mc.player != null) {
            this.serverYaw = mc.player.getYaw();
            this.serverPitch = mc.player.getPitch();
            this.velocity = mc.player.getVelocity();
            ChatUtils.display(Text.literal("Velocity: " + this.velocity.toString()));
        }

        PacketStorage.getInstance().filter(CommonPongC2SPacket.class);
        PacketStorage.getInstance().storage(true);
    }

    @Override
    public void onDisable() {
        PacketStorage.getInstance().stop(true, false);
    }

    @Override
    public void reset() {
        this.tick = 0;
        this.rotationCount = 0;
        this.teleportId = 0;
        this.blockHitResult = null;
        this.needUpdate = false;
        this.needReset = false;
    }

    @Handler
    public void onPacket(PacketEvent event) {
        Packet<?> packet = event.getPacket();

        if (packet instanceof PlayerMoveC2SPacket wrapper) {
            if (cancelPacketValue.getValue()) {
                event.cancel();
            }

            if (!event.isCanceled()) {
                this.serverYaw = wrapper.getYaw(mc.player.getYaw());
                this.serverPitch = wrapper.getPitch(mc.player.getPitch());
            }
        }

        if (packet instanceof TeleportConfirmC2SPacket wrapper) {
            ChatUtils.display(Text.literal("Teleport ID: " + wrapper.getTeleportId() + " - Accept: " + this.acceptTeleport));

            if (this.acceptTeleport) {
                this.toggle();
                this.acceptTeleport = false;
            } else {
                this.teleportId = wrapper.getTeleportId();
            }
        }

        if (event.getDirection().equals(PacketEvent.Direction.SEND)) {
            if (packet instanceof ClientTickEndC2SPacket) {
                event.cancel();
            }

            ItemStack stack = mc.player.getMainHandStack();

            if (packet instanceof PlayerActionC2SPacket wrapper) {
                switch (wrapper.getAction()) {
                    case START_DESTROY_BLOCK -> {
                        event.cancel();
                        ChatUtils.display(Text.literal("Dont break."));
                    }
                    case ABORT_DESTROY_BLOCK, STOP_DESTROY_BLOCK -> {
                        event.cancel();
                    }
                    case RELEASE_USE_ITEM -> {
                        if (!stack.isEmpty() && stack.getItem() instanceof BowItem) {
                            event.cancel();

                            float yaw = mc.player.getYaw();
                            float pitch = mc.player.getPitch();
                            this.syncRotation(yaw, pitch);

                            NetworkUtils.sendPacketSilently(
                                    wrapper
                            );
                        }
                    }
                }
            }

            if (packet instanceof PlayerInteractEntityC2SPacket wrapper) {
                event.cancel();

                float yaw = mc.player.getYaw();
                float pitch = mc.player.getPitch();
                this.syncRotation(yaw, pitch);

                NetworkUtils.sendPacketSilently(
                        wrapper
                );
            }

            if (!stack.isEmpty() && !stack.getItem().getComponents().contains(DataComponentTypes.FOOD) && !(stack.getItem() instanceof BowItem) && !(stack.getItem() instanceof SwordItem)) {
                if (packet instanceof PlayerInteractBlockC2SPacket wrapper) {
                    event.cancel();

                    float yaw = mc.player.getYaw();
                    float pitch = mc.player.getPitch();
                    this.syncRotation(yaw, pitch);

                    ChatUtils.display(Text.literal("Block place[X=" + blockHitResult.getBlockPos().toString() + ", Y=" + blockHitResult.getBlockPos().getY() + ", Z=" + blockHitResult.getBlockPos().getZ() + "] - " + blockHitResult.getSide().asString()));

                    NetworkUtils.sendPacketSilently(
                            new PlayerInteractBlockC2SPacket(
                                    wrapper.getHand(),
                                    blockHitResult,
                                    wrapper.getSequence()
                            )
                    );
                }

                if (packet instanceof PlayerInteractItemC2SPacket wrapper) {
                    event.cancel();

                    float yaw = mc.player.getYaw();
                    float pitch = mc.player.getPitch();

                    if (stack.getItem() instanceof BlockItem) {
                        if (blockHitResult.getType().equals(HitResult.Type.MISS)) {
                            return;
                        }

                        ChatUtils.display(Text.literal("Block place[X=" + blockHitResult.getBlockPos().getX() + ", Y=" + blockHitResult.getBlockPos().getY() + ", Z=" + blockHitResult.getBlockPos().getZ() + "] - " + blockHitResult.getSide().asString()));

                        NetworkUtils.sendPacketSilently(
                                new PlayerInteractBlockC2SPacket(
                                        Hand.MAIN_HAND,
                                        blockHitResult,
                                        0
                                )
                        );
                    }

                    this.syncRotation(yaw, pitch);

                    NetworkUtils.sendPacketSilently(
                            new PlayerInteractItemC2SPacket(
                                    wrapper.getHand(),
                                    wrapper.getSequence(),
                                    yaw,
                                    pitch
                            )
                    );

                    if (stack.getItem().equals(Items.ENDER_PEARL)) {
                        this.acceptTeleport = true;
                    }
                }
            }
        }
    }

    @Handler
    public void onNetworkMovement(NetworkMovementEvent event) {
        Rotation rotation = new Rotation(serverYaw, serverPitch, true)
                .setPriority(0)
                .setCmdType(CmdType.RENDER);

        rotation.submit();
    }

    @Handler
    public void onLocalTickMovement(LocalTickMovementEvent event) {
        if (event.getState().equals(Event.State.PRE)) {
            if (this.tick >= 20) {
                this.tick = 0;
            } else {
                this.tick++;
            }

            if (!this.needUpdate && (this.tickValue.getValue() == -1 || this.tick < this.tickValue.getValue())) {
                event.cancel();
            }

            /*
            if (this.needUpdate) {
                NetworkUtils.sendPacketSilently(
                        new PlayerMoveC2SPacket.PositionAndOnGround(
                                mc.player.getX(),
                                mc.player.getY(),
                                mc.player.getZ(),
                                mc.player.isOnGround(),
                                mc.player.horizontalCollision
                        )
                );
                this.needReset = true;
            }

            if (this.needReset) {
                this.needUpdate = false;
                this.needReset = false;
            }
             */

            float yaw = mc.player.getYaw();
            float pitch = mc.player.getPitch();
            this.blockHitResult = TraceUtils.traceBlock(new QAngle(yaw, pitch), 4.5f, 1.0f);
        }
    }

    @Handler
    public void onJoinWorld(JoinWorldEvent event) {
        this.toggle();
    }

    public void syncRotation(float yaw, float pitch) {
        if (serverYaw == yaw && this.serverPitch == pitch) {
            return;
        }

        if (++this.rotationCount == 20) {
            ChatUtils.display(Text.literal("Update player move. (Maybe you will get vl)"));

            NetworkUtils.sendPacketSilently(
                    new PlayerMoveC2SPacket.PositionAndOnGround(
                            mc.player.getX(),
                            mc.player.getY(),
                            mc.player.getZ(),
                            mc.player.isOnGround(),
                            mc.player.horizontalCollision
                    )
            );

            ChatUtils.display(Text.literal("Send teleport confirm."));
            NetworkUtils.sendPacketSilently(
                    new TeleportConfirmC2SPacket(
                            this.teleportId
                    )
            );

            /*
            ChatUtils.display(Text.literal("Update player move."));

            this.needUpdate = true;
             */

            this.teleportId = 0;
            this.rotationCount = 0;
        }

        ChatUtils.display(Text.literal("Update player rotation (" + this.rotationCount + ")"));

        NetworkUtils.sendPacketSilently(
                new PlayerMoveC2SPacket.LookAndOnGround(
                        yaw,
                        pitch,
                        mc.player.isOnGround(),
                        mc.player.horizontalCollision
                )
        );

        this.serverYaw = yaw;
        this.serverPitch = pitch;
    }

    public static ModuleFreeze getInstance() {
        return Singleton.getInstance(ModuleFreeze.class);
    }
}
