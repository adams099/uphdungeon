package id.uphdungeon.entity;

import id.uphdungeon.GamePanel;
import id.uphdungeon.entity.animation.Animation;
import id.uphdungeon.entity.animation.EnemyAnimationState;
import id.uphdungeon.entity.animation.EnemySpriteManager;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

// Abstract base class for all animated enemies
public abstract class EnemyAnimated extends Enemy {

  // Current animation playing
  protected Animation currentAnimation;

  // Current logical state (walk, attack, death, etc.)
  protected EnemyAnimationState currentState;

  // True while attack animation is playing — blocks walk/idle transitions
  protected boolean attackAnimationPending = false;

  // True while death animation is playing — blocks all other transitions
  protected boolean deathAnimationPlaying = false;

  // True after the enemy has moved at least once — switches idle behavior
  protected boolean hasMovedOnce = false;

  // Holds the last rendered walk frame so enemy keeps facing direction when stopped
  protected BufferedImage lastWalkFrame = null;

  // Constructor EnemyAnimated
  public EnemyAnimated(GamePanel gamePanel, int startX, int startY, int dirX, int dirY, Color color,
      int maxHealth, int minDamage, int maxDamage) {
    super(gamePanel, startX, startY, dirX, dirY, color, maxHealth, minDamage, maxDamage);
  }

  // Returns the sprite manager for this enemy type
  protected abstract EnemySpriteManager getSpriteManager();

  // Initialises currentState and currentAnimation to the spawn default
  protected void initAnimation() {
    currentState = getSpriteManager().getSpawnState();
    currentAnimation = getSpriteManager().getAnimation(currentState);
  }

  // Triggers the attack animation based on the target position
  public void triggerAttackAnimation(Entity target) {
    boolean attackLeft = target.x < x;
    EnemyAnimationState attackState = attackLeft ? getSpriteManager().getAttackLeftState()
        : getSpriteManager().getAttackRightState();
    transitionTo(attackState);
    attackAnimationPending = true;
  }

  // Transitions to a new animation state, resetting the animation if the state changes
  protected void transitionTo(EnemyAnimationState newState) {
    if (newState == currentState) return;
    currentState = newState;
    currentAnimation = getSpriteManager().getAnimation(newState);
    currentAnimation.reset();
  }

  // Determines the walk animation state based on the direction
  protected EnemyAnimationState resolveWalkState() {
    int dx = targetX - x;
    int dy = targetY - y;

    if (Math.abs(dx) >= Math.abs(dy)) {
      return dx < 0 ? getSpriteManager().getWalkLeftState()
          : getSpriteManager().getWalkRightState();
    } else {
      return dy < 0 ? getSpriteManager().getWalkUpState() : getSpriteManager().getWalkDownState();
    }
  }

  // Draws the current animation frame based on the enemy state and movement
  protected BufferedImage resolveDrawFrame() {
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
    // Before first move, show spawn default frame
    return currentAnimation.getCurrentFrame();
  }

  // Entity overrides
  // Priority: death > attack > walk > idle hold
  @Override
  public void updateAnimations() {
    super.updateAnimations(); // handles damage indicators + calls updateFading()

    // Death animation takes full priority over everything else
    if (deathAnimationPlaying) {
      currentAnimation.update();
      if (currentAnimation.isFinished()) {
        deathAnimationPlaying = false;
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
      // Cache last walk frame so enemy holds direction when it stops
      lastWalkFrame = currentAnimation.getCurrentFrame();
    }
  }

  // Handles fading out after death animation finishes
  @Override
  public void updateFading() {
    if (isDead && !deathAnimationPlaying && !currentState.isDeath()) {
      // First time entering death: start the death animation
      transitionTo(getSpriteManager().getDeathState());
      deathAnimationPlaying = true;
      // isFading stays true GamePanel removing this entity
      return;
    }

    // Only start decrementing alpha after the death animation is done
    if (isFading && !deathAnimationPlaying) {
      alpha -= 0.02f;
      if (alpha < 0) {
        alpha = 0;
        isFading = false;
      }
    }
  }

  // Draws the sprite enemy with alpha fading, health bar, and damage indicators
  @Override
  public void draw(Graphics2D g2) {
    if (!isDead || isFading || deathAnimationPlaying) {
      if (isFading && !deathAnimationPlaying) {
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
        int offset = (gamePanel.tileSize - spriteSize) / 2;
        g2.setColor(color);
        g2.fillRect(x + offset, y + offset, spriteSize, spriteSize);
      }

      if (!isDead) {
        drawHealthBar(g2);
      }

      if (isFading && !deathAnimationPlaying) {
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
      }
    }

    drawIndicators(g2);
  }

  // Draws a health bar above the enemy, showing current HP proportionally
  private void drawHealthBar(Graphics2D g2) {
    int barWidth = gamePanel.tileSize;
    int barHeight = 4;
    int barX = x;
    int barY = y - barHeight - 2;

    // health bar
    g2.setColor(Color.RED);
    g2.fillRect(barX, barY, barWidth, barHeight);
    g2.setColor(Color.GREEN);
    int hpWidth = (int) (((double) health / maxHealth) * barWidth);
    g2.fillRect(barX, barY, hpWidth, barHeight);
  }
}
