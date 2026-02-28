package domain;

//Интерфейс сущность: Для игрока, монстров, предметов
public interface Entity {
    public Position getPosition();

    void setPosition(Position position);
}
