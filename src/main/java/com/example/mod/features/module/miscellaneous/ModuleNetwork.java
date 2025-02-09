package com.example.mod.features.module.miscellaneous;

// import com.diaoling.network.client.SimpleClient;

import com.example.mod.enums.ModuleCategory;
import com.example.mod.features.module.AbstractModule;
import com.example.utils.pattern.Singleton;

public class ModuleNetwork extends AbstractModule {
    public ModuleNetwork() {
        super("Network", "Network client.", ModuleCategory.MISCELLANEOUS);
    }

   /*
    private final SimpleClient client = new SimpleClient();

    @Override
    public void onEnable() {
        if (!this.client.isConnected()) {
            try {
                this.client.start();
                this.client.connect(5000, "localhost", 54555, 54777);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDisable() {
        if (this.client.isConnected()) {
            this.client.stop();
            // this.client = new SimpleClient();
        }
    }

    public SimpleClient getClient() {
        return client;
    }
    */

    public static ModuleNetwork getInstance() {
        return Singleton.getInstance(ModuleNetwork.class);
    }
}
