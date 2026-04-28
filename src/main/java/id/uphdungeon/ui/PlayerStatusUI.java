package id.uphdungeon.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import id.uphdungeon.GamePanel;
import id.uphdungeon.entity.Player;
import id.uphdungeon.entity.PlayerStatusManager;

public class PlayerStatusUI {
  private final GamePanel gamePanel;
  private final int x = 10;
  private final int y = 10;
  private final int width = 200;
  private final int height = 70;
  private final int barHeight = 12;
  private final int margin = 5;
  private boolean isHovered = false;

  public PlayerStatusUI(GamePanel gamePanel) {
    this.gamePanel = gamePanel;
  }

  // di panggil di gamepanel biar tau mouse lagi di atas ui (hover)
  // atau ngga
  public void updateMousePosition(int mouseX, int mouseY) {
    isHovered = (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height);
  }

  public void draw(Graphics2D g2) {
    Player player = gamePanel.getPlayer();
    if (player == null) return;
    PlayerStatusManager status = player.getStatusManager();

    // kalau mouse lagi di atas ui (hover)
    // tampilan lebih keliatan
    float alpha = isHovered ? 1.0f : 0.4f;
    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

    // Render Background
    g2.setColor(new Color(0, 0, 0, 40));
    g2.fillRect(x, y, width, height);
    g2.setColor(new Color(255, 255, 255, 20));
    g2.drawRect(x, y, width, height);

    g2.setFont(new Font("Arial", Font.PLAIN, 12));
    int currentY = y + 15;

    // Render Level
    g2.setColor(Color.WHITE);
    g2.drawString("Player Level: " + status.getLevel(), x + margin, currentY);
    currentY += 5;

    // render Health Bar
    renderBar(g2, x + margin, currentY, width - (margin * 2), barHeight, player.health,
        player.maxHealth, Color.RED, Color.GREEN, "HP: ");
    currentY += barHeight + 5;

    // Render experience bar
    int nextLevelExp = status.getNextLevelExp();
    if (nextLevelExp != -1) {
      renderBar(g2, x + margin, currentY, width - (margin * 2), barHeight, status.getExperience(),
          nextLevelExp, Color.DARK_GRAY, Color.YELLOW, "XP: ");
    } else {
      g2.setColor(Color.YELLOW);
      g2.drawString("XP: MAX", x + margin, currentY + 10);
    }
    currentY += barHeight + 15;

    // Render Damage Min Max
    g2.setColor(Color.WHITE);
    g2.drawString("Damage: " + player.minDamage + " - " + player.maxDamage, x + margin, currentY);

    // biar AlphaComposite yang dipakai
    // di ui lain reset ke awal. Jadi pengubahan di ui ini ga bocor ke ui lain
    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
  }

  private void renderBar(Graphics2D g2, int barPosX, int barPosY, int barWidth, int barHeight,
      int current, int max, Color bgColor, Color fgColor, String label) {
    // background
    g2.setColor(bgColor);
    g2.fillRect(barPosX, barPosY, barWidth, barHeight);

    // Foreground
    g2.setColor(fgColor);
    int fillWidth = (int) (((double) current / max) * barWidth);
    g2.fillRect(barPosX, barPosY, fillWidth, barHeight);

    // garis pinggir
    g2.setColor(Color.WHITE);
    g2.drawRect(barPosX, barPosY, barWidth, barHeight);

    g2.setFont(new Font("Arial", Font.BOLD, 10));
    String text = label + current + "/" + max;
    int textWidth = g2.getFontMetrics().stringWidth(text);
    int textPosX = barPosX + (barWidth - textWidth) / 2;
    int textPosY = barPosY + barHeight - 2;

    // ini untuk baygangan teks biar agak kontras
    g2.setColor(new Color(0, 0, 0, 150));
    g2.drawString(text, textPosX + 1, textPosY + 1);

    // render teks
    g2.setColor(Color.WHITE);
    g2.drawString(text, textPosX, textPosY);
  }
}
