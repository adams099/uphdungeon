package id.uphdungeon;

import id.uphdungeon.entity.Enemy;
import id.uphdungeon.entity.Entity;
import id.uphdungeon.entity.Player;
import id.uphdungeon.entity.Rat;
import id.uphdungeon.entity.Skeleton;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Comparator;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {
  public final int originalTileSize = 16;
  public final int scale = 3;
  public final int tileSize = originalTileSize * scale;

  public final int maxScreenCol = 18;
  public final int maxScreenRow = 14;
  public final int screenWidth = tileSize * maxScreenCol;
  public final int screenHeight = tileSize * maxScreenRow;
  public final int FPS = 60;

  public KeyHandler keyHandler = new KeyHandler();
  private Thread gameThread;

  private Player player;
  public ArrayList<Entity> entities = new ArrayList<>();
  private ArrayList<Entity> turnOrder = new ArrayList<>();
  private int turnIndex = 0;
  private boolean actionInProgress = false;

  private enum GameState {
    START_ROUND,
    PLAYER_TURN,
    ENEMY_TURN,
    GAME_OVER,
    END_ROUND,
  }

  private GameState gameState = GameState.START_ROUND;

  public GamePanel() {
    this.setPreferredSize(new Dimension(screenWidth, screenHeight));
    this.setBackground(Color.BLACK);
    // improves rendering performance
    this.setDoubleBuffered(true);
    // allows the panel to receive key inputs
    this.setFocusable(true);
    this.addKeyListener(keyHandler);

    player = new Player(this, keyHandler);
    entities.add(player);
    entities.add(new Skeleton(this, tileSize * 5, tileSize * 5, 1, 0));
    entities.add(new Rat(this, tileSize * 8, tileSize * 2, 0, 1));
    entities.add(new Rat(this, tileSize * 10, tileSize * 10, -1, -1));
  }

  public Player getPlayer() {
    return player;
  }

  public Entity getEntityAt(int x, int y) {
    for (Entity e : entities) {
      if (!e.isDead && e.x == x && e.y == y) {
        return e;
      }
    }
    return null;
  }

  public void startGameThread() {
    gameThread = new Thread(this);
    gameThread.start();
  }

  @Override
  public void run() {
    double drawInterval = 1000000000 / FPS; // 0.0166 seconds
    double nextDrawTime = System.nanoTime() + drawInterval;

    while (gameThread != null) {
      update();
      repaint();

      try {
        double remainingTime = nextDrawTime - System.nanoTime();
        // convert to milliseconds
        remainingTime = remainingTime / 1000000;

        if (remainingTime < 0) remainingTime = 0;

        Thread.sleep((long) remainingTime);
        nextDrawTime += drawInterval;
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public void update() {
    for (Entity e : entities) {
      e.updateAnimations();
    }
    if (actionInProgress) {
      // an animation is playing, just update all entities for animation
      for (Entity e : entities) e.update();

      // check if all animations are done
      boolean allDone = true;
      for (Entity e : entities) {
        if (e instanceof Player && ((Player) e).isMoving) {
          allDone = false;
          break;
        }
        if (e instanceof Enemy && ((Enemy) e).isMoving) {
          allDone = false;
          break;
        }
      }
      if (allDone) {
        actionInProgress = false;
        // The action is done, now we can proceed with the turn logic.
        turnIndex++;
        processNextTurn();
      }
    } else if (gameState != GameState.GAME_OVER) {
      // No animation, process game logic
      switch (gameState) {
        case START_ROUND:
          turnOrder.clear();
          entities.removeIf(e -> e.isDead && !e.isFading);
          turnOrder.addAll(entities);
          turnOrder.sort(Comparator.comparingInt(e -> -e.initiative));
          turnIndex = 0;
          processNextTurn();
          break;
        case PLAYER_TURN:
          player.determineIntent(this);
          if (player.hasIntent()) {
            player.executeAction(this);
            if (player.isMoving) {
              actionInProgress = true;
            } else {
              // it was an attack or something instant
              turnIndex++;
              processNextTurn();
            }
          }
          break;
        case ENEMY_TURN:
          Entity currentEntity = turnOrder.get(turnIndex);
          currentEntity.determineIntent(this);
          currentEntity.executeAction(this);
          if (
            currentEntity instanceof Enemy && ((Enemy) currentEntity).isMoving
          ) {
            actionInProgress = true;
          } else {
            turnIndex++;
            processNextTurn();
          }
          break;
        case END_ROUND:
          if (player.isDead) {
            gameState = GameState.GAME_OVER;
          } else {
            gameState = GameState.START_ROUND;
          }
          break;
        case GAME_OVER:
          // No updates, just repaint the game over screen
          break;
      }
    }
  }

  private void processNextTurn() {
    if (turnIndex >= turnOrder.size()) {
      gameState = GameState.END_ROUND;
      return;
    }

    Entity currentEntity = turnOrder.get(turnIndex);
    if (currentEntity.isDead) {
      turnIndex++;
      processNextTurn();
      return;
    }

    if (currentEntity instanceof Player) {
      gameState = GameState.PLAYER_TURN;
    } else {
      gameState = GameState.ENEMY_TURN;
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;

    g2.setColor(Color.DARK_GRAY);
    for (int i = 0; i < maxScreenCol; i++) {
      g2.drawLine(i * tileSize, 0, i * tileSize, screenHeight);
    }
    for (int i = 0; i < maxScreenRow; i++) {
      g2.drawLine(0, i * tileSize, screenWidth, i * tileSize);
    }

    for (Entity e : entities) {
      e.draw(g2);
    }

    if (player.isDead) {
      g2.setColor(Color.RED);
      g2.drawString("YOU DIED", screenWidth / 2 - 40, screenHeight / 2);
    }

    g2.dispose();
  }
}
