package id.uphdungeon.entity;

import id.uphdungeon.GamePanel;
import id.uphdungeon.entity.animation.EnemySpriteManager;
import id.uphdungeon.entity.animation.RatSpriteManager;
import java.awt.Color;


public class Rat extends EnemyAnimated {

  private final RatSpriteManager spriteManager = new RatSpriteManager();

  // Constructor for rat
  public Rat(GamePanel gamePanel, int startX, int startY) {
    super(gamePanel, startX, startY, Color.DARK_GRAY);

    this.maxHealth = 10;
    this.health = maxHealth;
    this.minDamage = 1;
    this.maxDamage = 2;

    this.aggroRange = 1;
    this.idleWaitChance = 0.1;
    initAnimation();
  }

  // Initialize the animation for the rat
  @Override
  protected EnemySpriteManager getSpriteManager() {
    return spriteManager;
  }

  @Override
  public int getExpReward() {
    return 10;
  }
}
