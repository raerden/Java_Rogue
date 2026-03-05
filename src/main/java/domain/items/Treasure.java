package domain.items;

import domain.Entity;
import domain.Position;
import domain.player.Player;

public class Treasure extends BaseItem  {
    private int value;

    public Treasure(String name, int value, Position position) {
        super(name, ItemType.TREASURE, position);
        this.value = value;
    }
    public void apply(Player player){
        player.pickUpItem(this);
    }

    public int getValue(){
        return value;
    }
    @Override
    public char getDisplayChar() {
        return '*';
    }
}
