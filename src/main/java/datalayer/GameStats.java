package datalayer;

public class GameStats {
    private String playerName;
    private int score;
    private int level;
    private int consumedFoods;
    private int consumedElixirs;
    private int readedScrolls;
    private int kills;
    private long attacks;
    private long missed;
    private long steps;

    // Пустой конструктор для Gson
    public GameStats() {
        this.score = 0;
        this.level = 1;
        this.consumedFoods = 0;
        this.consumedElixirs = 0;
        this.readedScrolls = 0;
        this.kills = 0;
        this.attacks = 0;
        this.missed = 0;
        this.steps = 0;
    }

    public GameStats(String name) {
        this();
        this.playerName = name;
    }

    public void reset() {
        this.score = 0;
        this.level = 1;
        this.consumedFoods = 0;
        this.consumedElixirs = 0;
        this.readedScrolls = 0;
        this.kills = 0;
        this.attacks = 0;
        this.missed = 0;
        this.steps = 0;
    }

    @Override
    public String toString() {
        return String.format("GameStats{player='%s', score=%d, level=%d, kills=%d, steps=%d}",
                playerName, score, level, kills, steps);
    }

    // Геттеры для всех полей
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

    public int getReadedScrolls() {  // оставляем с опечаткой для совместимости
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

    // Сеттеры для всех полей (нужны Gson)
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setConsumedFoods(int consumedFoods) {
        this.consumedFoods = consumedFoods;
    }

    public void setConsumedElixirs(int consumedElixirs) {
        this.consumedElixirs = consumedElixirs;
    }

    public void setReadedScrolls(int readedScrolls) {
        this.readedScrolls = readedScrolls;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void setAttacks(long attacks) {
        this.attacks = attacks;
    }

    public void setMissed(long missed) {
        this.missed = missed;
    }

    public void setSteps(long steps) {
        this.steps = steps;
    }

    // Инкременторы
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
}
