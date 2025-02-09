package com.example.mod.injection.mixin.minecraft.client.input;

import net.minecraft.client.input.Input;
import net.minecraft.util.PlayerInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Input.class)
public class InputMixin {
    @Shadow public PlayerInput playerInput;
    @Shadow public float movementSideways;
    @Shadow public float movementForward;
}
