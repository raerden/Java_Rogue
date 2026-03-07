package domain.level;

import domain.Entity;
import domain.Position;
import domain.items.BaseItem;
import domain.monsters.Enemy;

import java.util.*;
public class LevelUnits {
    private Set<Entity> entities = new HashSet<>();

    public LevelUnits() {
        this.entities = new HashSet<>();
    }

    public boolean addEntity(Entity entity) {
        // Проверяем, не занята ли позиция
        if (findEntityAt(entity.getPosition()) != null) {
            return false;
        }
        return entities.add(entity);
    }

    /**
     * Доработать удаление из комнаты возможно
     * @param entity
     * @return
     */
    public boolean deleteEntity(Entity entity) {
        return entities.remove(entity);
    }

    public Entity getEntityAt(Position pos) {
        return entities.stream()
                .filter(e -> e.getPosition().equals(pos))
                .findFirst()
                .orElse(null);
    }

    public boolean moveEntity(Entity entity, Position newPos) {
        if (!entities.contains(entity)) return false;

        // Проверяем, не занята ли новая позиция другим Entity
        Entity other = getEntityAt(newPos);
        if (other != null && other != entity) {
            return false;
        }

        entity.setPosition(newPos);
        return true;
    }

    public Entity findEntityAt(Position pos) {
        for (Entity e : entities) {
            if (e.getPosition().equal(pos)) {
                return e;
            }
        }
        return null;
    }

    public void printAllEntities() {
        System.out.println("=== Все сущности на уровне ===");
        if (entities.isEmpty()) {
            System.out.println("Нет сущностей");
            return;
        }

        for (Entity entity : entities) {
            System.out.printf("\t" + entity + "\n");
        }
        System.out.println("=============================");
    }
    public Set<Entity> getAllEntities() {
        return new HashSet<>(entities);
    }

    // Геттер и сеттер
    public Set<Entity> getEntities() {
        return entities;
    }

    public void setEntities(Set<Entity> entities) {
        this.entities = entities != null ? entities : new HashSet<>();
    }
}
