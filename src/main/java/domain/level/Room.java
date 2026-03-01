package domain.level;

import domain.Entity;
import domain.Position;
import domain.items.BaseItem;
import domain.monsters.Enemy;

import java.util.*;

// Конструктор комнаты возвращает объект со случайным размером комнаты в заданном диапазоне координат углов
public class Room {
    private final int MAX_DOORS = 4;
    private final Position leftCorner;
    private final Position rightCorner;
    private final Door[] doors = new Door[MAX_DOORS];
    private boolean isFreePositions;



    //Координаты сущностей в комнате.
    private List<Entity> entities = new ArrayList<>();
    // Разделяем сущности по типам для их подсчета в комнате
    private List<Enemy> enemies = new ArrayList<>();
    private List<BaseItem> items = new ArrayList<>();

    // минимальные размеры комнаты
    private static final int minRoomWidth = 8;
    private static final int minRoomHeight = 6;

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

    public Door[] getDoors() {
        return doors;
    }

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

    public boolean putEntintyToRndPlace(Entity entity) {
        Position position = getRandomFreePosition();
        if (position == null) {
            return false;
        }
        System.out.printf("rnd pos: %d %d", position.getX(), position.getY());
        entity.setPosition(position);
        entities.add(entity);
        return true;
    }


    public Position getRandomFreePosition(int decreaseArea) {
        if (!isFreePositions) return null;

        //Собрать координаты занятых позиций в виде Set String "x,y"
        Set<String> occupied = new HashSet<>();
        for (Entity entity : entities) {
            Position pos = entity.getPosition();
            occupied.add(pos.getX() + "," + pos.getY());
        }

        int leftX = this.getLeftCorner().getX() + 1 + decreaseArea;
        int leftY = this.getLeftCorner().getY() + 1 + decreaseArea;
        int rightX = this.getRightCorner().getX() - decreaseArea;
        int rightY = this.getRightCorner().getY() - decreaseArea;

        // Список свободных клеток
        List<Position> freePositions = new ArrayList<>();
        for (int x = leftX; x < rightX; x++) {
            for (int y = leftY; y < rightY; y++) {
                if (!occupied.contains(x + "," + y)) {
                    freePositions.add(new Position(x,y));
                }
            }
        }

        if (freePositions.isEmpty()) {
            this.isFreePositions = false;
            return null;
        }

        return freePositions.get(rndBetween(0,freePositions.size() - 1));
    }

    public Position getRandomFreePosition() {
       return getRandomFreePosition(0);
    }

    // Гетеры, сеттеры
    public Position getLeftCorner() {
        return leftCorner;
    }

    public Position getRightCorner() {
        return rightCorner;
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
        for (Entity entity : entities) {
            if (position.equal(entity.getPosition())) {
                return false;
            }
        }
        return true;
    }

    public boolean addEntity(Entity entity) {
        if (isFreePositions &&
                isPositionInRoom(entity.getPosition()) &&
                isPositionFree(entity.getPosition())) {
            if (entity instanceof BaseItem && canAddItem()){
                items.add((BaseItem) entity);
                entities.add(entity);
            }
            else if (entity instanceof Enemy && canAddEnemy()){
                enemies.add((Enemy) entity);
                entities.add(entity);
            }
            else {
                entities.add(entity);
            }

            return true;
        }
        return false;
    }


//    // Метод для получения всех свободных позиций
//    public List<Position> getFreePositions() {
//        // возвращает все свободные клетки в комнате
//    }

    // Проверка лимитов на типы сущностей
    public boolean canAddEnemy() {
        return enemies.size() < 1; // максимум 1 враг
    }

    public boolean canAddItem() {
        return items.size() < 3; // максимум 3 предмета
    }

    public void deleteEntity(Entity entity) {
        entities.remove(entity);
        if (entity instanceof BaseItem){
            items.remove(entity);
        }
        if (entity instanceof Enemy){
            enemies.remove(entity);
        }
    }

}
