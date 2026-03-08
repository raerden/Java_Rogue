package domain.monsters;

import com.googlecode.lanterna.TextColor;
import domain.Position;
import domain.level.Room;
import domain.player.Player;

import java.util.Random;

public class Mimic extends Enemy  {
    private static final double BASE_HEALTH = 80;
    private static final double BASE_STRENGTH = 5;
    private static final double BASE_DEXTERITY = 20;
    private static final double HEALTH_GROWTH = 0.03;
    private static final double STRENGTH_GROWTH = 0.03;
    private static final double DEXTERITY_GROWTH = 0.01;
    private static final int BASE_HOSTILITY = 10;
    private static final int BASE_TREASURE = 50;

    private static final Random random = new Random();
    private static final double VARIATION = 0.1;

    private char fakeChar;
    private static final char[] FAKE_CHARS = {'^', '~', '/', '*', '%'};

    public Mimic() {
        super();
        this.fakeChar = FAKE_CHARS[random.nextInt(FAKE_CHARS.length)];
    }

    public Mimic(int enemyLevel, Position position){
        super(position,
                (int)(BASE_HEALTH * ((double) enemyLevel * HEALTH_GROWTH + 1.0) * (1 + random.nextDouble() * VARIATION - VARIATION/2)),
                (int)(BASE_HEALTH * ((double) enemyLevel * HEALTH_GROWTH + 1.0) * (1 + random.nextDouble() * VARIATION - VARIATION/2)),
                (int)(BASE_STRENGTH * ((double) enemyLevel * STRENGTH_GROWTH + 1.0) * (1 + random.nextDouble() * VARIATION - VARIATION/2)),
                (int)(BASE_DEXTERITY * ((double) enemyLevel * DEXTERITY_GROWTH + 1.0) * (1 + random.nextDouble() * VARIATION - VARIATION/2)),
                EnemyType.MIMIC, BASE_HOSTILITY, BASE_TREASURE);
        this.fakeChar = FAKE_CHARS[random.nextInt(FAKE_CHARS.length)];
    }

    public Mimic(Position position) {
        super(position,
                (int)BASE_HEALTH,
                (int)BASE_HEALTH,
                (int)BASE_STRENGTH,
                (int)BASE_DEXTERITY,
                EnemyType.MIMIC,
                BASE_HOSTILITY,
                BASE_TREASURE);
        this.fakeChar = FAKE_CHARS[random.nextInt(FAKE_CHARS.length)];
    }

    @Override
    public void movePattern(Room currentRoom, Player player) {
        // Если не атакован - стоит на месте
        if (!wasAttacked) {
            return;
        }

        // Если атакован и должен преследовать - преследует
        if (shouldChase(currentRoom, player)) {
            chasePlayer(currentRoom, player);
        } else {
            // Если атакован, но игрок далеко - ходит случайно
            randomMove(currentRoom);
        }
    }

    private void randomMove(Room currentRoom) {
        boolean wasMoveMade = false;
        int attempts = 0;
        while (!wasMoveMade && attempts < 10) {
            int direction = random.nextInt(4);
            Position newPos = null;

            switch (direction) {
                case 0: newPos = position.translate(1, 0); break;
                case 1: newPos = position.translate(-1, 0); break;
                case 2: newPos = position.translate(0, 1); break;
                case 3: newPos = position.translate(0, -1); break;
            }

            if (currentRoom.isPositionInRoom(newPos)) {
                this.position = newPos;
                wasMoveMade = true;
            }
            attempts++;
        }
    }

    @Override
    public boolean shouldChase(Room currentRoom, Player player) {
        // Если не атакован - не преследует
        if (!wasAttacked) {
            return false;
        }
        // После атаки используем родительскую логику
        return super.shouldChase(currentRoom, player);
    }

    @Override
    public void chasePlayer(Room currentRoom, Player player) {
        if (!wasAttacked) {
            return;
        }
        super.chasePlayer(currentRoom, player);
    }

    @Override
    protected void applySpecialAttackEffects(Player player) {
        // Можно добавить спецэффект, например, испуг игрока
    }

    @Override
    public char getDisplayChar() {
        return wasAttacked ? 'm' : fakeChar;
    }

    @Override
    public TextColor getDisplayColor() {
        return wasAttacked ? TextColor.ANSI.WHITE : TextColor.ANSI.GREEN;
    }

    public char getFakeChar() {
        return fakeChar;
    }

    public void setFakeChar(char fakeChar) {
        this.fakeChar = fakeChar;
    }
}