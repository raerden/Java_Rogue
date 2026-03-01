package domain;

import com.googlecode.lanterna.TextColor;

import java.util.List;

public abstract class Character implements Entity{
    protected int strength;             // сила
    protected int dexterity;            // ловкость
    protected int maxHealth;            // максимальное здоровье
    protected int health;        // текущее здоровье
    protected Position position;        // текущая позиция


    protected Character(Position position, int health, int maxHealth, int strength, int dexterity) {
        this.position = position;
        this.health = health;
        this.maxHealth = maxHealth;
        this.strength = strength;
        this.dexterity = dexterity;
    }

    // Геттеры
    public int getStrength() {return strength;}

    public int getDexterity() {return dexterity;}

    public int getMaxHealth() {return maxHealth;}

    public int getHealth() {return health;}

    @Override
    public Position getPosition() {return this.position;}

    // Сеттеры

    public void setMaxHealth(int value) {
        this.maxHealth = value;
        //Для чего это проверка?
        if (this.health > this.maxHealth) {
            this.health = this.maxHealth;
        }
    }

    public void setHealth(int value) {
        this.health = value;
    }
    public void setStrength(int value) {
        this.strength = value;
    }
    public void setDexterity(int value) {
        this.dexterity = value;
    }

    @Override
    public void setPosition(Position position) {this.position = position;}

    @Override
    public String toString() {
        return String.format("Character (%d/%d hp) | Сила: %d | Ловкость: %d | Position: %s",
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

    //public void applySpecialAttackEffects(Character target, boolean targetDied){

    //}

    public void heal(int amount) {
        this.health = Math.min(this.health + amount, maxHealth);
    }

//    public boolean attack(Character target) {
//        if (!target.isAlive() || !this.isAlive()){
//            return false;
//        }
//        double hitChance = this.calculateHitChance(target.getDexterity());
//        if (Math.random() >= hitChance) return false;
//
//        int damage = calculateHitDamage(target.getStrength());
//        target.takeDamage(damage);
//        //applySpecialAttackEffects(target, targetDied);
//        return true;
//    }

    public boolean isAlive(){
        return this.health > 0;
    }

    public abstract char getDisplayChar();
    public abstract TextColor getDisplayColor();
}