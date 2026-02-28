package domain.items;

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
}

