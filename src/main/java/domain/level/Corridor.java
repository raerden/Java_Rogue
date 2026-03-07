package domain.level;

import domain.Position;

public class Corridor {
    private Position leftCorner;
    private Position rightCorner;

    public Corridor() {}

    public Corridor(Position leftCorner, Position rightCorner) {
        this.leftCorner = leftCorner;
        this.rightCorner = rightCorner;
    }

    public boolean positionInCorridor(Position position) {
        return  position.getX() >= leftCorner.getX() &&
                position.getY() >= leftCorner.getY() &&
                position.getX() <= rightCorner.getX() &&
                position.getY() <= rightCorner.getY();
    }

    // Геттеры и сеттеры
    public Position getLeftCorner() { return leftCorner; }
    public void setLeftCorner(Position leftCorner) { this.leftCorner = leftCorner; }

    public Position getRightCorner() { return rightCorner; }
    public void setRightCorner(Position rightCorner) { this.rightCorner = rightCorner; }
}
