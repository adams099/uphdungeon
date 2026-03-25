package id.uphdungeon;

import id.uphdungeon.entity.Player;
import id.uphdungeon.entity.Enemy;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
  public Enemy enemy1;
  public Enemy enemy2;
  public Enemy enemy3;

  public GamePanel() {
    this.setPreferredSize(new Dimension(screenWidth, screenHeight));
    this.setBackground(Color.BLACK);
    this.setDoubleBuffered(true); // Improves rendering performance
    this.setFocusable(true); // Allows the panel to receive key inputs
    this.addKeyListener(keyHandler);

    enemy1 = new Enemy(this, tileSize * 5, tileSize * 5, 1, 0, Color.RED);
    enemy2 = new Enemy(this, tileSize * 8, tileSize * 2, 0, 1, Color.BLUE);
    enemy3 = new Enemy(this, tileSize * 10, tileSize * 10, -1, -1, Color.GREEN);
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
    player.update();
    enemy1.update();
    enemy2.update();
    enemy3.update();
  }

  public void advanceTurn() {
    // This is called each time the player makes an action/move.
    // In a roguelike, this is where initiatives are rolled or enemies take their turns.
    System.out.println("Turn advanced! Entities take their action based on initiative.");
    enemy1.takeTurn();
    enemy2.takeTurn();
    enemy3.takeTurn();
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

    player.draw(g2);
    enemy1.draw(g2);
    enemy2.draw(g2);
    enemy3.draw(g2);

    g2.dispose(); // Housekeeping to save memory
  }
}
