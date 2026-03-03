package domain.level;

import domain.Position;

public class Corridor {
    private final Position leftCorner;
    private final Position rightCorner;

    public Corridor(Position leftCorner, Position rightCorner) {
        this.leftCorner = leftCorner;
        this.rightCorner = rightCorner;
    }

    public Position getLeftCorner() {
        return leftCorner;
    }

    public Position getRightCorner() {
        return rightCorner;
    }

    public boolean positionInCorridor(Position position) {
        return  position.getX() >= leftCorner.getX() &&
                position.getY() >= leftCorner.getY() &&
                position.getX() <= rightCorner.getX() &&
                position.getY() <= rightCorner.getY();
    }

    public boolean isHorizontal() {
        return leftCorner.getY() == rightCorner.getY();
    }

    public boolean isVertical() {
        return leftCorner.getX() == rightCorner.getX();
    }
}
