package com.example.mod.features.module.miscellaneous;

import com.example.mod.enums.ModuleCategory;
import com.example.mod.events.client.GameTickEvent;
import com.example.mod.events.client.network.AttackEntityEvent;
import com.example.mod.events.client.render.LayerRenderEvent;
import com.example.mod.events.client.render.WorldRenderEvent;
import com.example.mod.events.network.NetworkMovementEvent;
import com.example.mod.events.network.PacketEvent;
import com.example.mod.features.module.AbstractModule;
import com.example.mod.utils.player.ChatUtils;
import com.example.mod.utils.player.FakePlayer;
import com.example.mod.utils.render.skija.SkijaRenderer;
import com.example.mod.utils.render.skija.text.Texts;
import com.example.utils.pattern.Singleton;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Tlhelp32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import io.github.humbleui.skija.Font;
import io.github.humbleui.skija.Paint;
import net.engio.mbassy.listener.Handler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ModuleDebug extends AbstractModule {
    public ModuleDebug() {
        super("Debug", "Convenient for developers to debug.", ModuleCategory.MISCELLANEOUS);
    }

    private List<String> moduleList = new ArrayList<>();
    private Entity lastTarget;
    private float lastTargetHealth;

    private FakePlayer fakePlayer;

    @Override
    public void onEnable() {
        this.moduleList = getLoadedModules();
        this.lastTarget = null;
        this.lastTargetHealth = -1f;
/*
        mc.setScreen(new TestScreen());
        this.toggle();
 */
        // this.fakePlayer = new FakePlayer(mc.player, true);
    }

    @Override
    public void onDisable() {
        if (this.fakePlayer != null) {
            // this.fakePlayer.remove();
        }
    }

    @Handler
    public void onGameTick(GameTickEvent event) {
        // mc.worldRenderer.reload();
        /*
        List<String> currentModuleList = getLoadedModules();

        Set<String> cacheModules = new HashSet<>(moduleList);
        Set<String> currentModules = new HashSet<>(currentModuleList);

        currentModules.removeAll(cacheModules);

        if (!currentModules.isEmpty()) {
            logger.info("可噁的作弊者加載了神秘迪樂樂:");
            for (String moduleName : currentModules) {
                logger.info(moduleName);
            }
            logger.info("布吉島>> 虛空娘害死了一位作弊者");
            moduleList = currentModuleList;
        }

         */
    }

    @Handler
    public void onPacket(PacketEvent event) {
        if (mc.player == null) {
            return;
        }

        Packet<?> packet = event.getPacket();

        /*
        if (packet instanceof CommonPingS2CPacket wrapper) {
            ChatUtils.display(Text.literal("CommonPingS2CPacket: " + wrapper.getParameter()));
        }
         */
        /*
        if (packet instanceof EntityS2CPacket wrapper) {
            event.cancel();
        }
        if (packet instanceof EntityPositionSyncS2CPacket wrapper) {
            event.cancel();
        }

        if (packet instanceof EntitySetHeadYawS2CPacket wrapper) {
            event.cancel();
        }
         */
    }

    @Handler
    public void onNetworkMovement(NetworkMovementEvent event) {
        /*
        Rotation rotation = new Rotation(mc.player.getYaw() - 180, 90, true)
                .setVelocityCorrection(VelocityCorrection.STANDARD)
                .setCmdType(CmdType.NETWORK);

        if (!rotation.submit()) {
            ChatUtils.display(Text.literal("Offer failed."));
        }
         */

       /*
        Rotation rotation = new Rotation(mc.player.getYaw() - 180, 90, true)
                .setCmdType(CmdType.NETWORK);

        if (!rotation.submit()) {
            ChatUtils.display(Text.literal("Offer failed."));
        }
        */

        /*
        List<BlockPos> positions = PositionUtils.filterPositions(PositionUtils.getNearbyPositions(mc.player.getBlockPos().down()), pos -> !(mc.world.getBlockState(pos).getBlock() instanceof AirBlock));

        if (!positions.isEmpty()) {
            List<BlockPos> posList = PositionUtils.findClosest(mc.player.getBlockPos().down(2), positions);

            for (BlockPos pos : posList) {

            }
        }
         */

        /*
        BlockPos pos = mc.player.getBlockPos().down();
        Rotation rotation = new Rotation(RotationUtils.getRotationTo(new Vec3d(pos.getX(),  pos.getY(),  pos.getZ())), true)
                .setCmdType(CmdType.NETWORK)
                .setCallback(
                        new Rotation.Callback() {
                            @Override
                            public void onExecute(Rotation data) {
                                mc.interactionManager.interactBlock(
                                        mc.player,
                                        Hand.MAIN_HAND,
                                        TraceUtils.trace(
                                                data.getAngle(),
                                                5,
                                                1
                                        )
                                );
                            }
                        }
                );

        if (!rotation.submit()) {
            ChatUtils.display(Text.literal("Offer failed."));
        }
         */
    }
    private Font font = new Font(Texts.makeFace(Texts.makeData("jet-brains-mono_regular", "ttf")), 30);

    @Handler
    public void onRenderLayer(LayerRenderEvent event) {
        if (MinecraftClient.getInstance().world != null) {
            Paint paint = new Paint().setARGB(255, 255, 255, 255);

            SkijaRenderer.drawText(
                    "Hello, World!", 50, 50, font, paint
            );

            SkijaRenderer.drawRRect(
                    50, 60, (float) mc.getWindow().getWidth() / 2, (float) mc.getWindow().getHeight() / 2, 4, paint
            );
        }
    }



    @Handler
    public void onRenderWorld(WorldRenderEvent event) {
    }

    @Handler
    public void onAttackEntity(AttackEntityEvent event) {
        Entity target = event.getTarget();

        if (target != null) {
            if (!target.equals(this.lastTarget)) {
                this.lastTarget = event.getTarget();
                this.lastTargetHealth = -1f;
            }

            if (target instanceof LivingEntity livingEntity) {
                if (this.lastTargetHealth == -1f) {
                    this.lastTargetHealth = livingEntity.getHealth();
                    ChatUtils.display(Text.literal("目標剩餘血量: " + this.lastTargetHealth), this.getClass().hashCode());
                } else if (this.lastTargetHealth > livingEntity.getHealth()) {
                    ChatUtils.display(Text.literal("目標剩餘血量: " + this.lastTargetHealth + " 造成傷害: " + (this.lastTargetHealth - livingEntity.getHealth()) + " - " + livingEntity.hurtTime), this.getClass().hashCode());
                    this.lastTargetHealth = livingEntity.getHealth();
                }
            }
        }

    }

    @Handler
    public void onLayerRender(LayerRenderEvent event) {
    }

    private List<String> getLoadedModules() {
        List<String> modules = new ArrayList<>();
        int pid = Kernel32.INSTANCE.GetCurrentProcessId();
        WinDef.DWORD dwPid = new WinDef.DWORD(pid);

        Tlhelp32.MODULEENTRY32W moduleEntry = new Tlhelp32.MODULEENTRY32W();
        Kernel32 kernel32 = Kernel32.INSTANCE;

        WinNT.HANDLE hSnap = kernel32.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPMODULE, dwPid);
        if (hSnap != null) {
            try {
                boolean bSuccess = kernel32.Module32FirstW(hSnap, moduleEntry);
                while (bSuccess) {
                    modules.add(moduleEntry.szModule());
                    bSuccess = kernel32.Module32NextW(hSnap, moduleEntry);
                }
            } finally {
                kernel32.CloseHandle(hSnap);
            }
        }
        return modules;
    }

    public static ModuleDebug getInstance() {
        return Singleton.getInstance(ModuleDebug.class);
    }
}
