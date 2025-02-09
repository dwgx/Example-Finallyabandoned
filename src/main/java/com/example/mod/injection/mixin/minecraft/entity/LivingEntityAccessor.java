package com.example.mod.injection.mixin.minecraft.entity;

import net.minecraft.entity.LimbAnimator;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Mutable
    @Accessor("limbAnimator")
    void setLimbAnimator(LimbAnimator animator);
}
