package id.uphdungeon.entity;

import id.uphdungeon.GamePanel;
import id.uphdungeon.entity.animation.DragonSpriteManager;
import id.uphdungeon.entity.animation.EnemySpriteManager;
import java.awt.Color;

public class Dragon extends EnemyAnimated {
  private final DragonSpriteManager spriteManager = new DragonSpriteManager();

  public Dragon(GamePanel gamePanel, int startX, int startY) {
    super(gamePanel, startX, startY, Color.RED);

    this.maxHealth = 100;
    this.health = maxHealth;
    this.minDamage = 5;
    this.maxDamage = 15;

    this.aggroRange = 5;
    this.idleWaitChance = 0.3;
    initAnimation();
  }

  @Override
  protected EnemySpriteManager getSpriteManager() {
    return spriteManager;
  }

  @Override
  public int getExpReward() {
    return 500;
  }
}
