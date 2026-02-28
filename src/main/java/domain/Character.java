package domain;

import java.util.List;

public abstract class Character implements Entity{
    List<String> types = List.of("Ghost", "Ogre", "SnakeMagician", "Vampire", "Zombie", "Player");
    private String type;


    private int strength;             // сила
    private int dexterity;            // ловкость
    private int maxHealth;            // максимальное здоровье
    private int currentHealth;        // текущее здоровье

    private int level = 0;
    private Position position;


    protected Character(String type, int level, Position position) {
        this.type = type;
        if (!types.contains(type)) {
            throw new IllegalArgumentException("Не соответствие типу: Ghost, Ogre, SnakeMagician, Vampire, Zombie, Player: " + type);
        }
        this.strength = calculateStrength(type, level);
        this.dexterity = calculateDexterity(type, level);
        this.maxHealth = calculateMaxHealth(type, level);
        this.currentHealth = this.maxHealth;
        this.position = position;
        this.level = level;
        if (type == "Player") this.level = 0;
    }

    // Геттеры
    public String getType() {return type;}

    public int getStrength() {return strength;}

    public int getDexterity() {return dexterity;}

    public int getMaxHealth() {return maxHealth;}

    public int getCurrentHealth() {return currentHealth;}

    public Position getPosition() {return this.position;}

    private int getLevel(int level) {return this.level;}

    // Сеттеры
    public void setMaxHealth(int health) {
        this.maxHealth = health;
    }

    public void setHealth(int health) {
        this.currentHealth = Math.min(health, this.maxHealth);
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public void setDexterity(int dexterity) {
        this.dexterity = dexterity;
    }

    private void setLevel(int level) {this.level = level;}

    public void setPosition(Position position) {this.position = position;}





    public int calculateMaxHealth(String type, int level) {
        switch (type) {
            case "Zombie":
                return 40 + level * 2 + (int)(Math.random() * 6 - 3);
            case "Ogre":
                return 60 + level * 4 + (int)(Math.random() * 6 - 3);
            case "SnakeMagician":
                return 30 + level * 1 + (int)(Math.random() * 6 - 3);
            case "Vampire":
                return 35 + level * 3 + (int)(Math.random() * 6 - 3);
            case "Ghost":
                return 20 + level * 2 + (int)(Math.random() * 6 - 3);
            case "Player":
                return 80;
            default:
                return 0;
        }
    }
    public  int calculateDexterity(String type, int level) {
        switch (type) {
            case "Zombie":
                return 5 + level * 1;
            case "Ogre":
                return 4 + level * 1;
            case "SnakeMagician":
                return 20 + level * 3;
            case "Vampire":
                return 10 + level * 2;
            case "Ghost":
                return 15 + level * 3;
            case "Player":
                return 10;
            default:
                return 0;
        }
    }
    public int calculateStrength(String type, int level){
        switch (type) {
            case "Zombie":
                return 12 + level * 2;
            case "Ogre":
                return 20 + level * 4;
            case "SnakeMagician":
                return 8 + level * 3;
            case "Vampire":
                return 10 + level * 3;
            case "Ghost":
                return 6 + level * 1;
            case "Player":
                return 10;
            default:
                return 0;
        }
    }

    @Override
    public String toString() {
        return String.format("%s (%d/%d hp) | Сила: %d | Ловкость: %d",
                type, currentHealth, maxHealth, strength, dexterity);
    }


    public boolean takeDamage(int damage) {
        if (damage <= 0) return false;

        this.currentHealth -= damage;
        if (this.currentHealth < 0) {
            this.currentHealth = 0;
        }

        return this.currentHealth == 0;
    }

    public double calculateHitChance(int targetDexterity){
        return 0.5 + (this.getDexterity() - targetDexterity) * 0.03 + Math.random() * 0.2 - 0.1;
    }

    public int calculateHitDamage(int targetDexterity){
        int base = strength / 3;
        int varianceMax = strength / 5;
        return Math.max(1, (int) (base + Math.random() * (varianceMax + 1)));
    }

    public void applySpecialAttackEffects(Character target, boolean targetDied){

    }



    public boolean attack(Character target) {
        if (!target.isAlive() || !this.isAlive()){
            return false;
        }
        double hitChance = this.calculateHitChance(target.getDexterity());
        if (Math.random() >= hitChance) return false;

        int damage = calculateHitDamage(target.getStrength());
        boolean targetDied = target.takeDamage(damage);
        applySpecialAttackEffects(target, targetDied);
        return true;
    }





    public boolean isAlive(){
        return this.currentHealth > 0;
    }
}