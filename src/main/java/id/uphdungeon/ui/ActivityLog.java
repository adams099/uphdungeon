package id.uphdungeon.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
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

  private final int logPosX = 10;
  private final int logWidth = 250;
  private final int logHeight = 120;
  private final int lineHeight = 20;
  private final int maxLogMessages = 1000;
  private final ArrayList<LogMessage> activityLog = new ArrayList<>();
  private int scrollStatus = 0;
  private boolean isHovered = false;

  public void addLogMessage(String text, Color color) {
    activityLog.add(new LogMessage(text, color));
    if (activityLog.size() > maxLogMessages) {
      activityLog.remove(0);
    }
    // kalo ada message baru log balik ke bawah
    scrollStatus = 0;
  }

  public void handleMouseMove(int mouseX, int mouseY, int screenHeight) {
    int logPosY = screenHeight - logHeight - 10;
    isHovered = (mouseX >= logPosX && mouseX <= logPosX + logWidth && mouseY >= logPosY
        && mouseY <= logPosY + logHeight);
  }

  public void handleMouseWheel(int rotation) {
    if (isHovered) {
      int totalHeight = activityLog.size() * lineHeight;
      if (totalHeight <= logHeight) return;

      scrollStatus -= rotation * lineHeight;

      int maxScroll = totalHeight - logHeight + 10;

      if (scrollStatus < 0) scrollStatus = 0;
      if (scrollStatus > maxScroll) scrollStatus = maxScroll;
    }
  }

  public void draw(Graphics2D g2, int screenHeight) {
    int logPosY = screenHeight - logHeight - 10;
    int totalHeight = activityLog.size() * lineHeight;

    if (isHovered) {
      g2.setColor(new Color(255, 255, 255, 40));
      g2.fillRect(logPosX, logPosY, logWidth, logHeight);
    }

    if (isHovered && totalHeight > logHeight) {
      int barWidth = 4;
      int barX = logPosX + logWidth - barWidth - 2;

      double viewRatio = (double) logHeight / totalHeight;
      int scrollBarHeight = (int) (logHeight * viewRatio);
      scrollBarHeight = Math.max(scrollBarHeight, 20);

      int maxScroll = totalHeight - logHeight + 10;
      double scrollRatio = (double) scrollStatus / maxScroll;

      // scrollStatus == 0 paling bawah
      // scrollStatus == maxScroll paling atas
      int scrollBarPosY = logPosY + (int) ((1.0 - scrollRatio) * (logHeight - scrollBarHeight));

      // render scroll railing
      g2.setColor(new Color(255, 255, 255, 20));
      g2.fillRect(barX, logPosY, barWidth, logHeight);

      // render scroll bar
      g2.setColor(new Color(255, 255, 255, 120));
      g2.fillRect(barX, scrollBarPosY, barWidth, scrollBarHeight);
    }

    Shape oldClip = g2.getClip();
    g2.setClip(logPosX, logPosY, logWidth, logHeight);

    g2.setFont(new Font("Arial", Font.PLAIN, 12));

    // render semua log
    // iterative bawah ke atas
    int currentY = logPosY + logHeight - 5 + scrollStatus;
    for (int i = activityLog.size() - 1; i >= 0; i--) {
      LogMessage msg = activityLog.get(i);
      g2.setColor(msg.color);
      g2.drawString(msg.text, logPosX + 5, currentY);
      currentY -= lineHeight;

      // handle area render logging udah di paling atas
      if (currentY < logPosY - lineHeight) break;
    }

    g2.setClip(oldClip);
  }
}
