package domain;

import domain.items.*;
import domain.level.*;
import domain.player.Player;
import domain.monsters.*;

import java.util.List;
import java.util.Random;


public class Game {
    private Level level;
    private Player player;
    private Generation generator = new Generation();
    private int currentRoom = -1;//-1 если игрок не в комнате
    private String gameLog;
    private ItemType backpackCurrentItems; // Текущая вкладка рюкзака
    private static final Random RANDOM = new Random();
    private Exploration exploration;

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

    private void setNewPosition(Position newPosition) {
        setGameLog("");

        //проверить не спит ли игрок

        Entity baseItem = level.getBaseItemByPos(newPosition);
        Enemy enemy = (Enemy) level.getEnemyByPos(newPosition);

        if (enemy != null && !player.isAsleep()) {
            setGameLog("Игрок атаковал " + enemy.getType() + enemy.toString());
            player.attack(enemy);
            if (!enemy.isAlive()) {
                setGameLog(enemy.getType() + " убит. Получено " + enemy.getTreasureValue() + " золота.");
                player.setScore(enemy.getTreasureValue());
                level.deleteEntity(enemy);
            }
        } else if (!player.isAsleep() && checkBounds(newPosition)) { //Проверка границ комнат и, коридоров
            player.setPosition(newPosition);
        }

        player.processTurn();

        //Проверка, что под ногами:
        // 1. предмет,
        if (baseItem != null) {
            if (player.pickUpItem((Backpackable) baseItem) ) {
                level.deleteEntity(baseItem);
                setGameLog("Игрок поднял " + baseItem.toString());
            } else {
                //Рюкзак полон. Просто печатаем название предмета под ногами
                setGameLog(baseItem.toString());
            }
        }

        // 2. выход с уровня
        checkStairsDown();

        //После хода игрока, ходят все монстры
        moveAllEnemies();

        if (player.getHealth() == 0) {
            //Игрок убит
            System.out.println("Игрок убит");
            setGameLog("Вы были убиты! Конец игры!");
        }
    }

    public void generateLevel(int levelNumber) {
        this.level = generator.generateLevel(levelNumber);

        // помещаем игрока в стартовую комнату
        currentRoom = level.getStartRoom();
        Position position = level.getRoom(currentRoom).getRandomFreePosition();
        player.setPosition(position);

        // Создаем объект расчета тумана войны и хранения пройденных комнат и маршрутов
        exploration = new Exploration(level, player);
        exploration.markRoomVisited(currentRoom);

    }




    private void checkStairsDown() {
        if (player.getPosition().equal(level.getStairsDown())) {
            if (level.getLevelNumber() == 20) {
                setGameLog("You are won game!");
            } else {
                generateLevel(level.getLevelNumber() + 1);
            }
        }
    }
    
    public void updateVisible() {
        exploration.updateVisibility();
    }

    public Exploration getExploration() {
        return exploration;
    }



    public Level getLevel() {
        return level;
    }

    public Player getPlayer() {
        return  player;
    }

    private void moveAllEnemies() {
        // Получаем всех врагов с уровня
        for (Entity entity : level.getAllEntities()) {
            if (entity instanceof Enemy) {
                Enemy enemy = (Enemy) entity;

                // Определяем текущую комнату врага
                int enemyRoom = level.findRoomByPosition(enemy.getPosition());
                if (enemyRoom != -1) {
                    Room room = level.getRoom(enemyRoom);
                    enemy.movePattern(room, player);
                }
                // Если враг в коридоре - можно добавить логику позже
            }
        }
    }

    private boolean checkBounds(Position newPosition) {
        //Проверить стены комнаты
        Room[] rooms = level.getRooms();
        //-1 если игрок не в коридоре
        for (int i = 0; i < rooms.length; i++) {
            if (rooms[i].isPositionInRoom(newPosition) || rooms[i].isPositionInDoor(newPosition)) {
                currentRoom = i;
                return true;
            }
        }

        //Проверить коридор
        if (level.findCorridorByPosition(newPosition) != null) {
            return true;
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
            level.addEntity(currentWeapon, currentRoom);
            player.equipWeapon(null);
            return true;
        }
        System.out.println("Нельзя выбросить оружие. На полу нет свободного места.");
        return false;
    }

    private Position getFreePositionNearPlayer() {
        List<Position> freePosList = level.getFreeNearPositions(player.getPosition());
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

}
