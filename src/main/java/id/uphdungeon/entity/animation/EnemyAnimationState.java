package id.uphdungeon.entity.animation;

// Marker interface for all enemy animation state enums.
// Each enemy's state enum (RatAnimationState, SkeletonAnimationState, etc.)
// implements this interface so EnemyAnimated can reference states generically
// without being coupled to any specific enemy type.
//
// Usage example:
//   public enum RatAnimationState implements EnemyAnimationState { ... }
//
// EnemyAnimated then works with EnemyAnimationState references for
// transitionTo(), death state detection, and walk state resolution.
public interface EnemyAnimationState {

  // Returns true if this state is the death animation state.
  // Used by EnemyAnimated.updateFading() to detect when death has begun.
  boolean isDeath();

  // Returns true if this state is a walk state (any direction).
  // Used by EnemyAnimated to decide whether to cache lastWalkFrame.
  boolean isWalk();
}