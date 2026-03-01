package domain.monsters;

import com.googlecode.lanterna.TextColor;
import domain.Position;
import domain.level.Room;
import domain.player.Player;

import java.util.Random;

public class Ghost extends Enemy {
    //Базовые статы
    private static final double BASE_HEALTH = 20;
    private static final double BASE_STRENGTH = 20;
    private static final double BASE_DEXTERITY = 10;
    //Прирост базовых стат за уровень коэффициент (будет округляться из-за int)
    private static final double HEALTH_GROWTH = 0.03;
    private static final double STRENGTH_GROWTH = 0.03;
    private static final double DEXTERITY_GROWTH = 0.01;

    private static final int BASE_HOSTILITY = 3;
    private static final int BASE_TREASURE = 75;

    private static final Random random = new Random();
    private static final double VARIATION = 0.4;

    public Ghost(int enemyLevel, Position position){
        super(position, (int)(BASE_HEALTH * ((double) enemyLevel * HEALTH_GROWTH + 1.0) * (1 + random.nextDouble() * VARIATION - VARIATION/2)),
                (int)(BASE_HEALTH * ((double) enemyLevel * HEALTH_GROWTH + 1.0) * (1 + random.nextDouble() * VARIATION - VARIATION/2)),
                (int)(BASE_STRENGTH * ((double) enemyLevel * STRENGTH_GROWTH + 1.0) * (1 + random.nextDouble() * VARIATION - VARIATION/2)),
                (int)(BASE_DEXTERITY * ((double) enemyLevel * DEXTERITY_GROWTH + 1.0) * (1 + random.nextDouble() * VARIATION - VARIATION/2)),
                EnemyType.ZOMBIE, BASE_HOSTILITY, BASE_TREASURE);
    }

    private boolean isInvisible = false;
    private int invisibleTurns = 0;
    private boolean inCombat = false;

    public Ghost(Position position) {
        super(position, 20, 20, 20, 5,
                EnemyType.GHOST, 3, 75);
    }

    @Override
    public void movePattern(Room currentRoom, Player player) {
        // Привидение постоянно телепортируется
        if (!shouldChase(currentRoom, player)) {
            // Случайная телепортация по комнате
            if (Math.random() < 0.3) { // 30% шанс телепортации
                teleportRandomly(currentRoom);
            }

            // Периодическая невидимость
            if (!isInvisible && Math.random() < 0.2) {
                isInvisible = true;
                invisibleTurns = 3;
            }

            if (isInvisible) {
                invisibleTurns--;
                if (invisibleTurns <= 0) {
                    isInvisible = false;
                }
            }
        } else {
            chasePlayer(currentRoom, player);
        }
    }

    private void teleportRandomly(Room currentRoom) {
        if (currentRoom != null) {
            this.position = currentRoom.getRandomFreePosition();
        }
    }

//    @Override
//    public boolean shouldChase(Player player) {
//        boolean chase = super.shouldChase(player);
//        if (chase) {
//            inCombat = true;
//            invisible = false; // В бою становится видимым
//        }
//        return chase;
//    }

    @Override
    protected void applySpecialAttackEffects(Player player) {
        // Привидение не имеет специальных эффектов атаки
    }

    public boolean isInvisible() {
        return isInvisible;
    }

    @Override
    public char getDisplayChar() {
        return isInvisible ? ' ' : 'g';
    }
    @Override
    public TextColor getDisplayColor() {
        return TextColor.ANSI.WHITE;
    }
}