package id.uphdungeon.entity.animation;

// Interface for enemy animation state, to determine if the enemy is in death or walk state
public interface EnemyAnimationState {
  // Method to check if the enemy is in death state
  boolean isDeath();

  // Method to check if the enemy is in walk state
  boolean isWalk();
}
