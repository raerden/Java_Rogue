package domain.monsters;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public enum EnemyType {
        ZOMBIE("зомби", 1),
        GHOST("призрак", 2),
        OGRE("огр", 3),
        VAMPIRE("вампир", 4),
        SNAKE_MAGICIAN("змей-маг", 5);


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

//    public static EnemyType getRandomForLevel(int levelNumber) {
//            // На первых уровнях не появляются сильные монстры
//            List<EnemyType> available = new ArrayList<>();
//
//            for (EnemyType type : values()) {
//                if (type.difficulty <= levelNumber / 2 + 1) {
//                    available.add(type);
//                }
//            }
//
//            return available.get(new Random().nextInt(available.size()));
//        }

}