package domain.items;

import domain.Entity;
import domain.Position;
import domain.player.Player;

public class Treasure extends BaseItem  {
    private int value;

    public Treasure() {
        super();
        this.value = 0;
    }

    public Treasure(String name, int value, Position position) {
        super(name, ItemType.TREASURE, position);
        this.value = value;
    }
    public void apply(Player player){
        player.pickUpItem(this);
    }

    @Override
    public char getDisplayChar() {
        return '*';
    }

    // Геттер
    public int getValue() {
        return value;
    }

    // Сеттер
    public void setValue(int value) {
        this.value = value;
    }
}
