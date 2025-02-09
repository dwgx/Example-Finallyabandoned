package com.example.mod.injection.mixin.minecraft.client;

import com.example.Global;
import com.example.event.Event;
import com.example.information.AppInfo;
import com.example.mod.events.client.GameActionEvent;
import com.example.mod.events.client.GameTickEvent;
import com.example.mod.events.client.world.JoinWorldEvent;
import com.example.mod.events.screen.ScreenEvent;
import com.example.mod.features.module.rage.ModuleMultitask;
import com.example.mod.utils.GuiMainMenu;
import com.example.mod.utils.render.skija.Skija;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//这里是MC源代码 你们不要偷看和抄袭！！
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    private static boolean isReplacingScreen = false; // 标志

    @Inject(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/QuickPlayLogger;create(Ljava/lang/String;)Lnet/minecraft/client/QuickPlayLogger;",
                    shift = At.Shift.BEFORE
            )
    )
    private void onMinecraftInit(RunArgs args, CallbackInfo ci) {
        GameActionEvent event = new GameActionEvent(GameActionEvent.Action.INIT);
        Global.getEventBus().post(event).now();
    }

    @Inject(
            method = "stop",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private void onMinecraftStop(CallbackInfo ci) {
        GameActionEvent event = new GameActionEvent(GameActionEvent.Action.SHUTDOWN);
        Global.getEventBus().post(event).now();

        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private void onPreMinecraftTick(CallbackInfo ci) {
        Profiler profiler = Profilers.get();

        profiler.push(AppInfo.NAME_LOWERCASE + "PreClientTick");
        GameTickEvent event = new GameTickEvent(Event.State.PRE);
        Global.getEventBus().post(event).now();

        if (event.isCanceled()) {
            ci.cancel();
        }

        profiler.pop();
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "TAIL"
            )
    )
    private void onPostMinecraftTick(CallbackInfo ci) {
        Profiler profiler = Profilers.get();

        profiler.push(AppInfo.NAME_LOWERCASE + "PostClientTick");
        Global.getEventBus().post(new GameTickEvent(Event.State.POST)).now();

        profiler.pop();
    }

    @Inject(
            method = "setScreen",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private void onMinecraftSetScreen(Screen screen, CallbackInfo ci) {
        if (screen instanceof TitleScreen && !isReplacingScreen) {
            isReplacingScreen = true; //
            MinecraftClient.getInstance().setScreen(new GuiMainMenu());
            ci.cancel();
            isReplacingScreen = false;
            return;
        }

        ScreenEvent event = new ScreenEvent(screen);
        Global.getEventBus().post(event).now();

        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @ModifyExpressionValue(
            method = "doItemUse",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;isBreakingBlock()Z"
            )
    )
    private boolean onDoItemUseBreaking(boolean original) {
        return !ModuleMultitask.getInstance().isEnabled() && original;
    }

    @ModifyExpressionValue(
            method = "handleBlockBreaking",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"
            )
    )
    private boolean onHandleBlockBreakingUsing(boolean original) {
        return !ModuleMultitask.getInstance().isEnabled() && original;
    }

    @Inject(
            method = "joinWorld",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private void onJoinWorld(ClientWorld world, DownloadingTerrainScreen.WorldEntryReason worldEntryReason, CallbackInfo ci) {
        JoinWorldEvent event = new JoinWorldEvent(world, worldEntryReason);
        Global.getEventBus().post(event).now();

        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(
            method = "onResolutionChanged",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gl/Framebuffer;resize(II)V",
                    shift = At.Shift.AFTER
            )
    )
    private void onResolutionChangedFramebufferResize(CallbackInfo ci) {
        Skija.getInstance().resize();
        // Skija.getInstance().draw();
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gl/Framebuffer;draw(II)V"
            )
    )
    private void onRenderFramebufferDraw(CallbackInfo ci) {
        // Skija.getInstance().draw();
    }
}
