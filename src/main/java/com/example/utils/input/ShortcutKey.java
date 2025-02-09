package com.example.utils.input;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 表示一个可绑定的快捷键。
 */
public class ShortcutKey {
    /**
     * 仅用作“无效快捷键”的常量参考，不要直接存入模块。
     */
    public static final ShortcutKey NONE = new ShortcutKey(-1);

    public enum ActionType { TOGGLE, HOLD, RELEASE }

    private int primaryKey;
    private Set<Integer> modifiers;
    private ActionType actionType;

    public ShortcutKey(int primaryKey) {
        this(primaryKey, Collections.emptySet(), ActionType.TOGGLE);
    }

    public ShortcutKey(int primaryKey, Set<Integer> modifiers, ActionType actionType) {
        this.primaryKey = primaryKey;
        this.modifiers = new HashSet<>(modifiers);
        this.actionType = actionType;
    }

    public ShortcutKey() {
        this(-1, Collections.emptySet(), ActionType.TOGGLE);
    }
    public boolean matchesAction(KeyAction action) {
        if (action == null) return false;
        switch (this.actionType) {
            case TOGGLE:
                return action.isPress();
            case HOLD:
                return action.isPress() || action.isRepeat();
            case RELEASE:
                return action.isRelease();
            default:
                return false;
        }
    }

    public int getPrimaryKey() {
        return primaryKey;
    }

    public Set<Integer> getModifiers() {
        return modifiers;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setPrimaryKey(int primaryKey) {
        this.primaryKey = primaryKey;
    }

    public void setModifiers(Set<Integer> modifiers) {
        this.modifiers = new HashSet<>(modifiers);
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public ShortcutKey withPrimaryKey(int primaryKey) {
        return new ShortcutKey(primaryKey, this.modifiers, this.actionType);
    }

    public ShortcutKey withModifiers(Set<Integer> modifiers) {
        return new ShortcutKey(this.primaryKey, modifiers, this.actionType);
    }

    public ShortcutKey withActionType(ActionType actionType) {
        return new ShortcutKey(this.primaryKey, this.modifiers, actionType);
    }

    public boolean matches(int primaryKey) {
        return this.primaryKey == primaryKey;
    }

    public boolean matches(int primaryKey, Set<Integer> modifiers) {
        return this.primaryKey == primaryKey && this.modifiers.equals(modifiers);
    }

    public boolean isPrimaryKeyOnly() {
        return modifiers.isEmpty() && primaryKey != -1;
    }

    public String serialize() {
        StringBuilder builder = new StringBuilder();
        for (Integer mod : modifiers) {
            builder.append(mod).append("+");
        }
        builder.append(primaryKey).append(":").append(actionType);
        return builder.toString();
    }

    public static ShortcutKey deserialize(String serializedKey) {
        String[] parts = serializedKey.split(":");
        if (parts.length < 2) return NONE;

        String[] keys = parts[0].split("\\+");
        Set<Integer> modifiers = new HashSet<>();
        int primaryKey;
        try {
            primaryKey = Integer.parseInt(keys[keys.length - 1]);
            for (int i = 0; i < keys.length - 1; i++) {
                modifiers.add(Integer.parseInt(keys[i]));
            }
        } catch (NumberFormatException e) {
            return NONE;
        }

        ActionType actionType;
        try {
            actionType = ActionType.valueOf(parts[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            actionType = ActionType.TOGGLE;
        }

        return new ShortcutKey(primaryKey, modifiers, actionType);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ShortcutKey)) return false;
        ShortcutKey that = (ShortcutKey) obj;
        return primaryKey == that.primaryKey &&
                Objects.equals(modifiers, that.modifiers) &&
                actionType == that.actionType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(primaryKey, modifiers, actionType);
    }

    @Override
    public String toString() {
        if (primaryKey == -1) {
            return "NONE";
        }
        StringBuilder strResult = new StringBuilder();
        for (Integer mod : modifiers) {
            strResult.append(mod).append("+");
        }
        strResult.append(primaryKey).append(" (").append(actionType).append(")");
        return strResult.toString();
    }
}
