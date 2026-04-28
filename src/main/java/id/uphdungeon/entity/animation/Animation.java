package id.uphdungeon.entity.animation;

import java.awt.image.BufferedImage;

// Manage Sprite for Frame animation
public class Animation {
  private final BufferedImage[] frames;
  private final int frameDurationTicks;
  private int currentFrameIndex = 0;
  private int tickCounter = 0;
  private final boolean loop;

  // Constructor for Make new animation
  public Animation(BufferedImage[] frames, int frameDurationTicks, boolean loop) {
    if (frames == null || frames.length == 0) {
      throw new IllegalArgumentException("Animation must have at least one frame.");
    }
    this.frames = frames;
    this.frameDurationTicks = frameDurationTicks;
    this.loop = loop;
  }

  // Method for update animation frame based on tick count
  public void update() {
    tickCounter++;
    if (tickCounter >= frameDurationTicks) {
      tickCounter = 0;
      if (currentFrameIndex < frames.length - 1) {
        currentFrameIndex++;
      } else if (loop) {
        currentFrameIndex = 0;
      }
    }
  }

  public BufferedImage getCurrentFrame() {
    return frames[currentFrameIndex];
  }

  // Method for reset animation to first frame
  public void reset() {
    currentFrameIndex = 0;
    tickCounter = 0;
  }

  // Method give true if animation has last frame and is not looping
  public boolean isFinished() {
    return !loop && currentFrameIndex == frames.length - 1;
  }
}
