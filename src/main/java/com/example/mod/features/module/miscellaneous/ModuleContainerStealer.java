package com.example.mod.features.module.miscellaneous;

import com.example.event.Event;
import com.example.mod.enums.ModuleCategory;
import com.example.mod.events.client.GameTickEvent;
import com.example.mod.events.entity.LivingEntityTickEvent;
import com.example.mod.features.module.AbstractModule;
import com.example.mod.features.task.impl.InventoryTask;
import com.example.mod.utils.TickTimer;
import com.example.mod.utils.player.InventoryUtils;
import com.example.mod.utils.player.ScreenUtils;
import com.example.utils.math.RandomUtils;
import com.example.utils.pattern.Singleton;
import com.example.value.BasicValue;
import com.example.value.ChoiceValue;
import com.example.value.RangeNumberValue;
import net.engio.mbassy.listener.Handler;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class ModuleContainerStealer extends AbstractModule {

    private final RangeNumberValue<Integer> delayValue = new RangeNumberValue<>("Delay", 1, 1, 0, 20, 1);
    private final ChoiceValue<Event.State> eventStateValue = new ChoiceValue<>("Event State", Arrays.asList(Event.State.values()), Event.State.PRE);
    private final BasicValue<Boolean> checkInventoryFullValue = new BasicValue<>("Check Inventory Full", true);
    private final BasicValue<Boolean> autoCloseValue = new BasicValue<>("Auto Close", true);
    private final BasicValue<Boolean> selectBestValue = new BasicValue<>("Select Best", false);
    private final BasicValue<Boolean> enableDebugger = new BasicValue<>("Enable Debugger", false);

    private boolean shouldSteal, filtered, polled;
    private int nextDelay = -1;
    private Slot nextSlot;
    private final TickTimer tickTimer = new TickTimer();
    private final Queue<Slot> slots = new LinkedBlockingQueue<>();

    public ModuleContainerStealer() {
        super("ContainerStealer", "auto", ModuleCategory.MISCELLANEOUS);
    }

    @Override
    public void reset() {
        shouldSteal = false;
        filtered = false;
        polled = false;
        slots.clear();
        nextSlot = null;
        nextDelay = -1;
        tickTimer.reset();
    }

    @Handler
    public void onGameTick(GameTickEvent event) {
        if (!shouldSteal && filtered && slots.isEmpty() && autoCloseValue.getValue()) {
            GenericContainerScreen screen = ScreenUtils.getContainerScreen();
            if (screen != null) screen.close();
        }
    }

    @Handler
    public void onLivingEntityTick(LivingEntityTickEvent event) {
        if (!event.isLocalPlayer() || !event.getState().equals(eventStateValue.getValue()))
            return;
        ScreenHandler handler = ScreenUtils.getHandler();
        if (handler instanceof GenericContainerScreenHandler containerHandler) {
            execute(containerHandler);
        } else {
            reset();
        }
    }

    private void execute(GenericContainerScreenHandler handler) {
        if (!shouldSteal) {
            if (checkInventoryFullValue.getValue() && InventoryUtils.isFull(mc.player.getInventory())) {
                filtered = true;
                return;
            }
            filter(handler);
            if (!slots.isEmpty()) shouldSteal = true;
        }
        if (!shouldSteal) return;
        if (!tickTimer.isRunning()) tickTimer.start();
        if (!next()) {
            reset();
            return;
        }
        if (polled && nextSlot != null && tickTimer.hasElapsed(nextDelay)) {
            new InventoryTask(handler.syncId, SlotActionType.QUICK_MOVE, nextSlot.id).submit();
            nextSlot = null;
            nextDelay = -1;
            polled = false;
            tickTimer.reset();
        }
    }

    private void filter(GenericContainerScreenHandler handler) {
        slots.clear();
        List<Slot> candidates = handler.slots.stream()
                .filter(s -> !(s.inventory instanceof PlayerInventory))
                .filter(Slot::hasStack)
                .collect(Collectors.toList());
        if (!selectBestValue.getValue()) {
            slots.addAll(candidates);
            filtered = true;
            return;
        }
        List<Slot> better = new ArrayList<>();
        for (Slot slot : candidates) {
            ItemStack stack = slot.getStack();
            int containerScore = getItemScore(stack);
            ItemMaterialType type = classifyItem(stack);
            int playerBest = getPlayerBestScoreFor(type);
            if (containerScore > playerBest) {
                better.add(slot);
            }
        }
        slots.addAll(better);
        filtered = true;
    }

    private int getItemScore(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        int base = 0;
        String name = Registries.ITEM.getId(stack.getItem()).toString().toLowerCase(Locale.ROOT);
        if (name.contains("netherite")) base += 20;
        else if (name.contains("diamond")) base += 15;
        else if (name.contains("iron")) base += 10;
        else if (name.contains("gold")) base += 8;
        else if (name.contains("chainmail")) base += 6;
        else if (name.contains("leather")) base += 4;
        if (stack.getMaxDamage() > 0) {
            int remain = stack.getMaxDamage() - stack.getDamage();
            base += (remain / 10);
        }
        return base;
    }

    private int getPlayerBestScoreFor(ItemMaterialType type) {
        int best = 0;
        PlayerInventory inv = mc.player.getInventory();
        for (EquipmentSlot eq : EquipmentSlot.values()) {
            ItemStack s = mc.player.getEquippedStack(eq);
            if (!s.isEmpty() && classifyItem(s) == type) {
                best = Math.max(best, getItemScore(s));
            }
        }
        for (int i = 0; i < inv.size(); i++) {
            ItemStack s = inv.getStack(i);
            if (!s.isEmpty() && classifyItem(s) == type) {
                best = Math.max(best, getItemScore(s));
            }
        }
        return best;
    }

    private boolean next() {
        if (!polled) {
            if (checkInventoryFullValue.getValue() && InventoryUtils.isFull(mc.player.getInventory()))
                return false;
            nextDelay = RandomUtils.randomInt(delayValue.getValue(), delayValue.getSecondValue());
            nextSlot = slots.poll();
            polled = true;
            return nextSlot != null;
        }
        return true;
    }

    private ItemMaterialType classifyItem(ItemStack stack) {
        if (stack.getItem() instanceof BlockItem) return ItemMaterialType.BLOCK;
        String regName = Registries.ITEM.getId(stack.getItem()).toString().toLowerCase(Locale.ROOT);
        if (stack.getItem() instanceof ArmorItem) {
            if (regName.contains("netherite")) return ItemMaterialType.NETHERITE;
            if (regName.contains("diamond")) return ItemMaterialType.DIAMOND;
            if (regName.contains("iron")) return ItemMaterialType.IRON;
            if (regName.contains("golden") || regName.contains("gold")) return ItemMaterialType.GOLD;
            if (regName.contains("chainmail")) return ItemMaterialType.CHAINMAIL;
            if (regName.contains("leather")) return ItemMaterialType.LEATHER;
            return ItemMaterialType.OTHER;
        }
        if (stack.getItem() instanceof PickaxeItem ||
                stack.getItem() instanceof AxeItem ||
                stack.getItem() instanceof ShovelItem ||
                stack.getItem() instanceof HoeItem ||
                stack.getItem() instanceof SwordItem) {
            if (regName.contains("netherite")) return ItemMaterialType.NETHERITE;
            if (regName.contains("diamond")) return ItemMaterialType.DIAMOND;
            if (regName.contains("iron")) return ItemMaterialType.IRON;
            if (regName.contains("golden") || regName.contains("gold")) return ItemMaterialType.GOLD;
            return ItemMaterialType.OTHER;
        }
        return ItemMaterialType.OTHER;
    }

    private enum ItemMaterialType {
        LEATHER, CHAINMAIL, GOLD, IRON, DIAMOND, NETHERITE, BLOCK, OTHER
    }

    private void logDebug(String msg) {
        if (enableDebugger.getValue()) {
            com.example.mod.utils.player.ChatUtils.display(Text.literal("ContainerStealer: " + msg));
        }
    }

    @Override
    public Set<BasicValue<?>> getValues() {
        return Set.of(delayValue, eventStateValue, checkInventoryFullValue, autoCloseValue, selectBestValue, enableDebugger);
    }

    public static ModuleContainerStealer getInstance() {
        return Singleton.getInstance(ModuleContainerStealer.class);
    }
}
