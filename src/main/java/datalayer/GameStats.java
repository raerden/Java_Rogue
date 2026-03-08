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
    private String result; // "death", "completed", "quit"

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
        this.result = "unknown";
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
        this.result = "unknown";
    }

    @Override
    public String toString() {
        return String.format("%-10s | Score: %-5d | Level: %-3d | Kills: %-3d | Steps: %-5d | Result: %s",
                playerName, score, level, kills, steps, result);
    }

    // Геттеры и сеттеры...
    public String getPlayerName() { return playerName; }

    public int getScore() { return score; }

    public int getLevel() { return level; }

    public int getConsumedFoods() { return consumedFoods; }


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
    public int getConsumedElixirs() { return consumedElixirs; }
    public void setConsumedElixirs(int consumedElixirs) { this.consumedElixirs = consumedElixirs; }

    public int getReadedScrolls() { return readedScrolls; }
    public void setReadedScrolls(int readedScrolls) { this.readedScrolls = readedScrolls; }

    public int getKills() { return kills; }
    public void setKills(int kills) { this.kills = kills; }

    public long getAttacks() { return attacks; }
    public void setAttacks(long attacks) { this.attacks = attacks; }

    public long getMissed() { return missed; }
    public void setMissed(long missed) { this.missed = missed; }

    public long getSteps() { return steps; }
    public void setSteps(long steps) { this.steps = steps; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

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
