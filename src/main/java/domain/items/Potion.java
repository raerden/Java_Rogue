package domain.items;

import domain.Entity;
import domain.player.Player;
import domain.Position;

public class Potion extends BaseItem {
    private final int bonus;         // На сколько повышает характеристику
    private ConsumableType effectType; // Тип эффекта (enum)
    private int usageTime;

    public Potion(String name, char symbol, int bonus, int usageTime, ConsumableType effectType, Position position) {
        super(name, symbol, ItemType.POTION, position);
        this.usageTime = usageTime;
        this.bonus = bonus;
        this.effectType = effectType;
    }

    @Override
    public void apply(Player player) {
        switch (effectType) {
            case HEALTH:
                player.setHealth(player.getCurrentHealth() + bonus);
                System.out.printf("%s выпил зелье здоровья! Макс. HP +%d на %d ходов%n",
                        player.getType(), bonus, usageTime);
                break;
            case STRENGTH:
                player.setStrength(player.getStrength() + bonus);
                System.out.printf("%s выпил зелье силы! Сила +%d на %d ходов%n",
                        player.getType(), bonus, usageTime);
                break;
            case DEXTERITY:
                player.setDexterity(player.getDexterity() + bonus);
                System.out.printf("%s выпил зелье ловкости! Ловкость +%d на %d ходов%n",
                        player.getType(), bonus, usageTime);
                break;
        }
    }

    public int decUsageTime(){return --usageTime;}

    public boolean isTimeUp(){
        return usageTime <= 0;
    }
    public int getTimeRemaining(){
        return usageTime;
    }

    @Override
    public String toString() {
        return String.format("Зелье '%s' (%s +%d) на %s на %d ходов",
                name, type, bonus, position, usageTime);
    }

    public ConsumableType getEffectType() {
        return effectType;
    }

    public int getBonus() {
        return bonus;
    }

    public void removeBonus(Player player) {
        if (isTimeUp()){
            switch (effectType) {
                case HEALTH:
                    player.setHealth(Math.max(player.getCurrentHealth() - bonus, 1));
                    System.out.printf("У %s закончился эффект зелья здоровья! Текущее HP приведено к %d %n",
                            player.getType(), player.getCurrentHealth());
                    break;
                case STRENGTH:
                    player.setStrength(Math.max(player.getStrength() - bonus, 1));
                    System.out.printf("У %s закончился эффект зелья силы! Текущая Strength приведена к %d %n",
                            player.getType(), player.getStrength());
                    break;
                case DEXTERITY:
                    player.setDexterity(Math.max(player.getDexterity() - bonus, 1));
                    System.out.printf("У %s закончился эффект зелья ловкости! Текущая Dexterity приведена к %d %n",
                            player.getType(), player.getDexterity());
                    break;
            }
        }
    }
}
