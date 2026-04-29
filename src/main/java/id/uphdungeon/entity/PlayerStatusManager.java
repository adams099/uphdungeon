package id.uphdungeon.entity;

import java.awt.Color;
import id.uphdungeon.GamePanel;

public class PlayerStatusManager {
  private int level = 1;
  private int experience = 0;
  private final Player player;
  private final GamePanel gamePanel;

  public PlayerStatusManager(Player player, GamePanel gamePanel) {
    this.player = player;
    this.gamePanel = gamePanel;
  }

  public void addExperience(int amount) {
    experience += amount;
    gamePanel.addLogMessage("Got " + amount + " XP", Color.LIGHT_GRAY);
    checkLevelUp();
  }

  private void checkLevelUp() {
    int requiredExp = getRequiredExp(level + 1);
    if (requiredExp != -1 && experience >= requiredExp) {
      levelUp();
    }
  }

  private int getRequiredExp(int nextLevel) {
    switch (nextLevel) {
      case 2:
        return 50;
      case 3:
        return 80;
      case 4:
        return 100;
      case 5:
        return 120;
      default:
        return -1;
    }
  }

  private void levelUp() {
    level++;
    experience = 0;

    // setiap naik level, max health nambah 5
    player.maxHealth += 5;
    // hp jadi maximum lagi
    player.health = player.maxHealth;

    // kalau level berikutnya angka genap, min damage nambah 1
    if (level % 2 == 0) {
      player.minDamage += 1;
    } else {
      // kalau level berikutnya angka gajnil, max damage nambah 1
      player.maxDamage += 1;
    }

    gamePanel.addLogMessage("LEVEL UP! You are now level " + level, Color.YELLOW);
    gamePanel.addLogMessage("Max HP +5, damage increased!", Color.YELLOW);
  }

  public int getLevel() {
    return level;
  }

  public int getExperience() {
    return experience;
  }

  public int getNextLevelExp() {
    return getRequiredExp(level + 1);
  }
}
