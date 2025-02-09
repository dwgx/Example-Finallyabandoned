package com.example.utils.interfaces;

import com.diaoling.schema.ConfigUtils;
import com.example.information.AppInfo;
import com.example.value.BasicValue;

import java.util.Set;

public interface Configurable<T> {
    @SuppressWarnings("unchecked")
    default T getConfig() {
        return (T) ConfigUtils.makeSettingConfig(
                this.getConfigName(),
                this.getConfigDescription(),
                AppInfo.AUTHOR,
                AppInfo.VERSION.toString(),
                ConfigUtils.makeConfigValue(this.getValues())
        );
    }

    String getConfigName();
    String getConfigDescription();

    default Set<BasicValue<?>> getValues() {
        return Set.of();
    }
}
