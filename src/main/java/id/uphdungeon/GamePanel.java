package id.uphdungeon;

import id.uphdungeon.entity.Enemy;
import id.uphdungeon.entity.Entity;
import id.uphdungeon.entity.Player;
import id.uphdungeon.entity.Rat;
import id.uphdungeon.entity.Skeleton;
import id.uphdungeon.ui.ActivityLog;
import id.uphdungeon.ui.DeathMessage;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
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
    END_ROUND,
  }

  private GameState gameState = GameState.START_ROUND;

  private final ActivityLog activityLog = new ActivityLog();
  private final DeathMessage deathMessage = new DeathMessage();

  public GamePanel() {
    this.setPreferredSize(new Dimension(screenWidth, screenHeight));
    this.setBackground(Color.BLACK);
    this.setDoubleBuffered(true);

    // allows the panel to receive key inputs
    this.setFocusable(true);
    this.addKeyListener(keyHandler);

    MouseAdapter mouseAdapter = new MouseAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
        activityLog.handleMouseMove(e.getX(), e.getY(), screenHeight);
      }

      @Override
      public void mouseExited(MouseEvent e) {
        activityLog.handleMouseMove(-1, -1, screenHeight);
      }

      @Override
      public void mouseWheelMoved(MouseWheelEvent e) {
        activityLog.handleMouseWheel(e.getWheelRotation());
      }
    };
    this.addMouseListener(mouseAdapter);
    this.addMouseMotionListener(mouseAdapter);
    this.addMouseWheelListener(mouseAdapter);

    player = new Player(this, keyHandler);
    entities.add(player);
    entities.add(new Skeleton(this, tileSize * 5, tileSize * 5, 1, 0));
    entities.add(new Rat(this, tileSize * 8, tileSize * 2, 0, 1));
    entities.add(new Rat(this, tileSize * 10, tileSize * 10, -1, -1));

    addLogMessage("Welcome to UPH Dungeon!", Color.YELLOW);
  }

  public void addLogMessage(String text, Color color) {
    activityLog.addLogMessage(text, color);
  }

  public Player getPlayer() {
    return player;
  }

  public Entity getEntityAt(int x, int y) {
    for (Entity e : entities) {
      if (!e.isDead && e.x == x && e.y == y) return e;
    }
    return null;
  }

  public void startGameThread() {
    gameThread = new Thread(this);
    gameThread.start();
  }

  @Override
  public void run() {
    double drawInterval = 1000000000 / FPS;
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
      // biar animasi semua Entity tetep jalan walau ga ada initiative
      for (Entity e : entities) e.update();

      boolean isAnimationDone = true;
      for (Entity e : entities) {
        if (e instanceof Player && ((Player) e).isMoving) {
          isAnimationDone = false;
          break;
        }
        if (e instanceof Enemy && ((Enemy) e).isMoving) {
          isAnimationDone = false;
          break;
        }
      }

      if (isAnimationDone) {
        actionInProgress = false;
        // action done, continue turn logics
        turnIndex++;
        processNextTurn();
      }
    } else {
      // no animation, continue game logic
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
              // kalau attack atau action instant, langsung proses turn
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
          gameState = GameState.START_ROUND;
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

    activityLog.draw(g2, screenHeight);

    if (player.isDead) {
      deathMessage.draw(g2, screenWidth, screenHeight);
    }

    g2.dispose();
  }
}
