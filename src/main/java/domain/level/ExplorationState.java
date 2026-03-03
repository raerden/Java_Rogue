package domain.level;

import domain.Position;
import java.util.HashSet;
import java.util.Set;

public class ExplorationState {
    private static final int SIGHT_RADIUS = 5;                  // Радиус обзора
    private Set<Integer> visitedRooms = new HashSet<>();        // Посещенные комнаты
    private Set<String> everVisitedCells = new HashSet<>();     // Все клетки, которые когда-либо видел игрок
    private Set<String> currentlyVisibleCells = new HashSet<>();// Клетки, видимые в текущий момент

    public ExplorationState(int visitedRoom) {
        visitedRooms.add(visitedRoom);
    }

    public void markRoomVisited(int roomIndex) {
        visitedRooms.add(roomIndex);
    }

    public boolean isRoomVisited(int roomIndex) {
        return visitedRooms.contains(roomIndex);
    }

    public void markCellVisited(Position pos) {
        everVisitedCells.add(pos.getX() + "," + pos.getY());
    }

    public boolean isCellVisited(Position pos) {
        return everVisitedCells.contains(pos.getX() + "," + pos.getY());
    }

    public void markCellVisible(Position pos) {
        currentlyVisibleCells.add(pos.getX() + "," + pos.getY());
    }

    public boolean isCellVisible(Position pos) {
        return currentlyVisibleCells.contains(pos.getX() + "," + pos.getY());
    }

    public void clearVisible() {
        currentlyVisibleCells.clear();
    }

    public int getSightRadius() {
        return SIGHT_RADIUS;
    }
}