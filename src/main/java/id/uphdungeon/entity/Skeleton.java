package id.uphdungeon.entity;

import id.uphdungeon.GamePanel;
import id.uphdungeon.entity.animation.EnemySpriteManager;
import id.uphdungeon.entity.animation.SkeletonSpriteManager;
import java.awt.Color;

public class Skeleton extends EnemyAnimated {
  private final SkeletonSpriteManager spriteManager = new SkeletonSpriteManager();

  // Constructor for skeleton
  public Skeleton(GamePanel gamePanel, int startX, int startY) {
    super(gamePanel, startX, startY, Color.DARK_GRAY);

    this.maxHealth = 25;
    this.health = maxHealth;
    this.minDamage = 3;
    this.maxDamage = 5;

    this.aggroRange = 2;
    this.idleWaitChance = 0.1;
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
}
