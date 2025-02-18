package com.example.mod.features.module.rage;

import com.example.event.Event;
import com.example.mod.datatypes.Rotation;
import com.example.mod.enums.ModuleCategory;
import com.example.mod.enums.VelocityCorrection;
import com.example.mod.enums.cmd.CmdType;
import com.example.mod.events.entity.LivingEntityTickEvent;
import com.example.mod.events.network.NetworkMovementEvent;
import com.example.mod.features.module.AbstractModule;
import com.example.mod.utils.entity.EntityUtils;
import com.example.mod.utils.player.ChatUtils;
import com.example.mod.utils.player.FakePlayer;
import com.example.mod.utils.player.RotationUtils;
import com.example.utils.DelayUtils;
import com.example.utils.Stopwatch;
import com.example.utils.pattern.Singleton;
import com.example.value.BasicValue;
import com.example.value.ChoiceValue;
import com.example.value.NumberValue;
import com.example.value.ValueGroup;
import net.engio.mbassy.listener.Handler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ModuleAttackBot extends AbstractModule {
    public ModuleAttackBot() {
        super("AttackBot", "Auto attack sb entity.", ModuleCategory.RAGE);
    }

    private final ValueGroup mainGroup = new ValueGroup("Main");
    private final ValueGroup protectionGroup = new ValueGroup("Protection");
    private final ValueGroup selectionGroup = new ValueGroup("Selection");

    private final BasicValue<Boolean> silentAimValue = new BasicValue<>("Silent Aim", true);

    private final BasicValue<Boolean> aimValue = this.mainGroup.add(
            new BasicValue<>("Aim", true)
                    .child(silentAimValue)
    );

    private final BasicValue<Boolean> automaticAttackValue = this.mainGroup.add(
            new BasicValue<>("Automatic Attack", true)
    );

    private final BasicValue<Boolean> penetrateWallsValue = this.mainGroup.add(
            new BasicValue<>("Penetrate Walls", false)
    );

    private final BasicValue<Boolean> rayCastValue = this.mainGroup.add(
            new BasicValue<>("Ray Cast", true)
    );

    private final NumberValue<Float> scanRangeValue = this.mainGroup.add(
            new NumberValue<>("Scan Range", 3.0f, 0.0f, 6.0f, 0.01f)
    );

    private final NumberValue<Float> attackRangeValue = this.mainGroup.add(
            new NumberValue<>("Attack Range", 3.0f, 0.0f, 6.0f, 0.01f)
    );

    private final NumberValue<Float> fovValue = this.mainGroup.add(
            new NumberValue<>("Field of View", 180.0f, 0.0f, 180.0f, 0.01f)
    );

    private final BasicValue<Boolean> autoShieldValue = this.protectionGroup.add(
            new BasicValue<>("Auto Shield", false)
    );

    private final ChoiceValue<AutoShieldMode> autoShieldModeValue = this.protectionGroup.add(
            new ChoiceValue<>("Auto Shield Mode", Arrays.asList(AutoShieldMode.values()), AutoShieldMode.Vanilla)
    );

    private final BasicValue<Set<EntityType<?>>> entitiesValue = this.selectionGroup.add(
            new BasicValue<>("Entities", Set.of(
                    EntityType.PLAYER, EntityType.ZOMBIE, EntityType.VILLAGER, EntityType.ZOMBIFIED_PIGLIN, EntityType.SLIME
            ))
    );

    private final Stopwatch stopwatch = new Stopwatch();

    private Set<Entity> targets = new HashSet<>();
    private Entity currentTarget;

    @Override
    public void reset() {
        this.stopwatch.reset();
        this.targets.clear();
        this.currentTarget = null;
    }

    @Handler
    public void onLivingEntityTick(LivingEntityTickEvent event) {
        if (!event.isLocalPlayer() || event.getState().equals(Event.State.POST)) {
            return;
        }

        for (Entity entity : mc.world.getEntities()) {
            if (entity != null) this.targets.add(entity);
        }
        this.targets = EntityUtils.filter(this.targets, this.entitiesValue.getValue())
                .stream()
                .filter(entity -> !entity.equals(mc.player))
                .filter(entity -> !(entity instanceof FakePlayer))
                .filter(entity -> entity.distanceTo(mc.player) <= scanRangeValue.getValue())
                .filter(Entity::isAlive)
                .collect(Collectors.toSet());

        if (!this.targets.isEmpty()) {
            this.currentTarget = this.targets.stream().min((t1, t2) -> {
                        double distance1 = t1.getPos().distanceTo(mc.player.getPos());
                        double distance2 = t2.getPos().distanceTo(mc.player.getPos());
                        return Double.compare(distance1, distance2);
                    })
                    .orElse(null);
        } else {
            this.reset();
        }
    }

    @Handler
    public void onNetworkMovement(NetworkMovementEvent event) {
        if (this.currentTarget != null) {
            if (this.aimValue.getValue()) {
                CmdType cmdType = this.silentAimValue.getValue() ? CmdType.NETWORK : CmdType.LOCAL;

                Rotation rotation = new Rotation(RotationUtils.getRotationTo(this.currentTarget.getEyePos()), true)
                        .setPriority(1)
                        .setVelocityCorrection(VelocityCorrection.BLENDED)
                        .setCmdType(cmdType);

                if (!rotation.submit()) {
                    ChatUtils.display(Text.literal("Offer failed."));
                }
            }

            if (automaticAttackValue.getValue()) {
                if (mc.player.distanceTo(this.currentTarget) > this.attackRangeValue.getValue()) {
                    return;
                }
                if (!this.stopwatch.isRunning()) {
                    this.stopwatch.start();
                }
                long delay = DelayUtils.getCpsDelay(15.0);
                if (this.stopwatch.hasElapsed(delay)) {
                    mc.interactionManager.attackEntity(mc.player, this.currentTarget);
                    mc.player.swingHand(Hand.MAIN_HAND);
                    this.stopwatch.reset();
                }
            }
        }
    }

    enum AutoShieldMode { Vanilla }

    @Override
    public Set<BasicValue<?>> getValues() {
        return Set.of(
                // Main
                this.aimValue,
                this.automaticAttackValue,
                this.penetrateWallsValue,
                this.rayCastValue,
                this.scanRangeValue,
                this.attackRangeValue,
                this.fovValue,

                // Protection
                this.autoShieldValue,
                this.autoShieldModeValue,

                // Selection
                this.entitiesValue
        );
    }

    public Entity getCurrentTarget() {
        return currentTarget;
    }

    public static ModuleAttackBot getInstance() {
        return Singleton.getInstance(ModuleAttackBot.class);
    }
}
