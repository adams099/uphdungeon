package id.uphdungeon.entity;

import id.uphdungeon.GamePanel;
import java.awt.Color;

public class Skeleton extends Enemy {
  public Skeleton(GamePanel gamePanel, int startX, int startY, int dirX, int dirY) {
    super(gamePanel, startX, startY, dirX, dirY, Color.LIGHT_GRAY, 25, 3, 5);
  }
}
