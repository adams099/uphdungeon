package id.uphdungeon.entity;

import id.uphdungeon.GamePanel;
import id.uphdungeon.utils.PathFinder;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

public abstract class Enemy extends Entity {
  public Color color;
  protected int aggroRange = 1;
  protected double idleWaitChance = 0.0;
  protected final Random rand = new Random();
  protected PathFinder.Path currentPath = null;

  public Enemy(GamePanel gamePanel, int startX, int startY, Color color) {
    super(gamePanel);
    this.x = this.targetX = startX;
    this.y = this.targetY = startY;
    this.color = color;
    this.initiative = rand.nextInt(9) + 1;
  }

  public abstract int getExpReward();

  protected boolean isPlayerInAggroRange(int range) {
    Player player = gamePanel.getPlayer();
    if (player.isDead) return false;
    return PathFinder.getGridDistance(x, y, player.x, player.y, gamePanel.tileSize) <= range;
  }

  protected boolean isAdjacentTo(Entity other) {
    int distance = PathFinder.getGridDistance(x, y, other.x, other.y, gamePanel.tileSize);
    return distance > 0 && distance <= 1;
  }

  protected void wander() {
    if (currentPath == null || currentPath.isEmpty()) {
      int tx = rand.nextInt(gamePanel.maxScreenCol);
      int ty = rand.nextInt(gamePanel.maxScreenRow);
      int toIndex = tx + ty * gamePanel.maxScreenCol;
      int fromIndex = getGridIndex(x, y);
      PathFinder.setMapSize(gamePanel.maxScreenCol, gamePanel.maxScreenRow);
      currentPath = PathFinder.find(fromIndex, toIndex, getPassableMap());
    }

    if (currentPath != null && !currentPath.isEmpty()) {
      int nextIdx = currentPath.peek();
      int nextX = getXFromIndex(nextIdx);
      int nextY = getYFromIndex(nextIdx);
      if (gamePanel.getEntityAt(nextX, nextY) == null) {
        setMoveIntent(nextX, nextY);
        currentPath.poll();
        return;
      }
    }
    currentPath = null;
    setWaitIntent();
  }

  protected void chasePlayer() {
    Player p = gamePanel.getPlayer();
    if (p.isDead) {
      wander();
      return;
    }

    boolean[] passable = getPassableMap();
    int targetIdx = getGridIndex(p.x, p.y);
    if (targetIdx >= 0 && targetIdx < passable.length) passable[targetIdx] = true;

    PathFinder.setMapSize(gamePanel.maxScreenCol, gamePanel.maxScreenRow);
    int nextIdx = PathFinder.getStep(getGridIndex(x, y), targetIdx, passable);

    if (nextIdx != -1) {
      int nextX = getXFromIndex(nextIdx);
      int nextY = getYFromIndex(nextIdx);
      Entity blockingEntity = gamePanel.getEntityAt(nextX, nextY);
      if (blockingEntity == p) {
        setAttackIntent(p);
        return;
      }
      if (blockingEntity == null) {
        setMoveIntent(nextX, nextY);
        return;
      }
    }
    wander();
  }

  protected void setAttackIntent(Entity target) {
    intent = () -> this.attack(target);
  }

  protected void setMoveIntent(int nextX, int nextY) {
    targetX = nextX;
    targetY = nextY;
    intent = () -> isMoving = true;
  }

  protected void setWaitIntent() {
    intent = () -> {};
  }

  @Override
  public void determineIntent(GamePanel gamePanel) {
    if (intent != null || isMoving || isDead) return;
    if (rand.nextDouble() < idleWaitChance) {
      setWaitIntent();
      return;
    }

    Player p = gamePanel.getPlayer();
    if (!p.isDead) {
      if (isAdjacentTo(p)) {
        setAttackIntent(p);
        return;
      }
      if (isPlayerInAggroRange(aggroRange)) {
        chasePlayer();
        return;
      }
    }
    wander();
  }

  private boolean[] getPassableMap() {
    return PathFinder.buildPassableMap(gamePanel.maxScreenCol, gamePanel.maxScreenRow,
        gamePanel.tileSize, gamePanel.entities, this);
  }

  @Override
  public void draw(Graphics2D g2) {
    if (isDead && !isFading) {
      drawIndicators(g2);
      return;
    }
    if (isFading) g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

    int size = (int) (gamePanel.tileSize * 0.8), off = (gamePanel.tileSize - size) / 2;
    g2.setColor(color);
    g2.fillRect(x + off, y + off, size, size);
    if (!isDead) drawHealthBar(g2);

    if (isFading) g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    drawIndicators(g2);
  }
}
