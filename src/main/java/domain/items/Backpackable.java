package domain.items;

import domain.player.Player;
import domain.Position;

public interface Backpackable {

    /**
    *Использование на player
     */
    void apply(Player player);

    /**
     * Получить тип предмета
     */
    ItemType getType();

    /**
     * Получить название предмета
     */
    String getName();

}