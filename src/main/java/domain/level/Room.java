package domain.level;

import domain.Entity;
import domain.Position;
import domain.items.BaseItem;
import domain.monsters.Enemy;
import domain.player.Player;

import java.util.*;

// Конструктор комнаты возвращает объект со случайным размером комнаты в заданном диапазоне координат углов
public class Room {
    private final int MAX_DOORS = 4;
    private Position leftCorner;
    private Position rightCorner;
    private Door[] doors = new Door[MAX_DOORS];
    private boolean isFreePositions;
    private boolean limitItemsAdd;

    // Разделяем сущности по типам для их подсчета в комнате
    private List<Enemy> enemies = new ArrayList<>();
    private List<BaseItem> items = new ArrayList<>();

    // минимальные размеры комнаты
    private static final int minRoomWidth = 8;
    private static final int minRoomHeight = 6;

    public Room() {}

    // Конструктор случайно генерирует оба угла комнаты в заданном квадрате
    public Room(Position roomBoundsMin, Position roomBoundsMax) {

        int leftX = rndBetween(roomBoundsMin.getX(), roomBoundsMax.getX() - minRoomWidth);
        int leftY = rndBetween(roomBoundsMin.getY(), roomBoundsMax.getY() - minRoomHeight);

        // Правый нижний угол генерируем с гарантией минимальных размеров
        int rightX = rndBetween(
                Math.max(leftX + minRoomWidth, roomBoundsMin.getX() + minRoomWidth),
                roomBoundsMax.getX()
        );

        int rightY = rndBetween(
                Math.max(leftY + minRoomHeight, roomBoundsMin.getY() + minRoomHeight),
                roomBoundsMax.getY()
        );

        this.limitItemsAdd = true;
        this.isFreePositions = true;
        this.leftCorner = new Position(leftX, leftY);
        this.rightCorner = new Position(rightX, rightY);
    }

    //принимает boolean массив с направлениями дверей
    public void genDoors(boolean[] doorDirection) {
        // Вычисляем длины сторон для размещения дверей
        int minX = leftCorner.getX() + 1;
        int maxX = rightCorner.getX() - 1;
        int minY = leftCorner.getY() + 1;
        int maxY = rightCorner.getY() - 1;

        // северная стена
        if (doorDirection[0]) {
            doors[0] = new Door( new Position(rndBetween(minX, maxX), leftCorner.getY()));
        }
        // восточная стена
        if (doorDirection[1]) {
            doors[1] = new Door( new Position(rightCorner.getX(), rndBetween(minY, maxY)));
        }
        // Южная стена
        if (doorDirection[2]) {
            doors[2] = new Door( new Position(rndBetween(minX, maxX), rightCorner.getY()));
        }
        // Западная стена
        if (doorDirection[3]) {
            doors[3] = new Door( new Position(leftCorner.getX(), rndBetween(minY, maxY)));
        }
    }


    public Position getRandomFreePosition(int decreaseArea) {
        if (!isFreePositions) return null;

        // Список свободных клеток
        List<Position> freePositions = getFreePositions(decreaseArea);

        if (freePositions.isEmpty()) {
            this.isFreePositions = false;
            return null;
        }

        return freePositions.get(rndBetween(0,freePositions.size() - 1));
    }

    public List<Position> getFreePositions(int decreaseArea) {
        //Собрать координаты занятых позиций в виде Set String "x,y"
        Set<String> occupied = new HashSet<>();

        for (Enemy enemy : enemies) {
            Position pos = enemy.getPosition();
            occupied.add(pos.getX() + "," + pos.getY());
        }
        for (BaseItem item : items) {
            Position pos = item.getPosition();
            occupied.add(pos.getX() + "," + pos.getY());
        }

        int leftX = this.leftCorner.getX() + 1 + decreaseArea;
        int leftY = this.leftCorner.getY() + 1 + decreaseArea;
        int rightX = this.rightCorner.getX() - decreaseArea;
        int rightY = this.rightCorner.getY() - decreaseArea;

        // Список свободных клеток
        List<Position> freePositions = new ArrayList<>();
        for (int x = leftX; x < rightX; x++) {
            for (int y = leftY; y < rightY; y++) {
                if (!occupied.contains(x + "," + y)) {
                    freePositions.add(new Position(x,y));
                }
            }
        }

        return freePositions;
    }

    public Position getRandomFreePosition() {
       return getRandomFreePosition(0);
    }

    //Случайное число от и до включительно
    public static int rndBetween(int min, int max) {
        Random rnd = new Random();
        return rnd.nextInt(min, max + 1);
    }

    //Проверить что позиция внутри комнаты
    public boolean isPositionInRoom(Position position) {
        return position.getX() > leftCorner.getX() &&
                position.getX() < rightCorner.getX() &&
                position.getY() > leftCorner.getY() &&
                position.getY() < rightCorner.getY();
    }

    public boolean isPositionInDoor(Position position) {
        for (Door door : doors) {
            if (door != null && door.getPosition().equal(position))
                return true;
        }
        return false;
    }

    //Проверить что позиция не занята
    public boolean isPositionFree(Position position) {
        for (Enemy enemy : enemies) {
            if (position.equal(enemy.getPosition())) {
                return false;
            }
        }
        for (BaseItem item : items) {
            if (position.equal(item.getPosition())) {
                return false;
            }
        }
        return true;
    }

    public void removeEnemy(Enemy enemy) {
        enemies.remove(enemy);
    }

    public boolean addEnemy(Enemy enemy) {
        if (isFreePositions &&
                canAddEnemy() &&
                isPositionInRoom(enemy.getPosition()) &&
                isPositionFree(enemy.getPosition())) {

            enemies.add(enemy);

            return true;
        }
        return false;
    }

    public void removeItem(BaseItem item) {
        items.remove(item);
    }

    public boolean addItem(BaseItem item) {
        if (isFreePositions &&
                canAddItem() &&
                isPositionInRoom(item.getPosition()) &&
                isPositionFree(item.getPosition())) {

            items.add(item);

            return true;
        }
        return false;
    }

    // Проверка лимитов на типы сущностей
    public boolean canAddEnemy() {
        return enemies.size() < 1; // максимум 1 враг
    }

    public boolean canAddItem() {
        return limitItemsAdd ? items.size() < 3 : true; // максимум 3 предмета
    }

    @Override
    public String toString(){
        return String.format("%s, %s", leftCorner, rightCorner);
    }

    public Position getLeftCorner() { return leftCorner; }
    public void setLeftCorner(Position leftCorner) { this.leftCorner = leftCorner; }

    public Position getRightCorner() { return rightCorner; }
    public void setRightCorner(Position rightCorner) { this.rightCorner = rightCorner; }

    public Door[] getDoors() { return doors; }
    public void setDoors(Door[] doors) { this.doors = doors; }

    public boolean isFreePositions() { return isFreePositions; }
    public void setFreePositions(boolean freePositions) { isFreePositions = freePositions; }
    public Door getUpperDoor() {
        return doors[0];
    }
    public Door getRigthDoor() {
        return doors[1];
    }
    public Door getBottomDoor() {
        return doors[2];
    }
    public Door getLeftDoor() {
        return doors[3];
    }

    public void setLimitItemsAdd(boolean limitItemsAdd) {
        this.limitItemsAdd = limitItemsAdd;
    }

    public boolean isLimitItemsAdd() {
        return limitItemsAdd;
    }
}
