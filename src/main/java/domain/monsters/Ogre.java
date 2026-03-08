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
    private static final double BASE_STRENGTH = 10;
    private static final double BASE_DEXTERITY = 3;
    //Прирост базовых стат за уровень коэффициент (будет округляться из-за int)
    private static final double HEALTH_GROWTH = 0.09;
    private static final double STRENGTH_GROWTH = 0.04;
    private static final double DEXTERITY_GROWTH = 0.00;

    private static final int BASE_HOSTILITY = 10;
    private static final int BASE_TREASURE = 150;

    private static final Random random = new Random();
    private static final double VARIATION = 0.1;

    public Ogre() {
        super(); // вызываем пустой конструктор родителя
        this.resting = false;
        this.moveCounter = 0;
    }

    public Ogre(int enemyLevel, Position position){
        super(position, (int)(BASE_HEALTH * ((double) enemyLevel * HEALTH_GROWTH + 1.0) * (1 + random.nextDouble() * VARIATION - VARIATION/2)),
                (int)(BASE_HEALTH * ((double) enemyLevel * HEALTH_GROWTH + 1.0) * (1 + random.nextDouble() * VARIATION - VARIATION/2)),
                (int)(BASE_STRENGTH * ((double) enemyLevel * STRENGTH_GROWTH + 1.0) * (1 + random.nextDouble() * VARIATION - VARIATION/2)),
                (int)(BASE_DEXTERITY * ((double) enemyLevel * DEXTERITY_GROWTH + 1.0) * (1 + random.nextDouble() * VARIATION - VARIATION/2)),
                EnemyType.OGRE, BASE_HOSTILITY, BASE_TREASURE);
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

        int dx = player.getPosition().getX() - this.position.getX();
        int dy = player.getPosition().getY() - this.position.getY();
        int distance = Math.abs(dx) + Math.abs(dy); // Манхэттенское расстояние

        // Проверяем, может ли огр атаковать с текущей позиции
        // Огр атакует, если игрок на соседней клетке (дистанция 1)
        if (Math.abs(dx) <= 1 && Math.abs(dy) <= 1 && !(dx == 0 && dy == 0)) {
            if (!resting) attack(player);
            resting = !resting;
            return;
        }

        // Если игрок на расстоянии 2 клеток, нужно встать рядом, а не на голову
        if (distance == 2) {
            // Определяем направление к игроку
            int stepX = Integer.signum(dx);
            int stepY = Integer.signum(dy);

            // Если движение только по одной оси
            if (dx == 0 || dy == 0) {
                // Двигаемся на 1 клетку к игроку, чтобы оказаться рядом
                if (dx != 0) {
                    this.position = this.position.translate(stepX, 0);
                } else {
                    this.position = this.position.translate(0, stepY);
                }
            } else {
                // Если игрок по диагонали на расстоянии 2
                // Выбираем движение по оси с большим расстоянием
                if (Math.abs(dx) > Math.abs(dy)) {
                    this.position = this.position.translate(stepX, 0);
                } else {
                    this.position = this.position.translate(0, stepY);
                }
            }

            // После движения проверяем, можем ли атаковать
            int newDx = player.getPosition().getX() - this.position.getX();
            int newDy = player.getPosition().getY() - this.position.getY();
            if (Math.abs(newDx) <= 1 && Math.abs(newDy) <= 1 && !(newDx == 0 && newDy == 0)) {
                if (!resting) attack(player);
                resting = !resting;
            }
            return;
        }

        // Обычное движение с шагом 2 клетки
        int dXOne = Integer.signum(dx);
        int dYOne = Integer.signum(dy);

        while (moveCounter < MOVE_STEP) {
            // Проверяем, не приблизились ли мы к игроку на расстояние атаки
            int currentDx = player.getPosition().getX() - this.position.getX();
            int currentDy = player.getPosition().getY() - this.position.getY();

            if (Math.abs(currentDx) <= 1 && Math.abs(currentDy) <= 1 && !(currentDx == 0 && currentDy == 0)) {
                if (!resting) attack(player);
                resting = !resting;
                moveCounter += 2;
                break;
            } else if (Math.abs(currentDx) > Math.abs(currentDy)) {
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
    public int attack(Player player) {
        if (resting) {
            resting = false;
            return -1;
        }

        resting = true; // Отдыхает после атаки
        return super.attack(player);
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

    public boolean isResting() {
        return resting;
    }

    public void setResting(boolean resting) {
        this.resting = resting;
    }

    public int getMoveCounter() {
        return moveCounter;
    }

    public void setMoveCounter(int moveCounter) {
        this.moveCounter = moveCounter;
    }
}