package id.uphdungeon.entity;

import id.uphdungeon.GamePanel;
import id.uphdungeon.KeyHandler;
import id.uphdungeon.utils.PathFinder;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;

public class Player extends Entity {
  KeyHandler keyH;

  public boolean isMoving = false;
  public int targetX, targetY;

  private Runnable intent = null;
  private PathFinder.Path currentPath = null;
  private Entity targetEnemy = null;

  public Player(GamePanel gamePanel, KeyHandler keyH) {
    super(gamePanel);
    this.keyH = keyH;

    this.x = gamePanel.tileSize * 2;
    this.y = gamePanel.tileSize * 2;

    this.speed = 4;
    this.initiative = 10;

    this.maxHealth = 30;
    this.health = 30;
    this.minDamage = 2;
    this.maxDamage = 5;

    this.targetX = this.x;
    this.targetY = this.y;
  }

  @Override
  public void update() {
    if (isMoving) {
      if (x < targetX) x += speed;
      if (x > targetX) x -= speed;
      if (y < targetY) y += speed;
      if (y > targetY) y -= speed;

      // snap to target if very close to prevent jitter
      if (Math.abs(x - targetX) < speed && Math.abs(y - targetY) < speed) {
        x = targetX;
        y = targetY;
        isMoving = false;
      }
    }
  }

  public void setPath(int col, int row) {
    int fromIndex =
      (x / gamePanel.tileSize) +
      (y / gamePanel.tileSize) * gamePanel.maxScreenCol;
    int toIndex = col + row * gamePanel.maxScreenCol;

    if (fromIndex == toIndex) return;

    // reset action
    currentPath = null;
    targetEnemy = null;

    Entity clickedEntity = gamePanel.getEntityAt(
      col * gamePanel.tileSize,
      row * gamePanel.tileSize
    );

    if (clickedEntity != null && clickedEntity instanceof Enemy) {
      targetEnemy = clickedEntity;
    } else {
      boolean[] passable = getPassableMap();
      PathFinder.setMapSize(gamePanel.maxScreenCol, gamePanel.maxScreenRow);
      currentPath = PathFinder.find(fromIndex, toIndex, passable);
    }
  }

  private boolean[] getPassableMap() {
    boolean[] passable = new boolean[gamePanel.maxScreenCol *
    gamePanel.maxScreenRow];
    Arrays.fill(passable, true);
    for (Entity e : gamePanel.entities) {
      if (!e.isDead && e != this) {
        int index =
          (e.x / gamePanel.tileSize) +
          (e.y / gamePanel.tileSize) * gamePanel.maxScreenCol;
        if (index >= 0 && index < passable.length) {
          passable[index] = false;
        }
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
        intent = () -> {
          gamePanel.addLogMessage("Player waited.", Color.CYAN);
        };
      } else if (keyH.moveTriggered) {
        int nextX = x;
        int nextY = y;

        if (keyH.wasUpPressed) {
          nextY -= gamePanel.tileSize;
        }
        if (keyH.wasDownPressed) {
          nextY += gamePanel.tileSize;
        }
        if (keyH.wasLeftPressed) {
          nextX -= gamePanel.tileSize;
        }
        if (keyH.wasRightPressed) {
          nextX += gamePanel.tileSize;
        }

        // cancel kalo pencet arahnya bertubrukan
        if (keyH.wasUpPressed && keyH.wasDownPressed) nextY = y;
        if (keyH.wasLeftPressed && keyH.wasRightPressed) nextX = x;

        // initiative start only if player changed position
        if (nextX != x || nextY != y) {
          Entity targetEntity = gamePanel.getEntityAt(nextX, nextY);

          if (targetEntity != null) {
            // if enemy exists attack enemy
            if (targetEntity instanceof Enemy) {
              intent = () -> this.attack(targetEntity);
            }
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
        intent = () -> this.attack(enemyToAttack);
        targetEnemy = null;
      } else {
        // not adjacent, one step to enemy
        boolean[] passable = getPassableMap();
        // temporarily make target tile passable
        // so we can move there
        int targetIndex =
          (targetEnemy.x / gamePanel.tileSize) +
          (targetEnemy.y / gamePanel.tileSize) * gamePanel.maxScreenCol;
        if (targetIndex >= 0 && targetIndex < passable.length) {
          passable[targetIndex] = true;
        }

        int fromIndex =
          (x / gamePanel.tileSize) +
          (y / gamePanel.tileSize) * gamePanel.maxScreenCol;
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
            intent = () -> this.attack(enemyToAttack);
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
      if (targetEntity != null && targetEntity instanceof Enemy) {
        // attack, abis itu stop
        final Entity enemyToAttack = targetEntity;
        intent = () -> this.attack(enemyToAttack);
        currentPath = null;
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

  @Override
  public void draw(Graphics2D g2) {
    if (!isDead) {
      int spriteSize = (int) (gamePanel.tileSize * 0.8);
      int offset = (gamePanel.tileSize - spriteSize) / 2;

      g2.setColor(Color.WHITE);
      g2.fillRect(x + offset, y + offset, spriteSize, spriteSize);

      // health bar
      g2.setColor(Color.RED);
      g2.fillRect(x + offset, y + offset - 5, spriteSize, 4);
      g2.setColor(Color.GREEN);
      int hpWidth = (int) (((double) health / maxHealth) * spriteSize);
      g2.fillRect(x + offset, y + offset - 5, hpWidth, 4);
    }

    drawIndicators(g2);
  }
}
