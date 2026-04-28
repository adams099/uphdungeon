package id.uphdungeon.entity.animation;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

// Loads and manages potion animation frames
public class PotionSpriteManager {
  private static final int IDLE_FRAME_DURATION = 20;

  private final Animation idleAnimation;

  // construc to load the animations
  public PotionSpriteManager() {
    idleAnimation = new Animation(frames("P1.png", "P2.png"), IDLE_FRAME_DURATION, true);
  }

  // Returns the looping idle animation for the potion
  public Animation getIdleAnimation() {
    return idleAnimation;
  }

  // Method for loading frames from resource files
  private BufferedImage[] frames(String... fileNames) {
    BufferedImage[] images = new BufferedImage[fileNames.length];
    for (int i = 0; i < fileNames.length; i++) {
      String path = "/sprites/potion/" + fileNames[i];
      try {
        BufferedImage img = ImageIO.read(PotionSpriteManager.class.getResourceAsStream(path));
        if (img == null) {
          throw new IOException("Resource not found: " + path);
        }
        images[i] = img;
      } catch (IOException e) {
        // Fail loudly during development - missing sprites should never be silent.
        throw new RuntimeException("Failed to load sprite: " + path, e);
      }
    }
    return images;
  }
}
