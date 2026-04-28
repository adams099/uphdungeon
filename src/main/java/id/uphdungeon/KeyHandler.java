package id.uphdungeon;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
  public boolean upPressed, downPressed, leftPressed, rightPressed, qPressed;
  public boolean wasUpPressed, wasDownPressed, wasLeftPressed, wasRightPressed, wasWaitPressed;
  public boolean moveTriggered, waitTriggered;

  @Override
  public void keyTyped(KeyEvent e) {
    // TODO more handler
  }

  @Override
  public void keyPressed(KeyEvent e) {
    int code = e.getKeyCode();
    if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
      upPressed = true;
      wasUpPressed = true;
    }
    if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
      leftPressed = true;
      wasLeftPressed = true;
    }
    if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
      downPressed = true;
      wasDownPressed = true;
    }
    if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
      rightPressed = true;
      wasRightPressed = true;
    }
    if (code == KeyEvent.VK_Q) {
      qPressed = true;
      wasWaitPressed = true;
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
    int code = e.getKeyCode();
    if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
      upPressed = false;
    }
    if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
      leftPressed = false;
    }
    if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
      downPressed = false;
    }
    if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
      rightPressed = false;
    }
    if (code == KeyEvent.VK_Q) {
      qPressed = false;
    }

    // triggers move only when all movement keys are released
    if (!upPressed && !downPressed && !leftPressed && !rightPressed) {
      if (wasUpPressed || wasDownPressed || wasLeftPressed || wasRightPressed) {
        moveTriggered = true;
      }
    }

    if (!qPressed) {
      if (wasWaitPressed) {
        waitTriggered = true;
      }
    }
  }

  public void consumeAction() {
    moveTriggered = false;
    wasUpPressed = false;
    wasDownPressed = false;
    wasLeftPressed = false;
    wasRightPressed = false;

    waitTriggered = false;
    wasWaitPressed = false;
  }
}
