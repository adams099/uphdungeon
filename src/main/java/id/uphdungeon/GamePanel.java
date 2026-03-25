package id.uphdungeon;

import id.uphdungeon.entity.Entity;
import id.uphdungeon.entity.Player;
import id.uphdungeon.entity.Enemy;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {
  public final int originalTileSize = 16; // 16x16 tile size
  public final int scale = 3; // zoom or scale up character pixel size
  public final int tileSize = originalTileSize * scale;

  // Screen Settings
  public final int maxScreenCol = 18;
  public final int maxScreenRow = 14;
  public final int screenWidth = tileSize * maxScreenCol;
  public final int screenHeight = tileSize * maxScreenRow;
  public final int FPS = 60;

  public KeyHandler keyHandler = new KeyHandler();
  private Thread gameThread;

  public Player player = new Player(this, keyHandler);
  public ArrayList<Enemy> enemies = new ArrayList<>();

  public GamePanel() {
    this.setPreferredSize(new Dimension(screenWidth, screenHeight));
    this.setBackground(Color.BLACK);
    this.setDoubleBuffered(true); // Improves rendering performance
    this.setFocusable(true); // Allows the panel to receive key inputs
    this.addKeyListener(keyHandler);

    enemies.add(new Enemy(this, tileSize * 5, tileSize * 5, 1, 0, Color.RED));
    enemies.add(new Enemy(this, tileSize * 8, tileSize * 2, 0, 1, Color.BLUE));
    enemies.add(new Enemy(this, tileSize * 10, tileSize * 10, -1, -1, Color.GREEN));
  }

  public Entity getEntityAt(int x, int y) {
      if (!player.isDead && player.x == x && player.y == y) {
          return player;
      }

      for (Enemy e : enemies) {
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
      // 1. UPDATE: Update information such as character positions
      update();

      // 2. DRAW: Draw the screen with the updated information
      repaint();

      try {
        double remainingTime = nextDrawTime - System.nanoTime();
        remainingTime = remainingTime / 1000000; // convert to milliseconds

        if (remainingTime < 0) remainingTime = 0;

        Thread.sleep((long) remainingTime);
        nextDrawTime += drawInterval;
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public void update() {
    if (!player.isDead) {
        player.update();
    }

    enemies.removeIf(e -> e.isDead);

    for (Enemy e : enemies) {
        e.update();
    }
  }

  public void advanceTurn() {
    if (player.isDead) return;

    // This is called each time the player makes an action/move.
    for (Enemy e : enemies) {
        e.takeTurn();
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;

    // Draw Grid
    g2.setColor(Color.DARK_GRAY);
    for (int i = 0; i < maxScreenCol; i++) {
        g2.drawLine(i * tileSize, 0, i * tileSize, screenHeight);
    }
    for (int i = 0; i < maxScreenRow; i++) {
        g2.drawLine(0, i * tileSize, screenWidth, i * tileSize);
    }

    if (!player.isDead) {
        player.draw(g2);
    }

    for (Enemy e : enemies) {
        e.draw(g2);
    }

    if (player.isDead) {
        g2.setColor(Color.RED);
        g2.drawString("GAME OVER", screenWidth/2 - 40, screenHeight/2);
    }

    g2.dispose(); // Housekeeping to save memory
  }
}
