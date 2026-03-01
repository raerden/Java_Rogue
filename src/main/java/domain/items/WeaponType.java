package domain.items;

import java.util.Random;

public enum WeaponType {
    DAGGER("кинжал", 8, 5),
    Hammer("молот", 25, 5),
    SWORD("меч", 10, 12),
    AXE("топор", 15, 5),
    BOW("лук", 20, 0),
    STAFF("посох", 6, 8),
    KATANA("катана", 10, 25);

    private final String displayName;
    private final int strength;
    private final int dexterity;

    WeaponType(String displayName, int strength, int dexterity) {
        this.displayName = displayName;
        this.strength = strength;
        this.dexterity = dexterity;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getStrength() {
        return strength;
    }

    public int getDexterity() {
        return dexterity;
    }

    public static WeaponType getRandom() {
        WeaponType[] types = values();
        return types[new Random().nextInt(types.length)];
    }
}
