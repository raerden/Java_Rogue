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
    private int bonus;
    private int duration;
    private ConsumableType effectType;
    private boolean isUsed;

    public Potion() {
        super();
        this.isUsed = false;
    }

    public Potion(String name, int bonus, int duration, ConsumableType effectType, Position position) {
        super(name, ItemType.POTION, position);
        this.bonus = bonus;
        this.duration = duration;
        this.effectType = effectType;
        this.isUsed = false;
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
    }


    @Override
    public String toString() {
        String status = isUsed ? " (использовано)" : "";
        return String.format("'%s' (%s +%d)%s",
                name, effectType.getDisplayName(), bonus, status);
    }


    boolean getIsUsed() {return isUsed;}
    @Override
    public char getDisplayChar() {
        return '^';
    }

    public boolean isUsed() {
        return isUsed;
    }

    public int getBonus() {
        return bonus;
    }

    public int getDuration() {
        return duration;
    }

    public ConsumableType getEffectType() {
        return effectType;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setEffectType(ConsumableType effectType) {
        this.effectType = effectType;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }
}
