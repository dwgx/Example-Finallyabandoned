package com.example.mod.features.module.player;

import com.example.event.Event;
import com.example.mod.enums.ModuleCategory;
import com.example.mod.events.entity.LivingEntityTickEvent;
import com.example.mod.features.module.AbstractModule;
import com.example.mod.features.task.impl.InventoryTask;
import com.example.mod.utils.TickTimer;
import com.example.utils.math.RandomUtils;
import com.example.utils.pattern.Singleton;
import com.example.value.BasicValue;
import com.example.value.ChoiceValue;
import com.example.value.RangeNumberValue;
import com.example.mod.utils.player.ChatUtils;
import net.engio.mbassy.listener.Handler;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ModuleAutoArmor extends AbstractModule {

    private final RangeNumberValue<Integer> delayValue = new RangeNumberValue<>("Delay", 1, 1, 0, 20, 1);
    private final ChoiceValue<Event.State> eventStateValue = new ChoiceValue<>("Event State", Arrays.asList(Event.State.values()), Event.State.PRE);
    private final BasicValue<Boolean> onlyInventoryOpen = new BasicValue<>("Only Inventory Open", false);
    private final BasicValue<Boolean> enableDebugger = new BasicValue<>("Enable Debugger", false);
    private final TickTimer tickTimer = new TickTimer();
    private boolean equipped, polled;
    private int nextDelay = -1;

    public ModuleAutoArmor() {
        super("AutoArmor", "Auto", ModuleCategory.PLAYER);
    }

    @Override
    public void reset() {
        equipped = false;
        polled = false;
        nextDelay = -1;
        tickTimer.reset();
    }

    @Handler
    public void onLivingEntityTick(LivingEntityTickEvent event) {
        if (!event.isLocalPlayer() || !event.getState().equals(eventStateValue.getValue())) return;
        if (onlyInventoryOpen.getValue() && !mc.player.currentScreenHandler.getType().getRequiredFeatures().isEmpty()) return;
        equipBestArmorAsync();
    }

    private void equipBestArmorAsync() {
        if (!equipped) {
            CompletableFuture.runAsync(() -> {
                Map<EquipmentSlot, ItemStack> bestArmorMap = getBestArmor();
                if (!bestArmorMap.isEmpty()) {
                    equipped = true;
                    doEquip(bestArmorMap);
                }
            });
        }
        if (equipped && !tickTimer.isRunning()) tickTimer.start();
        if (tickTimer.isRunning() && !polled) {
            nextDelay = RandomUtils.randomInt(delayValue.getValue(), delayValue.getSecondValue());
            polled = true;
        }
        if (polled && tickTimer.hasElapsed(nextDelay)) reset();
    }

    private Map<EquipmentSlot, ItemStack> getBestArmor() {
        PlayerInventory inv = mc.player.getInventory();
        List<ItemStack> candidate = new ArrayList<>();
        for (ItemStack stack : inv.main) {
            if (stack.getItem() instanceof ArmorItem) candidate.add(stack);
        }
        Map<EquipmentSlot, ItemStack> bestMap = new HashMap<>();
        for (ItemStack stack : candidate) {
            EquipmentSlot slot = getSlotForArmor(stack);
            if (slot == null) continue;
            ItemStack current = bestMap.get(slot);
            if (current == null || isBetterArmor(stack, current)) bestMap.put(slot, stack);
        }
        return bestMap;
    }

    private EquipmentSlot getSlotForArmor(ItemStack stack) {
        if (stack.getItem() instanceof ArmorItem) {
            String s = stack.getItem().toString().toLowerCase(Locale.ROOT);
            if (s.contains("helmet")) return EquipmentSlot.HEAD;
            if (s.contains("chestplate")) return EquipmentSlot.CHEST;
            if (s.contains("leggings")) return EquipmentSlot.LEGS;
            if (s.contains("boots")) return EquipmentSlot.FEET;
        }
        return null;
    }

    private boolean isBetterArmor(ItemStack newArmor, ItemStack oldArmor) {
        return getArmorScore(newArmor) > getArmorScore(oldArmor);
    }

    private int getArmorScore(ItemStack armor) {
        if (armor.isEmpty()) return 0;
        int score = 0;
        String name = Registries.ITEM.getId(armor.getItem()).toString().toLowerCase(Locale.ROOT);
        if (name.contains("netherite")) score += 20;
        else if (name.contains("diamond")) score += 15;
        else if (name.contains("iron")) score += 10;
        else if (name.contains("gold")) score += 8;
        else if (name.contains("chainmail") || (name.contains("chain") && name.contains("mail"))) score += 6;
        else if (name.contains("leather")) score += 4;
        int remain = armor.getMaxDamage() - armor.getDamage();
        score += (remain / 10);
        return score;
    }

    private void doEquip(Map<EquipmentSlot, ItemStack> bestArmorMap) {
        PlayerInventory inv = mc.player.getInventory();
        bestArmorMap.forEach((slot, armorStack) -> {
            ItemStack current = mc.player.getEquippedStack(slot);
            logDebug("当前装备槽: " + slot + " 当前装备: " + current.getItem().getName().getString());
            if (current.isEmpty() || isBetterArmor(armorStack, current)) {
                logDebug("装备更换: " + current.getItem().getName().getString() + " -> " + armorStack.getItem().getName().getString() + " (" + slot + ")");
                int currentIndex = getInventorySlotForArmor(current);
                if (currentIndex < 0) {
                    currentIndex = getArmorSlotIndex(slot);
                    logDebug("当前装备不在主物品栏，使用装备槽索引: " + currentIndex);
                }
                if (currentIndex >= 0) {
                    logDebug("卸下装备: " + current.getItem().getName().getString() + " 从索引: " + currentIndex);
                    new InventoryTask(SlotActionType.QUICK_MOVE, currentIndex).submit();
                } else {
                    logDebug("无法卸下当前装备: " + current.getItem().getName().getString());
                }
                int newIndex = inv.main.indexOf(armorStack);
                if (newIndex < 0) {
                    logDebug("装备 " + armorStack.getItem().getName().getString() + " 不在主物品栏，跳过");
                    return;
                }
                int realSlot = newIndex < 9 ? newIndex + 36 : newIndex;
                logDebug("换上装备: " + armorStack.getItem().getName().getString() + " 从索引: " + realSlot);
                new InventoryTask(SlotActionType.QUICK_MOVE, realSlot).submit();
            } else {
                logDebug("当前装备已经是最好的装备");
            }
        });
    }

    private int getInventorySlotForArmor(ItemStack stack) {
        PlayerInventory inv = mc.player.getInventory();
        for (int i = 0; i < inv.main.size(); i++) {
            ItemStack s = inv.main.get(i);
            if (s.isEmpty()) continue;
            if (s.isOf(stack.getItem()) && s.getCount() == stack.getCount()) return i;
        }
        return -1;
    }

    private int getArmorSlotIndex(EquipmentSlot slot) {
        switch (slot) {
            case HEAD: return 5;
            case CHEST: return 6;
            case LEGS: return 7;
            case FEET: return 8;
            default: return -1;
        }
    }

    private void logDebug(String msg) {
        if (enableDebugger.getValue()) {
            ChatUtils.display(Text.literal("AutoArmor: " + msg));
        }
    }

    @Override
    public Set<BasicValue<?>> getValues() {
        return Set.of(delayValue, eventStateValue, onlyInventoryOpen, enableDebugger);
    }

    public static ModuleAutoArmor getInstance() {
        return Singleton.getInstance(ModuleAutoArmor.class);
    }
}
