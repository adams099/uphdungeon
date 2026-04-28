package id.uphdungeon.entity;

import id.uphdungeon.GamePanel;
import id.uphdungeon.entity.animation.EnemySpriteManager;
import id.uphdungeon.entity.animation.RatSpriteManager;
import java.awt.Color;


public class Rat extends EnemyAnimated {

  private final RatSpriteManager spriteManager = new RatSpriteManager();

  // Constructor for rat
  public Rat(GamePanel gamePanel, int startX, int startY, int dirX, int dirY) {
    super(gamePanel, startX, startY, dirX, dirY, Color.DARK_GRAY, 10, 1, 2);
    initAnimation();
  }

  // Initialize the animation for the rat
  @Override
  protected EnemySpriteManager getSpriteManager() {
    return spriteManager;
  }

  // Determine the intent of the rat, and trigger attack animation if player is adjacent
  @Override
  public void determineIntent(GamePanel gamePanel) {
    Player player = gamePanel.getPlayer();

    // Check adjacency before super runs, because super sets the intent
    boolean isAdjacentToPlayer = false;
    if (!player.isDead) {
      int dx = Math.abs(x - player.x);
      int dy = Math.abs(y - player.y);
      isAdjacentToPlayer =
          dx <= gamePanel.tileSize && dy <= gamePanel.tileSize && (dx != 0 || dy != 0);
    }

    super.determineIntent(gamePanel);

    // If Enemy scheduled attack and player is adjacent then trigger the attack animation
    // immediately
    if (isAdjacentToPlayer && !isDead) {
      triggerAttackAnimation(player);
    }
  }
}
