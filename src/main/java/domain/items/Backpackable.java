package domain.items;

import domain.player.Player;
import domain.Position;

public interface Backpackable {
    void apply(Player player);

    /**
     * Получить тип предмета
     */
    ItemType getType();

    /**
     * Получить название предмета
     */
    String getName();

    /**
     * Получить символ для отрисовки
     */
    char getSymbol();

}