package domain.items;

import domain.player.Player;
import domain.Position;

public class Food extends BaseItem{
    private ConsumableType effectType;          // Тип эффекта (enum)
    private int bonus;         // На сколько повышает характеристику

    public Food() {
        super();
    }

    public Food(String name, int bonus, Position position) {
        super(name, ItemType.FOOD, position);
        this.effectType = ConsumableType.HEALTH;
        this.bonus = bonus;
    }

    @Override
    public void apply(Player player) {
        player.setHealth(Math.min(player.getHealth() + bonus, player.getMaxHealth()));
        System.out.printf("%s съел %s и увеличил текущее здоровье до %d\n",
                player.getName(), name, player.getHealth()
        );
    }

    public ConsumableType getConsumableType() {
        return effectType;
    }

    @Override
    public String toString() {
        return String.format("Еда '%s' (%s +%d)",
                name, effectType, bonus);
    }

    @Override
    public char getDisplayChar() {
        return '%';
    }

    public ConsumableType getEffectType() {
        return effectType;
    }

    public int getBonus() {
        return bonus;
    }

    public void setEffectType(ConsumableType effectType) {
        this.effectType = effectType;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }
}