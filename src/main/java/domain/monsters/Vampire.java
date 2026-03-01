package domain.monsters;

import com.googlecode.lanterna.TextColor;
import domain.Position;
import domain.level.Room;
import domain.player.Player;

import java.util.Random;

public class Vampire extends Enemy {
    //Базовые статы
    private static final double BASE_HEALTH = 50;
    private static final double BASE_STRENGTH = 8;
    private static final double BASE_DEXTERITY = 15;
    //Прирост базовых стат за уровень коэффициент (будет округляться из-за int)
    private static final double HEALTH_GROWTH = 0.03;
    private static final double STRENGTH_GROWTH = 0.05;
    private static final double DEXTERITY_GROWTH = 0.05;

    private static final int BASE_HOSTILITY = 12;
    private static final int BASE_TREASURE = 100;

    private static final Random random = new Random();
    private static final double VARIATION = 0.1;

    public Vampire(int enemyLevel, Position position){
        super(position, (int)(BASE_HEALTH * ((double) enemyLevel * HEALTH_GROWTH + 1.0) * (1 + random.nextDouble() * VARIATION - VARIATION/2)),
                (int)(BASE_HEALTH * ((double) enemyLevel * HEALTH_GROWTH + 1.0) * (1 + random.nextDouble() * VARIATION - VARIATION/2)),
                (int)(BASE_STRENGTH * ((double) enemyLevel * STRENGTH_GROWTH + 1.0) * (1 + random.nextDouble() * VARIATION - VARIATION/2)),
                (int)(BASE_DEXTERITY * ((double) enemyLevel * DEXTERITY_GROWTH + 1.0) * (1 + random.nextDouble() * VARIATION - VARIATION/2)),
                EnemyType.ZOMBIE, BASE_HOSTILITY, BASE_TREASURE);
    }
    private boolean firstAttack = true;

    public Vampire(Position position) {
        super(position, 50, 50, 9, 15,
                EnemyType.VAMPIRE, 12, 100);
    }

    @Override
    public void movePattern(Room currentRoom, Player player) {

        if (!shouldChase(currentRoom, player)) {
            boolean wasMoveMade = false;
            while (!wasMoveMade) {
                int direction = (int) (Math.random() * 8);
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
                    case 4:
                        if (currentRoom.isPositionInRoom(position.translate(1, 1))) {
                            position.setX(position.getX() + 1);
                            position.setY(position.getY() + 1);
                            wasMoveMade = true;
                        }
                        break;
                    case 5:
                        if (currentRoom.isPositionInRoom(position.translate(1, -1))) {
                            position.setX(position.getX() + 1);
                            position.setY(position.getY() - 1);
                            wasMoveMade = true;
                        }
                        break;
                    case 6:
                        if (currentRoom.isPositionInRoom(position.translate(-1, 1))) {
                            position.setX(position.getX() - 1);
                            position.setY(position.getY() + 1);
                            wasMoveMade = true;
                        }
                        break;
                    case 7:
                        if (currentRoom.isPositionInRoom(position.translate(-1, -1))) {
                            position.setX(position.getX() - 1);
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

    /**
     * Добавляются атака и движение по горизонтали
     * @param currentRoom
     * @param player
     */
    @Override
    public void chasePlayer(Room currentRoom, Player player) {
        if (player == null || !isAlive() || !player.isAlive()) {
            return;
        }

        int dXOne = Integer.signum(player.getPosition().getX() - this.position.getX());
        int dYOne = Integer.signum(player.getPosition().getY() - this.position.getY());

        int dx = player.getPosition().getX() - this.position.getX();
        int dy = player.getPosition().getY() - this.position.getY();
        // Пытаемся двигаться сначала по горизонтали, потом по вертикали
        if ((Math.abs(dx) == 1 && Math.abs(dy) == 0) || (Math.abs(dx) == 0 && Math.abs(dy) == 1)) {
            attack(player);
        } else {
            this.position = this.position.translate(dXOne, dYOne);
        }
    }

    /**
     * Реализация calculateHitChance с 100%
     * промахом первой атаки по вампиру
     *
     * @param targetDexterity
     * @return шанс совершения атаки
     */
    @Override
    public double calculateHitChance(int targetDexterity) {
        if (firstAttack) {
            firstAttack = false;
            return 0; // Первый удар всегда промах
        }
        return super.calculateHitChance(targetDexterity);
    }

    /**
     * Вампир отнимает максимальное здоровье
     * у игрока после успешного удара и восстанавливает
     * себе долю отнятого у игрока здоровья
     *
     * @param player
     */
    @Override
    protected void applySpecialAttackEffects(Player player) {

        int healthReduction = 5;
        player.setMaxHealth(player.getMaxHealth() - healthReduction);
        // Вампир восстанавливает здоровье
        this.heal(healthReduction);
    }

    @Override
    public char getDisplayChar() {
        return 'v';
    }
    @Override
    public TextColor getDisplayColor() {
        return TextColor.ANSI.RED;
    }
}