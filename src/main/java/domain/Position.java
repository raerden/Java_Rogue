package domain;

public class Position {
    private int x;  // final - значение нельзя изменить
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public void setX(int x) {this.x = x;}
    public void setY(int y) {this.y = y;}

    // Для перемещаемых сущностей(Игрок, монстр) при движении создаем новую точку.
    public Position translate(int dx, int dy) {
        return new Position(this.x + dx, this.y + dy);
    }

    //определение дистанции до другой точки
    //для включения преследования монстром при вхождении игрока в круг враждебности
    public double distanceTo(Position other) {
        int dx = this.x - other.x;
        int dy = this.y - other.y;
        return Math.sqrt(dx*dx + dy*dy);
    }

    public boolean equal(Position position) {
        return x == position.getX() && y == position.getY();
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
