package domain.level;

import domain.Position;
import domain.player.Player;

import java.util.HashSet;
import java.util.Set;

public class Exploration {
    private static final int SIGHT_RADIUS = 16;                  // Радиус обзора
    private Set<Integer> visitedRooms = new HashSet<>();         // Посещенные комнаты
    private Set<String> everVisitedCells = new HashSet<>();      // Все клетки, которые когда-либо видел игрок
    private Set<String> currentlyVisibleCells = new HashSet<>(); // Клетки, видимые в текущий момент
    private Level level;
    private Player player;
    private boolean showAllMap;

    public Exploration() {
        this.visitedRooms = new HashSet<>();
        this.everVisitedCells = new HashSet<>();
        this.currentlyVisibleCells = new HashSet<>();
        this.showAllMap = false;
    }

    public Exploration(Level level, Player player) {
        this.visitedRooms = new HashSet<>();
        this.everVisitedCells = new HashSet<>();
        this.currentlyVisibleCells = new HashSet<>();
        this.level = level;
        this.player = player;
        this.showAllMap = "IDDQD".equals(player.getName());
    }

    public void sync(Level level, Player player) {
        this.level = level;
        this.player = player;
    }

    public void markRoomVisited(int roomIndex) {
        visitedRooms.add(roomIndex);
    }

    public boolean isRoomVisited(int roomIndex) {
        if (showAllMap) return true;
        return visitedRooms.contains(roomIndex);
    }

    public void markCellVisited(Position pos) {
        everVisitedCells.add(pos.getX() + "," + pos.getY());
    }

    public boolean isCellVisited(Position pos) {
        if (showAllMap) return true;
        return everVisitedCells.contains(pos.getX() + "," + pos.getY());
    }

    public void markCellVisible(Position pos) {
        currentlyVisibleCells.add(pos.getX() + "," + pos.getY());
    }

    public boolean isCellVisible(Position pos) {
        if (showAllMap) return true;
        return currentlyVisibleCells.contains(pos.getX() + "," + pos.getY());
    }

    public void clearVisible() {
        currentlyVisibleCells.clear();
    }

    // Добавление области видимости
    public void updateVisibility() {
        clearVisible();

        int currentRoom = level.findRoomByPosition(player.getPosition());
        if (currentRoom >= 0) {
            //отмечаем посещенную комнату
            if (!isRoomVisited(currentRoom)) {
                markRoomVisited(currentRoom);
            }
            if (isDoorAtPosition(player.getPosition())) {
                corridorSimpleVision(player.getPosition());
            }
        } else {
            corridorSimpleVision(player.getPosition());
        }

        // Рассчитываем видимые клетки с учетом препятствий
        calculateVisibleCells(player.getPosition());
    }

    private void corridorSimpleVision(Position currentPos) {
        markVisible(currentPos);
        //найти соседние клетки коридора и отметить их
        Position left = new Position(currentPos.getX() - 1, currentPos.getY());
        Position right = new Position(currentPos.getX() + 1, currentPos.getY());
        Position up = new Position(currentPos.getX(), currentPos.getY() - 1);
        Position down = new Position(currentPos.getX(), currentPos.getY() + 1);
        if (level.findCorridorByPosition(left) != null || isDoorAtPosition(left)) {
            markVisible(left);
            lookThroughDoor(left);
        }
        if (level.findCorridorByPosition(right) != null || isDoorAtPosition(right)) {
            markVisible(right);
            lookThroughDoor(right);
        }
        if (level.findCorridorByPosition(up) != null || isDoorAtPosition(up)) {
            markVisible(up);
            lookThroughDoor(up);
        }
        if (level.findCorridorByPosition(down) != null || isDoorAtPosition(down)) {
            markVisible(down);
            lookThroughDoor(down);
        }
    }


    private void calculateVisibleCells(Position center) {
        int startX = center.getX();
        int startY = center.getY();

        // Определяем, где стоит игрок
        Corridor corridorPlayer = level.findCorridorByPosition(center);
        boolean isInCorridor = (corridorPlayer != null);
        boolean isInDoor = isDoorAtPosition(center);

        // лучи с шагом 3 градуса
        for (double angle = 0; angle < 360; angle += 2) {
            double radians = Math.toRadians(angle);

            double dx = Math.cos(radians);
            double dy = Math.sin(radians);

            double x = startX + 0.5;
            double y = startY + 0.5;

            Position prevPos = null;
            boolean enteredRoom = false;
            int currentRoom = -1;

            //движемся по лучу проверяя клетки под ним
            for (int i = 0; i < SIGHT_RADIUS; i++) {
                x += dx * 0.5;
                y += dy * 0.5;

                int cellX = (int) Math.floor(x);
                int cellY = (int) Math.floor(y);

                Position checkPos = new Position(cellX, cellY);

                if (prevPos != null && prevPos.equal(checkPos)) {
                    continue;
                }
                prevPos = checkPos;

                // Проверяем стены
                if (isWall(checkPos)) {
                    break;
                }

                // не просвечиваем закрытые двери
                if (isClosedDoor(checkPos)) {
                    break;
                }

                // не просвечиваем открытую дверь если она не на том же уровне, что и игрок.
                Position door = findDoorAtPosition(checkPos);
                if (door != null) {
                    if (center.getX() != door.getX() && center.getY() != checkPos.getY())
                        break;
                }


                // Луч в коридоре
                if (level.findCorridorByPosition(checkPos) != null) {
                    // Если игрок не в коридоре и это первая клетка коридора,
                    if (!isInCorridor && !isInDoor) {
                        // Мы в комнате и луч уперся в коридор - обрываем
                        break;
                    }
                    // Останавливаем луч в неиследованных клетках
                    if (!isCellVisited(checkPos)) {
                        break;
                    }
                    //игрок в коридоре и луч в коридоре. Проверить что коридор прямой
                    if (isInCorridor) {
                        if(!hasStraightCorridorLine(center,checkPos))
                            break;
                    }
                    markVisible(checkPos);
                    continue;
                }

                // Если мы вошли в комнату
                int roomAtPos = level.findRoomByPosition(checkPos);
                if (roomAtPos != -1) {
                    if (!enteredRoom) {
                        enteredRoom = true;
                        currentRoom = roomAtPos;
                    }

                    // Если это другая комната - обрываем луч
                    // чтобы из другой комнаты не светилась уже прошедшая
                    if (roomAtPos != currentRoom) {
                        break;
                    }
                }

                // Отмечаем клетку
                markVisible(checkPos);
            }
        }
    }


    private boolean hasStraightCorridorLine(Position from, Position to) {
        // Проверяем, что обе точки в коридоре
        Corridor fromCorridor = level.findCorridorByPosition(from);
        Corridor toCorridor = level.findCorridorByPosition(to);

        if (fromCorridor == null || toCorridor == null) return false;

        // Проверяем, что линия прямая (горизонталь или вертикаль)
        if (from.getX() == to.getX()) {
            // Вертикальная линия
            int minY = Math.min(from.getY(), to.getY());
            int maxY = Math.max(from.getY(), to.getY());

            // Проверяем каждую клетку между ними
            for (int y = minY + 1; y < maxY; y++) {
                Position checkPos = new Position(from.getX(), y);
                if (level.findCorridorByPosition(checkPos) == null) {
                    return false; // Разрыв
                }
            }
            return true;

        } else if (from.getY() == to.getY()) {
            // Горизонтальная линия
            int minX = Math.min(from.getX(), to.getX());
            int maxX = Math.max(from.getX(), to.getX());

            // Проверяем каждую клетку между ними
            for (int x = minX + 1; x < maxX; x++) {
                Position checkPos = new Position(x, from.getY());
                if (level.findCorridorByPosition(checkPos) == null) {
                    return false; // Разрыв
                }
            }
            return true;
        }

        return false; // Не прямая линия
    }

    private void markVisible(Position pos) {
        markCellVisible(pos);
        markCellVisited(pos);
    }


    private boolean isDoorAtPosition(Position pos) {
        for (Room room : level.getRooms()) {
            for (Door door : room.getDoors()) {
                if (door != null && door.getPosition().equal(pos)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Position findDoorAtPosition(Position pos) {
        for (Room room : level.getRooms()) {
            for (Door door : room.getDoors()) {
                if (door != null && door.getPosition().equal(pos)) {
                    return door.getPosition();
                }
            }
        }
        return null;
    }

    private boolean isWall(Position pos) {
        // Проверяем все комнаты
        for (Room room : level.getRooms()) {
            Position lc = room.getLeftCorner();
            Position rc = room.getRightCorner();

            // Проверяем, находится ли позиция на границе комнаты (стена)
            boolean isOnHorizontalWall = (pos.getY() == lc.getY() || pos.getY() == rc.getY()) &&
                    pos.getX() >= lc.getX() && pos.getX() <= rc.getX();
            boolean isOnVerticalWall = (pos.getX() == lc.getX() || pos.getX() == rc.getX()) &&
                    pos.getY() >= lc.getY() && pos.getY() <= rc.getY();

            if ((isOnHorizontalWall || isOnVerticalWall) &&
                    !room.isPositionInDoor(pos)
            ) { // Не дверь
                return true;
            }
        }
        return false;
    }

    private boolean isClosedDoor(Position pos) {
        // Проверяем все двери во всех комнатах
        for (Room room : level.getRooms()) {
            for (Door door : room.getDoors()) {
                if (door != null && door.getPosition().equal(pos)) {
                    // Дверь закрыта если:
                    // 1. Игрок не стоит в этой двери
                    // 2. Комната с другой стороны двери не посещена
                    // 3. Нет прямой видимости через дверь

                    if (player != null && player.getPosition().equal(pos)) {
                        return false; // Игрок в двери - она "открыта" для обзора
                    }

                    // Проверяем, видна ли комната с другой стороны
                    int roomWithDoor = level.findRoomByPosition(pos);
                    if (roomWithDoor != -1) {
                        // Если комната посещена, дверь считается открытой для обзора
                        if (isRoomVisited(roomWithDoor)) {
                            return false;
                        }
                    }

                    return true; // Дверь закрыта
                }
            }
        }
        return false;
    }

    // Смотрим в комнату через дверь
    private void lookThroughDoor(Position doorPos) {
        int roomNumber = level.findRoomByPosition(doorPos);
        if (roomNumber == -1) return;

        Room room = level.getRoom(roomNumber);

        // Если комната не исследована, прерываем
        if (!isRoomVisited(roomNumber)) return;

        // Определяем направление от двери внутрь комнаты
        Position direction = getDirectionIntoRoom(doorPos, room);
        if (direction == null) return;

        // Заглядываем внутрь комнаты, проверяя лучами
        for (int step = 1; step <= SIGHT_RADIUS / 2; step++) {
            Position lookPos = new Position(
                    doorPos.getX() + direction.getX() * step,
                    doorPos.getY() + direction.getY() * step
            );

            if (room.isPositionInRoom(lookPos)) {
                // Проверяем, нет ли стены на пути
                if (!isWall(lookPos)) {
                    markVisible(lookPos);
                }
            }
        }
    }

    private Position getDirectionIntoRoom(Position doorPos, Room room) {
        Position lc = room.getLeftCorner();
        Position rc = room.getRightCorner();

        // Дверь на северной стене
        if (doorPos.getY() == lc.getY()) {
            return new Position(0, 1); // идем вниз
        }
        // Дверь на южной стене
        if (doorPos.getY() == rc.getY()) {
            return new Position(0, -1); // идем вверх
        }
        // Дверь на западной стене
        if (doorPos.getX() == lc.getX()) {
            return new Position(1, 0); // идем вправо
        }
        // Дверь на восточной стене
        if (doorPos.getX() == rc.getX()) {
            return new Position(-1, 0); // идем влево
        }

        return null;
    }


    // Геттеры и сеттеры для всех полей
    public Set<Integer> getVisitedRooms() {
        return visitedRooms;
    }

    public void setVisitedRooms(Set<Integer> visitedRooms) {
        this.visitedRooms = visitedRooms != null ? visitedRooms : new HashSet<>();
    }

    public Set<String> getEverVisitedCells() {
        return everVisitedCells;
    }

    public void setEverVisitedCells(Set<String> everVisitedCells) {
        this.everVisitedCells = everVisitedCells != null ? everVisitedCells : new HashSet<>();
    }

    public Set<String> getCurrentlyVisibleCells() {
        return currentlyVisibleCells;
    }

    public void setCurrentlyVisibleCells(Set<String> currentlyVisibleCells) {
        this.currentlyVisibleCells = currentlyVisibleCells != null ? currentlyVisibleCells : new HashSet<>();
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean isShowAllMap() {
        return showAllMap;
    }

    public void setShowAllMap(boolean showAllMap) {
        this.showAllMap = showAllMap;
    }
}