package datalayer;

public class GameStats {
    private String playerName;
    private int score = 0;
    private int level = 1;
    private int consumedFoods = 0;
    private int consumedElixirs = 0;
    private int readedScrolls = 0;
    private int kills = 0;
    private long attacks = 0;
    private long missed = 0;
    private long steps = 0;

    public GameStats(String name) {
        this.playerName = name;
    }

    public GameStats() {
    }

    // 1. Геттеры для всех полей
    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    public int getLevel() {
        return level;
    }

    public int getConsumedFoods() {
        return consumedFoods;
    }

    public int getConsumedElixirs() {
        return consumedElixirs;
    }

    public int getReadedScrools() {
        return readedScrolls;
    }

    public int getKills() {
        return kills;
    }

    public long getAttacks() {
        return attacks;
    }

    public long getMissed() {
        return missed;
    }

    public long getSteps() {
        return steps;
    }

    // 2. Инкременторы (обратите внимание на исправление опечатки readedScrools -> readedScrolls)
    public void addStep() {
        steps++;
    }

    public void addAttack() {
        attacks++;
    }

    public void addMiss() {
        missed++;
    }

    public void addKill() {
        kills++;
    }

    public void addFoodConsumed() {
        consumedFoods++;
    }

    public void addElixirConsumed() {
        consumedElixirs++;
    }

    public void addScrollRead() {
        readedScrolls++;
    }

    public void addScore(int points) {
        score += points;
    }

    public void addLevel() {
        level++;
    }

    // 3. GameStatToString() - пакует поля в строку для JSON
    public String GameStatToString() {
        return String.format("{%n" +
                        "  \"playerName\": \"%s\",%n" +
                        "  \"score\": %d,%n" +
                        "  \"level\": %d,%n" +
                        "  \"consumedFoods\": %d,%n" +
                        "  \"consumedElixirs\": %d,%n" +
                        "  \"readedScrolls\": %d,%n" +
                        "  \"kills\": %d,%n" +
                        "  \"attacks\": %d,%n" +
                        "  \"missed\": %d,%n" +
                        "  \"steps\": %d%n" +
                        "}",
                playerName, score, level, consumedFoods, consumedElixirs,
                readedScrolls, kills, attacks, missed, steps);
    }

    // 4. GameStatFromString() - распаковывает строку в поля
    public void GameStatFromString(String data) {
        // Простой парсинг JSON-подобной строки
        String[] lines = data.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.contains(":")) {
                String[] parts = line.split(":", 2);
                String key = parts[0].trim().replace("\"", "");
                String value = parts[1].trim().replace(",", "").replace("\"", "");

                switch (key) {
                    case "playerName":
                        playerName = value;
                        break;
                    case "score":
                        score = Integer.parseInt(value);
                        break;
                    case "level":
                        level = Integer.parseInt(value);
                        break;
                    case "consumedFoods":
                        consumedFoods = Integer.parseInt(value);
                        break;
                    case "consumedElixirs":
                        consumedElixirs = Integer.parseInt(value);
                        break;
                    case "readedScrolls":
                        readedScrolls = Integer.parseInt(value);
                        break;
                    case "kills":
                        kills = Integer.parseInt(value);
                        break;
                    case "attacks":
                        attacks = Long.parseLong(value);
                        break;
                    case "missed":
                        missed = Long.parseLong(value);
                        break;
                    case "steps":
                        steps = Long.parseLong(value);
                        break;
                }
            }
        }
    }

}
