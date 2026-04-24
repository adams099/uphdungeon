package id.uphdungeon.ui;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class WaitButton {

  private BufferedImage image;
  private int positionX,
    positionY,
    width = 32,
    height = 32;
  private boolean isHovered = false;

  public WaitButton(int screenWidth, int screenHeight) {
    try {
      image = ImageIO.read(
        getClass().getResourceAsStream("/ui/wait_button/wait_button.png")
      );

      this.positionX = screenWidth - width - 20;
      this.positionY = screenHeight - height - 20;
    } catch (IOException | NullPointerException e) {
      System.err.println("Could not load wait button image!");
    }
  }

  public void update(int mouseX, int mouseY) {
    isHovered = (mouseX >= positionX &&
      mouseX <= positionX + width &&
      mouseY >= positionY &&
      mouseY <= positionY + height);
  }

  public boolean isClicked(int mouseX, int mouseY) {
    return (
      mouseX >= positionX &&
      mouseX <= positionX + width &&
      mouseY >= positionY &&
      mouseY <= positionY + height
    );
  }

  public void draw(Graphics2D g2) {
    if (image != null) {
      if (isHovered) {
        g2.setComposite(
          AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f)
        );
      } else {
        g2.setComposite(
          AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)
        );
      }

      g2.drawImage(image, positionX, positionY, width, height, null);

      g2.setComposite(
        AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)
      );
    }
  }
}
