package domain.level;

import domain.Entity;
import domain.items.BaseItem;
import domain.monsters.Enemy;
import domain.Position;

import java.util.HashSet;
import java.util.Set;

public class LevelUnits {
    private Set<Enemy> enemies = new HashSet<>();
    private Set<BaseItem> items = new HashSet<>();

    // Пустой конструктор для Gson
    public LevelUnits() {}

    public LevelUnits(Set<Enemy> enemies, Set<BaseItem> items) {
        this.enemies = enemies != null ? enemies : new HashSet<>();
        this.items = items != null ? items : new HashSet<>();
    }

    // Геттеры и сеттеры
    public Set<Enemy> getEnemies() {
        return enemies;
    }

    public void setEnemies(Set<Enemy> enemies) {
        this.enemies = enemies != null ? enemies : new HashSet<>();
    }

    public Set<BaseItem> getItems() {
        return items;
    }

    public void setItems(Set<BaseItem> items) {
        this.items = items != null ? items : new HashSet<>();
    }

    // Методы для работы с врагами
    public boolean addEnemy(Enemy enemy) {
        if (findEntityAt(enemy.getPosition()) != null) {
            return false;
        }
        return enemies.add(enemy);
    }

    public boolean removeEnemy(Enemy enemy) {
        return enemies.remove(enemy);
    }

    public Enemy getEnemyAt(Position pos) {
        for (Enemy e : enemies) {
            if (e.getPosition().equal(pos)) {
                return e;
            }
        }
        return null;
    }

    // Методы для работы с предметами
    public boolean addItem(BaseItem item) {
        if (findEntityAt(item.getPosition()) != null) {
            return false;
        }
        return items.add(item);
    }

    public boolean removeItem(BaseItem item) {
        return items.remove(item);
    }

    public BaseItem getItemAt(Position pos) {
        for (BaseItem i : items) {
            if (i.getPosition().equal(pos)) {
                return i;
            }
        }
        return null;
    }

    // Общие методы
    public Entity findEntityAt(Position pos) {
        Enemy enemy = getEnemyAt(pos);
        if (enemy != null) return enemy;

        BaseItem item = getItemAt(pos);
        if (item != null) return item;

        return null;
    }

    public boolean moveEnemy(Enemy enemy, Position newPos) {
        if (!enemies.contains(enemy)) return false;

        // Проверяем, не занята ли позиция другим врагом или предметом
        if (getEnemyAt(newPos) != null || getItemAt(newPos) != null) {
            return false;
        }

        enemy.setPosition(newPos);
        return true;
    }

    public boolean moveItem(BaseItem item, Position newPos) {
        if (!items.contains(item)) return false;

        // Проверяем, не занята ли позиция врагом или другим предметом
        if (getEnemyAt(newPos) != null || getItemAt(newPos) != null) {
            return false;
        }

        item.setPosition(newPos);
        return true;
    }

    public Set<Enemy> getAllEnemies() {
        return new HashSet<>(enemies);
    }

    public Set<BaseItem> getAllItems() {
        return new HashSet<>(items);
    }

    public Set<Entity> getAllEntities() {
        Set<Entity> all = new HashSet<>();
        all.addAll(enemies);
        all.addAll(items);
        return all;
    }

    public void printAllEntities() {
        System.out.println("=== Все сущности на уровне ===");

        if (enemies.isEmpty() && items.isEmpty()) {
            System.out.println("Нет сущностей");
        } else {
            if (!enemies.isEmpty()) {
                System.out.println("Враги:");
                for (Enemy enemy : enemies) {
                    System.out.println("  " + enemy);
                }
            }

            if (!items.isEmpty()) {
                System.out.println("Предметы:");
                for (BaseItem item : items) {
                    System.out.println("  " + item);
                }
            }
        }
        System.out.println("=============================");
    }

    public boolean isEmpty() {
        return enemies.isEmpty() && items.isEmpty();
    }

    public int getTotalCount() {
        return enemies.size() + items.size();
    }
}