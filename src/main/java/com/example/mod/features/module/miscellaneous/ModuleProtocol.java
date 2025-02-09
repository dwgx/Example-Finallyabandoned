package com.example.mod.features.module.miscellaneous;

import com.example.mod.enums.ModuleCategory;
import com.example.mod.events.client.world.AddEntityEvent;
import com.example.mod.events.network.PacketEvent;
import com.example.mod.features.module.AbstractModule;
import com.example.mod.protocol.heypixel.HeypixelProtocol;
import com.example.utils.pattern.Singleton;
import com.example.value.ChoiceValue;
import com.example.value.NumberValue;
import net.engio.mbassy.listener.Handler;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;

import java.util.Arrays;


public class ModuleProtocol extends AbstractModule {
    private final NumberValue<Integer> mythPort = new NumberValue<>("Myth Port", 14250, 14250, 14255, 1);
    private final NumberValue<Integer> prismaPort = new NumberValue<>("Prisma Port", 14250, 14250, 14255, 1);
    private final ChoiceValue<RequestMode> requestMode = new ChoiceValue<>("Request Mode", Arrays.asList(RequestMode.values()), RequestMode.Port);
    private final ChoiceValue<NeteaseLauncher> neteaseLauncher = new ChoiceValue<>("Netease Launcher", Arrays.asList(NeteaseLauncher.values()), NeteaseLauncher.Myth);

    public ModuleProtocol() {
        super("Protocol", "sbcaosiyaomao", ModuleCategory.MISCELLANEOUS);
    }

    public int getMythPort() {
        return mythPort.getValue().intValue();
    }

    public int getPrismaPort() {
        return prismaPort.getValue().intValue();
    }

    @Handler
    public void onAddEntity(AddEntityEvent e) {
        HeypixelProtocol.get().onAddEntity(e);
    }

    @Handler
    public void onPacket(PacketEvent e) {
        try {
            if (e.getPacket() instanceof DisconnectS2CPacket d) {
                HeypixelProtocol.get().reload();
                return;
            }

            if (HeypixelProtocol.get().onPacket(e.getPacket())) {
                e.cancel();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public NeteaseLauncher getNEL() {
    return neteaseLauncher.getValue();
    }

    public RequestMode getRequestMode() {
        return requestMode.getValue();
    }

    public enum NeteaseLauncher {
        Zone,
        Myth,
        WNF,
        Prisma,
        Echoo,
        Auto
    }

    public enum RequestMode {
        Port,
        UUID,
        Merge
    }
    public static ModuleProtocol getInstance() {
        return Singleton.getInstance(ModuleProtocol.class);
    }
}



