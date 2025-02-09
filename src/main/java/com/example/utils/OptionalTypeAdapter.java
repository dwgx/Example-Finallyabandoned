package com.example.utils;

import com.google.gson.*;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

public class OptionalTypeAdapter implements JsonSerializer<Optional<?>>, JsonDeserializer<Optional<?>> {

    @Override
    public JsonElement serialize(Optional<?> src, Type typeOfSrc, JsonSerializationContext context) {
        if (src.isEmpty()) {
            return JsonNull.INSTANCE;
        }
        Object value = src.get();
        return context.serialize(value);
    }

    @Override
    public Optional<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if (json.isJsonNull()) {
            return Optional.empty();
        }

        if (typeOfT instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) typeOfT;
            Type[] typeArgs = paramType.getActualTypeArguments();
            if (typeArgs.length == 1) {
                Type innerType = typeArgs[0];
                Object value = context.deserialize(json, innerType);
                return Optional.ofNullable(value);
            }
        }
        throw new JsonParseException("Unable to determine the actual type for Optional<T>");
    }
}
