package id.uphdungeon.entity;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import id.uphdungeon.GamePanel;
import id.uphdungeon.entity.animation.Animation;
import id.uphdungeon.entity.animation.RatAnimationState;
import id.uphdungeon.entity.animation.RatSpriteManager;

// Controller class for Rat enemy
public class Rat extends Enemy {
    
    private boolean hasMovedOnce = false; // For IDLE to B2 frame
    private BufferedImage lastWalkFrame = null;
    private boolean attackAnimationPending = false; // For attack frame until animation finishes
    private boolean deathAnimationPlaying = false; // For blocking all other animations
    
    // Animation state
    private final RatSpriteManager spriteManager = new RatSpriteManager();
    private RatAnimationState currentState       = RatAnimationState.WALK_DOWN;
    private Animation currentAnimation;
    
    // Constructor for rat
    public Rat(GamePanel gamePanel, int startX, int startY, int dirX, int dirY) {
        super(gamePanel, startX, startY, dirX, dirY, Color.DARK_GRAY, 10, 1, 2);
        // Initialise at frame 0 of WALK_DOWN which is B2.png (spawn default)
        currentAnimation = spriteManager.getAnimation(RatAnimationState.WALK_DOWN);
    }

    // Method to trigger attack animation based on target position
    public void triggerAttackAnimation(Entity target) {
        boolean attackLeft = target.x < x;
        RatAnimationState attackState = attackLeft
                ? RatAnimationState.ATTACK_LEFT
                : RatAnimationState.ATTACK_RIGHT;
        transitionTo(attackState);
        attackAnimationPending = true;
    }

    // Method walk animation state based on movement direction
    private RatAnimationState resolveWalkState() {
        int dx = targetX - x;
        int dy = targetY - y;

        // Horizontal movement takes visual priority over vertical
        if (Math.abs(dx) >= Math.abs(dy)) {
            return dx < 0 ? RatAnimationState.WALK_LEFT : RatAnimationState.WALK_RIGHT;
        } else {
            return dy < 0 ? RatAnimationState.WALK_UP : RatAnimationState.WALK_DOWN;
        }
    }

    // Switch to a new animation state n resetting frame 0
    private void transitionTo(RatAnimationState newState) {
        if (newState == currentState) return;
        currentState     = newState;
        currentAnimation = spriteManager.getAnimation(newState);
        currentAnimation.reset();
    }

    // Entity overrides
    // Method called at the start of each turn to decide what the Rat will do
    @Override
    public void determineIntent(GamePanel gamePanel) {
        Player player = gamePanel.getPlayer();

        boolean isAdjacentToPlayer = false;
        if (!player.isDead) {
            int dx = Math.abs(x - player.x);
            int dy = Math.abs(y - player.y);
            isAdjacentToPlayer = dx <= gamePanel.tileSize
                    && dy <= gamePanel.tileSize
                    && (dx != 0 || dy != 0);
        }

        // Let Enemy handle the full intent logic (movement + attack scheduling)
        super.determineIntent(gamePanel);

        // If Enemy scheduled attack and player is adjacent then trigger the attack animation immediately
        if (isAdjacentToPlayer && !isDead) {
            triggerAttackAnimation(player);
        }
    }

    // Called every frame
    // Priority order: death > attack > walk > idle
    @Override
    public void updateAnimations() {
        super.updateAnimations(); // handles damage indicators + calls updateFading()

        // Death animation takes full priority over everything else
        if (deathAnimationPlaying) {
            currentAnimation.update();
            if (currentAnimation.isFinished()) {
                // Death sprite done then Enemy fade system
                deathAnimationPlaying = false;
                isFading = true;
            }
            return;
        }

        // Attack animation plays out fully before returning to walk/idle
        if (attackAnimationPending) {
            currentAnimation.update();
            if (currentAnimation.isFinished()) {
                attackAnimationPending = false;
            }
            return;
        }

        if (isMoving) {
            transitionTo(resolveWalkState());
            hasMovedOnce = true;
            currentAnimation.update();
            // Cache last walk frame so Rat holds direction when it stops
            lastWalkFrame = currentAnimation.getCurrentFrame();
        }
    }

    // Dead animation first then fade out
    @Override
    public void updateFading() {
        if (isDead && !deathAnimationPlaying && !isFading) {
            transitionTo(RatAnimationState.DEATH);
            deathAnimationPlaying = true;
            return;
        }

        if (isFading) {
            alpha -= 0.02f;
            if (alpha < 0) {
                alpha    = 0;
                isFading = false;
            }
        }
    }

    // For drawing the rat to decide which frame to show based on the current state
    @Override
    public void draw(Graphics2D g2) {
        if (!isDead || isFading || deathAnimationPlaying) {
            if (isFading) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            }

            BufferedImage frame = resolveDrawFrame();

            if (frame != null) {
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                g2.drawImage(frame, x, y, gamePanel.tileSize, gamePanel.tileSize, null);
            } else {
                // Display colored rectangle if sprites are missing
                int spriteSize = (int) (gamePanel.tileSize * 0.8);
                int offset     = (gamePanel.tileSize - spriteSize) / 2;
                g2.setColor(color);
                g2.fillRect(x + offset, y + offset, spriteSize, spriteSize);
            }

            if (!isDead) {
                drawHealthBar(g2);
            }

            if (isFading) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }
        }

        drawIndicators(g2);
    }

    // Method to determine which frame to draw based on current state and animation
    private BufferedImage resolveDrawFrame() {
        if (attackAnimationPending || deathAnimationPlaying) {
            return currentAnimation.getCurrentFrame();
        }

        if (isMoving) {
            return currentAnimation.getCurrentFrame();
        }

        // After first move, hold last direction frame as idle
        if (hasMovedOnce && lastWalkFrame != null) {
            return lastWalkFrame;
        }

        // Before first move, show spawn default (B2.png = frame 0 of WALK_DOWN)
        return currentAnimation.getCurrentFrame();
    }

    private void drawHealthBar(Graphics2D g2) {
        int barWidth  = gamePanel.tileSize;
        int barHeight = 4;
        int barX      = x;
        int barY      = y - barHeight - 2;

        // health bar
        g2.setColor(Color.RED);
        g2.fillRect(barX, barY, barWidth, barHeight);
        g2.setColor(Color.GREEN);
        int hpWidth = (int) (((double) health / maxHealth) * barWidth);
        g2.fillRect(barX, barY, hpWidth, barHeight);
    }
}