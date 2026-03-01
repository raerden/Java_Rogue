package domain.monsters;

import com.googlecode.lanterna.TextColor;
import domain.Position;
import domain.level.Room;
import domain.player.Player;

import java.util.Random;

public class SnakeMagician extends Enemy {
    //Базовые статы
    private static final double BASE_HEALTH = 70;
    private static final double BASE_STRENGTH = 10;
    private static final double BASE_DEXTERITY = 30;
    //Прирост базовых стат за уровень коэффициент (будет округляться из-за int)
    private static final double HEALTH_GROWTH = 0.01;
    private static final double STRENGTH_GROWTH = 0.03;
    private static final double DEXTERITY_GROWTH = 0.02;

    private static final int BASE_HOSTILITY = 10;
    private static final int BASE_TREASURE = 125;

    private static final Random random = new Random();
    private static final double VARIATION = 0.1;

    public SnakeMagician(int enemyLevel, Position position){
        super(position, (int)(BASE_HEALTH * ((double) enemyLevel * HEALTH_GROWTH + 1.0) * (1 + random.nextDouble() * VARIATION - VARIATION/2)),
                (int)(BASE_HEALTH * ((double) enemyLevel * HEALTH_GROWTH + 1.0) * (1 + random.nextDouble() * VARIATION - VARIATION/2)),
                (int)(BASE_STRENGTH * ((double) enemyLevel * STRENGTH_GROWTH + 1.0) * (1 + random.nextDouble() * VARIATION - VARIATION/2)),
                (int)(BASE_DEXTERITY * ((double) enemyLevel * DEXTERITY_GROWTH + 1.0) * (1 + random.nextDouble() * VARIATION - VARIATION/2)),
                EnemyType.ZOMBIE, BASE_HOSTILITY, BASE_TREASURE);
    }

    private boolean moveRight = true;
    private boolean moveDown = true;
    private static final double SLEEP_CHANCE = 0.2;
    private static final int SLEEP_DURATION = 1; // Количество ходов сна

    public SnakeMagician(Position position) {
        super(position, 70, 70, 12, 30,
                EnemyType.SNAKE_MAGICIAN, 10, 125);
    }

    @Override
    public void movePattern(Room currentRoom, Player player) {
        if (!shouldChase(currentRoom, player)) {
            // Змей-маг ходит по диагонали, постоянно меняя сторону
            moveZigZag(currentRoom);
        }else{
            chasePlayer(currentRoom, player);
        }
    }

    private void moveZigZag(Room currentRoom) {
        Position nextPosition = calculateNextZigZagPosition();
        if (!currentRoom.isPositionInRoom(nextPosition)) {
            moveRight = !moveRight;
            nextPosition = calculateNextZigZagPosition();
            if (!currentRoom.isPositionInRoom(nextPosition)) {
                moveDown = !moveDown;
                nextPosition = calculateNextZigZagPosition();
            }
        }
        if (currentRoom.isPositionInRoom(nextPosition)) {
            position = nextPosition;
        }
    }
    private Position calculateNextZigZagPosition() {
        int dx = moveRight ? 1 : -1;
        int dy = moveDown ? 1 : -1;

        return position.translate(dx, dy);
    }

    @Override
    public void chasePlayer(Room currentRoom, Player player) {
        if (player == null || !isAlive() || !player.isAlive()) {
            return;
        }
        // При преследовании змей двигается напрямую к игроку
        int dXOne = Integer.signum(player.getPosition().getX() - this.position.getX());
        int dYOne = Integer.signum(player.getPosition().getY() - this.position.getY());

        int dx = player.getPosition().getX() - this.position.getX();
        int dy = player.getPosition().getY() - this.position.getY();
        // Пытаемся двигаться сначала по горизонтали, потом по вертикали
        if (Math.abs(dx) <= 1 && Math.abs(dy) <= 1) {
            attack(player);
        } else {
            this.position = this.position.translate(dXOne, dYOne);
        }
    }

    @Override
    protected void applySpecialAttackEffects(Player player) {
        // Шанс усыпить игрока при успешной атаке
        if (Math.random() < SLEEP_CHANCE) {
            player.setSleepTurns(SLEEP_DURATION);
        }
    }

    @Override
    public char getDisplayChar() {
        return 's';
    }
    @Override
    public TextColor getDisplayColor() {
        return TextColor.ANSI.WHITE;
    }
}
