package id.uphdungeon.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class ActivityLog {
  public static class LogMessage {
    public String text;
    public Color color;

    public LogMessage(String text, Color color) {
      this.text = text;
      this.color = color;
    }
  }

  private final ArrayList<LogMessage> activityLog = new ArrayList<>();
  private final int maxLogMessages = 5;

  public void addLogMessage(String text, Color color) {
    activityLog.add(new LogMessage(text, color));
    if (activityLog.size() > maxLogMessages) {
      activityLog.remove(0);
    }
  }

  public void draw(Graphics2D g2, int screenHeight) {
    g2.setFont(new Font("Arial", Font.PLAIN, 12));
    int logX = 10;
    int logY = screenHeight - 20;
    for (int i = activityLog.size() - 1; i >= 0; i--) {
      LogMessage msg = activityLog.get(i);
      g2.setColor(msg.color);
      g2.drawString(msg.text, logX, logY);
      logY -= 20;
    }
  }
}
