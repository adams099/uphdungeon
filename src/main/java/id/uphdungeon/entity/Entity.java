package id.uphdungeon.entity;

import id.uphdungeon.GamePanel;
import java.awt.Graphics2D;
import java.util.Random;

public abstract class Entity {
  public int x, y;
  public int speed;
  public int initiative;

  public int maxHealth;
  public int health;
  public int minDamage;
  public int maxDamage;
  public boolean isDead = false;

  public abstract void determineIntent(GamePanel gamePanel);

  public abstract void executeAction(GamePanel gamePanel);

  public abstract void update();

  public abstract void draw(Graphics2D g2);

  public void attack(Entity target) {
    Random rand = new Random();
    int damage = rand.nextInt(maxDamage - minDamage + 1) + minDamage;
    target.health -= damage;

    System.out.println(
        this.getClass().getSimpleName()
            + " attacked "
            + target.getClass().getSimpleName()
            + " for "
            + damage
            + " damage! ("
            + target.health
            + "/"
            + target.maxHealth
            + " HP left)");

    if (target.health <= 0) {
      target.isDead = true;
      System.out.println(target.getClass().getSimpleName() + " died!");
    }
  }
}
