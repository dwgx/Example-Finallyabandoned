package com.example.mod.injection.mixin.minecraft.client.input;

import com.example.Global;
import com.example.mod.events.client.input.MovementInputEvent;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.util.PlayerInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin extends InputMixin {
    @ModifyExpressionValue(
            method = "tick",
            at = @At(
                    value = "NEW",
                    target = "(ZZZZZZZ)Lnet/minecraft/util/PlayerInput;"
            )
    )
    private PlayerInput onTickNewPlayerInput(PlayerInput original) {
        MovementInputEvent event = new MovementInputEvent(original);
        Global.getEventBus().post(event).now();

        return event.getInput().get();
    }
}
