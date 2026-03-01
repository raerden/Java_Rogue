package domain.player;

import domain.items.ConsumableType;
import domain.items.Potion;

public class TemporaryEffect {
    private ConsumableType type;
    private int bonus;
    private int duration;
    private String potionName;

    public TemporaryEffect(String potionName, ConsumableType type, int bonus, int duration) {
        this.potionName = potionName;
        this.type = type;
        this.bonus = bonus;
        this.duration = duration;
    }

    public ConsumableType getType() {
        return type;
    }

    public int getBonus() {
        return bonus;
    }

    public int getDuration() {return duration;}

    public void decrementDuration() {
        if (duration > 0) {
            duration--;
        }
    }

    public boolean isExpired() {
        return duration <= 0;
    }

    @Override
    public String toString() {
        return type.getDisplayName() + " +" + bonus + " (" + duration + " ходов)";
    }
}
