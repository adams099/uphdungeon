package id.uphdungeon.potion;

import id.uphdungeon.entity.Player;
import id.uphdungeon.entity.animation.Animation;
import id.uphdungeon.entity.animation.PotionSpriteManager;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

// Base class for all potions in the game
public abstract class Potion {
    // Number of initiative turns before potion respawns after being picked up
    public static final int RESPAWN_TURNS = 25;
    public int x, y; // Position in pixels
    public boolean isActive = false; // True when this potion is visible and can be picked up
    private int respawnCounter = 0; // For counting down turns until respawn after pickup
    private final Animation animation;
    private final int tileSize;

    // Constructor initializes animation and tile size
    public Potion(int tileSize) {
        this.tileSize = tileSize;
        PotionSpriteManager spriteManager = new PotionSpriteManager();
        this.animation = spriteManager.getIdleAnimation();
    }

    public abstract void applyEffect(Player player);

    // Updates the potion animation if active
    public void updateAnimation() {
        if (isActive) {
            animation.update();
        }
    }

    // Draws the potion sprite if active
    public void draw(Graphics2D g2) {
        if (!isActive)
            return;
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.drawImage(animation.getCurrentFrame(), x, y, tileSize, tileSize, null);
    }

    // Places the potion at the given pixel position and marks it active
    public void spawn(int x, int y) {
        this.x = x;
        this.y = y;
        this.isActive = true;
        animation.reset();
    }

    // Marks potion inactive and starts the respawn countdown
    public void pickup() {
        isActive = false;
        respawnCounter = RESPAWN_TURNS;
    }

    // Decrements respawn counter if inactive, returns true if potion should respawn
    public boolean tickRespawn() {
        if (isActive || respawnCounter <= 0)
            return false;
        respawnCounter--;
        return respawnCounter == 0;
    }
}