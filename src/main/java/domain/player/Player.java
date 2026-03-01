package domain.player;

import com.googlecode.lanterna.TextColor;
import domain.Position;
import domain.Treasure;
import domain.items.*;

import java.util.*;

import domain.Character;
import domain.Entity;
import domain.level.Room;

/**
 * Важно у игрока:
 * <p>Взаимодействие с предметами через рюкзак</p>
 * <p>updateTemporaryEffects после каждого хода</p>
 * Есть список действующих эффектов от зелий
 * Нужно проверять состояние сна при ходе
 * <p>В processTurn обновляет и то и то</p>
 * <p>Реализовывать ли передвижение?</p>
 */
public class Player extends Character implements Entity {
    String name;
    private Backpack backpack;
    private Weapon equippedWeapon;
    private int score; // собранные сокровища
    private List<TemporaryEffect> activeEffects;

    private int sleepTurns = 0;

    //Конструктор
    public Player(String name, Position startPosition) {
        super(startPosition, 200, 200, 10, 10);
        this.name = name;
        this.backpack = new Backpack();
        this.equippedWeapon = null;
        this.score = 0;
        this.activeEffects = new ArrayList<>();
    }
    // ========== УПРАВЛЕНИЕ ПРЕДМЕТАМИ ==========
    /**
     * Подбор предметов в рюкзак.
     * Если предмет не влез в рюкзак,
     * возвращает false
     */
    public boolean pickUpItem(Backpackable item) {
        //подбор предмета в рюкзак
        if (item == null) return false;

        boolean isPickUped = false;
        if (item instanceof Treasure) {
            // Сокровища сразу идут в счет
            int value = ((Treasure) item).getValue();
            backpack.addTreasure(value);
            this.score += value;
            System.out.println("Подобрано сокровище: " + item + " (+" + value + " золота)");
            isPickUped = true;
        } else {
            // Обычные предметы идут в рюкзак
            if (backpack.addItem(item)) {
                System.out.println("Предмет добавлен в рюкзак: " + item);
                isPickUped = true;
            } else {
                System.out.println("Рюкзак полон! Предмет остался на полу.");
                // Здесь предмет останется на полу, не добавляем его
                isPickUped = false;
            }
        }
        return isPickUped;
    }

    public void useItem(ItemType type, int slot) {
        //Исользование предмета из рюкзака
        // slot от 1 до 9 в UI, конвертируем в 0-8 для внутреннего использования
        int internalSlot = slot - 1;
        Optional<Backpackable> usedItem = backpack.useItem(type, internalSlot, this);
    }

    public Weapon equipWeapon(Weapon newWeapon) {
        Weapon oldWeapon = this.equippedWeapon;
        this.equippedWeapon = newWeapon;


        if (oldWeapon != null && newWeapon != null) {
            System.out.println("Вы экипировали " + newWeapon.getName() +
                    " и сняли " + oldWeapon.getName());
            oldWeapon.discharge(this);
            newWeapon.apply(this);
        } else if (newWeapon != null && oldWeapon == null){
            newWeapon.apply(this);
            System.out.println("Вы экипировали " + newWeapon.getName());
        } else if (oldWeapon != null && newWeapon == null){
            oldWeapon.discharge(this);
            System.out.println("Вы сняли " + oldWeapon.getName());
        }

        return oldWeapon;
    }

    public int getScore() {
        return score;
    }
    public String getName() {
        return name;
    }

    public Backpack getBackpack() {return backpack; }

    public void setSleepTurns(int turns) {
        if (turns > 0) {
            this.sleepTurns = turns;
        }
    }

    public int getSleepTurns() {
        return sleepTurns;
    }

    public void decrementSleepTurns() {
        if (sleepTurns > 0) {
            sleepTurns--;
        }
    }

    public boolean isAsleep() {
        return sleepTurns > 0;
    }

    public void setScore(int score) {
        this.score = score;
    }
    /**
     * Обработка хода игрока
     * @return true если игрок может действовать, false если спит
     */
    public boolean processTurn() {
        // Обновляем временные эффекты
        updateTemporaryEffects();

        if (isAsleep()) {
            decrementSleepTurns();
            return false; // Игрок спит
        }
        return true; // Игрок может действовать
    }

    // ========== УПРАВЛЕНИЕ ВРЕМЕННЫМИ ЭФФЕКТАМИ ==========

    public void addTemporaryEffectFromPotion(Potion potion) {
        if (potion != null) {
            // Применяем эффект
            potion.apply(this);
        }
    }
    public void addTemporaryEffect(TemporaryEffect effect) {
        if (effect != null) {
            applyEffectBonuses(effect);
            activeEffects.add(effect);
        }
    }

    private void applyEffectBonuses(TemporaryEffect effect) {
        switch (effect.getType()) {
            case HEALTH:
                // Для здоровья увеличиваем текущее и максимальное
                setMaxHealth(getMaxHealth() + effect.getBonus());
                setHealth(getHealth() + effect.getBonus());
                break;
            case STRENGTH:
                setStrength(getStrength() + effect.getBonus());
                break;
            case DEXTERITY:
                setDexterity(getDexterity() + effect.getBonus());
                break;
        }
    }

    private void removeEffectBonuses(TemporaryEffect effect) {
        switch (effect.getType()) {
            case HEALTH:
                setMaxHealth(Math.max(getMaxHealth() - effect.getBonus(), 1));
                setHealth(Math.max(getHealth() - effect.getBonus(), 1));
                break;
            case STRENGTH:
                setStrength(Math.max(getStrength() - effect.getBonus(), 1));
                break;
            case DEXTERITY:
                setDexterity(Math.max(getDexterity() - effect.getBonus(), 1));
                break;
        }
    }

    private void updateTemporaryEffects() {
        Iterator<TemporaryEffect> iterator = activeEffects.iterator();
        while (iterator.hasNext()) {
            TemporaryEffect effect = iterator.next();
            effect.decrementDuration();

            if (effect.isExpired()) {
                // Эффект закончился - отменяем
                removeEffectBonuses(effect);
                System.out.println("Эффект '" + effect.getType() + "+" + effect.getBonus() + " закончился");
                iterator.remove();
            }
        }
    }

    public List<TemporaryEffect> getActiveEffects() {
        return new ArrayList<>(activeEffects);
    }

    @Override
    public char getDisplayChar() {
        return '@';
    }
    @Override
    public TextColor getDisplayColor() {
        return TextColor.ANSI.WHITE;
    }
}
