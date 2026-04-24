package id.uphdungeon.entity;

import id.uphdungeon.GamePanel;
import id.uphdungeon.entity.animation.EnemySpriteManager;
import id.uphdungeon.entity.animation.RatSpriteManager;
import java.awt.Color;

// Rat enemy — basic melee dungeon monster with directional sprites and claw attack.
// All animation logic is handled by EnemyAnimated.
// This class only provides stats, the sprite manager, and attack intent detection.
public class Rat extends EnemyAnimated {

    // Rat's sprite manager — loaded once per instance
    private final RatSpriteManager spriteManager = new RatSpriteManager();

    // Constructor for rat
    public Rat(GamePanel gamePanel, int startX, int startY, int dirX, int dirY) {
        super(gamePanel, startX, startY, dirX, dirY, Color.DARK_GRAY, 10, 1, 2);
        // initAnimation() must be called after super() so getSpriteManager() is ready
        initAnimation();
    }

    // Provides Rat's sprite manager to EnemyAnimated
    @Override
    protected EnemySpriteManager getSpriteManager() {
        return spriteManager;
    }

    // Override determineIntent to trigger directional attack animation
    // when Rat is adjacent to the player. Movement logic is inherited from Enemy.
    @Override
    public void determineIntent(GamePanel gamePanel) {
        Player player = gamePanel.getPlayer();

        // Check adjacency before super runs, because super sets the intent
        boolean isAdjacentToPlayer = false;
        if (!player.isDead) {
            int dx = Math.abs(x - player.x);
            int dy = Math.abs(y - player.y);
            isAdjacentToPlayer = dx <= gamePanel.tileSize
                    && dy <= gamePanel.tileSize
                    && (dx != 0 || dy != 0);
        }

        // Let Enemy handle the full intent logic (movement + attack scheduling)
        super.determineIntent(gamePanel);

        // If Enemy scheduled attack and player is adjacent then trigger the attack animation immediately
        if (isAdjacentToPlayer && !isDead) {
            triggerAttackAnimation(player);
        }
    }
}