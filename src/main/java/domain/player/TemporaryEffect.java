package domain.player;

import domain.items.ConsumableType;
import domain.items.Potion;

public class TemporaryEffect {
    private ConsumableType type;
    private int bonus;
    private int duration;
    private String potionName;

    public TemporaryEffect() {}

    public TemporaryEffect(String potionName, ConsumableType type, int bonus, int duration) {
        this.potionName = potionName;
        this.type = type;
        this.bonus = bonus;
        this.duration = duration;
    }

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

    // Геттеры и сеттеры для ВСЕХ полей
    public ConsumableType getType() {
        return type;
    }

    public void setType(ConsumableType type) {
        this.type = type;
    }

    public int getBonus() {
        return bonus;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getPotionName() {
        return potionName;
    }

    public void setPotionName(String potionName) {
        this.potionName = potionName;
    }
}
