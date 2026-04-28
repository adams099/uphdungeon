package id.uphdungeon.potion;

import id.uphdungeon.entity.Player;

// Potion of Healing heals +5 HP on pickup most common potion with 88% spawn chance
public class PotionOfHealing extends Potion {
    private static final int HEAL_AMOUNT = 5;

    // Constructor
    public PotionOfHealing(int tileSize) {
        super(tileSize);
    }

    // Heals Player by HEAL_AMOUNT, capped at maxHealth
    @Override
    public void applyEffect(Player player) {
        player.health = Math.min(player.health + HEAL_AMOUNT, player.maxHealth);
    }
}