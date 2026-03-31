package id.uphdungeon.entity;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class DamageIndicator {
  String text;
  int x, y;
  int life; // lifetime in frames
  float alpha = 1.0f;
  int yOffset = 0;

  public DamageIndicator(String text, int x, int y) {
    this.text = text;
    this.x = x;
    this.y = y;
    this.life = 60; // 1 second at 60 FPS
  }

  public void update() {
    life--;
    yOffset++;
    y--;

    if (life < 30) {
      alpha = life / 30.0f;
    }
  }

  public void draw(Graphics2D g2) {
    g2.setFont(new Font("Arial", Font.BOLD, 14));
    int stringWidth = g2.getFontMetrics().stringWidth(text);

    // center the text
    int drawX = x - (stringWidth / 2);

    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
    g2.setColor(Color.RED);
    g2.drawString(text, drawX, y);
    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)); // reset alpha
  }

  public boolean isFinished() {
    return life <= 0;
  }
}
