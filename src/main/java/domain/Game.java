package domain;

import domain.items.Backpackable;
import domain.items.ItemType;
import domain.items.Weapon;
import domain.level.*;
import domain.player.Player;
import domain.monsters.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Game {
    private Level currentLevel;
    private Player player;
    private Generation generator = new Generation();
    private int currentRoom = -1;//-1 если игрок не в комнате
    private int currentCorridor = -1;
    private String gameLog;
    private ItemType backpackCurrentItems; // Текущая вкладка рюкзака
    private static final Random RANDOM = new Random();

    public Game(String name) {
        this.player = new Player(name, null);
        // генерируем первый уровень
        generateLevel(1);

        for (int i = 0; i < 9; i++) {
            player.getBackpack().addItem(EntityGenerator.generateRandomFood());
        }

        for (int i = 0; i < 9; i++) {
            player.getBackpack().addItem(EntityGenerator.generateRandomWeapon());
        }

        for (int i = 0; i < 9; i++) {
            player.getBackpack().addItem(EntityGenerator.generateRandomPotion());
        }

        for (int i = 0; i < 9; i++) {
            player.getBackpack().addItem(EntityGenerator.generateRandomScroll());
        }

        setGameLog("Game started");

    }

    public void generateLevel(int levelNumber) {
        this.currentLevel = generator.generateLevel(levelNumber);

        // помещаем игрока в стартовую комнату
        currentRoom = currentLevel.getStartRoom();
        Position position = currentLevel.getRoom(currentRoom).getRandomFreePosition();
        player.setPosition(position);
        currentLevel.getRoom(currentRoom).markRoomVisited(player);    //Маркируем комнату посещенной
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public Player getPlayer() {
        return  player;
    }

    private void setNewPosition(Position newPosition) {
        //проверка монстра:
        //нанести удар
        //если удар убил монстра: 1.забираем золото, 2.встаем на клетку
        //иначе
        //Проверка границ комнат и, коридоров
        if (checkBounds(newPosition)) {
            player.setPosition(newPosition);

        }

        //Проверка, что под ногами: 1. предмет, 2. выход с уровня

        //После хода игрока, ходят все монстры
        moveAllEnemies();
    }

    private void moveAllEnemies() {
        // Получаем всех врагов с уровня
        for (Entity entity : currentLevel.getAllEntities()) {
            if (entity instanceof Enemy) {
                Enemy enemy = (Enemy) entity;

                // Определяем текущую комнату врага
                int enemyRoom = findRoomByPosition(enemy.getPosition());
                if (enemyRoom != -1) {
                    Room room = currentLevel.getRoom(enemyRoom);
                    enemy.movePattern(room, player);
                }
                // Если враг в коридоре - можно добавить логику позже
            }
        }
    }

    private int findRoomByPosition(Position position) {
        Room[] rooms = currentLevel.getRooms();
        for (int i = 0; i < rooms.length; i++) {
            if (rooms[i].isPositionInRoom(position) || rooms[i].isPositionInDoor(position)) {
                return i;
            }
        }
        return -1;
    }

    private boolean checkBounds(Position newPosition) {
        //Проверить стены комнаты
        Room[] rooms = currentLevel.getRooms();
        //-1 если игрок не в коридоре
        for (int i = 0; i < rooms.length; i++) {
            if (rooms[i].isPositionInRoom(newPosition) || rooms[i].isPositionInDoor(newPosition)) {
                currentRoom = i;
                currentCorridor = -1;
                return true;
            }
        }

        //Проверить коридор
        for (Corridor corridor : currentLevel.getCorridors()) {
            if (corridor.positionInCorridor(newPosition)) {
                currentCorridor = currentLevel.getCorridors().indexOf(corridor);
                currentRoom = -1;
                return true;
            }
        }

        return false;
    }

    public void moveLeft() {
        Position newPosition = new Position(player.getPosition().getX() - 1, player.getPosition().getY());
        setNewPosition(newPosition);
    }

    public void moveRight() {
        Position newPosition = new Position(player.getPosition().getX() + 1, player.getPosition().getY());
        setNewPosition(newPosition);
    }

    public void moveUp() {
        Position newPosition = new Position(player.getPosition().getX(), player.getPosition().getY() - 1);
        setNewPosition(newPosition);
    }

    public void moveDown() {
        Position newPosition = new Position(player.getPosition().getX(), player.getPosition().getY() + 1);
        setNewPosition(newPosition);
    }

    public void selectBackpackItem(int itemIndex) {
        List<Backpackable> backpackItemsList = player.getBackpack().getListByType(backpackCurrentItems);

        if (itemIndex > backpackItemsList.size()) return;

        //получили нажатую клавишу в выбранном отделе рюкзака (0-9)
        //Но список идет от 0. кнопка 1 соотвествует 0 в списке рюкзака
        itemIndex--;

        if (backpackCurrentItems == ItemType.WEAPON) { //смена оружия
            // бросить оружие из рук
            if (itemIndex == -1) {
                dropCurrentWeapon();
            } else  if (dropCurrentWeapon()) {
                // если оружие успешно сброшено или руки были пустые
                // взять выбранное оружие в руки
                player.equipWeapon((Weapon) backpackItemsList.get(itemIndex));
                //удалить оружие из списка рюкзака
                backpackItemsList.remove(itemIndex);
            }
        } else {//используем еду, зелье, свиток
            backpackItemsList.get(itemIndex).apply(player);
            backpackItemsList.remove(itemIndex);
        }
    }

    private boolean dropCurrentWeapon() {
        Weapon currentWeapon = player.getEquippedWeapon();
        if (currentWeapon == null) return true;
        //проверить есть ли рядом свободная клетка, чтобы сбросить оружие
        Position freePosToDrop = getFreePositionNearPlayer();
        if (freePosToDrop != null) {
            //добавить currentWeapon в предметы на карте в указанную позицию
            currentWeapon.setPosition(freePosToDrop);
            currentLevel.addEntity(currentWeapon, currentRoom);
            player.equipWeapon(null);
            return true;
        }
        System.out.println("Нельзя выбросить оружие. На полу нет свободного места.");
        return false;
    }

    private Position getFreePositionNearPlayer() {
        List<Position> freePosList = currentLevel.getFreeNearPositions(player.getPosition());
        Position randomPos = null;
        if (freePosList != null && !freePosList.isEmpty()) {
            int randomIndex = RANDOM.nextInt(freePosList.size());
            randomPos = freePosList.get(randomIndex);
        }
        return randomPos;
    }


    public void setGameLog(String str) {
        gameLog = str;
    }

    public String getGameLog() {
        return gameLog;
    }

    public void setBackpackCurrentItems(ItemType itemType) {
        backpackCurrentItems = itemType;
    }

    public ItemType getBackpackCurrentItems() {
        return backpackCurrentItems;
    }


    // Добавление области видимости
    public void updateVisibility() {
        ExplorationState exploration = currentLevel.getExplorationState();
        exploration.clearVisible();

        // Рассчитываем видимые клетки с учетом препятствий
        calculateVisibleCells(player.getPosition(), exploration);

        if (currentRoom >= 0) {
            //отмечаем посещенную комнату
            if (!exploration.isRoomVisited(currentRoom)) {
                exploration.markRoomVisited(currentRoom);
            }
            if (isDoorAtPosition(player.getPosition())) {
                corridorSimpleVision(player.getPosition(), exploration);
            }
        } else {
            corridorSimpleVision(player.getPosition(), exploration);
        }
    }

    private void corridorSimpleVision(Position currentPos, ExplorationState exploration) {
        markVisible(currentPos, exploration);
        //найти соседние клетки коридора и отметить их
        Position left = new Position(currentPos.getX() - 1, currentPos.getY());
        Position right = new Position(currentPos.getX() + 1, currentPos.getY());
        Position up = new Position(currentPos.getX(), currentPos.getY() - 1);
        Position down = new Position(currentPos.getX(), currentPos.getY() + 1);
        if (findCorridorByPosition(left) != null || isDoorAtPosition(left)) {
            markVisible(left, exploration);
            lookThroughDoor(left, exploration);
        }
        if (findCorridorByPosition(right) != null || isDoorAtPosition(right)) {
            markVisible(right, exploration);
            lookThroughDoor(right, exploration);
        }
        if (findCorridorByPosition(up) != null || isDoorAtPosition(up)) {
            markVisible(up, exploration);
            lookThroughDoor(up, exploration);
        }
        if (findCorridorByPosition(down) != null || isDoorAtPosition(down)) {
            markVisible(down, exploration);
            lookThroughDoor(down, exploration);
        }
    }


    private void calculateVisibleCells(Position center, ExplorationState exploration) {
        int radius = 16;
        int startX = center.getX();
        int startY = center.getY();

        // Определяем, где стоит игрок
        boolean isInCorridor = (findCorridorByPosition(center) != null);
        boolean isInDoor = isDoorAtPosition(center);

        // лучи с шагом 3 градуса
        for (double angle = 0; angle < 360; angle += 3) {
            double radians = Math.toRadians(angle);

            double dx = Math.cos(radians);
            double dy = Math.sin(radians);

            double x = startX + 0.5;
            double y = startY + 0.5;

            Position prevPos = null;
            boolean enteredRoom = false;
            int currentRoom = -1;

            for (int i = 0; i < radius; i++) {
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

                // Если мы в коридоре и это клетка коридора
                if (findCorridorByPosition(checkPos) != null) {
                    // Если игрок не в коридоре и это первая клетка коридора,
                    // которую мы встретили - не обрываем луч, а продолжаем
                    if (!isInCorridor && !isInDoor) {
                        // Мы в комнате и луч уперся в коридор - обрываем
                        // (нельзя увидеть коридор из комнаты через стены)
                        break;
                    }
                    //мы в коридоре и пытаемся светить в неиследованные клетки коридора. Останавливаем луч
                    if (!exploration.isCellVisited(checkPos)) {
                        break;
                    }
                    // Иначе (мы в коридоре или в двери) который уже посетили - продолжаем луч
                    // здесь нужно добавить проверку чтобы коридор просвечивался только по прямой
                    // если коридор повернул то обрываем луч
                    markVisible(checkPos, exploration);
                    continue;
                }

                // Если мы вошли в комнату
                int roomAtPos = findRoomByPosition(checkPos);
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
                markVisible(checkPos, exploration);
            }
        }
    }


    private void markVisible(Position pos, ExplorationState exploration) {
        exploration.markCellVisible(pos);
        exploration.markCellVisited(pos);
    }


    private boolean isDoorAtPosition(Position pos) {
        for (Room room : currentLevel.getRooms()) {
            for (Door door : room.getDoors()) {
                if (door != null && door.getPosition().equal(pos)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isWall(Position pos) {
        // Проверяем все комнаты
        for (Room room : currentLevel.getRooms()) {
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
        for (Room room : currentLevel.getRooms()) {
            for (Door door : room.getDoors()) {
                if (door != null && door.getPosition().equal(pos)) {
                    // Дверь закрыта если:
                    // 1. Игрок не стоит в этой двери
                    // 2. Комната с другой стороны двери не посещена
                    // 3. Нет прямой видимости через дверь
                    Player player = getPlayer();
                    if (player != null && player.getPosition().equal(pos)) {
                        return false; // Игрок в двери - она "открыта" для обзора
                    }

                    // Проверяем, видна ли комната с другой стороны
                    int roomWithDoor = findRoomByPosition(pos);
                    if (roomWithDoor != -1) {
                        ExplorationState exploration = currentLevel.getExplorationState();
                        // Если комната посещена, дверь считается открытой для обзора
                        if (exploration.isRoomVisited(roomWithDoor)) {
                            return false;
                        }
                    }

                    return true; // Дверь закрыта
                }
            }
        }
        return false;
    }

    private Corridor findCorridorByPosition(Position pos) {
        for (Corridor corridor : currentLevel.getCorridors()) {
            if (corridor.positionInCorridor(pos)) {
                return corridor;
            }
        }
        return null;
    }


    // Смотрим в комнату через дверь
    private void lookThroughDoor(Position doorPos, ExplorationState exploration) {
        int roomNumber = findRoomByPosition(doorPos);
        if (roomNumber == -1) return;

        Room room = currentLevel.getRoom(roomNumber);

        // Если комната не исследована, прерываем
        if (!exploration.isRoomVisited(roomNumber)) return;

        // Определяем направление от двери внутрь комнаты
        Position direction = getDirectionIntoRoom(doorPos, room);
        if (direction == null) return;

        // Заглядываем на 3 клетки внутрь комнаты, проверяя лучами
        for (int step = 1; step <= 3; step++) {
            Position lookPos = new Position(
                    doorPos.getX() + direction.getX() * step,
                    doorPos.getY() + direction.getY() * step
            );

            if (room.isPositionInRoom(lookPos)) {
                // Проверяем, нет ли стены на пути
                if (!isWall(lookPos)) {
                    markVisible(lookPos, exploration);
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

}
