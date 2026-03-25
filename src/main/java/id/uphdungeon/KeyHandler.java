package id.uphdungeon;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
  public boolean upPressed, downPressed, leftPressed, rightPressed;
  public boolean wasUpPressed, wasDownPressed, wasLeftPressed, wasRightPressed;
  public boolean moveTriggered;

  @Override
  public void keyTyped(KeyEvent e) {
    // TODO more handler
  }

  @Override
  public void keyPressed(KeyEvent e) {
    int code = e.getKeyCode();
    if (code == KeyEvent.VK_W) {
      upPressed = true;
      wasUpPressed = true;
    }
    if (code == KeyEvent.VK_A) {
      leftPressed = true;
      wasLeftPressed = true;
    }
    if (code == KeyEvent.VK_S) {
      downPressed = true;
      wasDownPressed = true;
    }
    if (code == KeyEvent.VK_D) {
      rightPressed = true;
      wasRightPressed = true;
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
    int code = e.getKeyCode();
    if (code == KeyEvent.VK_W) {
      upPressed = false;
    }
    if (code == KeyEvent.VK_A) {
      leftPressed = false;
    }
    if (code == KeyEvent.VK_S) {
      downPressed = false;
    }
    if (code == KeyEvent.VK_D) {
      rightPressed = false;
    }

    // Trigger move only when all movement keys are released
    if (!upPressed && !downPressed && !leftPressed && !rightPressed) {
        if (wasUpPressed || wasDownPressed || wasLeftPressed || wasRightPressed) {
            moveTriggered = true;
        }
    }
  }

  public void consumeMove() {
      moveTriggered = false;
      wasUpPressed = false;
      wasDownPressed = false;
      wasLeftPressed = false;
      wasRightPressed = false;
  }
}
