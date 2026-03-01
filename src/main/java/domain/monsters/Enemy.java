package domain.monsters;

import domain.Character;
import domain.Position;
import domain.level.Room;
import domain.player.Player;
import domain.monsters.EnemyType;

import java.util.List;

public abstract class Enemy extends Character {
    //Состояния врага Возможно добавление
    protected EnemyType type;
    protected int treasureValue;
    protected int hostility;

    public Enemy(Position position, int health, int maxHealth, int strength, int dexterity,
                 EnemyType type, int hostility, int treasureValue) {
        super(position, health, maxHealth, strength, dexterity);

        this.type = type;
        this.hostility = hostility;
        this.treasureValue = treasureValue;
    }
    //Геттеры
    public EnemyType getType() {
        return type;
    }

    public int getHostility() {
        return hostility;
    }

    public int getTreasureValue() {
        return treasureValue;
    }

    public abstract void movePattern(Room currentRoom, Player player);

    /**
    Можем ли преследовать игрока с учетом его нахождения в комнате
     */
    public boolean shouldChase(Room currentRoom, Player player) {
        if (player == null || !isAlive() || !player.isAlive() || !currentRoom.isPositionInRoom(player.getPosition())) {
            return false;
        }

        // Вычисляем расстояние до игрока
        int dx = Math.abs(player.getPosition().getX() - this.position.getX());
        int dy = Math.abs(player.getPosition().getY() - this.position.getY());
        int distance = dx + dy; // Манхэттенское расстояние, то есть зона реагирования визуализированна в виде ромба

        return distance <= hostility && player.isAlive();
    }

    public void chasePlayer(Room currentRoom, Player player) {
        if (player == null || !isAlive() || !player.isAlive()) {
            return;
        }

        int dXOne = Integer.signum(player.getPosition().getX() - this.position.getX());
        int dYOne = Integer.signum(player.getPosition().getY() - this.position.getY());

        int dx = player.getPosition().getX() - this.position.getX();
        int dy = player.getPosition().getY() - this.position.getY();
        // Пытаемся двигаться сначала по горизонтали, потом по вертикали

        //Атака доступна только влево, вправо, вверх, вниз
        if ((Math.abs(dx) == 1 && Math.abs(dy) == 0) || (Math.abs(dx) == 0 && Math.abs(dy) == 1)) {
            attack(player);
        } else if (Math.abs(dx) > Math.abs(dy)) {
            this.position = this.position.translate(dXOne, 0);
        } else {
            this.position = this.position.translate(0, dYOne);
        }
    }

    protected abstract void applySpecialAttackEffects(Player player);

    public boolean attack(Player player) {
        if (player == null || !player.isAlive() || !this.isAlive()){
            return false;
        }
        boolean wasAttacked = false;
        double hitChance = this.calculateHitChance(player.getDexterity());
        if (Math.random() < hitChance) {
            int damage = calculateHitDamage();
            player.takeDamage(damage, this);
            applySpecialAttackEffects(player);
            wasAttacked = true;
        }
        return wasAttacked;
    }

}
