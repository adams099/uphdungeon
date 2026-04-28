package id.uphdungeon.entity;

import id.uphdungeon.GamePanel;
import id.uphdungeon.entity.animation.EnemySpriteManager;
import id.uphdungeon.entity.animation.SkeletonSpriteManager;
import java.awt.Color;

public class Skeleton extends EnemyAnimated {
  private final SkeletonSpriteManager spriteManager = new SkeletonSpriteManager();

  // Constructor for skeleton
  public Skeleton(GamePanel gamePanel, int startX, int startY, int dirX, int dirY) {
    super(gamePanel, startX, startY, dirX, dirY, Color.DARK_GRAY, 25, 3, 5);
    initAnimation();
  }

  // Initialize the animation for the skeleton
  @Override
  protected EnemySpriteManager getSpriteManager() {
    return spriteManager;
  }

  @Override
  public int getExpReward() {
    return 30;
  }

  // Determine the intent of the skeleton, and trigger attack animation if player is adjacent
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
