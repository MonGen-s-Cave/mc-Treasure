package com.mongenscave.mctreasure.managers.cooldown;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mongenscave.mctreasure.api.data.CooldownData;

import java.lang.reflect.Type;

public class CooldownAdapter implements JsonSerializer<CooldownData>, JsonDeserializer<CooldownData> {
    @Override
    public JsonElement serialize(CooldownData src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        obj.addProperty("timestamp", src.lastOpenedTimestamp());
        obj.addProperty("cooldown", src.cooldownMillis());
        return obj;
    }

    @Override
    public CooldownData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        long timestamp = obj.get("timestamp").getAsLong();
        long cooldown = obj.get("cooldown").getAsLong();
        return new CooldownData(timestamp, cooldown);
    }
}
