package id.uphdungeon.entity.animation;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class DragonSpriteManager implements EnemySpriteManager {
  private static final int WALK_FRAME_DURATION = 12;
  private static final int ATTACK_FRAME_DURATION = 10;
  private static final int DEATH_FRAME_DURATION = 25;

  private final Map<DragonAnimationState, Animation> animations =
      new EnumMap<>(DragonAnimationState.class);

  public DragonSpriteManager() {
    loadAnimations();
  }

  @Override
  public Animation getAnimation(EnemyAnimationState state) {
    return animations.getOrDefault(state, animations.get(DragonAnimationState.WALK_DOWN));
  }

  @Override
  public EnemyAnimationState getSpawnState() {
    return DragonAnimationState.WALK_DOWN;
  }

  @Override
  public EnemyAnimationState getDeathState() {
    return DragonAnimationState.DEATH;
  }

  @Override
  public EnemyAnimationState getWalkLeftState() {
    return DragonAnimationState.WALK_LEFT;
  }

  @Override
  public EnemyAnimationState getWalkRightState() {
    return DragonAnimationState.WALK_RIGHT;
  }

  @Override
  public EnemyAnimationState getWalkUpState() {
    return DragonAnimationState.WALK_UP;
  }

  @Override
  public EnemyAnimationState getWalkDownState() {
    return DragonAnimationState.WALK_DOWN;
  }

  @Override
  public EnemyAnimationState getAttackLeftState() {
    return DragonAnimationState.ATTACK_LEFT;
  }

  @Override
  public EnemyAnimationState getAttackRightState() {
    return DragonAnimationState.ATTACK_RIGHT;
  }

  private void loadAnimations() {
    animations.put(DragonAnimationState.WALK_LEFT,
        new Animation(frames("L1.png", "L2.png"), WALK_FRAME_DURATION, true));

    animations.put(DragonAnimationState.WALK_RIGHT,
        new Animation(frames("R1.png", "R2.png"), WALK_FRAME_DURATION, true));

    animations.put(DragonAnimationState.WALK_UP,
        new Animation(frames("U1.png", "U2.png"), WALK_FRAME_DURATION, true));

    animations.put(DragonAnimationState.WALK_DOWN,
        new Animation(frames("B1.png", "B2.png"), WALK_FRAME_DURATION, true));

    animations.put(DragonAnimationState.ATTACK_RIGHT,
        new Animation(frames("AR.png", "AR.png"), ATTACK_FRAME_DURATION, false));

    animations.put(DragonAnimationState.ATTACK_LEFT,
        new Animation(frames("AL.png", "AL.png"), ATTACK_FRAME_DURATION, false));

    animations.put(DragonAnimationState.DEATH,
        new Animation(frames("D1.png", "D2.png"), DEATH_FRAME_DURATION, false));
  }

  private BufferedImage[] frames(String... fileNames) {
    BufferedImage[] images = new BufferedImage[fileNames.length];
    for (int i = 0; i < fileNames.length; i++) {
      String path = "/sprites/dragon/" + fileNames[i];
      try {
        BufferedImage img = ImageIO.read(DragonSpriteManager.class.getResourceAsStream(path));
        if (img == null) {
          throw new IOException("Resource not found: " + path);
        }
        images[i] = img;
      } catch (IOException e) {
        throw new RuntimeException("Failed to load sprite: " + path, e);
      }
    }
    return images;
  }
}
