package id.uphdungeon.potion;

import id.uphdungeon.entity.Player;

// Greater Healing Potion hals +10 HP on pickup this rare potion with 12% spawn chance
public class GreaterPotion extends Potion {

    private static final int HEAL_AMOUNT = 10;

    // Constructor
    public GreaterPotion(int tileSize) {
        super(tileSize);
    }

    // Heals Player by HEAL_AMOUNT, capped at maxHealth
    @Override
    public void applyEffect(Player player) {
        player.health = Math.min(player.health + HEAL_AMOUNT, player.maxHealth);
    }
}