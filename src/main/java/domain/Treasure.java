package domain;

import domain.items.Backpackable;
import domain.items.BaseItem;
import domain.items.ItemType;
import domain.player.Player;

public class Treasure extends BaseItem {
    private int value;

    public Treasure(String name, char symbol, int value, Position position) {
        super(name, symbol, ItemType.TREASURE, position);
        this.value = value;
    }
    public void apply(Player player){
        player.pickUpItem(this);
    }

    public int getValue(){
        return value;
    }
}
