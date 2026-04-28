package id.uphdungeon.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class DeathMessage {
  public void draw(Graphics2D g2, int screenWidth, int screenHeight) {
    g2.setColor(Color.RED);
    g2.setFont(new Font("Arial", Font.BOLD, 48));
    String deathMessage = "YOU DIED";
    int stringWidth = g2.getFontMetrics().stringWidth(deathMessage);
    int stringHeight = g2.getFontMetrics().getHeight();
    g2.drawString(deathMessage, (screenWidth / 2) - (stringWidth / 2),
        (screenHeight / 2) + (stringHeight / 4));
  }
}
