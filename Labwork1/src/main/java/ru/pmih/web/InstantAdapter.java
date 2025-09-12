package ru.pmih.web;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.Instant;

/**
 * Этот класс-адаптер учит библиотеку Gson, как правильно
 * преобразовывать объекты java.time.Instant в JSON и обратно.
 */
public class InstantAdapter implements JsonSerializer<Instant>, JsonDeserializer<Instant> {

    @Override
    public JsonElement serialize(Instant src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }

    @Override
    public Instant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Instant.parse(json.getAsString());
    }
}