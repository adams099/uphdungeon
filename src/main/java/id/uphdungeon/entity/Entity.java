package id.uphdungeon.entity;

import id.uphdungeon.GamePanel;
import id.uphdungeon.ui.DamageIndicator;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Entity {
  protected GamePanel gamePanel;
  public int x, y;
  public int targetX, targetY;
  public boolean isMoving = false;
  protected Runnable intent = null;

  // kecepatan animasi
  public int speed = 10;
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

  public void executeAction(GamePanel gamePanel) {
    if (intent != null) {
      intent.run();
      intent = null;
    }
  }

  public void update() {
    if (isMoving && !isDead) {
      if (x < targetX) x += speed;
      if (x > targetX) x -= speed;
      if (y < targetY) y += speed;
      if (y > targetY) y -= speed;

      // Snap to target if very close to prevent jitter
      if (Math.abs(x - targetX) < speed && Math.abs(y - targetY) < speed) {
        x = targetX;
        y = targetY;
        isMoving = false;
      }
    }
  }

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

  public void updateFading() {
    if (isFading) {
      alpha -= 0.02f;
      if (alpha < 0) {
        alpha = 0;
        isFading = false;
      }
    }
  }

  public void updateAnimations() {
    updateIndicators();
    updateFading();
  }

  public void drawIndicators(Graphics2D g2) {
    for (DamageIndicator di : damageIndicators) {
      di.draw(g2);
    }
  }

  protected void drawHealthBar(Graphics2D g2) {
    int barWidth = gamePanel.tileSize;
    int barHeight = 4;
    int barX = x;
    int barY = y - barHeight - 2;

    g2.setColor(Color.RED);
    g2.fillRect(barX, barY, barWidth, barHeight);
    g2.setColor(Color.GREEN);
    int hpWidth = (int) (((double) health / maxHealth) * barWidth);
    g2.fillRect(barX, barY, hpWidth, barHeight);
  }

  protected int getGridIndex(int x, int y) {
    return (x / gamePanel.tileSize) + (y / gamePanel.tileSize) * gamePanel.maxScreenCol;
  }

  protected int getXFromIndex(int index) {
    return (index % gamePanel.maxScreenCol) * gamePanel.tileSize;
  }

  protected int getYFromIndex(int index) {
    return (index / gamePanel.maxScreenCol) * gamePanel.tileSize;
  }

  public void attack(Entity target) {
    Random rand = new Random();
    int damage = rand.nextInt(maxDamage - minDamage + 1) + minDamage;
    target.health -= damage;

    int indicatorX = target.x + (gamePanel.tileSize / 2);
    int indicatorY = target.y;
    target.addDamageIndicator(String.valueOf(damage), indicatorX, indicatorY);

    String message = this.getClass().getSimpleName() + " attacked "
        + target.getClass().getSimpleName() + " for " + damage + " damage!";
    Color color = (this instanceof Player) ? Color.LIGHT_GRAY : Color.ORANGE;
    gamePanel.addLogMessage(message, color);

    if (target.health <= 0) {
      target.isDead = true;
      target.isFading = true;
      gamePanel.addLogMessage(target.getClass().getSimpleName() + " died!", Color.RED);

      if (this instanceof Player && target instanceof Enemy) {
        ((Player) this).getStatusManager().addExperience(((Enemy) target).getExpReward());
      }
    }
  }

  public void addDamageIndicator(String text, int x, int y) {
    damageIndicators.add(new DamageIndicator(text, x, y));
  }
}
