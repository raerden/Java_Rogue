package domain;

import com.googlecode.lanterna.TextColor;

import java.util.List;

public abstract class Character implements Entity{
    protected int strength;             // сила
    protected int dexterity;            // ловкость
    protected int maxHealth;            // максимальное здоровье
    protected int health;        // текущее здоровье
    protected Position position;        // текущая позиция

    protected Character() {}

    protected Character(Position position, int health, int maxHealth, int strength, int dexterity) {
        this.position = position;
        this.health = health;
        this.maxHealth = maxHealth;
        this.strength = strength;
        this.dexterity = dexterity;
    }


    @Override
    public String toString() {
        return String.format(" (%d/%d hp) | Сила: %d | Ловкость: %d | Position: %s",
                health, maxHealth, strength, dexterity, position);
    }

    //Принятие урона
    public boolean takeDamage(int damage, Character fromUnit) {
        if (damage <= 0) return false;

        this.health -= damage;
        if (this.health < 0) {
            this.health = 0;
        }

        return this.health == 0;
    }
    //Шанс попадания атаки
    protected double calculateHitChance(int targetDexterity){
        return 0.5 + (this.getDexterity() - targetDexterity) * 0.03 + Math.random() * 0.2 - 0.1;
    }

    //Расчет урона от атаки
    protected int calculateHitDamage(){
        int base = strength / 2;
        int varianceMax = strength / 5;
        return Math.max(1, (int) (base + Math.random() * (varianceMax + 1)));
    }

    public void heal(int amount) {
        this.health = Math.min(this.health + amount, maxHealth);
    }

    public boolean isAlive(){
        return this.health > 0;
    }

    public abstract char getDisplayChar();
    public abstract TextColor getDisplayColor();

    // Геттеры и сеттеры
    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getDexterity() {
        return dexterity;
    }

    public void setDexterity(int dexterity) {
        this.dexterity = dexterity;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
        if (this.health > this.maxHealth) {
            this.health = this.maxHealth;
        }
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public void setPosition(Position position) {
        this.position = position;
    }

}