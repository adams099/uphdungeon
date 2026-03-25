package id.uphdungeon;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {
  private final int originalTileSize = 16; // 16x16 tile size
  private final int scale = 3; // zoom or scale up character pixel size
  private final int tileSize = originalTileSize * scale;

  // Screen Settings
  private final int maxScreenCol = 18;
  private final int maxScreenRow = 14;
  // private final int screenWidth = 800;
  // private final int screenHeight = 600;
  private final int screenWidth = tileSize * maxScreenCol;
  private final int screenHeight = tileSize * maxScreenRow;
  private final int FPS = 60;

  private int playerX = 100;
  private int playerY = 100;
  private int playerSpeed = 4;

  KeyHandler keyHandler = new KeyHandler();
  private Thread gameThread;

  public GamePanel() {
    this.setPreferredSize(new Dimension(screenWidth, screenHeight));
    this.setBackground(Color.BLACK);
    this.setDoubleBuffered(true); // Improves rendering performance
    this.setFocusable(true); // Allows the panel to receive key inputs
    this.addKeyListener(keyHandler);
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
    if (keyHandler.upPressed) {
      playerY -= playerSpeed;
    } else if (keyHandler.downPressed) {
      playerY += playerSpeed;
    } else if (keyHandler.leftPressed) {
      playerX -= playerSpeed;
    } else if (keyHandler.rightPressed) {
      playerX += playerSpeed;
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;

    // Example: Drawing a simple player square
    g2.setColor(Color.WHITE);
    g2.fillRect(playerX, playerY, tileSize, tileSize);

    g2.dispose(); // Housekeeping to save memory
  }
}
