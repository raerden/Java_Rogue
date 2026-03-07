package domain.level;

import domain.Entity;
import domain.Position;

public class Door implements Entity {
    private Position position;

    public Door() {}

    public Door(Position position) {
        this.position = position;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public void setPosition(Position position) {
        this.position = position;
    }
}
