package id.uphdungeon.entity.animation;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import javax.imageio.ImageIO;

// Loader and manage player animations
public class PlayerSpriteManager {
    // Tick per frame 60 FPS: if 10 ticks =60 / 10 = 6 fps
    private static final int WALK_FRAME_DURATION = 10;
    private static final int ATTACK_FRAME_DURATION = 8;
    private static final int IDLE_FRAME_DURATION = 30;

    // Enumaration map for PlayerAnimationState
    private final Map<PlayerAnimationState, Animation> animations = new EnumMap<>(PlayerAnimationState.class);

    // construc to load the animations
    public PlayerSpriteManager() {
        loadAnimations();
    }

    // Default Animation if the requested state is missing
    public Animation getAnimation(PlayerAnimationState state) {
        return animations.getOrDefault(state, animations.get(PlayerAnimationState.IDLE));
    }

    // Load all sprite player animations
    private void loadAnimations() {
        animations.put(PlayerAnimationState.IDLE,
                new Animation(frames("1.png"), IDLE_FRAME_DURATION, true));

        animations.put(PlayerAnimationState.WALK_LEFT,
                new Animation(frames("L1.png", "L2.png"), WALK_FRAME_DURATION, true));

        animations.put(PlayerAnimationState.WALK_RIGHT,
                new Animation(frames("R1.png", "R2.png"), WALK_FRAME_DURATION, true));

        animations.put(PlayerAnimationState.WALK_UP,
                new Animation(frames("U1.png", "U2.png"), WALK_FRAME_DURATION, true));

        animations.put(PlayerAnimationState.WALK_DOWN,
                new Animation(frames("D1.png", "D2.png"), WALK_FRAME_DURATION, true));

        animations.put(PlayerAnimationState.ATTACK_LEFT,
                new Animation(frames("AL.png", "AL.png"), ATTACK_FRAME_DURATION, false));

        animations.put(PlayerAnimationState.ATTACK_RIGHT,
                new Animation(frames("AR.png", "AR.png"), ATTACK_FRAME_DURATION, false));
    }

    // Method for loading frames from resource files
    private BufferedImage[] frames(String... fileNames) {
        BufferedImage[] images = new BufferedImage[fileNames.length];
        for (int i = 0; i < fileNames.length; i++) {
            String path = "/sprites/player/" + fileNames[i];
            try {
                BufferedImage img = ImageIO.read(PlayerSpriteManager.class.getResourceAsStream(path));
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