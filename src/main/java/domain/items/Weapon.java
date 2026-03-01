package domain.items;

import domain.player.Player;
import domain.Position;

public class Weapon extends BaseItem{
    private final int bonusStrength;
    private final int bonusDexterity;
    private boolean equipped;

    public Weapon(String name, int bonusStrength, int bonusDexterity, Position position) {
        super(name, ItemType.WEAPON, position);
        this.bonusStrength = bonusStrength;
        this.bonusDexterity = bonusDexterity;
        this.equipped = false;
    }

    @Override
    public void apply(Player player) {
        player.setStrength(player.getStrength() + bonusStrength);
        player.setDexterity(player.getDexterity() + bonusDexterity);
        System.out.printf("%s снарядил %s, Strength стала %d, Dexterity стала %d\n",
                player.getName(), name, player.getStrength(), player.getDexterity()
        );
        equipped = true;
    }

    public boolean discharge(Player player){
        if (equipped){
            player.setStrength(Math.max(player.getStrength() - bonusStrength, 1));
            player.setDexterity(Math.max(player.getDexterity() - bonusDexterity, 1));
            System.out.printf("%s убрал %s, Strength стала %d, Dexterity стала %d\n",
                    player.getName(), name, player.getStrength(), player.getDexterity()
            );
            equipped = false;
            return true;
        } else {
            return false;
        }
    }

//    @Override
//    public void setPosition(Position position) {
//        throw new UnsupportedOperationException("Оружие нельзя переместить!");
//    }

    public int getBonusStrength() {
        return bonusStrength;
    }

    public int getBonusDexterity() {
        return bonusDexterity;
    }

    @Override
    public String toString() {
        return String.format("Оружие '%s' (%s +%d) (%s +%d) на %s",
                name, ConsumableType.STRENGTH, bonusStrength, ConsumableType.DEXTERITY, bonusDexterity, position);
    }

    @Override
    public char getDisplayChar() {
        return '/';
    }
}
