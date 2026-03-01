package domain.items;

import domain.Character;
import domain.player.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Backpack {
    private final List<Backpackable> weapons;
    private final List<Backpackable> potions; // эликсиры
    private final List<Backpackable> scrolls; // свитки
    private final List<Backpackable> foods;     // еда
    private int totalTreasureValue; // сокровища хранятся суммарно

    private static final int MAX_ITEMS_PER_TYPE = 9;

    public Backpack() {
        this.weapons = new ArrayList<>();   // оружие
        this.potions = new ArrayList<>();   // эликсиры
        this.scrolls = new ArrayList<>();   // свитки
        this.foods = new ArrayList<>();     // еда
        this.totalTreasureValue = 0;        // сокровища
    }

    public boolean addItem(Backpackable item) {
        if (item == null) return false;

        switch (item.getType()) {
            case WEAPON:
                return tryAddToList(weapons, item);
            case POTION:
                return tryAddToList(potions, item);
            case SCROLL:
                return tryAddToList(scrolls, item);
            case FOOD:
                return tryAddToList(foods, item);
            default:
                return false;
        }
    }

    private boolean tryAddToList(List<Backpackable> list, Backpackable item) {
        if (list.size() >= MAX_ITEMS_PER_TYPE) {
            System.out.println("Рюкзак полон для предметов этого типа! " + item);
            return false;
        }
        return list.add(item);
    }

    public Optional<Backpackable> useItem(ItemType type, int slotIndex, Player player) {
        if (slotIndex < 0 || slotIndex >= MAX_ITEMS_PER_TYPE) {
            return Optional.empty();
        }

        List<Backpackable> targetList = getListByType(type);

        if (slotIndex >= targetList.size()) {
            System.out.println("Нет предмета в слоте " + (slotIndex + 1));
            return Optional.empty();
        }

        Backpackable item = targetList.remove(slotIndex);
        item.apply(player);

        return Optional.of(item);
    }

    public List<Backpackable> getListByType(ItemType type) {
        switch (type) {
            case WEAPON: return weapons;
            case POTION: return potions;
            case SCROLL: return scrolls;
            case FOOD: return foods;
            default: throw new IllegalArgumentException("Неизвестный тип: " + type);
        }
    }

    public void addTreasure(int value) {
        this.totalTreasureValue += value;
    }

    public int getTotalTreasureValue() {
        return totalTreasureValue;
    }

    public List<Backpackable> getWeapons() {
        return new ArrayList<>(weapons);
    }

    public List<Backpackable> getPotions() {
        return new ArrayList<>(potions);
    }

    public List<Backpackable> getScrolls() {
        return new ArrayList<>(scrolls);
    }

    public List<Backpackable> getFoods() {
        return new ArrayList<>(foods);
    }

    public boolean hasSpaceFor(ItemType type) {
        if (type == null) return false;

        List<Backpackable> list = getListByType(type);
        return list.size() < MAX_ITEMS_PER_TYPE;
    }

    public int getItemCount(ItemType type) {
        return getListByType(type).size();
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== РЮКЗАК ===\n");
        sb.append("Сокровища: ").append(totalTreasureValue).append(" золота\n\n");

        sb.append("Оружие (").append(weapons.size()).append("/").append(MAX_ITEMS_PER_TYPE).append("):\n");
        for (int i = 0; i < weapons.size(); i++) {
            sb.append("  ").append(i + 1).append(". ").append(weapons.get(i)).append("\n");
        }

        sb.append("Зелья (").append(potions.size()).append("/").append(MAX_ITEMS_PER_TYPE).append("):\n");
        for (int i = 0; i < potions.size(); i++) {
            sb.append("  ").append(i + 1).append(". ").append(potions.get(i)).append("\n");
        }

        sb.append("Свитки (").append(scrolls.size()).append("/").append(MAX_ITEMS_PER_TYPE).append("):\n");
        for (int i = 0; i < scrolls.size(); i++) {
            sb.append("  ").append(i + 1).append(". ").append(scrolls.get(i)).append("\n");
        }

        sb.append("Еда (").append(foods.size()).append("/").append(MAX_ITEMS_PER_TYPE).append("):\n");
        for (int i = 0; i < foods.size(); i++) {
            sb.append("  ").append(i + 1).append(". ").append(foods.get(i)).append("\n");
        }

        return sb.toString();
    }
}
