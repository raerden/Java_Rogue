package domain.monsters;

import com.googlecode.lanterna.TextColor;
import domain.Character;
import domain.Position;
import domain.level.Room;
import domain.player.Player;

import java.util.Random;

public class Ogre extends Enemy {
    //Базовые статы
    private static final double BASE_HEALTH = 100;
    private static final double BASE_STRENGTH = 20;
    private static final double BASE_DEXTERITY = 3;
    //Прирост базовых стат за уровень коэффициент (будет округляться из-за int)
    private static final double HEALTH_GROWTH = 0.09;
    private static final double STRENGTH_GROWTH = 0.04;
    private static final double DEXTERITY_GROWTH = 0.00;

    private static final int BASE_HOSTILITY = 10;
    private static final int BASE_TREASURE = 150;

    private static final Random random = new Random();
    private static final double VARIATION = 0.1;

    public Ogre(int enemyLevel, Position position){
        super(position, (int)(BASE_HEALTH * ((double) enemyLevel * HEALTH_GROWTH + 1.0) * (1 + random.nextDouble() * VARIATION - VARIATION/2)),
                (int)(BASE_HEALTH * ((double) enemyLevel * HEALTH_GROWTH + 1.0) * (1 + random.nextDouble() * VARIATION - VARIATION/2)),
                (int)(BASE_STRENGTH * ((double) enemyLevel * STRENGTH_GROWTH + 1.0) * (1 + random.nextDouble() * VARIATION - VARIATION/2)),
                (int)(BASE_DEXTERITY * ((double) enemyLevel * DEXTERITY_GROWTH + 1.0) * (1 + random.nextDouble() * VARIATION - VARIATION/2)),
                EnemyType.ZOMBIE, BASE_HOSTILITY, BASE_TREASURE);
    }

    private boolean resting = false;
    private int moveCounter = 0;
    private static final int MOVE_STEP = 2;

    public Ogre(Position position) {
        super(position, 100, 100, 25, 3,
                EnemyType.OGRE, 10, 150);
    }

    /**
     * Паттерн подразумевает хождение Огра на 2 клетки
     * @param currentRoom
     * @param player
     */
    @Override
    public void movePattern(Room currentRoom, Player player) {
        if (!shouldChase(currentRoom, player)) {
            // Огр ходит по комнате на две клетки
            while (moveCounter < MOVE_STEP) {
                int direction = (int) (Math.random() * 4);
                switch (direction) {
                    case 0:
                        if (currentRoom.isPositionInRoom(position.translate(1, 0))) {
                            position.setX(position.getX() + 1);
                            moveCounter++;
                        }
                        break;
                    case 1:
                        if (currentRoom.isPositionInRoom(position.translate(-1, 0))) {
                            position.setX(position.getX() - 1);
                            moveCounter++;
                        }
                        break;
                    case 2:
                        if (currentRoom.isPositionInRoom(position.translate(0, 1))) {
                            position.setY(position.getY() + 1);
                            moveCounter++;
                        }
                        break;
                    case 3:
                        if (currentRoom.isPositionInRoom(position.translate(0, -1))) {
                            position.setY(position.getY() - 1);
                            moveCounter++;
                        }
                        break;
                }
            }
            moveCounter = 0;
        } else {
            chasePlayer(currentRoom, player);
        }
    }

    @Override
    protected void applySpecialAttackEffects(Player player) {

    }

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
        //boolean wasAttackInStep = false;
        while (moveCounter < MOVE_STEP){
            if (Math.abs(dx) <= 1 && Math.abs(dy) <= 1) {
                if (!resting) attack(player);
                resting = !resting;
                moveCounter += 2;
            } else if (Math.abs(dx) > Math.abs(dy)) {
                this.position = this.position.translate(dXOne, 0);
                moveCounter++;
            } else {
                this.position = this.position.translate(0, dYOne);
                moveCounter++;
            }
        }
        moveCounter = 0;
    }

    @Override
    public boolean attack(Player player) {
        if (resting) {
            resting = false;
            return false;
        }

        super.attack(player);
        resting = true; // Отдыхает после атаки
        return true;
    }

    @Override
    public boolean takeDamage(int damage, Character fromUnit) {
        if (damage <= 0 || !(fromUnit instanceof Player)) return false;


        this.health -= damage;
        if (this.health < 0) {
            this.health = 0;
        }
        if (this.isAlive()){
            counterattack((Player) fromUnit);
        }

        return !this.isAlive();
    }

    protected void counterattack(Player player) {
        // После успешной атаки гарантированно контратакует
        if (player.isAlive()) {
            player.takeDamage(this.calculateHitDamage(), this);
        }
    }


    @Override
    public char getDisplayChar() {
        return 'O';
    }
    @Override
    public TextColor getDisplayColor() {
        return TextColor.ANSI.YELLOW_BRIGHT;
    }
}