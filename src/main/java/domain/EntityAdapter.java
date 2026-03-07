package domain;

import com.google.gson.*;
import java.lang.reflect.Type;

public class EntityAdapter implements JsonSerializer<Entity>, JsonDeserializer<Entity> {

    @Override
    public JsonElement serialize(Entity src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = context.serialize(src).getAsJsonObject();
        // Добавляем поле с именем класса прямо в объект
        jsonObject.addProperty("CLASSNAME", src.getClass().getName());
        return jsonObject;
    }

    @Override
    public Entity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        JsonElement classnameElement = jsonObject.get("CLASSNAME");
        if (classnameElement == null) {
            System.err.println("Доступные поля в JSON: " + jsonObject.keySet());
            throw new JsonParseException("Поле 'CLASSNAME' отсутствует в JSON");
        }

        String className = classnameElement.getAsString();
        System.out.println("Загружаем класс: " + className);

        try {
            Class<?> clazz = Class.forName(className);
            // Удаляем поле CLASSNAME перед десериализацией
            jsonObject.remove("CLASSNAME");
            return (Entity) context.deserialize(jsonObject, clazz);

        } catch (ClassNotFoundException e) {
            throw new JsonParseException("Класс не найден: " + className, e);
        }
    }
}