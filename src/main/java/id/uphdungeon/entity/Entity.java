package id.uphdungeon.entity;

import id.uphdungeon.GamePanel;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Entity {
  protected GamePanel gamePanel;
  public int x, y;
  public int speed;
  public int initiative;

  public int maxHealth;
  public int health;
  public int minDamage;
  public int maxDamage;
  public boolean isDead = false;
  public boolean isFading = false;
  public float alpha = 1.0f;

  private List<DamageIndicator> damageIndicators = new ArrayList<>();

  public Entity(GamePanel gamePanel) {
    this.gamePanel = gamePanel;
  }

  public abstract void determineIntent(GamePanel gamePanel);

  public abstract void executeAction(GamePanel gamePanel);

  public abstract void update();

  public abstract void draw(Graphics2D g2);

  public void updateIndicators() {
    for (int i = damageIndicators.size() - 1; i >= 0; i--) {
      DamageIndicator di = damageIndicators.get(i);
      di.update();
      if (di.isFinished()) {
        damageIndicators.remove(i);
      }
    }
  }

  public void updateFading() {}

  public void updateAnimations() {
    updateIndicators();
    updateFading();
  }

  public void drawIndicators(Graphics2D g2) {
    for (DamageIndicator di : damageIndicators) {
      di.draw(g2);
    }
  }

  public void attack(Entity target) {
    Random rand = new Random();
    int damage = rand.nextInt(maxDamage - minDamage + 1) + minDamage;
    target.health -= damage;

    int indicatorX = target.x + (gamePanel.tileSize / 2);
    int indicatorY = target.y;
    target.addDamageIndicator(String.valueOf(damage), indicatorX, indicatorY);

    System.out.println(
      this.getClass().getSimpleName() +
        " attacked " +
        target.getClass().getSimpleName() +
        " for " +
        damage +
        " damage! (" +
        target.health +
        "/" +
        target.maxHealth +
        " HP left)"
    );

    if (target.health <= 0) {
      target.isDead = true;
      target.isFading = true;
      System.out.println(target.getClass().getSimpleName() + " died!");
    }
  }

  public void addDamageIndicator(String text, int x, int y) {
    damageIndicators.add(new DamageIndicator(text, x, y));
  }
}
