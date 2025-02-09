package com.example.mod.injection.mixin.minecraft.client.world;

import com.example.Global;
import com.example.event.Event;
import com.example.mod.events.client.world.WorldTickEvent;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @Shadow @Final private ClientWorld.Properties clientWorldProperties;

    @Inject(
            method = "tick",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private void onPreTick(CallbackInfo ci) {
        WorldTickEvent event = new WorldTickEvent(Event.State.PRE);
        Global.getEventBus().post(event).now();

        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "TAIL"
            )
    )
    private void onPostTick(CallbackInfo ci) {
        WorldTickEvent event = new WorldTickEvent(Event.State.POST);
        Global.getEventBus().post(event).now();
    }

    /*@Inject(method = "addEntity", at = @At("RETURN"))
    private void onAddEntity(Entity entity, CallbackInfo info) {
        if (ModuleProtocol.getInstance().isEnabled()) {
            HeypixelProtocol.get().onAddEntity(new AddEntityEvent(entity, (ClientWorld) (Object) this));
        }
        Global.getEventBus().post(new AddEntityEvent(entity, (ClientWorld) (Object) this));
    }
*/
}
