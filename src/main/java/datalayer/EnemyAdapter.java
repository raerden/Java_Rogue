package datalayer;

import com.google.gson.*;
import domain.monsters.Enemy;

import java.lang.reflect.Type;

public class EnemyAdapter implements JsonSerializer<Enemy>, JsonDeserializer<Enemy> {

    @Override
    public JsonElement serialize(Enemy src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = context.serialize(src).getAsJsonObject();
        jsonObject.addProperty("ENEMY_CLASS", src.getClass().getName());
        return jsonObject;
    }

    @Override
    public Enemy deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        JsonElement classElement = jsonObject.get("ENEMY_CLASS");

        String className = classElement.getAsString();

        try {
            Class<?> clazz = Class.forName(className);
            jsonObject.remove("ENEMY_CLASS");
            return (Enemy) context.deserialize(jsonObject, clazz);

        } catch (ClassNotFoundException e) {
            throw new JsonParseException("Класс не найден: " + className, e);
        }
    }
}