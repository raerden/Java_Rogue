package domain.monsters;

import java.util.Random;

public enum EnemyType {
        ZOMBIE("зомби", 1),
        GHOST("призрак", 2),
        OGRE("огр", 3),
        VAMPIRE("вампир", 4),
        SNAKE_MAGICIAN("змей-маг", 5),
        MIMIC("Мимик", 2);


        private final String displayName;
        private final int difficulty;

        EnemyType(String displayName, int difficulty) {
            this.displayName = displayName;
            this.difficulty = difficulty;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getDifficulty() {
            return difficulty;
        }

        public static EnemyType getRandom() {
            EnemyType[] types = values();
            return types[new Random().nextInt(types.length)];
        }
}