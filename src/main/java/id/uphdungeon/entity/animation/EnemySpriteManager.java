package id.uphdungeon.entity.animation;

// Contract for all enemy sprite managers.
// Each enemy type (Rat, Skeleton, Goblin, etc.) provides its own
// implementation that loads sprites from its own resource folder.
//
// EnemyAnimated depends only on this interface, so adding a new enemy
// requires no changes to EnemyAnimated — only a new SpriteManager
// and a new AnimationState enum.
public interface EnemySpriteManager {

  // Returns the Animation for the given state.
  // Must never return null — fall back to a default animation if state is
  // missing.
  Animation getAnimation(EnemyAnimationState state);

  // Returns the spawn idle state for this enemy.
  // This is the state shown before the enemy moves for the first time.
  EnemyAnimationState getSpawnState();

  // Returns the death animation state for this enemy.
  EnemyAnimationState getDeathState();

  // Returns the walk-left state for this enemy.
  EnemyAnimationState getWalkLeftState();

  // Returns the walk-right state for this enemy.
  EnemyAnimationState getWalkRightState();

  // Returns the walk-up state for this enemy.
  EnemyAnimationState getWalkUpState();

  // Returns the walk-down state for this enemy.
  EnemyAnimationState getWalkDownState();

  // Returns the attack-left state for this enemy.
  EnemyAnimationState getAttackLeftState();

  // Returns the attack-right state for this enemy.
  EnemyAnimationState getAttackRightState();
}