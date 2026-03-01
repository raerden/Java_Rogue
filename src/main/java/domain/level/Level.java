package domain.level;

import domain.Entity;
import domain.Position;

import java.util.*;

public class Level {
    private final int levelNumber;
    private final Room[] rooms;
    private final List<Corridor> corridors;
    //private final Map<Position, Entity> entities; // позиция -> сущность
    private final LevelUnits units;
    private int startRoom;
    private int endRoom;
    private Position stairsDown; // лестница вниз

    public Level(int levelNumber, Room[] rooms, List<Corridor> corridors /*, List<Corridor> corridors */) {
        this.levelNumber = levelNumber;
        this.rooms = rooms;
        this.corridors = corridors; // коридоры - список одномерных палок. С координатами начала и конца
        this.units = new LevelUnits();
        //this.entities = new HashMap<>();
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

    /**
     * Добавление происходит на уровень и в комнату
     * <p>Проверяет позицию сущности, ее нахождение в комнате </p>
     * @param entity
     * @param roomNumber
     * @return
     */
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

    public Set<Entity> getAllEntities() {
        return units.getAllEntities();
    }
    //    private static final int[][] ROOM_COORDS = {
//            {0, 0, 22, 9},    // комната 1
//            {25, 0, 47, 9},   // комната 2
//            {50, 0, 72, 9},   // комната 3
//            {0, 11, 22, 20},  // комната 4
//            {25, 11, 47, 20}, // комната 5
//            {50, 11, 72, 20}, // комната 6
//            {0, 22, 22, 30},  // комната 7
//            {25, 22, 47, 30}, // комната 8
//            {50, 22, 72, 30}  // комната 9
//    };
//
//    public Level(int level) {
//        this.rooms = generateRooms();
//    }
//
//    private ArrayList<Room> generateRooms() {
//        ArrayList<Room> rooms = new ArrayList<>(9);
//        for (int i = 0; i < 9; i++) {
//            Position min = new Position(ROOM_COORDS[i][0], ROOM_COORDS[i][1]);
//            Position max = new Position(ROOM_COORDS[i][2], ROOM_COORDS[i][3]);
//            rooms.add(new Room(min, max));
//        }
//        return rooms;
//    }
}