package id.uphdungeon.entity.animation;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import javax.imageio.ImageIO;

// Loader and manager for Rat enemy animations
public class RatSpriteManager implements EnemySpriteManager {
  // Tick per frame at 60 FPS: 10 ticks = 6 fps animation speed
  private static final int WALK_FRAME_DURATION = 10;
  private static final int ATTACK_FRAME_DURATION = 8;
  private static final int DEATH_FRAME_DURATION = 20;

  private final Map<RatAnimationState, Animation> animations =
      new EnumMap<>(RatAnimationState.class);

  // Constructor loads all rat animations
  public RatSpriteManager() {
    loadAnimations();
  }

  // Falls back to WALK_DOWN if the requested state is missing
  @Override
  public Animation getAnimation(EnemyAnimationState state) {
    return animations.getOrDefault(state, animations.get(RatAnimationState.WALK_DOWN));
  }

  // For mapping EnemyAnimationState to RatAnimationState
  // used by EnemyEntity to determine which animation to play
  @Override
  public EnemyAnimationState getSpawnState() {
    return RatAnimationState.WALK_DOWN;
  }

  @Override
  public EnemyAnimationState getDeathState() {
    return RatAnimationState.DEATH;
  }

  @Override
  public EnemyAnimationState getWalkLeftState() {
    return RatAnimationState.WALK_LEFT;
  }

  @Override
  public EnemyAnimationState getWalkRightState() {
    return RatAnimationState.WALK_RIGHT;
  }

  @Override
  public EnemyAnimationState getWalkUpState() {
    return RatAnimationState.WALK_UP;
  }

  @Override
  public EnemyAnimationState getWalkDownState() {
    return RatAnimationState.WALK_DOWN;
  }

  @Override
  public EnemyAnimationState getAttackLeftState() {
    return RatAnimationState.ATTACK_LEFT;
  }

  @Override
  public EnemyAnimationState getAttackRightState() {
    return RatAnimationState.ATTACK_RIGHT;
  }

  // Load all sprite Rat animations
  private void loadAnimations() {
    animations.put(RatAnimationState.WALK_LEFT,
        new Animation(frames("L1.png", "L2.png"), WALK_FRAME_DURATION, true));

    animations.put(RatAnimationState.WALK_RIGHT,
        new Animation(frames("R1.png", "R2.png"), WALK_FRAME_DURATION, true));

    animations.put(RatAnimationState.WALK_UP,
        new Animation(frames("U1.png", "U2.png"), WALK_FRAME_DURATION, true));

    animations.put(RatAnimationState.WALK_DOWN,
        new Animation(frames("B2.png", "B1.png"), WALK_FRAME_DURATION, true));

    animations.put(RatAnimationState.ATTACK_RIGHT,
        new Animation(frames("AR.png", "AR.png"), ATTACK_FRAME_DURATION, false));

    animations.put(RatAnimationState.ATTACK_LEFT,
        new Animation(frames("AL.png", "AL.png"), ATTACK_FRAME_DURATION, false));

    animations.put(RatAnimationState.DEATH,
        new Animation(frames("D1.png", "D2.png"), DEATH_FRAME_DURATION, false));
  }

  // Method for loading frames from resource files
  private BufferedImage[] frames(String... fileNames) {
    BufferedImage[] images = new BufferedImage[fileNames.length];
    for (int i = 0; i < fileNames.length; i++) {
      String path = "/sprites/rat/" + fileNames[i];
      try {
        BufferedImage img = ImageIO.read(RatSpriteManager.class.getResourceAsStream(path));
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
