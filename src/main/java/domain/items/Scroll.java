package domain.items;

import domain.Entity;
import domain.player.Player;
import domain.Position;
import domain.Character;

public class Scroll extends BaseItem implements Entity, Backpackable {
    private final ConsumableType effectType;          // Тип свитка (enum)
    private final int bonus;         // На сколько повышает характеристику

    public Scroll(String name, char symbol, int bonus, ConsumableType effectType, Position position) {
        super(name, symbol, ItemType.SCROLL, position);
        this.effectType = effectType;
        this.bonus = bonus;
    }

    public void apply(Player player) {
        switch (effectType) {
            case HEALTH:
                //здесь потом заменить вывод сообщения под статусную строку
                System.out.printf("%s зачитал %s и увеличил максимальное здоровье до %d\n",
                        player.getType(), name, player.getMaxHealth() + bonus
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

    @Override
    public void setPosition(Position position) {
        throw new UnsupportedOperationException("Свиток нельзя переместить!");
    }

    public ConsumableType getConsumableType() {
        return effectType;
    }

    public int getBonus() {
        return bonus;
    }

    @Override
    public String toString() {
        return String.format("Свиток '%s' (%s +%d) на %s",
                name, type, bonus, position);
    }


}
