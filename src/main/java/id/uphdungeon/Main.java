package id.uphdungeon;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
  public static void main(String[] args) {
    // Lambda Function as taught by Maam Irene (Session 13)
    // this is syntactic sugar that implements Runnable
    // the function body is what will be written inside Runnable.run() {}
    SwingUtilities.invokeLater(() -> {
      JFrame window = new JFrame();

      window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      window.setResizable(false);
      window.setTitle("UPH Dungeon");

      GamePanel gamePanel = new GamePanel();
      window.add(gamePanel);

      window.pack();
      window.setLocationRelativeTo(null);
      window.setVisible(true);

      gamePanel.startGameThread();
    });
  }
}
