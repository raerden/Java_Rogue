package domain.items;

import domain.player.Player;
import domain.Position;

public class Food extends BaseItem{
    private final ConsumableType effectType;          // Тип эффекта (enum)
    private final int bonus;         // На сколько повышает характеристику

    public Food(String name, char symbol, int bonus, Position position) {
        super(name, symbol, ItemType.SCROLL, position);
        this.effectType = ConsumableType.HEALTH;
        this.bonus = bonus;
    }

    @Override
    public void apply(Player player) {
        player.setHealth(Math.min(player.getCurrentHealth() + bonus, player.getCurrentHealth()));
        System.out.printf("%s съел %s и увеличил текущее здоровье до %d\n",
                player.getType(), name, player.getCurrentHealth()
        );
    }

    @Override
    public void setPosition(Position position) {
        throw new UnsupportedOperationException("Еду нельзя переместить!");
    }

    public ConsumableType getConsumableType() {
        return effectType;
    }

    public int getBonus() {
        return bonus;
    }

    @Override
    public String toString() {
        return String.format("Еда '%s' (%s +%d) на %s",
                name, effectType, bonus, position);
    }
}