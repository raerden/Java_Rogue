package domain.items;

import com.google.gson.*;
import java.lang.reflect.Type;

public class BackpackableAdapter implements JsonSerializer<Backpackable>, JsonDeserializer<Backpackable> {

    @Override
    public JsonElement serialize(Backpackable src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = context.serialize(src).getAsJsonObject();
        jsonObject.addProperty("backpackableClass", src.getClass().getName());
        return jsonObject;
    }

    @Override
    public Backpackable deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        JsonElement classElement = jsonObject.get("backpackableClass");
        if (classElement == null) {
            System.err.println("Доступные поля в JSON: " + jsonObject.keySet());
            throw new JsonParseException("Поле 'backpackableClass' отсутствует в JSON");
        }

        String className = classElement.getAsString();
        System.out.println("Загружаем предмет из рюкзака: " + className);

        try {
            Class<?> clazz = Class.forName(className);
            jsonObject.remove("backpackableClass");
            return (Backpackable) context.deserialize(jsonObject, clazz);

        } catch (ClassNotFoundException e) {
            throw new JsonParseException("Класс не найден: " + className, e);
        }
    }
}