package domain.level;

import domain.Entity;
import domain.Position;

public class Door implements Entity {
    private final Position position;

    public Door(Position position) {
        this.position = position;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public void setPosition(Position position) {
        //Дверь нельзя передвинуть
    }
}
