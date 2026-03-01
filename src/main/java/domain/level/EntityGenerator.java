package domain.level;


import domain.Position;
import domain.Treasure;
import domain.items.*;
import domain.monsters.*;

import java.util.*;

public class EntityGenerator {
    private static final Random random = new Random();

    // ========== ГЕНЕРАЦИЯ МОНСТРОВ ==========

    /**
     * Генерирует монстра с учётом уровня сложности
     */
    public static Enemy generateEnemyForLevel(int levelNumber) {
        EnemyType type = EnemyType.getRandom();

        switch (type) {
            case ZOMBIE:
                return new Zombie(levelNumber, null);
            case GHOST:
                return new Ghost(levelNumber, null);
            case OGRE:
                return new Ogre(levelNumber, null);
            case VAMPIRE:
                return new Vampire(levelNumber, null);
            case SNAKE_MAGICIAN:
                return new SnakeMagician(levelNumber, null);
            default:
                return new Zombie(levelNumber, null);
        }
    }

    // ========== ГЕНЕРАЦИЯ ПРЕДМЕТОВ ==========

    /**
     * Генерирует случайный предмет
     */
    public static BaseItem generateRandomItem() {
        ItemType type = ItemType.getRandomItem();

        switch (type) {
            case WEAPON:
                return generateRandomWeapon();
            case POTION:
                return generateRandomPotion();
            case FOOD:
                return generateRandomFood();
            case SCROLL:
                return generateRandomScroll();
            case TREASURE:
                return generateRandomTreasure();
            default:
                return generateRandomFood();
        }
    }

    /**
     * Генерирует случайное оружие
     */
    public static Weapon generateRandomWeapon() {
        WeaponType weaponType = WeaponType.getRandom();
        String name = generateWeaponName(weaponType);
        int strength = calculateWeaponStrength(weaponType);
        int dexterity = calculateWeaponDexterity(weaponType);

        return new Weapon(name, strength, dexterity, null);
    }


    private static String generateWeaponName(WeaponType type) {
        String[] prefixes = {"Старый", "Острый", "Магический", "Ржавый", "Блестящий", "Проклятый"};
        String[] suffixes = {"убийца", "воина", "героя", "варвара", "рыцаря"};

        String prefix = prefixes[random.nextInt(prefixes.length)];
        String suffix = suffixes[random.nextInt(suffixes.length)];

        return prefix + " " + type.getDisplayName() + " " + suffix;
    }

    private static int calculateWeaponStrength(WeaponType type) {
        // Базовая сила + случайная вариация
        int strength = type.getStrength();
        int variation = random.nextInt(5) - 2; // -2 до +2
        return Math.max(1, strength + variation);
    }

    private static int calculateWeaponDexterity(WeaponType type) {
        // Базовая ловкость + случайная вариация
        int dexterity = type.getDexterity();
        int variation = random.nextInt(5) - 2; // -2 до +2
        return Math.max(1, dexterity + variation);
    }

    /**
     * Генерирует случайное зелье
     */
    public static Potion generateRandomPotion() {
        ConsumableType effectType = ConsumableType.getRandom();
        String name = generatePotionName(effectType);
        int bonus = calculatePotionBonus(effectType);
        int duration = random.nextInt(5) + 6; // 6-11 ходов

        return new Potion(name, bonus, duration, effectType, null);
    }

    private static String generatePotionName(ConsumableType type) {
        String[] prefixesForHeal = {"Живая вода", "Активированный уголь", "Подорожник", "Зелье исцеления"};
        String[] prefixesForStrength = {"Балтика 9", "Cветлое нефильтрованное", "Сила Огра"};
        String[] prefixesForDexterity = {"Скользкий тип", "Нео", "Я у мамы вор"};
        String prefix;
        if (type.equals(ConsumableType.HEALTH)) {
            prefix = prefixesForHeal[random.nextInt(prefixesForHeal.length)];
        }else if (type.equals(ConsumableType.STRENGTH)) {
            prefix = prefixesForStrength[random.nextInt(prefixesForStrength.length)];
        }else{
            prefix = prefixesForDexterity[random.nextInt(prefixesForDexterity.length)];
        }
        return "Зелье " + type.getDisplayName() + " " + prefix;
    }

    private static int calculatePotionBonus(ConsumableType type) {
        switch (type) {
            case HEALTH:
                return random.nextInt(30) + 20; // 20-50 HP
            case STRENGTH:
                return random.nextInt(6) + 8;    // 8-14 силы
            case DEXTERITY:
                return random.nextInt(5) + 3;    // 3-8 ловкости
            default:
                return 5;
        }
    }

    /**
     * Генерирует случайную еду
     */
    public static Food generateRandomFood() {
        String[] foodNames = {"Яблоко", "Хлеб", "Мясо", "Рыба", "Сыр", "Пирог", "Сухофрукты"};
        String name = foodNames[random.nextInt(foodNames.length)];
        int healthRestore = random.nextInt(30) + 10; // 10-40 HP

        return new Food(name, healthRestore, null);
    }

    /**
     * Генерирует случайное сокровище
     */
    public static Treasure generateRandomTreasure() {
        String[] treasureNames = {
                "Золотая монета", "Драгоценный камень",
                "Золотое кольцо", "Статуэтка", "Древняя монета"
        };
        String name = treasureNames[random.nextInt(treasureNames.length)];
        int value = random.nextInt(100) + 10; // 10-110 золота

        return new Treasure(name, value, null);
    }
    /**
     * Генерирует случайный свиток
     */
    public static Scroll generateRandomScroll() {
        ConsumableType effectType = ConsumableType.getRandom();
        String name = generateScrollName(effectType);
        int bonus = calculateScrollBonus(effectType);

        return new Scroll(name, bonus, effectType, null);
    }

    private static String generateScrollName(ConsumableType type) {
        String[] prefixesForHeal = {"Исцеление", "Здоровье", "Жизненная сила", "Регенерация"};
        String[] prefixesForStrength = {"Мощь", "Сила", "Гроза", "Разрушение"};
        String[] prefixesForDexterity = {"Ловкость", "Проворство", "Гибкость", "Уклонение"};

        String[] suffixes = {"древних", "мага", "героя", "великана", "дракона"};

        String prefix;
        if (type.equals(ConsumableType.HEALTH)) {
            prefix = prefixesForHeal[random.nextInt(prefixesForHeal.length)];
        } else if (type.equals(ConsumableType.STRENGTH)) {
            prefix = prefixesForStrength[random.nextInt(prefixesForStrength.length)];
        } else {
            prefix = prefixesForDexterity[random.nextInt(prefixesForDexterity.length)];
        }

        String suffix = suffixes[random.nextInt(suffixes.length)];

        return "Свиток " + type.getDisplayName() + " \"" + prefix + " " + suffix + "\"";
    }

    private static int calculateScrollBonus(ConsumableType type) {
        switch (type) {
            case HEALTH:
                return random.nextInt(20) + 5; // 5-25 HP
            case STRENGTH:
                return random.nextInt(4) + 3;    // 3-7 силы
            case DEXTERITY:
                return random.nextInt(4) + 3;    // 3-7 ловкости (постоянно)
            default:
                return 5;
        }
    }

    // ========== ГЕНЕРАЦИЯ С УЧЁТОМ УРОВНЯ ==========

//    /**
//     * Генерирует набор предметов для уровня
//     */
//    public static List<BaseItem> generateItemsForLevel(int levelNumber, int count) {
//        List<BaseItem> items = new ArrayList<>();
//
//        for (int i = 0; i < count; i++) {
//            // С увеличением уровня растёт шанс найти лучшее оружие и зелья
//            int chance = random.nextInt(100);
//
//            if (chance < 40) {
//                items.add(generateRandomFood());
//            } else if (chance < 70) {
//                items.add(generateRandomPotion());
//            } else if (chance < 90) {
//                items.add(generateRandomWeapon());
//            } else {
//                items.add(generateRandomTreasure());
//            }
//        }
//
//        return items;
//    }

    // ========== ВСПОМОГАТЕЛЬНЫЕ ENUM ==========

//    public enum WeaponType {
//        DAGGER("кинжал", 8, 5),
//        Hammer("молот", 25, 5),
//        SWORD("меч", 10, 12),
//        AXE("топор", 15, 5),
//        BOW("лук", 20, 0),
//        STAFF("посох", 6, 8),
//        KATANA("катана", 10, 25);
//
//        private final String displayName;
//        private final int strength;
//        private final int dexterity;
//
//        WeaponType(String displayName, int strength, int dexterity) {
//            this.displayName = displayName;
//            this.strength = strength;
//            this.dexterity = dexterity;
//        }
//
//        public String getDisplayName() {
//            return displayName;
//        }
//
//        public int getStrength() {
//            return strength;
//        }
//
//        public int getDexterity() {
//            return dexterity;
//        }
//
//        public static WeaponType getRandom() {
//            WeaponType[] types = values();
//            return types[new Random().nextInt(types.length)];
//        }
//    }
}
