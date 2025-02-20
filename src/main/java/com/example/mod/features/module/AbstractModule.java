package com.example.mod.features.module;

import com.diaoling.schema.ConfigUtils;
import com.diaoling.schema.config.ConfigSchema;
import com.example.Global;
import com.example.entity.NamedEntity;
import com.example.information.AppInfo;
import com.example.mod.client.GameAccessor;
import com.example.mod.enums.ModuleCategory;
import com.example.utils.input.ShortcutKey;
import com.example.utils.interfaces.Configurable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 模块抽象基类：提供启用/禁用、按键绑定等通用功能。
 */
public abstract class AbstractModule extends NamedEntity
        implements GameAccessor, Configurable<ConfigSchema.ModuleConfig> {

    protected final Logger logger;

    private final String description;
    private final ModuleCategory category;

    // 每个模块都独立持有一个 ShortcutKey 对象
    private ShortcutKey shortcutKey;

    private boolean canToggle;
    private boolean state;

    public AbstractModule(String name,
                          String description,
                          ModuleCategory category,
                          ShortcutKey shortcutKey,
                          boolean canToggle,
                          boolean defaultState) {
        this.logger = LoggerFactory.getLogger(name);
        this.id = name;
        this.description = description;
        this.category = category;
        this.canToggle = canToggle;
        this.shortcutKey = shortcutKey;


        // 咋可能不为null。这是构造函数
  /*
        // 修复关键：这里要自己 new 一个对象，而不是直接赋值(避免共享一个 static final)
        if (shortcutKey == null) {
            this.shortcutKey = new ShortcutKey(-1);
        } else {
            this.shortcutKey = new ShortcutKey(
                    shortcutKey.getPrimaryKey(),
                    shortcutKey.getModifiers(),
                    shortcutKey.getActionType()
            );
        }
   */

        setState(defaultState);
    }

    // 简化的构造函数：若没给具体 ShortcutKey，则默认用 -1 (NONE)
    public AbstractModule(String name, String description, ModuleCategory category, ShortcutKey shortcutKey) {
        this(name, description, category, shortcutKey, true, false);
    }

    public AbstractModule(String name, String description, ModuleCategory category, boolean defaultState) {
        this(name, description, category, ShortcutKey.NONE, true, defaultState);
    }

    public AbstractModule(String name, String description, ModuleCategory category) {
        this(name, description, category, ShortcutKey.NONE, true, false);
    }

    // -------------------- 启用/禁用 -------------------- //
    public void onEnable() {
        logger.info("{} enabled", getName());
    }

    public void onDisable() {
        logger.info("{} disabled", getName());
    }


    public void reset() {
        logger.info("{} reset", getName());
    }

    public boolean setState(boolean state) {
        if (this.state == state) {
            return false; // 状态无变化，不重复操作
        }
        this.state = state;
        if (state) {
            Global.getEventBus().subscribe(this);
            reset();
            onEnable();
        } else {
            Global.getEventBus().unsubscribe(this);
            reset();
            onDisable();
        }
        return true;
    }

    public boolean toggle() {
        if (!canToggle) {
            logger.warn("Module {} cannot be toggled", getName());
            return false;
        }
        return setState(!state);
    }

    public void setEnabled(boolean newState) {
        setState(newState);
    }

    // -------------------- 按键绑定相关 -------------------- //
    public ShortcutKey getShortcutKey() {
        return shortcutKey;
    }

    /**
     * 当只给一个 int 主按键时，我们重新 new 一个 ShortcutKey 来覆盖。
     * 如果要“解绑”，则传入 -1 即可。
     */
    public void setShortcutKey(int key) {
        if (key == -1) {
            // 解绑
            this.shortcutKey = new ShortcutKey(-1);
        } else {
            // 保留原本的 modifiers/actionType，只有 primaryKey 替换
            this.shortcutKey = new ShortcutKey(
                    key,
                    this.shortcutKey.getModifiers(),
                    this.shortcutKey.getActionType()
            );
        }
    }

    // -------------------- 常规 Getter/Setter -------------------- //
    public boolean isCanToggle() {
        return canToggle;
    }

    public void setCanToggle(boolean canToggle) {
        this.canToggle = canToggle;
    }

    public boolean isEnabled() {
        return state;
    }

    public String getDescription() {
        return description;
    }

    public ModuleCategory getCategory() {
        return category;
    }

    public Logger getLogger() {
        return logger;
    }

    // -------------------- 配置序列化（示例） -------------------- //
    public ConfigSchema.ModuleStatus getConfigStatus() {
        return state ? ConfigSchema.ModuleStatus.ACTIVE_STATUS : ConfigSchema.ModuleStatus.INACTIVE_STATUS;
    }

    @Override
    public ConfigSchema.ModuleConfig getConfig() {
        return ConfigSchema.ModuleConfig.newBuilder()
                .setConfig(
                        ConfigUtils.makeSettingConfig(
                                getConfigName(),
                                getConfigDescription(),
                                AppInfo.AUTHOR,
                                AppInfo.VERSION.toString(),
                                ConfigUtils.makeConfigValue(getValues())
                        )
                )
                .setModuleName(getName())
                .setModuleKey(shortcutKey.getPrimaryKey())
                .setStatus(getConfigStatus())
                .build();
    }

    @Override
    public String getConfigName() {
        return getName();
    }

    @Override
    public String getConfigDescription() {
        return getDescription();
    }

}
