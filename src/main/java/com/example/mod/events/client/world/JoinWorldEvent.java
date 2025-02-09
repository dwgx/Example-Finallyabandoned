package com.example.mod.events.client.world;

import com.example.event.Event;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.world.ClientWorld;

public class JoinWorldEvent extends Event.Cancellable {
    private ClientWorld world;
    private DownloadingTerrainScreen.WorldEntryReason worldEntryReason;

    public JoinWorldEvent(ClientWorld world, DownloadingTerrainScreen.WorldEntryReason worldEntryReason) {
        this.world = world;
        this.worldEntryReason = worldEntryReason;
    }

    public ClientWorld getWorld() {
        return world;
    }

    public void setWorld(ClientWorld world) {
        this.world = world;
    }

    public DownloadingTerrainScreen.WorldEntryReason getWorldEntryReason() {
        return worldEntryReason;
    }

    public void setWorldEntryReason(DownloadingTerrainScreen.WorldEntryReason worldEntryReason) {
        this.worldEntryReason = worldEntryReason;
    }
}
