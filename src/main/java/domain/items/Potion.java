package domain.items;

import domain.Entity;
import domain.player.Player;
import domain.Position;
import domain.player.TemporaryEffect;

/**
 * Особенность зелья: таймер у зелья привязан к эффекту у игрока.
 * То есть если есть один экземпляр класс зелья,
 * и его эффект будет действовать на игрока,
 * то в экземпляре зелья тоже будет убавляться время эффекта.
 * Значит если мы захотим использовать повторно этот экземпляр класса, то мы сможем
 */
public class Potion extends BaseItem {
    //private final int bonus;         // На сколько повышает характеристику
    //private ConsumableType effectType; // Тип эффекта (enum)
    //private int usageTime;
    //private TemporaryEffect temporaryEffect;

    private final int bonus;
    private final int duration;
    private final ConsumableType effectType;
    private boolean isUsed;

    public Potion(String name, int bonus, int duration, ConsumableType effectType, Position position) {
        super(name, ItemType.POTION, position);
        this.bonus = bonus;
        this.duration = duration;
        this.effectType = effectType;
        this.isUsed = false;
        //TemporaryEffect temporaryEffect = new TemporaryEffect(name, effectType, bonus, duration);
    }

    @Override
    public void apply(Player player) {
        if (isUsed) {
            System.out.println("Зелье уже использовано!");
            return;
        }

        TemporaryEffect temporaryEffect = new TemporaryEffect(name, effectType, bonus, duration);
        player.addTemporaryEffect(temporaryEffect);
        isUsed = true;


        System.out.printf("%s выпил зелье %s! %s +%d на %d ходов%n",
                player.getName(), name,
                temporaryEffect.getType().getDisplayName(),
                temporaryEffect.getBonus(),
                temporaryEffect.getDuration());
//        switch (effectType) {
//            case HEALTH:
//                player.setHealth(player.getHealth() + bonus);
//                System.out.printf("%s выпил зелье здоровья! Макс. HP +%d на %d ходов%n",
//                        player.getName(), bonus, usageTime);
//                break;
//            case STRENGTH:
//                player.setStrength(player.getStrength() + bonus);
//                System.out.printf("%s выпил зелье силы! Сила +%d на %d ходов%n",
//                        player.getName(), bonus, usageTime);
//                break;
//            case DEXTERITY:
//                player.setDexterity(player.getDexterity() + bonus);
//                System.out.printf("%s выпил зелье ловкости! Ловкость +%d на %d ходов%n",
//                        player.getName(), bonus, usageTime);
//                break;
//        }
    }

    public boolean isUsed() {
        return isUsed;
    }

//    public TemporaryEffect getTemporaryEffect() {
//        return temporaryEffect;
//    }

//    @Override
//    public String toString() {
//        return String.format("Зелье '%s' (%s +%d) на %s на %d ходов",
//                name, temporaryEffect.getType().getDisplayName(),
//                temporaryEffect.getBonus(), position,
//                temporaryEffect.getDuration());
//    }
    @Override
    public String toString() {
        String status = isUsed ? " (использовано)" : "";
        return String.format("Зелье '%s' (%s +%d)%s",
                name, effectType.getDisplayName(), bonus, status);
    }

    int getBonus() {return bonus;}
    int getDuration() {return duration;}
    ConsumableType getEffectType() {return effectType;}
    boolean getIsUsed() {return isUsed;}
    @Override
    public char getDisplayChar() {
        return '^';
    }


//    public void removeBonus(Player player) {
//        if (isTimeUp()){
//            switch (effectType) {
//                case HEALTH:
//                    player.setHealth(Math.max(player.getHealth() - bonus, 1));
//                    System.out.printf("У %s закончился эффект зелья здоровья! Текущее HP приведено к %d %n",
//                            player.getName(), player.getHealth());
//                    break;
//                case STRENGTH:
//                    player.setStrength(Math.max(player.getStrength() - bonus, 1));
//                    System.out.printf("У %s закончился эффект зелья силы! Текущая Strength приведена к %d %n",
//                            player.getName(), player.getStrength());
//                    break;
//                case DEXTERITY:
//                    player.setDexterity(Math.max(player.getDexterity() - bonus, 1));
//                    System.out.printf("У %s закончился эффект зелья ловкости! Текущая Dexterity приведена к %d %n",
//                            player.getName(), player.getDexterity());
//                    break;
//            }
//        }
//    }
}
