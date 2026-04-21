package id.uphdungeon.entity.animation;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import javax.imageio.ImageIO;

// Loader and manage rat animations
public class RatSpriteManager {
    // Tick per frame 60 FPS: if 20 ticks = 60 / 20  = 3 fps
    private static final int WALK_FRAME_DURATION   = 10;
    private static final int ATTACK_FRAME_DURATION = 8;
    private static final int DEATH_FRAME_DURATION  = 20;

    private final Map<RatAnimationState, Animation> animations = new EnumMap<>(RatAnimationState.class);

    // Constructor loads all rat animations
    public RatSpriteManager() {
        loadAnimations();
    }

    // Default Animation if the requested state is missing
    public Animation getAnimation(RatAnimationState state) {
        return animations.getOrDefault(state, animations.get(RatAnimationState.WALK_DOWN));
    }

    // Load all rat sprite animations
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

        animations.put(RatAnimationState.ATTACK_RIGHT,
                new Animation(frames("AR.png", "AR.png"), ATTACK_FRAME_DURATION, false));

        animations.put(RatAnimationState.ATTACK_LEFT,
                new Animation(frames("AL.png", "AL.png"), ATTACK_FRAME_DURATION, false));

        // Death plays once (loop=false), holds last frame until Enemy fade begins
        animations.put(RatAnimationState.DEATH,
                new Animation(frames("D1.png", "D2.png"), DEATH_FRAME_DURATION, false));
    }

    // Method for loading frames from resource files
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