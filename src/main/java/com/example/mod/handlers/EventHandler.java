package com.example.mod.handlers;

import com.example.event.Event;
import com.example.mod.datatypes.Rotation;
import com.example.mod.enums.VelocityCorrection;
import com.example.mod.enums.cmd.CmdType;
import com.example.mod.events.client.GameTickEvent;
import com.example.mod.events.client.input.MovementInputEvent;
import com.example.mod.events.client.network.SendMessageEvent;
import com.example.mod.events.client.render.entity.RenderStateUpdateEvent;
import com.example.mod.events.entity.LivingEntityTickEvent;
import com.example.mod.events.entity.VelocityUpdateEvent;
import com.example.mod.events.input.KeyboardEvent;
import com.example.mod.events.network.NetworkMovementEvent;
import com.example.mod.events.network.PacketEvent;
import com.example.mod.features.ClientSettings;
import com.example.mod.features.command.impl.CommandBind;
import com.example.mod.features.module.AbstractModule;
import com.example.mod.managers.CommandManager;
import com.example.mod.managers.ModuleManager;
import com.example.mod.managers.RotationManager;
import com.example.mod.utils.input.PlayerInputModifiable;
import com.example.mod.utils.player.ChatUtils;
import com.example.mod.utils.player.VelocityUtils;
import com.example.utils.input.KeyAction;
import com.example.utils.input.ShortcutKey;
import com.example.utils.pattern.Singleton;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.engio.mbassy.listener.Handler;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.example.mod.client.GameAccessor.mc;

public class EventHandler {
    private final Logger logger = LoggerFactory.getLogger(EventHandler.class);

    @Handler
    public void onKeyboard(KeyboardEvent event) {
        if (mc.player != null) {
            if (CommandBind.getInstance().isWaitInput()) {
                ChatUtils.display(Text.literal("wait"));
            }

            if (!(mc.currentScreen instanceof ChatScreen) && (mc.currentScreen == null || ClientSettings.ALWAYS_HANDLE_KEY_INPUT.getValue())) {
                for (AbstractModule module : ModuleManager.getInstance().getModules()) {
                    ShortcutKey shortcutKey = module.getShortcutKey();

                    // , Set.of(event.getModifiers())
                    if (shortcutKey.matches(event.getKey())) {
                        if (shortcutKey.getActionType().equals(ShortcutKey.ActionType.HOLD)) {
                            if (event.getAction() == KeyAction.PRESS) {
                                module.setState(true);
                            } else if (event.getAction() == KeyAction.RELEASE) {
                                module.setState(false);
                            }
                        } else {
                            if (shortcutKey.matchesAction(event.getAction())) {
                                module.toggle();
                            }
                        }
                    }
                }
            }

            // TODO: ChatInputSuggestor.class Modify boolean bl = stringReader.canRead() && stringReader.peek() == '/';

            /*
            if (mc.currentScreen == null && mc.getOverlay() == null && KeyMapper.getKeyCode(ClientSettings.COMMAND_INPUT_PREFIX.getValue()) == event.getKey() && event.getAction().isPress()) {
                mc.openChatScreen(ClientSettings.COMMAND_INPUT_PREFIX.getValue());
            }
             */
        }
    }

    @Handler
    public void onGameTick(GameTickEvent event) {
        RotationManager.getInstance().update();
    }

    @Handler
    public void onLivingEntityTick(LivingEntityTickEvent event) {
        if (!event.isLocalPlayer()) {
            return;
        }

        RotationManager rotationManager = RotationManager.getInstance();
        if (event.getState().equals(Event.State.PRE)) {
            if (rotationManager.getCmdType().has(CmdType.LOCAL)) {
                Rotation current = rotationManager.getWorkingRotation();
                event.getEntity().setAngles(current.getYaw(), current.getPitch());
            }
            if (rotationManager.isRotating()) {
                rotationManager.getRotationDuration().incrementAndGet();
            } else {
                rotationManager.getRotationDuration().set(0);
            }
        }
    }

    @Handler
    public void onRenderStateUpdate(RenderStateUpdateEvent event) {
        if (event.getType().equals(RenderStateUpdateEvent.RenderStateType.LOCAL_PLAYER)) {
            LivingEntity entity = event.asLivingEntity();
            LivingEntityRenderState state = event.asLivingEntityRenderState();
            float delta = event.getDelta();

            RotationManager rotationManager = RotationManager.getInstance();

            if (RotationManager.getInstance().isNetwork() || RotationManager.getInstance().getCmdType().has(CmdType.RENDER)) {
                Vector2f current = rotationManager.getServerRotation();
                Vector2f previous = rotationManager.getPreviousServerRotation();
                float degrees;

                if (previous != null) {
                    degrees = MathHelper.lerpAngleDegrees(delta, previous.getX(), current.getX());
                    state.pitch = (delta == 1.0F)
                            ? current.getY()
                            : MathHelper.lerp(delta, previous.getY(), current.getY());
                } else {
                    degrees = current.getX();
                    state.pitch = current.getY();
                }

                state.bodyYaw = LivingEntityRenderer.clampBodyYaw(entity, degrees, delta);
                state.yawDegrees = MathHelper.wrapDegrees(degrees - state.bodyYaw);
            }
        }
    }

    @Handler
    public void onNetworkMovement(NetworkMovementEvent event) {
        RotationManager rotationManager = RotationManager.getInstance();

        if (event.getState().equals(Event.State.PRE)) {

        } else {
            if (rotationManager.isRotating()) {
                Rotation current = rotationManager.getWorkingRotation();

                current.trigger();

                rotationManager.setPreviousRotation(rotationManager.getLastRotation());
                rotationManager.setLastRotation(current);

                current.markFinish();
            }
        }
    }

    @Handler(priority = -1337)
    public void onPacket(PacketEvent event) {
        Packet<?> packet = event.getPacket();

        RotationManager rotationManager = RotationManager.getInstance();

        if (packet instanceof PlayerMoveC2SPacket wrapper) {
            if (wrapper.changesLook()) {
                rotationManager.setPreviousServerRotation(rotationManager.getServerRotation());
                rotationManager.setServerRotation(new Vector2f(wrapper.getYaw(0.0f), wrapper.getPitch(0.0f)));
            } else {
                rotationManager.setPreviousServerRotation(rotationManager.getServerRotation());
            }
        }

        if (packet instanceof PlayerPositionLookS2CPacket wrapper) {
            rotationManager.setPreviousServerRotation(rotationManager.getServerRotation());
            rotationManager.setServerRotation(new Vector2f(wrapper.change().yaw(), wrapper.change().pitch()));
        }
    }

    @Handler
    public void onMovementInput(MovementInputEvent event) {
        PlayerInputModifiable input = event.getInput();

        RotationManager rotationManager = RotationManager.getInstance();

        float movementForward = KeyboardInput.getMovementMultiplier(input.isForward(), input.isBackward());
        float movementSideways = KeyboardInput.getMovementMultiplier(input.isLeft(), input.isRight());

        if (rotationManager.isNetwork() && rotationManager.getWorkingRotation().getVelocityCorrection().equals(VelocityCorrection.BLENDED)) {
            Rotation current = rotationManager.getWorkingRotation();

            float deltaYaw = mc.player.getYaw() - current.getYaw();

            float radians = (float) Math.toRadians(deltaYaw);

            float cosYaw = MathHelper.cos(radians);
            float sinYaw = MathHelper.sin(radians);

            float newMovementSideways = Math.round(movementSideways * cosYaw - movementForward * sinYaw);
            float newMovementForward = Math.round(movementForward * cosYaw + movementSideways * sinYaw);

            input.setDirection(newMovementForward, newMovementSideways);
        }
    }

    @Handler
    public void onVelocityUpdate(VelocityUpdateEvent event) {
        if (!event.isLocalPlayer()) {
            return;
        }

        RotationManager rotationManager = RotationManager.getInstance();

        if (rotationManager.isNetwork() && rotationManager.getWorkingRotation().getVelocityCorrection().need()) {
            event.setVelocity(VelocityUtils.movementInputToVelocity(event, rotationManager.getWorkingRotation().getYaw()));
        }
    }

    @Handler
    public void onSendMessage(SendMessageEvent event) {
        String context = event.getContext();
        logger.info("Processing SendMessageEvent: {}", context);

        if (context.startsWith(ClientSettings.COMMAND_INPUT_PREFIX.getValue())) {
            event.cancel();

            try {
                CommandManager.getInstance().execute(context.substring(ClientSettings.COMMAND_INPUT_PREFIX.getValue().length()));
                mc.getCommandHistoryManager().add(context);
            } catch (CommandSyntaxException e) {
                ChatUtils.display(Text.literal(e.getMessage()).formatted(Formatting.RED));
            } catch (Exception e) {
                logger.error("Failed to execute command: {}", context, e);
                ChatUtils.display(Text.literal("Command execution failed!").formatted(Formatting.RED));
            }
        }
    }


    public static EventHandler getInstance() {
        return Singleton.getInstance(EventHandler.class);
    }
}
