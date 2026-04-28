package id.uphdungeon.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import id.uphdungeon.GamePanel;
import id.uphdungeon.KeyHandler;
import id.uphdungeon.entity.animation.Animation;
import id.uphdungeon.entity.animation.PlayerAnimationState;
import id.uphdungeon.entity.animation.PlayerSpriteManager;
import id.uphdungeon.utils.PathFinder;

// Controller for the player character
public class Player extends Entity {
  KeyHandler keyH;

  public boolean isMoving = false;
  public int targetX, targetY;
  private Runnable intent = null;
  private PathFinder.Path currentPath = null;
  private Entity targetEnemy = null;

  // Animation state
  private final PlayerSpriteManager spriteManager = new PlayerSpriteManager();
  private PlayerAnimationState currentState = PlayerAnimationState.IDLE;
  private Animation currentAnimation;
  private boolean attackAnimationPending = false; // for returning to idle
  private boolean facingLeft = false; // attack defualt direction Right
  private boolean consumeAnimationPending = false; // consume plays once then returns to idle
  private boolean hasMovedOnce = false; // tracks if player has ever moved to switch idle behavior
  private BufferedImage lastWalkFrame = null; // holds last walk frame to keep facing direction when
                                              // stopped

  // constructor for player, sets initial position, stats, and idle animation
  public Player(GamePanel gamePanel, KeyHandler keyH) {
    super(gamePanel);
    this.keyH = keyH;

    this.x = gamePanel.tileSize * 2;
    this.y = gamePanel.tileSize * 2;

    // comment dulu, handled di super biar lebih cepet
    // this.speed = 4;
    this.initiative = 10;

    this.maxHealth = 30;
    this.health = 30;
    this.minDamage = 2;
    this.maxDamage = 5;

    this.targetX = this.x;
    this.targetY = this.y;

    // Initialise with idle animation
    currentAnimation = spriteManager.getAnimation(PlayerAnimationState.IDLE);
  }

  // Method to trigger posisition atttack animation Right or Left
  public void triggerAttackAnimation(Entity target) {
    boolean attackLeft = (target.x < x) || (target.x == x && facingLeft);
    PlayerAnimationState attackState =
        attackLeft ? PlayerAnimationState.ATTACK_LEFT : PlayerAnimationState.ATTACK_RIGHT;
    transitionTo(attackState);
    attackAnimationPending = true;
  }

  // Method to trigger consume animation
  public void triggerConsumeAnimation() {
    transitionTo(PlayerAnimationState.CONSUME);
    consumeAnimationPending = true;
  }

  // Method direction walk animation
  private PlayerAnimationState resolveWalkState() {
    int dx = targetX - x;
    int dy = targetY - y;

    // Condition for facing direction: if horizontal distance is greater
    // than vertical face left/right and otherwise face up/down
    if (Math.abs(dx) >= Math.abs(dy)) {
      facingLeft = dx < 0;
      return facingLeft ? PlayerAnimationState.WALK_LEFT : PlayerAnimationState.WALK_RIGHT;
    } else {
      return dy < 0 ? PlayerAnimationState.WALK_UP : PlayerAnimationState.WALK_DOWN;
    }
  }

  // Method to handle walk animation transitions to Walk, Attack etc
  private void transitionTo(PlayerAnimationState newState) {
    if (newState == currentState) return;
    currentState = newState;
    currentAnimation = spriteManager.getAnimation(newState);
    currentAnimation.reset();
  }

  // Entity override
  // Called every frame by GamePanel.update() for update position
  // base on current path or target
  @Override
  public void update() {
    if (isMoving) {
      if (x < targetX) x += speed;
      if (x > targetX) x -= speed;
      if (y < targetY) y += speed;
      if (y > targetY) y -= speed;

      // Snap to target when close enough to prevent jitter
      if (Math.abs(x - targetX) < speed && Math.abs(y - targetY) < speed) {
        x = targetX;
        y = targetY;
        isMoving = false;
      }
    }
  }

  // Method update animation handles attack animation priority and walk direction
  @Override
  public void updateAnimations() {
    super.updateAnimations(); // handles damage indicators & fading

    // Attack has highest priority plays fully before anything else
    if (attackAnimationPending) {
      currentAnimation.update();
      if (currentAnimation.isFinished()) {
        attackAnimationPending = false;
        // Return to last walk frame if moved
        if (hasMovedOnce && lastWalkFrame != null) {
          transitionTo(PlayerAnimationState.IDLE);
        } else {
          transitionTo(PlayerAnimationState.IDLE);
        }
      }
      return;
    }

    // Consume has second priority plays fully before walk or idle
    if (consumeAnimationPending) {
      currentAnimation.update();
      if (currentAnimation.isFinished()) {
        consumeAnimationPending = false;
      }
      return;
    }

    if (isMoving) {
      transitionTo(resolveWalkState());
      hasMovedOnce = true;
      currentAnimation.update();
      // Cache last walk frame so Player holds direction when stopped
      lastWalkFrame = currentAnimation.getCurrentFrame();
    }
  }

  // Draw method handles mirroring walk-right, fallback rectangle, health bar, and
  // indicators
  @Override
  public void draw(Graphics2D g2) {
    if (isDead) {
      drawIndicators(g2);
      return;
    }

    BufferedImage frame = resolveDrawFrame();

    if (frame != null) {
      int drawX = x;
      int drawY = y;
      int size = gamePanel.tileSize;

      g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
          RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

      g2.drawImage(frame, drawX, drawY, size, size, null);
    } else {
      // Display white rectangle if sprites are missing
      int spriteSize = (int) (gamePanel.tileSize * 0.8);
      int offset = (gamePanel.tileSize - spriteSize) / 2;
      g2.setColor(Color.WHITE);
      g2.fillRect(x + offset, y + offset, spriteSize, spriteSize);
    }

    drawHealthBar(g2);
    drawIndicators(g2);
  }

  public void setPath(int col, int row) {
    int fromIndex = (x / gamePanel.tileSize) + (y / gamePanel.tileSize) * gamePanel.maxScreenCol;
    int toIndex = col + row * gamePanel.maxScreenCol;

    if (fromIndex == toIndex) return;

    // reset action
    currentPath = null;
    targetEnemy = null;

    Entity clickedEntity =
        gamePanel.getEntityAt(col * gamePanel.tileSize, row * gamePanel.tileSize);

    if (clickedEntity instanceof Enemy) {
      targetEnemy = clickedEntity;
    } else {
      boolean[] passable = getPassableMap();
      PathFinder.setMapSize(gamePanel.maxScreenCol, gamePanel.maxScreenRow);
      currentPath = PathFinder.find(fromIndex, toIndex, passable);
    }
  }

  private boolean[] getPassableMap() {
    boolean[] passable = new boolean[gamePanel.maxScreenCol * gamePanel.maxScreenRow];
    Arrays.fill(passable, true);
    for (Entity e : gamePanel.entities) {
      if (!e.isDead && e != this) {
        int index =
            (e.x / gamePanel.tileSize) + (e.y / gamePanel.tileSize) * gamePanel.maxScreenCol;
        if (index >= 0 && index < passable.length) passable[index] = false;
      }
    }
    return passable;
  }

  @Override
  public void determineIntent(GamePanel gamePanel) {
    if (intent != null || isMoving) return;

    if (keyH.moveTriggered || keyH.waitTriggered) {
      // cancel jalan pathfinding pake keyboard
      currentPath = null;
      targetEnemy = null;

      if (keyH.waitTriggered) {
        intent = () -> gamePanel.addLogMessage("Player waited.", Color.CYAN);

      } else if (keyH.moveTriggered) {
        int nextX = x;
        int nextY = y;

        if (keyH.wasUpPressed) nextY -= gamePanel.tileSize;
        if (keyH.wasDownPressed) nextY += gamePanel.tileSize;
        if (keyH.wasLeftPressed) nextX -= gamePanel.tileSize;
        if (keyH.wasRightPressed) nextX += gamePanel.tileSize;

        // cancel kalo pencet arahnya bertubrukan
        if (keyH.wasUpPressed && keyH.wasDownPressed) nextY = y;
        if (keyH.wasLeftPressed && keyH.wasRightPressed) nextX = x;

        // initiative start only if player changed position
        if (nextX != x || nextY != y) {
          Entity targetEntity = gamePanel.getEntityAt(nextX, nextY);

          if (targetEntity instanceof Enemy) {
            // if enemy exists attack enemy
            intent = () -> {
              triggerAttackAnimation(targetEntity);
              this.attack(targetEntity);
            };
          } else if (gamePanel.getPotionManager().isPotionAt(nextX, nextY)) {
            // potion tile — move there and consume
            final int px = nextX;
            final int py = nextY;
            this.targetX = px;
            this.targetY = py;
            intent = () -> {
              isMoving = true;
              gamePanel.getPotionManager().onPlayerPickup(this);
            };
          } else {
            this.targetX = nextX;
            this.targetY = nextY;
            intent = () -> isMoving = true;
          }
        }
      }

      keyH.consumeAction();

    } else if (targetEnemy != null) {
      if (targetEnemy.isDead) {
        targetEnemy = null;
        return;
      }

      int dx = Math.abs(x - targetEnemy.x);
      int dy = Math.abs(y - targetEnemy.y);

      if (dx <= gamePanel.tileSize && dy <= gamePanel.tileSize) {
        // adjacent, attack
        // setelah itu stop ngejar
        final Entity enemyToAttack = targetEnemy;
        intent = () -> {
          triggerAttackAnimation(enemyToAttack);
          this.attack(enemyToAttack);
        };
        targetEnemy = null;
      } else {
        // not adjacent, one step to enemy
        boolean[] passable = getPassableMap();
        // temporarily make target tile passable
        // so we can move there
        int targetIndex = (targetEnemy.x / gamePanel.tileSize)
            + (targetEnemy.y / gamePanel.tileSize) * gamePanel.maxScreenCol;
        if (targetIndex >= 0 && targetIndex < passable.length) {
          passable[targetIndex] = true;
        }

        int fromIndex =
            (x / gamePanel.tileSize) + (y / gamePanel.tileSize) * gamePanel.maxScreenCol;
        PathFinder.setMapSize(gamePanel.maxScreenCol, gamePanel.maxScreenRow);
        int nextIndex = PathFinder.getStep(fromIndex, targetIndex, passable);

        if (nextIndex != -1) {
          int nextX = (nextIndex % gamePanel.maxScreenCol) * gamePanel.tileSize;
          int nextY = (nextIndex / gamePanel.maxScreenCol) * gamePanel.tileSize;

          Entity blocking = gamePanel.getEntityAt(nextX, nextY);
          if (blocking != null && blocking != targetEnemy) {
            // pathfinder blocked entity lain
            targetEnemy = null;
          } else if (blocking == targetEnemy) {
            // sampai ke dekat musuh,
            // serang sekali
            // lalu menyudahi inisiatif
            final Entity enemyToAttack = targetEnemy;
            intent = () -> {
              triggerAttackAnimation(enemyToAttack);
              this.attack(enemyToAttack);
            };
            targetEnemy = null;
          } else {
            this.targetX = nextX;
            this.targetY = nextY;
            intent = () -> isMoving = true;
          }
        } else {
          // harusnya ga sampe sini
          targetEnemy = null;
        }
      }

    } else if (currentPath != null && !currentPath.isEmpty()) {
      int nextIndex = currentPath.peek();
      int nextX = (nextIndex % gamePanel.maxScreenCol) * gamePanel.tileSize;
      int nextY = (nextIndex / gamePanel.maxScreenCol) * gamePanel.tileSize;

      Entity targetEntity = gamePanel.getEntityAt(nextX, nextY);
      if (targetEntity instanceof Enemy) {
        // attack, abis itu stop
        final Entity enemyToAttack = targetEntity;
        intent = () -> {
          triggerAttackAnimation(enemyToAttack);
          this.attack(enemyToAttack);
        };
        currentPath = null;
      } else if (gamePanel.getPotionManager().isPotionAt(nextX, nextY)) {
        // potion on pathfinding route — step on it and consume
        final int px = nextX;
        final int py = nextY;
        this.targetX = px;
        this.targetY = py;
        intent = () -> {
          isMoving = true;
          gamePanel.getPotionManager().onPlayerPickup(this);
        };
        currentPath.poll();
      } else if (targetEntity == null) {
        this.targetX = nextX;
        this.targetY = nextY;
        intent = () -> isMoving = true;
        currentPath.poll();
      } else {
        // pathfinder terhalangi
        currentPath = null;
      }
    }
  }

  @Override
  public void executeAction(GamePanel gamePanel) {
    if (intent != null) {
      intent.run();
      intent = null;
    }
  }

  public boolean hasIntent() {
    return intent != null;
  }

  // Resolves the correct frame to draw based on current animation state and movement
  private BufferedImage resolveDrawFrame() {
    if (attackAnimationPending || consumeAnimationPending) {
      return currentAnimation.getCurrentFrame();
    }

    if (isMoving) {
      return currentAnimation.getCurrentFrame();
    }

    // After first move, hold last direction frame as idle
    if (hasMovedOnce && lastWalkFrame != null) {
      return lastWalkFrame;
    }

    // Before first move, show spawn default
    return currentAnimation.getCurrentFrame();
  }

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
