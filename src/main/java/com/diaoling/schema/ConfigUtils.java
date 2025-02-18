package com.diaoling.schema;

import com.diaoling.schema.config.ConfigSchema;
import com.diaoling.schema.datatype.Datatypes;
import com.diaoling.schema.file.FileSchema;
import com.example.information.AppInfo;
import com.example.value.BasicValue;
import com.google.protobuf.ByteString;
import net.minecraft.entity.EntityType;
import net.minecraft.text.MutableText;

import java.awt.*;
import java.util.AbstractCollection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ConfigUtils {
    public static Datatypes.Type toType(BasicValue<?> value) {
        Datatypes.Type.Builder builder = Datatypes.Type.newBuilder();

        Object object = value.getValue();

        switch (object) {
            case Integer intVal:
                builder.setIntValue(intVal);
                break;
            case Long longVal:
                builder.setInt64Value(longVal);
                break;
            case Double doubleVal:
                builder.setDoubleValue(doubleVal);
                break;
            case Float floatVal:
                builder.setFloatValue(floatVal);
                break;
            case Boolean boolVal:
                builder.setBoolValue(boolVal);
                break;
            case String stringVal:
                builder.setStringValue(stringVal);
                break;
            case byte[] byteArray:
                builder.setBytesValue(ByteString.copyFrom(byteArray));
                break;
            case Enum<?> enumVal:
                builder.setEnumIndexValue(enumVal.ordinal());
                break;
            case Color colorVal:
                builder.setColorValue(
                        Datatypes.Color.newBuilder()
                                .setRed(colorVal.getRed())
                                .setGreen(colorVal.getGreen())
                                .setBlue(colorVal.getBlue())
                                .setAlpha(colorVal.getAlpha())
                                .build()
                );
                break;
            case AbstractCollection<?> collectionVal:
                //
                Datatypes.TypeCollection.Builder collectionBuilder = Datatypes.TypeCollection.newBuilder();
                for (Object item : collectionVal) {
                    if (item instanceof BasicValue<?>) {
                        collectionBuilder.addValues(toType((BasicValue<?>) item));
                    } else {
                        BasicValue<Object> wrappedValue = new BasicValue<>("item", item);
                        collectionBuilder.addValues(toType(wrappedValue));
                    }
                }
                builder.setAnyValue(com.google.protobuf.Any.pack(collectionBuilder.build()));
                break;
            case MutableText mutableText:
                builder.setStringValue(mutableText.getString());
                break;
            case EntityType<?> entityType:

                break;
            default:
                throw new IllegalArgumentException("Unsupported type: " + object.getClass().getName());
        }

        return builder.build();
    }

    @SuppressWarnings("unchecked")
    public static <T> void setValue(Datatypes.Type type, BasicValue<T> value) {
        Object object = value.getValue();

        switch (object) {
            case Integer intVal:
                value.setValue((T) Integer.valueOf(type.getIntValue()));
                break;
            case Long longVal:
                value.setValue((T) Long.valueOf(type.getInt64Value()));
                break;
            case Double doubleVal:
                value.setValue((T) Double.valueOf(type.getDoubleValue()));
                break;
            case Float floatVal:
                value.setValue((T) Float.valueOf(type.getFloatValue()));
                break;
            case Boolean boolVal:
                value.setValue((T) Boolean.valueOf(type.getBoolValue()));
                break;
            case String stringVal:
                value.setValue((T) type.getStringValue());
                break;
            case byte[] byteArray:
                value.setValue((T) type.getBytesValue().toByteArray());
                break;
            case Enum<?> enumVal:
                // TODO：fix java.lang.IllegalArgumentException: Value must be one of the available options.
                // value.setValue((T) Long.valueOf(enumVal.ordinal()));

                // 2024.1.26 Maybe fix
                value.setValue((T) ((Enum[]) enumVal.getClass().getEnumConstants())[type.getEnumIndexValue()]);

                break;
            case Color colorVal:
                value.setValue((T) new Color(type.getColorValue().getRed(), type.getColorValue().getGreen(), type.getColorValue().getBlue(), type.getColorValue().getAlpha()));
                break;
            case AbstractCollection<?> collectionVal:
                if (type.hasAnyValue()) {
                    try {
                        Datatypes.TypeCollection collection = type.getAnyValue().unpack(Datatypes.TypeCollection.class);
                        AbstractCollection<Object> targetCollection = (AbstractCollection<Object>) collectionVal;
                        targetCollection.clear();
                        for (Datatypes.Type itemType : collection.getValuesList()) {
                            BasicValue<Object> tempValue = new BasicValue<>("item", null);
                            setValue(itemType, tempValue);
                            targetCollection.add(tempValue.getValue());
                        }
                    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                        throw new IllegalArgumentException("Failed to unpack collection value", e);
                    }
                }
                break;
            case MutableText mutableText:
                if (type.hasStringValue()) {
                    // 将字符串转换为MutableText
                    value.setValue((T) net.minecraft.text.Text.literal(type.getStringValue()).asOrderedText());
                }
                break;

            case EntityType<?> entityType:

                break;
            default:
                throw new IllegalArgumentException("Unsupported type: " + object.getClass().getName());
        }
    }

    public static Map<String, Datatypes.Type> makeConfigValue(Set<BasicValue<?>> values) {
        Map<String, Datatypes.Type> settingMap = new LinkedHashMap<>();

        values.forEach(basicValue -> {
            Datatypes.Type type = toType(basicValue);
            settingMap.put(basicValue.getName(), type);
        });

        return settingMap;
    }

    public static FileSchema.BaseFile makeBaseFile(Map<String, String> metadata, FileSchema.FileType fileType, long createdTime, long modifiedTime, byte[] context) {
        return FileSchema.BaseFile.newBuilder()
                .putAllMetadata(metadata)
                .setType(fileType)
                .setCreatedTime(createdTime)
                .setModifiedTime(modifiedTime)
                .setContext(ByteString.copyFrom(context))
                .build();
    }

    public static ConfigSchema.BaseConfig makeBaseConfig(String name, String description, String creator, String version) {
        return ConfigSchema.BaseConfig.newBuilder()
                .setName(name)
                .setDescription(description)
                .setCreator(creator)
                .setVersion(version)
                .build();
    }

    public static ConfigSchema.SettingsConfig makeSettingConfig(String name, String description, String creator, String version, Map<String, Datatypes.Type> settings) {
        return ConfigSchema.SettingsConfig.newBuilder()
                .setBaseConfig(
                        makeBaseConfig(
                                name,
                                description,
                                creator,
                                version
                        )
                )
                .putAllSettings(settings)
                .build();
    }

    public static ConfigSchema.SettingsConfig makeSettingConfig(String name, String description, Map<String, Datatypes.Type> settings) {
        return makeSettingConfig(
                name,
                description,
                AppInfo.AUTHOR,
                AppInfo.VERSION.toString(),
                settings
        );
    }

    public static Datatypes.Type.Builder typeBuilder() {
        return Datatypes.Type.newBuilder();
    }
}
