package domain.level;

import domain.Entity;
import domain.Position;

import java.util.*;

public class Level {
    private final int levelNumber;
    private final Room[] rooms;
    private final List<Corridor> corridors;
    private final LevelUnits units;
    private int startRoom;
    private int endRoom;
    private Position stairsDown; // лестница вниз

    public Level(int levelNumber, Room[] rooms, List<Corridor> corridors /*, List<Corridor> corridors */) {
        this.levelNumber = levelNumber;
        this.rooms = rooms;
        this.corridors = corridors; // коридоры - список одномерных палок. С координатами начала и конца
        this.units = new LevelUnits();
    }

    public Room[] getRooms() {
        return rooms;
    }

    public Room getRoom(int roomNumber) {
        return rooms[roomNumber];
    }

    public List<Corridor> getCorridors() {
        return corridors;
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public void setStartRoom(int startRoom) {
        this.startRoom = startRoom;
    }

    public int getStartRoom() {
        return startRoom;
    }

    public void setEndRoom(int endRoom) {
        this.endRoom = endRoom;
    }

    public int getEndRoom() {
        return endRoom;
    }

    public void setStairsDown(Position stairsDown) {
        this.stairsDown = stairsDown;

    }

    public Position getStairsDown() {
        return stairsDown;
    }

    public boolean addEntity(Entity entity, int roomNumber) {
        if (roomNumber < 0 || roomNumber >= rooms.length) {
            return false;
        }

        Room room = rooms[roomNumber];

        // Проверяем, что позиция сущности установлена
        if (entity.getPosition() == null) {
            return false;
        }

        // Проверяем, что позиция находится внутри указанной комнаты
        if (!room.isPositionInRoom(entity.getPosition())) {
            return false;
        }

        // Проверяем, свободна ли позиция в комнате
        if (!room.isPositionFree(entity.getPosition())) {
            System.out.println("Позиция занята: " + entity.getPosition());
            return false;
        }

        // Пытаемся добавить сущность в общую коллекцию уровня
        if (units.addEntity(entity)) {
            // Если успешно добавили в уровень, добавляем и в комнату
            if (room.addEntity(entity)) {
                return true;
            } else {
                // Если не удалось добавить в комнату, то удаляем и с уровня
                units.deleteEntity(entity);
                return false;
            }
        }

        return false;
    }

    public List<Position> getFreePositionsInRoom(int roomNumber) {
        // Проверяем корректность номера комнаты
        if (roomNumber < 0 || roomNumber >= rooms.length) {
            return Collections.emptyList();
        }

        Room room = rooms[roomNumber];
        Position leftCorner = room.getLeftCorner();
        Position rightCorner = room.getRightCorner();

        List<Position> freePositions = new ArrayList<>();

        // Проходим по всем клеткам внутри комнаты (исключая стены)
        for (int x = leftCorner.getX() + 1; x < rightCorner.getX(); x++) {
            for (int y = leftCorner.getY() + 1; y < rightCorner.getY(); y++) {
                Position pos = new Position(x, y);

                // Проверяем, не занята ли позиция какой-либо сущностью
                if (units.getEntityAt(pos) == null) {
                    freePositions.add(pos);
                }
            }
        }

        return freePositions;
    }


 //Получить список свободных позиций в комнате вокруг указанной точки
     public List<Position> getFreeNearPositions(Position current) {
        int roomNumber = findRoomByPosition(current);
        if (roomNumber == -1) {
            return null; // точка не в комнате
        }

        Room room = rooms[roomNumber];
        List<Position> freePositions = new ArrayList<>();

        // Проверяем все 8 направлений (включая диагонали)
        int[] dx = {-1, 0, 1, -1, 1, -1, 0, 1};
        int[] dy = {-1, -1, -1, 0, 0, 1, 1, 1};

        for (int i = 0; i < dx.length; i++) {
            Position checkPos = new Position(
                    current.getX() + dx[i],
                    current.getY() + dy[i]
            );

            // Проверяем, что позиция внутри комнаты
            if (!room.isPositionInRoom(checkPos)) {
                continue;
            }

            if (!room.isPositionFree(checkPos)) {
                continue;
            }

            // Проверяем, что это не лестница вниз
            if (checkPos.equal(stairsDown)) {
                continue;
            }

            // Все проверки пройдены - позиция свободна
            freePositions.add(checkPos);
        }

        // 3. Возвращаем null, если свободных позиций нет
        return freePositions.isEmpty() ? null : freePositions;
    }

    //найти номер комнаты по позиции. Дверь тоже учитывается
    public int findRoomByPosition(Position position) {
        for (int i = 0; i < rooms.length; i++) {
            if (rooms[i].isPositionInRoom(position) || rooms[i].isPositionInDoor(position)) {
                return i;
            }
        }
        return -1;
    }

    public Set<Entity> getAllEntities() {
        return units.getAllEntities();
    }

//    public Exploration getExplorationState() {
//        return explorationState;
//    }
}