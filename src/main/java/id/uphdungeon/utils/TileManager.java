package id.uphdungeon.utils;

import id.uphdungeon.GamePanel;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

// Tile class manages the dungeon background
public class TileManager {
  private final GamePanel gamePanel;

  // For drawn as the background
  private BufferedImage floorMap;

  // Constructor loads the floor map image
  public TileManager(GamePanel gamePanel) {
    this.gamePanel = gamePanel;
    loadFloorMap();
  }

  // Draw the floor map as the dungeon background
  // Call this first in paintComponent, before grid lines and entities
  public void draw(Graphics2D g2) {
    g2.drawImage(floorMap, 0, 0, gamePanel.screenWidth, gamePanel.screenHeight, null);
  }

  // Returns true if the given grid position blocks movement.
  // All floor tiles are passable — wall tiles added later will return true.
  public boolean hasCollision(int col, int row) {
    if (col < 0 || col >= gamePanel.maxScreenCol) return true;
    if (row < 0 || row >= gamePanel.maxScreenRow) return true;
    return false;
  }

  // Load the single full-map floor image from resources
  private void loadFloorMap() {
    String path = "/sprites/dungeon/floor_map.png";
    try {
      BufferedImage img = ImageIO.read(TileManager.class.getResourceAsStream(path));
      if (img == null) {
        throw new IOException("Resource not found: " + path);
      }
      floorMap = img;
    } catch (IOException e) {
      // Fail loudly during development — missing map image should never be silent.
      throw new RuntimeException("Failed to load floor map: " + path, e);
    }
  }
}
