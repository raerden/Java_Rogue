package domain.player;

import domain.Position;
import domain.Treasure;
import domain.items.Backpack;

import java.util.List;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Optional;

import domain.Character;
import domain.Entity;
import domain.items.Backpackable;
import domain.items.ItemType;
import domain.items.Weapon;

public class Player extends Character implements Entity {
    String name;
    private Backpack backpack;
    private Weapon equippedWeapon;
    private int score; // собранные сокровища
    private List<TemporaryEffect> activeEffects;

    //Конструктор
    public Player(String name, Position startPosition) {
        super("Player", 0, startPosition);
        this.name = name;
        this.backpack = new Backpack();
        this.equippedWeapon = null;
        this.score = 0;
        this.activeEffects = new ArrayList<>();
    }
    // ========== УПРАВЛЕНИЕ ПРЕДМЕТАМИ ==========

    public void pickUpItem(Backpackable item) {
        //подбор предмета в рюкзак
        if (item == null) return;

        if (item instanceof Treasure) {
            // Сокровища сразу идут в счет
            int value = ((Treasure) item).getValue();
            backpack.addTreasure(value);
            this.score += value;
            System.out.println("Подобрано сокровище: " + item + " (+" + value + " золота)");
        } else {
            // Обычные предметы идут в рюкзак
            if (backpack.addItem(item)) {
                System.out.println("Предмет добавлен в рюкзак: " + item);
            } else {
                System.out.println("Рюкзак полон! Предмет остался на полу.");
                // Здесь предмет останется на полу, не добавляем его
            }
        }
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

    public void setScore(int score) {
        this.score = score;
    }
}
