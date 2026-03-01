package domain.monsters;

import com.googlecode.lanterna.TextColor;
import domain.Position;
import domain.items.BaseItem;
import domain.level.Room;
import domain.player.Player;

import java.util.Random;

public class Zombie extends Enemy {

    //Базовые статы
    private static final double BASE_HEALTH = 60;
    private static final double BASE_STRENGTH = 5;
    private static final double BASE_DEXTERITY = 6;
    //Прирост базовых стат за уровень коэффициент (будет округляться из-за int)
    private static final double HEALTH_GROWTH = 0.03;
    private static final double STRENGTH_GROWTH = 0.03;
    private static final double DEXTERITY_GROWTH = 0.01;

    private static final int BASE_HOSTILITY = 6;
    private static final int BASE_TREASURE = 50;

    private static final Random random = new Random();
    private static final double VARIATION = 0.1;

    public Zombie(int enemyLevel, Position position){
        super(position, (int)(BASE_HEALTH * ((double) enemyLevel * HEALTH_GROWTH + 1.0) * (1 + random.nextDouble() * VARIATION - VARIATION/2)),
                (int)(BASE_HEALTH * ((double) enemyLevel * HEALTH_GROWTH + 1.0) * (1 + random.nextDouble() * VARIATION - VARIATION/2)),
                (int)(BASE_STRENGTH * ((double) enemyLevel * STRENGTH_GROWTH + 1.0) * (1 + random.nextDouble() * VARIATION - VARIATION/2)),
                (int)(BASE_DEXTERITY * ((double) enemyLevel * DEXTERITY_GROWTH + 1.0) * (1 + random.nextDouble() * VARIATION - VARIATION/2)),
                EnemyType.ZOMBIE, BASE_HOSTILITY, BASE_TREASURE);
    }

    public Zombie(Position position) {
        super(position, 60, 60, 5, 6,
                EnemyType.ZOMBIE, 6, 50);
    }



    /*
    Зомби двигается медленно и предсказуемо
    Просто двигается в случайном направлении, если не преследует игрока
     */
    @Override
    public void movePattern(Room currentRoom, Player player) {

        if (!shouldChase(currentRoom, player)) {
            boolean wasMoveMade = false;
            while (!wasMoveMade) {
                int direction = (int) (Math.random() * 4);
                switch (direction) {
                    case 0:
                        if (currentRoom.isPositionInRoom(position.translate(1, 0))) {
                            position.setX(position.getX() + 1);
                            wasMoveMade = true;
                        }
                        break;
                    case 1:
                        if (currentRoom.isPositionInRoom(position.translate(-1, 0))) {
                            position.setX(position.getX() - 1);
                            wasMoveMade = true;
                        }
                        break;
                    case 2:
                        if (currentRoom.isPositionInRoom(position.translate(0, 1))) {
                            position.setY(position.getY() + 1);
                            wasMoveMade = true;
                        }
                        break;
                    case 3:
                        if (currentRoom.isPositionInRoom(position.translate(0, -1))) {
                            position.setY(position.getY() - 1);
                            wasMoveMade = true;
                        }
                        break;
                }
            }
        } else {
            chasePlayer(currentRoom, player);
        }
    }

    @Override
    protected void applySpecialAttackEffects(Player player) {

    }

    @Override
    public char getDisplayChar() {
        return 'z';
    }
    @Override
    public TextColor getDisplayColor() {
        return TextColor.ANSI.GREEN;
    }
}