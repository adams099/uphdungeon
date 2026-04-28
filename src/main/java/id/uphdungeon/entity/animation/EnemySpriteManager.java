package id.uphdungeon.entity.animation;

// Interface for managing enemy sprites and methods to retrieve animations based on enemy
public interface EnemySpriteManager {
  // Returns the animation corresponding to the given enemy animation state
  Animation getAnimation(EnemyAnimationState state);

  // Returns the spawn animation state for this enemy
  EnemyAnimationState getSpawnState();

  // Returns the death animation state for this enemy
  EnemyAnimationState getDeathState();

  // Returns the walk-left state for this enemy
  EnemyAnimationState getWalkLeftState();

  // Returns the walk-right state for this enemy
  EnemyAnimationState getWalkRightState();

  // Returns the walk-up state for this enemy
  EnemyAnimationState getWalkUpState();

  // Returns the walk-down state for this enemy
  EnemyAnimationState getWalkDownState();

  // Returns the attack-left state for this enemy
  EnemyAnimationState getAttackLeftState();

  // Returns the attack-right state for this enemy
  EnemyAnimationState getAttackRightState();
}