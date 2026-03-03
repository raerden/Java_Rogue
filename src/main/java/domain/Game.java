package domain;

import domain.items.Backpackable;
import domain.items.ItemType;
import domain.items.Weapon;
import domain.level.*;
import domain.player.Player;
import domain.monsters.*;

import java.util.List;
import java.util.Random;


public class Game {
    private Level currentLevel;
    private Player player;
    private Generation generator = new Generation();
    private int currentRoom = -1;//-1 если игрок не в комнате
    private int currentCorridor = -1;//-1 если игрок не в коридоре
    private boolean playerMoved = false; // флаг для отслеживания хода игрока
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

        playerMoved = true;
        update();
    }

    public void update() {
        if (playerMoved) {
            moveAllEnemies();
            playerMoved = false;
        }
    }

    /**
     * Перемещение всех врагов на уровень
     */
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

    /**
     * Находит номер комнаты по позиции
     * @return номер комнаты или -1, если позиция не в комнате
     */
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
                // применить выбранное оружие
                player.equipWeapon((Weapon) backpackItemsList.get(itemIndex));
                //удалить предмет из списка
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
            currentLevel.addEntity(currentWeapon, currentRoom);
            player.equipWeapon(null);
            System.out.println(currentRoom + "Выбросил на пол " + currentWeapon.toString());
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
}
