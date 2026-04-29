
package id.uphdungeon.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class RetryButton {
  private int x, y, width, height;
  private boolean isHovered = false;
  private String text;
  private Font font = new Font("Arial", Font.BOLD, 24);

  public RetryButton(int screenWidth, int screenHeight, String text) {
    this.width = 100;
    this.height = 30;
    this.text = text;
    this.x = (screenWidth / 2) - (width / 2);
    this.y = (screenHeight / 2) + 60;
  }

  public void update(int mouseX, int mouseY) {
    isHovered = (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height);
  }

  public boolean isClicked(int mouseX, int mouseY) {
    return (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height);
  }

  public void draw(Graphics2D g2) {
    if (isHovered) {
      g2.setColor(Color.YELLOW);
    } else {
      g2.setColor(Color.WHITE);
    }

    g2.setFont(font);
    int stringWidth = g2.getFontMetrics().stringWidth(text);
    int stringHeight = g2.getFontMetrics().getAscent();

    g2.drawString(text, x + (width / 2) - (stringWidth / 2), y + (height / 2) + (stringHeight / 3));
  }
}
