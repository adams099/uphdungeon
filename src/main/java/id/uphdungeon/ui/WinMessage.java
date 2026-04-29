package id.uphdungeon.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class WinMessage {
  public void draw(Graphics2D g2, int screenWidth, int screenHeight) {
    g2.setColor(new Color(255, 215, 0)); // gold color
    g2.setFont(new Font("Arial", Font.BOLD, 48));
    String winMessage = "YOU WIN";
    int stringWidth = g2.getFontMetrics().stringWidth(winMessage);
    int stringHeight = g2.getFontMetrics().getHeight();
    g2.drawString(winMessage, (screenWidth / 2) - (stringWidth / 2),
        (screenHeight / 2) + (stringHeight / 4));
  }
}
