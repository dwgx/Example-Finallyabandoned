package com.example.mod.managers;

import com.example.mod.enums.ModuleCategory;
import com.example.mod.features.module.AbstractModule;
import com.example.mod.features.module.exploit.ModuleNoPitchLimit;
import com.example.mod.features.module.miscellaneous.*;
import com.example.mod.features.module.movement.*;
import com.example.mod.features.module.player.ModuleAutoArmor;
import com.example.mod.features.module.player.ModuleInventorySorter;
import com.example.mod.features.module.rage.*;
import com.example.mod.features.module.visual.ModuleBlockOutline;
import com.example.mod.features.module.visual.ModuleWorldTime;
import com.example.utils.input.ShortcutKey;
import com.example.utils.interfaces.Initializable;
import com.example.utils.interfaces.Manageable;
import com.example.utils.pattern.Singleton;

import java.util.*;
import java.util.stream.Collectors;

public class ModuleManager implements Initializable, Manageable<AbstractModule> {
    private Set<AbstractModule> modules = new LinkedHashSet<>();

    @Override
    public boolean init() {
        items().forEach(this::add);
        return true;
    }

    @Override
    public boolean destroy() {
        items().forEach(this::remove);
        return true;
    }

    @Override
    public boolean add(AbstractModule element) {
        if (element != null && !modules.contains(element)) {
            return modules.add(element);
        }
        return false;
    }

    @Override
    public boolean remove(AbstractModule element) {
        // TODO: 保存逻辑
        // 略去 FileModuleConfig 等细节
        return modules.remove(element);
    }

    @Override
    public List<AbstractModule> items() {
        return Arrays.asList(
                // Rage
                ModuleAttackBot.getInstance(),
                ModuleAntiKnockback.getInstance(),
                ModuleHistory.getInstance(),
                ModuleMultitask.getInstance(),
                ModuleFakeLag.getInstance(),
                ModuleBacktrack.getInstance(),
                ModuleRag.getInstance(),
                // Player
                ModuleAutoArmor.getInstance(),
                ModuleInventorySorter.getInstance(),
                // Movement
                ModuleSprint.getInstance(),
                ModuleGuiMove.getInstance(),
                ModuleScaffold.getInstance(),
                ModuleFreeze.getInstance(),
                ModuleKeepSprint.getInstance(),
                ModuleEagle.getInstance(),
                ModuleNoSlow.getInstance(),
                ModuleChestaura.getInstance(),
                ModuleDISCSS2Cpacket.getInstance(),
                // Visual
                ModuleBlockOutline.getInstance(),
                ModuleWorldTime.getInstance(),
                // Misc
                ModuleDebug.getInstance(),
                ModuleNetwork.getInstance(),
                ModuleTimer.getInstance(),
                ModuleContainerStealer.getInstance(),
                // Exploit
                ModuleNoPitchLimit.getInstance()
        );
    }

    public Set<AbstractModule> getModules() {
        return modules;
    }

    public <T extends AbstractModule> T getModuleByClass(Class<T> clazz) {
        return modules.stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .findFirst()
                .orElse(null);
    }

    public AbstractModule getModuleByName(String name) {
        return modules.stream()
                .filter(module -> module.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public List<AbstractModule> getModulesByCategory(ModuleCategory category) {
        return modules.stream()
                .filter(module -> module.getCategory() == category)
                .collect(Collectors.toList());
    }

    public AbstractModule getModuleByShortcutKey(ShortcutKey shortcutKey) {
        return modules.stream()
                .filter(module -> module.getShortcutKey().equals(shortcutKey))
                .findFirst()
                .orElse(null);
    }

    public List<AbstractModule> getModulesByPrimaryKey(int primaryKey) {
        return modules.stream()
                .filter(module -> module.getShortcutKey().getPrimaryKey() == primaryKey)
                .collect(Collectors.toList());
    }

    public static ModuleManager getInstance() {
        return Singleton.getInstance(ModuleManager.class);
    }

    public void reorderModules(List<String> newOrder) {
        List<AbstractModule> reorderedModules = new ArrayList<>();
        for (String moduleName : newOrder) {
            AbstractModule module = getModuleByName(moduleName);
            if (module != null) {
                reorderedModules.add(module);
            }
        }
        this.modules = new LinkedHashSet<>(reorderedModules);
    }
}
