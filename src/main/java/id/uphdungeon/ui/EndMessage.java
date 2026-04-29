
package id.uphdungeon.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class EndMessage {
  public enum MessageType {
    DEATH,
    WIN
  }

  MessageType messageType;

  public EndMessage(MessageType messageType) {
    this.messageType = messageType;
  }

  public void draw(Graphics2D g2, int screenWidth, int screenHeight) {
    String message;
    if (messageType == MessageType.DEATH) {
      g2.setColor(Color.RED);
      g2.setFont(new Font("Arial", Font.BOLD, 48));
      message = "YOU DIED";
    } else {
      g2.setColor(Color.YELLOW);
      g2.setFont(new Font("Arial", Font.BOLD, 48));
      message = "BOSS SLAIN";
    }
    int stringWidth = g2.getFontMetrics().stringWidth(message);
    int stringHeight = g2.getFontMetrics().getHeight();
    g2.drawString(message, (screenWidth / 2) - (stringWidth / 2),
        (screenHeight / 2) + (stringHeight / 4));
  }
}
