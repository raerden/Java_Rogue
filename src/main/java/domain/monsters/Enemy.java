package domain.monsters;

import domain.Character;
import domain.Position;

import java.util.List;

public class Enemy extends Character {
    //Состояния врага Возможно добавление
    enum EnemyState {
        WANDERING,          //БЛУЖДАЮЩИЙ
        CHASING            //ПРЕСЛЕДУЮЩИЙ
    }
    List<String> typesOfEnemy = List.of("Ghost", "Ogre", "SnakeMagician", "Vampire", "Zombie");

    private int hostilityRange;
    private EnemyState state = EnemyState.WANDERING;
    private boolean firstHitMiss = false;   // для вампира — первый удар по нему всегда промах
    private boolean isVisible = true;       // для привидения (периодическая невидимость) будет работать через вероятность

    private char EnemyType;

    public char getEnemyType(){
        return this.EnemyType;
    }

    public Enemy(String type, int level, Position position) {
        super(type, level, position);

        if (!typesOfEnemy.contains(type) && type != "Player") {
            throw new IllegalArgumentException("Не соответствие типу: Ghost, Ogre, SnakeMagician, Vampire, Zombie");
        }
        this.hostilityRange = calculateHostility(type, level); // пример масштабирования
        this.firstHitMiss = (type == "Vampire");
    }

    public int calculateHostility(String type, int level) {
        switch (type) {
            case "Zombie":
                return 3 + level / 4;
            case "Ogre":
                return 3 + level / 4;
            case "SnakeMagician":
                return 4 + level / 3;
            case "Vampire":
                return 5 + level / 2;
            case "Ghost":
                return 2;
            default:
                return 0;
        }
    }
}
