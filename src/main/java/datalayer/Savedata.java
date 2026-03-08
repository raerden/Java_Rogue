package datalayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Savedata {
    private List<GameStats> gameStatsList;
    private static final int MAX_STATS_SIZE = 5;

    public Savedata() {
        this.gameStatsList = new ArrayList<>();
    }

    public void addGameStats(GameStats stats) {
        gameStatsList.add(stats);
        // Сортируем по убыванию score
        Collections.sort(gameStatsList,
                Comparator.comparingInt(GameStats::getScore).reversed());

        // Оставляем только топ-5
        if (gameStatsList.size() > MAX_STATS_SIZE) {
            gameStatsList = gameStatsList.subList(0, MAX_STATS_SIZE);
        }
    }

    public List<GameStats> getGameStatsList() {
        return gameStatsList;
    }

    public void setGameStatsList(List<GameStats> gameStatsList) {
        this.gameStatsList = gameStatsList;
    }

    public void clear() {
        gameStatsList.clear();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("=== LEADERBOARD (TOP 5) ===\n");
        sb.append(String.format("%-3s %-10s %-8s %-8s %-8s %-8s\n",
                "#", "Name", "Score", "Level", "Kills", "Steps"));

        for (int i = 0; i < gameStatsList.size(); i++) {
            GameStats stats = gameStatsList.get(i);
            sb.append(String.format("%-3d %-10s %-8d %-8d %-8d %-8d\n",
                    i + 1,
                    stats.getPlayerName(),
                    stats.getScore(),
                    stats.getLevel(),
                    stats.getKills(),
                    stats.getSteps()
            ));
        }
        return sb.toString();
    }
}