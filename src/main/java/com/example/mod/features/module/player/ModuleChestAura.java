package com.example.mod.features.module.player;

import com.example.mod.datatypes.QAngle;
import com.example.mod.datatypes.Rotation;
import com.example.mod.enums.ModuleCategory;
import com.example.mod.enums.VelocityCorrection;
import com.example.mod.enums.cmd.CmdType;
import com.example.mod.events.client.GameTickEvent;
import com.example.mod.features.module.AbstractModule;
import com.example.mod.managers.RotationManager;
import com.example.utils.AbstractCallbackImpl;
import com.example.utils.pattern.Singleton;
import com.example.value.BasicValue;
import com.example.value.NumberValue;
import net.engio.mbassy.listener.Handler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModuleChestAura extends AbstractModule {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private boolean autoRotate = true;

    private NumberValue<Double> scanRadiusValue = new NumberValue<>("Scan Radius", "The radius to scan for chests", 3.0, 3.0, 20.0, 1.0);

    private final Set<BlockPos> openedChests = new HashSet<>();
    private BlockPos currentTargetChest = null;

    public ModuleChestAura() {
        super("ChestAura", "auto open", ModuleCategory.PLAYER);
    }

    @Override
    public void onEnable() {
        if (autoRotate) {
            scanAndRotateToChests();
        }
    }

    @Override
    public void onDisable() {
        currentTargetChest = null;
    }

    /**
     * 扫描附近的箱子并旋转玩家视角以面对第一个未打开的箱子。
     */
    private void scanAndRotateToChests() {
        List<BlockPos> nearbyChests = getNearbyChests(mc.player);
        if (nearbyChests.isEmpty()) {
            System.out.println("No nearby chests found.");
            return;
        }

        // 遍历箱子，找到第一个未打开的箱子
        for (BlockPos chestPos : nearbyChests) {
            if (!openedChests.contains(chestPos)) {
                currentTargetChest = chestPos;
                // 计算旋转角度
                QAngle angle = calculateRotationToChest(chestPos);
                // 创建一个平滑旋转对象
                Rotation rotation = new Rotation(angle, true, 0, CmdType.LOCAL, VelocityCorrection.NONE, new RotationCallback());
                rotation.submit();
                break; // 处理一个箱子
            }
        }
    }

    /**
     * 自动旋转完成后回调，打开箱子并标记为已打开。
     */
    private class RotationCallback extends AbstractCallbackImpl<Rotation> {
        @Override
        public void onExecute(Rotation rotation) {
            // 旋转开始时的逻辑
            System.out.println("Executing rotation to angle: " + rotation.getAngle());
        }

        @Override
        public void onComplete(Rotation rotation) {
            // 旋转完成后的逻辑
            System.out.println("Rotation completed to angle: " + rotation.getAngle());
            if (currentTargetChest != null) {
                openChest(currentTargetChest);
                openedChests.add(currentTargetChest);
                currentTargetChest = null;
                // 继续扫描下一个箱子
                scanAndRotateToChests();
            }
        }

        @Override
        public void onFailure(Rotation rotation, Exception e) {
            // 旋转失败时的逻辑
            System.out.println("Rotation failed: " + e.getMessage());
        }
    }

    /**
     * 获取玩家周围的箱子。
     */
    /**
     * 打开指定位置的箱子。
     */
    private void openChest(BlockPos chestPos) {
        if (mc.player == null || mc.world == null) return;

        // 计算玩家的眼睛位置
        Vec3d playerPos = mc.player.getEyePos();
        // 计算箱子中心位置
        Vec3d chestCenter = new Vec3d(chestPos.getX() + 0.5, chestPos.getY() + 1.0, chestPos.getZ() + 0.5);

        // 模拟交互（右键）
        BlockHitResult hitResult = new BlockHitResult(
                chestCenter,
                net.minecraft.util.math.Direction.UP,
                chestPos,
                false
        );
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hitResult);
        mc.player.swingHand(Hand.MAIN_HAND);
        System.out.println("Chest opened at: " + chestPos);
    }

    /**
     * 获取玩家周围的箱子。
     */
    private List<BlockPos> getNearbyChests(PlayerEntity player) {
        ClientWorld world = mc.world;
        if (world == null) return List.of();

        List<BlockPos> chests = new ArrayList<>();
        double radius = scanRadiusValue.getValue(); // 使用 scanRadius 的值作为扫描半径

        BlockPos playerPos = player.getBlockPos();

        // 遍历玩家周围的所有区块
        for (int x = playerPos.getX() - (int) radius; x <= playerPos.getX() + radius; x++) {
            for (int y = playerPos.getY() - (int) radius; y <= playerPos.getY() + radius; y++) {
                for (int z = playerPos.getZ() - (int) radius; z <= playerPos.getZ() + radius; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (world.getBlockState(pos).getBlock() == net.minecraft.block.Blocks.CHEST) {
                        chests.add(pos);
                    }
                }
            }
        }
        return chests;
    }


    /**
     * 计算旋转角度以面对指定的箱子。
     */
    private QAngle calculateRotationToChest(BlockPos chestPos) {
        ClientPlayerEntity player = mc.player;
        if (player == null) return new QAngle(0, 0); // 玩家为空，返回默认角度

        // 获取玩家的眼睛位置
        Vec3d playerEyePos = player.getEyePos();

        // 获取箱子中心位置
        Vec3d chestCenter = new Vec3d(chestPos.getX() + 0.5, chestPos.getY() + 1.0, chestPos.getZ() + 0.5);

        // 计算差值
        double dx = chestCenter.x - playerEyePos.x;
        double dy = chestCenter.y - playerEyePos.y;
        double dz = chestCenter.z - playerEyePos.z;

        // 计算 yaw 和 pitch
        double distanceXZ = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0f;
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, distanceXZ));

        return new QAngle(yaw, pitch);
    }

    @Override
    public Set<BasicValue<?>> getValues() {
        return Set.of(
                this.scanRadiusValue
        );
    }

    public static ModuleChestAura getInstance() {
        return Singleton.getInstance(ModuleChestAura.class);
    }
}
