package datalayer;

import com.google.gson.*;
import domain.items.BaseItem;

import java.lang.reflect.Type;

public class BaseItemAdapter implements JsonSerializer<BaseItem>, JsonDeserializer<BaseItem> {

    @Override
    public JsonElement serialize(BaseItem src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = context.serialize(src).getAsJsonObject();
        // Добавляем поле с именем класса
        jsonObject.addProperty("itemClass", src.getClass().getName());
        return jsonObject;
    }

    @Override
    public BaseItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        JsonElement classElement = jsonObject.get("itemClass");
        if (classElement == null) {
            System.err.println("Доступные поля в JSON: " + jsonObject.keySet());
            throw new JsonParseException("Поле 'itemClass' отсутствует в JSON");
        }

        String className = classElement.getAsString();

        try {
            Class<?> clazz = Class.forName(className);
            // Удаляем поле itemClass перед десериализацией
            jsonObject.remove("itemClass");
            return (BaseItem) context.deserialize(jsonObject, clazz);

        } catch (ClassNotFoundException e) {
            throw new JsonParseException("Класс не найден: " + className, e);
        }
    }
}