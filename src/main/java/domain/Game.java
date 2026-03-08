package domain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import datalayer.BackpackableAdapter;
import datalayer.BaseItemAdapter;
import datalayer.EnemyAdapter;
import datalayer.GameStats;
import datalayer.LoadSaveData;
import domain.items.*;
import domain.level.*;
import domain.player.Player;
import domain.monsters.*;

import java.util.List;
import java.util.Random;


public class Game {
    private Level level;
    private Player player;
    private Generation generator;
    private int currentRoom;
    private String gameLog;
    private ItemType backpackCurrentItems;
    private static final Random RANDOM = new Random();
    private Exploration exploration;
    private GameStats gameStats;

    private static final Gson gson = createGson();

    private static Gson createGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Enemy.class, new EnemyAdapter())  // Специально для Enemy
                .registerTypeAdapter(BaseItem.class, new BaseItemAdapter())
                .registerTypeAdapter(Backpackable.class, new BackpackableAdapter())
                .create();
    }

    public Game() {
        this.generator = new Generation();
        this.currentRoom = -1;
    }

    public Game(String name) {
        this(); // вызываем пустой конструктор
        this.player = new Player(name, null);
        this.gameStats = new GameStats(name);

        // генерируем первый уровень
        generateLevel(1);

        setGameLog("Game started");
    }

    private void setNewPosition(Position newPosition) {
        setGameLog("");

        Entity baseItem = level.getBaseItemByPos(newPosition);
        Enemy enemy = (Enemy) level.getEnemyByPos(newPosition);

        if (enemy != null && !player.isAsleep()) {
            setGameLog("Игрок атаковал " + enemy.getType() + enemy.toString());
            int damage = player.attack(enemy);
            if (damage == 0) {
                gameStats.addMiss();
            } else if (damage > 0) {
                gameStats.addAttack();
            }
            if (!enemy.isAlive()) {
                setGameLog(enemy.getType() + " убит. Получено " + enemy.getTreasureValue() + " золота.");
                player.setScore(player.getScore() + enemy.getTreasureValue());
                gameStats.addScore(enemy.getTreasureValue());
                gameStats.addKill();
                level.removeEnemy(enemy);      //Удалить животное
            }
        } else if (!player.isAsleep() && checkBounds(newPosition)) { //Проверка границ комнат и, коридоров
            player.setPosition(newPosition);
            gameStats.addStep();    //добавить шаг в стату
        }

        player.processTurn();

        //Проверка, что под ногами:
        // 1. предмет,
        if (baseItem != null) {
            if (player.pickUpItem((Backpackable) baseItem) ) {
                level.removeItem((BaseItem) baseItem);
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

        //отключить лимит на добавление предметов в комнатах (для сброса оружия из рюкзака)
        level.setUnlimitedItemsAdd();

    }




    private void checkStairsDown() {
        if (player.getPosition().equal(level.getStairsDown())) {
            if (level.getLevelNumber() == 20) {
                setGameLog("You are won game!");
                gameStats.setResult("completed");
                LoadSaveData.saveStatistics(gameStats);
            } else {
                generateLevel(level.getLevelNumber() + 1);
                gameStats.addLevel();

    // todo После прохождения каждого уровня необходимо сохранять полученную статистику и номер пройденного уровня.

            }
        }
    }
    
    public void updateVisible() {
        exploration.updateVisibility();
    }

    private void moveAllEnemies() {
        // Получаем всех врагов с уровня
        for (Entity entity : level.getAllEntities()) {
            if (entity instanceof Enemy enemy) {

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

            switch (backpackItemsList.get(itemIndex).getType()) {
                case FOOD -> gameStats.addFoodConsumed();
                case POTION -> gameStats.addElixirConsumed();
                case SCROLL -> gameStats.addScrollRead();
            }

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
            if (!level.addItem(currentWeapon, currentRoom)) {
                return false;
            }
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

    // Геттеры и сеттеры для ВСЕХ полей (нужны Gson)
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

    public Generation getGenerator() {
        return generator;
    }

    public void setGenerator(Generation generator) {
        this.generator = generator;
    }

    public int getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(int currentRoom) {
        this.currentRoom = currentRoom;
    }

    public String getGameLog() {
        return gameLog;
    }

    public void setGameLog(String gameLog) {
        this.gameLog = gameLog;
    }

    public ItemType getBackpackCurrentItems() {
        return backpackCurrentItems;
    }

    public void setBackpackCurrentItems(ItemType backpackCurrentItems) {
        this.backpackCurrentItems = backpackCurrentItems;
    }

    public Exploration getExploration() {
        return exploration;
    }

    public void setExploration(Exploration exploration) {
        this.exploration = exploration;
    }

    public GameStats getGameStats() {
        return gameStats;
    }

    public void setGameStats(GameStats gameStats) {
        this.gameStats = gameStats;
    }

    // МЕТОДЫ СЕРИАЛИЗАЦИИ
    public String toJson() {
        return gson.toJson(this);
    }

    public static Game fromJson(String jsonString) {
        return gson.fromJson(jsonString, Game.class);
    }
}
