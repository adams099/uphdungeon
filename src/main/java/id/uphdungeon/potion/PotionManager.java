package id.uphdungeon.potion;

import id.uphdungeon.GamePanel;
import id.uphdungeon.entity.Entity;
import id.uphdungeon.entity.Player;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

// Manages the single active potion in the dungeon
public class PotionManager {
  // Spawn chance for Greater Potion 12/100
  private static final int GREATER_POTION_CHANCE = 12;

  private final GamePanel gamePanel;
  private final Random random = new Random();
  private Potion potion;

  // Constructor creates and spawns the first potion immediately
  public PotionManager(GamePanel gamePanel) {
    this.gamePanel = gamePanel;
    spawnAtRandomTile();
  }

  // Called every frame to advance potion animation
  public void update() {
    potion.updateAnimation();
  }

  // Draws the potion on the map, between floor and entities layer
  public void draw(Graphics2D g2) {
    potion.draw(g2);
  }

  // Called once per completed initiative turn by GamePanel.
  public void tickRespawn() {
    if (potion.tickRespawn()) {
      spawnAtRandomTile();
      gamePanel.addLogMessage("A potion appeared!", Color.MAGENTA);
    }
  }

  // Confirm if there is active potion at given pixel position (used for pickup)
  public boolean isPotionAt(int x, int y) {
    return potion.isActive && potion.x == x && potion.y == y;
  }

  // Called when Player picks up the potion
  public void onPlayerPickup(Player player) {
    int healthBefore = player.health;
    potion.applyEffect(player);
    int healed = player.health - healthBefore;

    player.triggerConsumeAnimation();
    potion.pickup();

    String msg = "Player picked potion and recovered " + healed + " HP!";
    gamePanel.addLogMessage(msg, Color.GREEN);
  }

  // Spawn Logic
  private void spawnAtRandomTile() {
    final int MAX_ATTEMPTS = 100;

    // Potion type Greater Potion or Potion of Healing
    potion = createRandomPotion();

    // Try up to MAX_ATTEMPTS to find a random unoccupied tile
    for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
      int col = random.nextInt(gamePanel.maxScreenCol);
      int row = random.nextInt(gamePanel.maxScreenRow);
      int px = col * gamePanel.tileSize;
      int py = row * gamePanel.tileSize;

      if (!isTileOccupied(px, py)) {
        potion.spawn(px, py);
        return;
      }
    }
    // If no free tile found after MAX_ATTEMPTS, potion stays inactive
  }

  // Returns a new GreaterPotion (12% chance) or PotionOfHealing (88% chance)
  private Potion createRandomPotion() {
    if (random.nextInt(100) < GREATER_POTION_CHANCE) {
      return new GreaterPotion(gamePanel.tileSize);
    }
    return new PotionOfHealing(gamePanel.tileSize);
  }

  // Returns true if the given pixel position is occupied by any living entity
  private boolean isTileOccupied(int x, int y) {
    for (Entity e : gamePanel.entities) {
      if (!e.isDead && e.x == x && e.y == y) return true;
    }
    return false;
  }
}
