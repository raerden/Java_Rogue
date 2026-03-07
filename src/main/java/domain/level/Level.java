package domain.level;

import domain.Entity;
import domain.Position;
import domain.items.BaseItem;
import domain.monsters.Enemy;

import java.util.*;

public class Level {
    private int levelNumber;
    private Room[] rooms;
    private List<Corridor> corridors;
    private LevelUnits units;
    private int startRoom;
    private int endRoom;
    private Position stairsDown; // лестница вниз

    public Level() {}

    public Level(int levelNumber, Room[] rooms, List<Corridor> corridors /*, List<Corridor> corridors */) {
        this.levelNumber = levelNumber;
        this.rooms = rooms;
        this.corridors = corridors; // коридоры - список одномерных палок. С координатами начала и конца
        this.units = new LevelUnits();
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

    public void setStairsDown(Position stairsDown) {
        this.stairsDown = stairsDown;

    }

    public Position getStairsDown() {
        return stairsDown;
    }

    public void removeEnemy(Enemy enemy) {
        units.removeEnemy(enemy);
        for (Room room : rooms) {
            room.removeEnemy(enemy);
        }
    }

    // Вместо addEntity используем конкретные методы
    public boolean addEnemy(Enemy enemy, int roomNumber) {
        if (roomNumber < 0 || roomNumber >= rooms.length) {
            return false;
        }

        Room room = rooms[roomNumber];

        // Проверяем, что позиция сущности установлена
        if (enemy.getPosition() == null) {
            return false;
        }

        // Проверяем, что позиция находится внутри указанной комнаты
        if (!room.isPositionInRoom(enemy.getPosition())) {
            return false;
        }

        // Проверяем, свободна ли позиция в комнате
        if (!room.isPositionFree(enemy.getPosition())) {
            System.out.println("Позиция занята: " + enemy.getPosition());
            return false;
        }

        // Пытаемся добавить сущность в общую коллекцию уровня
        if (units.addEnemy(enemy)) {
            // Если успешно добавили в уровень, добавляем и в комнату
            if (room.addEnemy(enemy)) {
                return true;
            } else {
                // Если не удалось добавить в комнату, то удаляем и с уровня
                units.removeEnemy(enemy);
                return false;
            }
        }

        return false;
    }

    public void removeItem(BaseItem item) {
        units.removeItem(item);
        for (Room room : rooms) {
            room.removeItem(item);
        }
    }

    public boolean addItem(BaseItem item, int roomNumber) {
        if (roomNumber < 0 || roomNumber >= rooms.length) {
            return false;
        }

        Room room = rooms[roomNumber];

        // Проверяем, что позиция сущности установлена
        if (item.getPosition() == null) {
            return false;
        }

        // Проверяем, что позиция находится внутри указанной комнаты
        if (!room.isPositionInRoom(item.getPosition())) {
            return false;
        }

        // Проверяем, свободна ли позиция в комнате
        if (!room.isPositionFree(item.getPosition())) {
            System.out.println("Позиция занята: " + item.getPosition());
            return false;
        }

        // Пытаемся добавить сущность в общую коллекцию уровня
        if (units.addItem(item)) {
            // Если успешно добавили в уровень, добавляем и в комнату
            if (room.addItem(item)) {
                return true;
            } else {
                // Если не удалось добавить в комнату, то удаляем и с уровня
                units.removeItem(item);
                return false;
            }
        }

        return false;
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

    public Corridor findCorridorByPosition(Position position) {
        for (Corridor corridor : corridors) {
            if (corridor.positionInCorridor(position)) {
                return corridor;
            }
        }
        return null;
    }

    public Set<Entity> getAllEntities() {
        return units.getAllEntities();
    }

    public Entity getEnemyByPos(Position pos) {
        Entity entity = units.findEntityAt(pos);
        if (entity instanceof Enemy) {
            return entity;
        }
        return null;
    }

    public Entity getBaseItemByPos(Position pos) {
        Entity entity = units.findEntityAt(pos);
        if (entity instanceof BaseItem) {
            return entity;
        }
        return null;
    }

    // Геттеры и сеттеры
    public int getLevelNumber() { return levelNumber; }
    public void setLevelNumber(int levelNumber) { this.levelNumber = levelNumber; }

    public Room[] getRooms() { return rooms; }
    public void setRooms(Room[] rooms) { this.rooms = rooms; }

    public List<Corridor> getCorridors() { return corridors; }
    public void setCorridors(List<Corridor> corridors) { this.corridors = corridors; }

    public Room getRoom(int roomNumber) {
        return rooms[roomNumber];
    }

}