package domain.monsters;

import com.google.gson.*;
import java.lang.reflect.Type;

public class EnemyAdapter implements JsonSerializer<Enemy>, JsonDeserializer<Enemy> {

    @Override
    public JsonElement serialize(Enemy src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = context.serialize(src).getAsJsonObject();
        jsonObject.addProperty("ENEMY_CLASS", src.getClass().getName());
        System.out.println("Сериализация Enemy: " + src.getClass().getName());
        return jsonObject;
    }

    @Override
    public Enemy deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        System.out.println("Десериализация Enemy. JSON: " + json.toString());

        JsonElement classElement = jsonObject.get("ENEMY_CLASS");
        if (classElement == null) {
            // Если нет ENEMY_CLASS, пробуем определить по наличию специфических полей
            if (jsonObject.has("isInvisible")) {
                System.out.println("Определен тип Ghost по полю isInvisible");
                return context.deserialize(json, Ghost.class);
            } else if (jsonObject.has("resting")) {
                System.out.println("Определен тип Ogre по полю resting");
                return context.deserialize(json, Ogre.class);
            } else if (jsonObject.has("moveRight")) {
                System.out.println("Определен тип SnakeMagician по полю moveRight");
                return context.deserialize(json, SnakeMagician.class);
            } else if (jsonObject.has("firstAttack")) {
                System.out.println("Определен тип Vampire по полю firstAttack");
                return context.deserialize(json, Vampire.class);
            } else {
                throw new JsonParseException("Не удалось определить тип Enemy. Поля: " + jsonObject.keySet());
            }
        }

        String className = classElement.getAsString();
        System.out.println("Загружаем конкретный класс Enemy: " + className);

        try {
            Class<?> clazz = Class.forName(className);
            jsonObject.remove("ENEMY_CLASS");
            return (Enemy) context.deserialize(jsonObject, clazz);

        } catch (ClassNotFoundException e) {
            throw new JsonParseException("Класс не найден: " + className, e);
        }
    }
}