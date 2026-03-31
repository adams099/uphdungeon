package id.uphdungeon.entity;

import id.uphdungeon.GamePanel;
import java.awt.Color;

public class Rat extends Enemy {
    public Rat(GamePanel gamePanel, int startX, int startY, int dirX, int dirY) {
        super(gamePanel, startX, startY, dirX, dirY, Color.DARK_GRAY, 10, 1, 2);
    }
}
