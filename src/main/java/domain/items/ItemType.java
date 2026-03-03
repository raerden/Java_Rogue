package domain.items;

import java.util.Random;

public enum ItemType {
    WEAPON("Weapons"),
    FOOD("Foods"),
    POTION("Potions"),
    SCROLL("Scrolls"),
    TREASURE("сокровища");

    private static final Random RANDOM = new Random();

    public static ItemType getRandomItem() {
        ItemType[] items = values();
        return items[RANDOM.nextInt(items.length)];
    }

    private final String displayName;

    ItemType(String displayName) {
        this.displayName = displayName;
    }


    public String getDisplayName() {
        return displayName;
    }
}
