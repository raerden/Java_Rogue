package domain.items;

import java.util.Random;

public enum ConsumableType {
    HEALTH("здоровья"),
    STRENGTH("силы"),
    DEXTERITY("ловкости");

    private final String displayName;

    ConsumableType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ConsumableType getRandom() {
        ConsumableType[] types = values();
        return types[new Random().nextInt(types.length)];
    }
}

