package domain.items;

import domain.Entity;
import domain.player.Player;
import domain.Position;

public class Scroll extends BaseItem implements Entity, Backpackable {
    private ConsumableType effectType;          // Тип свитка (enum)
    private int bonus;         // На сколько повышает характеристику

    public Scroll() {
        super();
    }

    public Scroll(String name, int bonus, ConsumableType effectType, Position position) {
        super(name, ItemType.SCROLL, position);
        this.effectType = effectType;
        this.bonus = bonus;
    }

    public void apply(Player player) {
        switch (effectType) {
            case HEALTH:
                System.out.printf("%s зачитал %s и увеличил максимальное здоровье до %d\n",
                        player.getName(), name, player.getMaxHealth() + bonus
                );
                player.setMaxHealth(player.getMaxHealth() + bonus);
                break;
            case STRENGTH:
                player.setStrength(player.getStrength() + bonus);
                System.out.println(name + " увеличивает силу на " + bonus + "!");
                break;
            case DEXTERITY:
                player.setDexterity(player.getDexterity() + bonus);
                System.out.println(name + " увеличивает ловкость на " + bonus + "!");
                break;
        }
    }

    public ConsumableType getConsumableType() {
        return effectType;
    }

    @Override
    public String toString() {
        return String.format("'%s' (%s +%d)",
                name, type, bonus);
    }

    @Override
    public char getDisplayChar() {
        return '~';
    }

    // Геттеры
    public ConsumableType getEffectType() {
        return effectType;
    }

    public int getBonus() {
        return bonus;
    }

    // Сеттеры
    public void setEffectType(ConsumableType effectType) {
        this.effectType = effectType;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }
}
