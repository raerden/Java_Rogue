package datalayer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import domain.Game;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LoadSaveData {
    private static final String SAVE_DIR = "./src/main/resources/";
    private static final String STATS_FILE = "game_statistics.json";
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
     * Быстрое сохранение
     */
    public static void quickSave(Game game) {
        saveGame(game);
    }

    /**
     * Быстрая загрузка
     */
    public static Game quickLoad() {
        return loadGame();
    }

    /**
     * Сохраняет статистику игры
     */
    public static void saveStatistics(GameStats stats) {
        try {
            // Загружаем существующую статистику
            Savedata savedata = loadSavedata();

            // Добавляем новую запись
            savedata.addGameStats(stats);

            // Сохраняем обратно
            String filename = SAVE_DIR + STATS_FILE;
            String json = gson.toJson(savedata);
            Files.write(Paths.get(filename), json.getBytes());

            System.out.println("Статистика сохранена: " + stats.getPlayerName() +
                    " | Score: " + stats.getScore() + " | Result: " + stats.getResult());

        } catch (IOException e) {
            System.err.println("Ошибка при сохранении статистики: " + e.getMessage());
        }
    }

    /**
     * Загружает статистику игр
     */
    public static Savedata loadSavedata() {
        try {
            String filename = SAVE_DIR + STATS_FILE;
            File file = new File(filename);

            if (!file.exists()) {
                // Если файла нет, создаем пустую статистику
                return new Savedata();
            }

            String json = Files.readString(Paths.get(filename));
            return gson.fromJson(json, Savedata.class);

        } catch (IOException e) {
            System.err.println("Ошибка при загрузке статистики: " + e.getMessage());
            return new Savedata();
        }
    }

    /**
     * Получает топ-5 записей
     */
    public static List<GameStats> getTopStats() {
        Savedata savedata = loadSavedata();
        return savedata.getGameStatsList();
    }

    /**
     * Очищает всю статистику
     */
    public static void clearStatistics() {
        Savedata savedata = new Savedata();
        saveSavedata(savedata);
    }

    private static void saveSavedata(Savedata savedata) {
        try {
            String filename = SAVE_DIR + STATS_FILE;
            String json = gson.toJson(savedata);
            Files.write(Paths.get(filename), json.getBytes());
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении статистики: " + e.getMessage());
        }
    }
}