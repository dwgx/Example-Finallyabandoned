package com.example.mod.features.task.impl;

import com.example.mod.features.task.AbstractTask;
import net.minecraft.screen.slot.SlotActionType;

public class InventoryTask extends AbstractTask {
    private final int syncId, slotId, button;
    private final SlotActionType action;

    public InventoryTask(int syncId, SlotActionType action, int slotId, int button) {
        this.syncId = syncId;
        this.action = action;
        this.slotId = slotId;
        this.button = button;
    }

    public InventoryTask(int syncId, SlotActionType action, int slotId) {
        this(syncId, action, slotId, 0);
    }

    public InventoryTask(SlotActionType action, int slotId, int button) {
        this(0, action, slotId, button);
    }

    public InventoryTask(SlotActionType action, int slotId) {
        this(action, slotId, 0);
    }

    @Override
    public void execute() throws Exception {
        mc.interactionManager.clickSlot(
                syncId,
                slotId,
                button,
                action,
                mc.player
        );
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onFailure(Exception e) {

    }

    public int getSyncId() {
        return syncId;
    }

    public int getSlotId() {
        return slotId;
    }

    public int getButton() {
        return button;
    }

    public SlotActionType getAction() {
        return action;
    }
}
