package id.uphdungeon;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

import javax.swing.JPanel;

import id.uphdungeon.entity.Enemy;
import id.uphdungeon.entity.Entity;
import id.uphdungeon.entity.Player;
import id.uphdungeon.potion.PotionManager;
import id.uphdungeon.ui.ActivityLog;
import id.uphdungeon.ui.DeathMessage;
import id.uphdungeon.ui.PlayerStatusUI;
import id.uphdungeon.ui.RetryButton;
import id.uphdungeon.ui.WaitButton;
import id.uphdungeon.utils.TileManager;
import id.uphdungeon.utils.WaveManager;

public class GamePanel extends JPanel implements Runnable {

  public final int originalTileSize = 16;
  public final int scale = 3;
  public final int tileSize = originalTileSize * scale;

  public final int maxScreenCol = 18;
  public final int maxScreenRow = 12;
  public final int screenWidth = tileSize * maxScreenCol;
  public final int screenHeight = tileSize * maxScreenRow;
  public final int FPS = 60;

  public KeyHandler keyHandler = new KeyHandler();
  private Thread gameThread;

  // Tile manager for dungeon background rendering
  private TileManager tile;

  private WaveManager waveManager;

  // Potion manager — handles spawn, respawn, pickup, and animation
  private PotionManager potionManager;

  private Player player;
  public ArrayList<Entity> entities = new ArrayList<>();
  private ArrayList<Entity> turnOrder = new ArrayList<>();
  private int turnIndex = 0;
  private boolean actionInProgress = false;

  private int shakeIntensity = 0;
  private int shakeDuration = 0;
  private final Random random = new Random();

  private enum GameState {
    START_ROUND,
    PLAYER_TURN,
    ENEMY_TURN,
    END_ROUND,
  }

  private GameState gameState = GameState.START_ROUND;

  private final ActivityLog activityLog = new ActivityLog();
  private final DeathMessage deathMessage = new DeathMessage();
  private final WaitButton waitButton;
  private final RetryButton retryButton;
  private final PlayerStatusUI playerStatusUI;

  public GamePanel() {
    this.setPreferredSize(new Dimension(screenWidth, screenHeight));
    this.setBackground(Color.BLACK);
    this.setDoubleBuffered(true);

    this.waitButton = new WaitButton(screenWidth, screenHeight);
    this.retryButton = new RetryButton(screenWidth, screenHeight);
    this.playerStatusUI = new PlayerStatusUI(this);

    // allows the panel to receive key inputs
    this.setFocusable(true);
    this.addKeyListener(keyHandler);

    MouseAdapter mouseAdapter = new MouseAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
        activityLog.handleMouseMove(e.getX(), e.getY(), screenHeight);
        playerStatusUI.updateMousePosition(e.getX(), e.getY());
        waitButton.update(e.getX(), e.getY());
        if (player != null && player.isDead) {
          retryButton.update(e.getX(), e.getY());
        }
      }

      @Override
      public void mouseExited(MouseEvent e) {
        activityLog.handleMouseMove(-1, -1, screenHeight);
        playerStatusUI.updateMousePosition(-1, -1);
        waitButton.update(-1, 1);
        retryButton.update(-1, -1);
      }

      @Override
      public void mouseWheelMoved(MouseWheelEvent e) {
        activityLog.handleMouseWheel(e.getWheelRotation());
      }

      @Override
      public void mousePressed(MouseEvent e) {
        if (player != null && player.isDead) {
          if (retryButton.isClicked(e.getX(), e.getY())) {
            resetGame();
            return;
          }
        }

        if (gameState == GameState.PLAYER_TURN && !actionInProgress) {
          handleMouseClick(e.getX(), e.getY());
        }
      }
    };
    this.addMouseListener(mouseAdapter);
    this.addMouseMotionListener(mouseAdapter);
    this.addMouseWheelListener(mouseAdapter);

    // Tile must be initialised before entities so the map is ready at first frame
    tile = new TileManager(this);

    waveManager = new WaveManager(this);

    initEntities();

    // Initialize potion manager
    potionManager = new PotionManager(this);

    addLogMessage("Welcome to UPH Dungeon!", Color.YELLOW);
  }

  private void initEntities() {
    entities.clear();
    player = new Player(this, keyHandler);
    entities.add(player);
    if (waveManager != null) {
      waveManager.startFirstWave();
    }
  }

  public void resetGame() {
    activityLog.clear();
    waveManager = new WaveManager(this);
    initEntities();
    potionManager = new PotionManager(this);
    gameState = GameState.START_ROUND;
    actionInProgress = false;
    turnIndex = 0;
    turnOrder.clear();
    addLogMessage("Game Restarted!", Color.YELLOW);
  }

  public void handleMouseClick(int mouseX, int mouseY) {
    if (waitButton.isClicked(mouseX, mouseY)) {
      keyHandler.waitTriggered = true;
      return;
    }

    int col = mouseX / tileSize;
    int row = mouseY / tileSize;

    if (col >= 0 && col < maxScreenCol && row >= 0 && row < maxScreenRow) {
      player.setPath(col, row);
    }
  }

  public void addLogMessage(String text, Color color) {
    activityLog.addLogMessage(text, color);
  }

  public Player getPlayer() {
    return player;
  }

  // Returns the potion manager Player can check and interact with potions
  public PotionManager getPotionManager() {
    return potionManager;
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

  public void triggerScreenShake() {
    this.shakeIntensity = 10;
    this.shakeDuration = 20;
  }

  public void update() {
    // layar goyang - request ara
    if (shakeDuration > 0) {
      shakeDuration--;
      if (shakeDuration == 0) {
        shakeIntensity = 0;
      }
    }

    for (Entity e : entities) {
      e.updateAnimations();
    }

    // Update potion animation every frame
    potionManager.update();

    waveManager.update();

    if (actionInProgress) {
      // biar animasi semua Entity tetep jalan walau ga ada initiative
      for (Entity e : entities)
        e.update();

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
          if (currentEntity instanceof Enemy && ((Enemy) currentEntity).isMoving) {
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

    // Tick potion respawn counter once per completed initiative turn
    potionManager.tickRespawn();

    waveManager.tickCountdown();

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

    // layar goyang - request ara
    if (shakeIntensity > 0) {
      int offsetX = random.nextInt(shakeIntensity * 2 + 1) - shakeIntensity;
      int offsetY = random.nextInt(shakeIntensity * 2 + 1) - shakeIntensity;
      g2.translate(offsetX, offsetY);
    }

    tile.draw(g2);

    potionManager.draw(g2);

    for (Entity e : entities) {
      e.draw(g2);
    }

    playerStatusUI.draw(g2);
    waitButton.draw(g2);
    activityLog.draw(g2, screenHeight);

    if (player.isDead) {
      deathMessage.draw(g2, screenWidth, screenHeight);
      retryButton.draw(g2);
    }

    g2.dispose();
  }
}
