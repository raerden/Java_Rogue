package domain.items;

import domain.Entity;
import domain.player.Player;
import domain.Position;

public abstract class BaseItem implements Backpackable, Entity {
    protected String name;
    protected char symbol;
    protected ItemType type;
    protected final Position position;

    public BaseItem(String name, char symbol, ItemType type, Position position) {
        this.name = name;
        this.symbol = symbol;
        this.type = type;
        this.position = position;
    }
    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public void setPosition(Position position) {
        throw new UnsupportedOperationException("Нельзя переместить простой предмет!");
    }

    @Override
    public ItemType getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public char getSymbol() {
        return symbol;
    }

    @Override
    public abstract void apply(Player player);

    @Override
    public String toString() {
        return name;
    }
}