package domain.items;

public enum ItemType {
    WEAPON("оружие"),
    FOOD("еда"),
    POTION("зелье"),
    SCROLL("свиток"),
    TREASURE("сокровища");

    private final String displayName;

    ItemType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
