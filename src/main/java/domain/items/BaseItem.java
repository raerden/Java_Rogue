package domain.items;

import domain.Entity;
import domain.player.Player;
import domain.Position;

public abstract class BaseItem implements Backpackable, Entity {
    protected String name;
    protected ItemType type;
    protected Position position;

    public BaseItem() {}

    public BaseItem(String name, ItemType type, Position position) {
        this.name = name;
        this.type = type;
        this.position = position;
    }

    @Override
    public abstract void apply(Player player);

    @Override
    public String toString() {
        return name;
    }

    public abstract char getDisplayChar();


    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public ItemType getType() {
        return type;
    }

    // Добавляем сеттер для type (нужен Gson)
    public void setType(ItemType type) {
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    // Добавляем сеттер для name (нужен Gson)
    public void setName(String name) {
        this.name = name;
    }
}