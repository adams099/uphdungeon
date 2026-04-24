package id.uphdungeon.entity.animation;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import javax.imageio.ImageIO;

// Loader and manager for Rat enemy animations.
// Implements EnemySpriteManager so EnemyAnimated can drive Rat animation
// without knowing RatAnimationState directly.
//
// Sprite files are expected at /sprites/rat/ (64x64 px):
//   L1.png, L2.png    — walk left
//   R1.png, R2.png    — walk right
//   U1.png, U2.png    — walk up
//   B2.png, B1.png    — walk down (B2.png is frame 0 = spawn default)
//   AR.png, AR2.png   — attack right (2 frames)
//   AL.png, AL2.png   — attack left (2 frames)
//   D1.png, D2.png    — death (plays once)
public class RatSpriteManager implements EnemySpriteManager {

    // Tick per frame at 60 FPS: 10 ticks = 6 fps animation speed
    private static final int WALK_FRAME_DURATION   = 10;
    private static final int ATTACK_FRAME_DURATION = 8;
    private static final int DEATH_FRAME_DURATION  = 20; // slower for dramatic effect

    private final Map<RatAnimationState, Animation> animations =
            new EnumMap<>(RatAnimationState.class);

    // Constructor loads all rat animations
    public RatSpriteManager() {
        loadAnimations();
    }

    // Returns the Animation for the given state.
    // Falls back to WALK_DOWN if the requested state is missing.
    @Override
    public Animation getAnimation(EnemyAnimationState state) {
        return animations.getOrDefault(state, animations.get(RatAnimationState.WALK_DOWN));
    }

    @Override public EnemyAnimationState getSpawnState()       { return RatAnimationState.WALK_DOWN;    }
    @Override public EnemyAnimationState getDeathState()       { return RatAnimationState.DEATH;        }
    @Override public EnemyAnimationState getWalkLeftState()    { return RatAnimationState.WALK_LEFT;    }
    @Override public EnemyAnimationState getWalkRightState()   { return RatAnimationState.WALK_RIGHT;   }
    @Override public EnemyAnimationState getWalkUpState()      { return RatAnimationState.WALK_UP;      }
    @Override public EnemyAnimationState getWalkDownState()    { return RatAnimationState.WALK_DOWN;    }
    @Override public EnemyAnimationState getAttackLeftState()  { return RatAnimationState.ATTACK_LEFT;  }
    @Override public EnemyAnimationState getAttackRightState() { return RatAnimationState.ATTACK_RIGHT; }

    // Load all rat sprite animations from /sprites/rat/
    private void loadAnimations() {
        animations.put(RatAnimationState.WALK_LEFT,
                new Animation(frames("L1.png", "L2.png"), WALK_FRAME_DURATION, true));

        animations.put(RatAnimationState.WALK_RIGHT,
                new Animation(frames("R1.png", "R2.png"), WALK_FRAME_DURATION, true));

        animations.put(RatAnimationState.WALK_UP,
                new Animation(frames("U1.png", "U2.png"), WALK_FRAME_DURATION, true));

        // B2.png is listed first so frame index 0 shows the spawn default sprite
        animations.put(RatAnimationState.WALK_DOWN,
                new Animation(frames("B2.png", "B1.png"), WALK_FRAME_DURATION, true));

        // Attack plays 2 distinct frames then holds (loop=false)
        animations.put(RatAnimationState.ATTACK_RIGHT,
                new Animation(frames("AR.png", "AR.png"), ATTACK_FRAME_DURATION, false));

        animations.put(RatAnimationState.ATTACK_LEFT,
                new Animation(frames("AL.png", "AL.png"), ATTACK_FRAME_DURATION, false));

        // Death plays once (loop=false), holds last frame until alpha fade begins
        animations.put(RatAnimationState.DEATH,
                new Animation(frames("D1.png", "D2.png"), DEATH_FRAME_DURATION, false));
    }

    // Load one or more sprite files from /sprites/rat/ and return as BufferedImage array
    private BufferedImage[] frames(String... fileNames) {
        BufferedImage[] images = new BufferedImage[fileNames.length];
        for (int i = 0; i < fileNames.length; i++) {
            String path = "/sprites/rat/" + fileNames[i];
            try {
                BufferedImage img = ImageIO.read(
                        RatSpriteManager.class.getResourceAsStream(path));
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