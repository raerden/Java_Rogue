package datalayer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import domain.Game;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LoadSaveData {
    private static final String SAVE_DIR = "./src/main/resources/";
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    // Статический блок для создания директории сохранений
    static {
        try {
            Files.createDirectories(Paths.get(SAVE_DIR));
        } catch (IOException e) {
            System.err.println("Не удалось создать директорию для сохранений: " + e.getMessage());
        }
    }

    /**
     * Сохраняет игру в файл
     *
     * @param game объект игры для сохранения
     */
    public static void saveGame(Game game) {
        try {

            String filename = SAVE_DIR + "save_game.json";
            String json = game.toJson();
            Files.write(Paths.get(filename), json.getBytes());

            System.out.println("Игра сохранена в файл: " + filename);

        } catch (IOException e) {
            System.err.println("Ошибка при сохранении игры: " + e.getMessage());
        }
    }

    /**
     * Загружает игру из файла
     * @return объект Game или null в случае ошибки
     */
    public static Game loadGame() {
        try {
            String filename = SAVE_DIR + "save_game.json";
            String json = Files.readString(Paths.get(filename));
            Game game = Game.fromJson(json);

            System.out.println("Игра загружена из файла: " + filename);
            return game;

        } catch (IOException e) {
            System.err.println("Ошибка при загрузке игры: " + e.getMessage());
            return null;
        }
    }

    /**
     * Быстрое сохранение (автоматическое имя)
     */
    public static void quickSave(Game game) {
        saveGame(game);
    }

    /**
     * Быстрая загрузка последнего быстрого сохранения
     */
    public static Game quickLoad() {
        return loadGame();
    }
}