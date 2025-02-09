package com.example.mod.features.module.rage;

import com.example.mod.enums.ModuleCategory;
import com.example.mod.events.client.network.AttackEntityEvent;
import com.example.mod.events.entity.LivingEntityTickEvent;
import com.example.mod.features.module.AbstractModule;
import com.example.mod.utils.world.position.PositionUtils;
import com.example.utils.pattern.Singleton;
import net.engio.mbassy.listener.Handler;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class ModuleHistory extends AbstractModule {
    public ModuleHistory() {
        super("History", "Backtrack target.", ModuleCategory.RAGE);
    }

    private Entity target;

    @Override
    public void reset() {
        this.target = null;
    }

    @Handler
    public void onAttackEntity(AttackEntityEvent event) {
        this.target = event.getTarget();
    }

    @Handler
    public void onLivingEntityTick(LivingEntityTickEvent event) {
        if (!event.isLocalPlayer()) {
            return;
        }

        if (this.target != null) {
            Vec3d prev = new Vec3d(this.target.prevX, this.target.prevY, this.target.prevZ);
            if (this.target.distanceTo(mc.player) > 3.0f && PositionUtils.distanceTo(prev, mc.player.getPos()) <= 3.0f) {
                this.target.setPosition(prev);
                this.target = null;
            }
        }
    }

    public static ModuleHistory getInstance() {
        return Singleton.getInstance(ModuleHistory.class);
    }
}
