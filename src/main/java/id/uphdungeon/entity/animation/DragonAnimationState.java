package id.uphdungeon.entity.animation;

public enum DragonAnimationState implements EnemyAnimationState {
  WALK_LEFT,
  WALK_RIGHT,
  WALK_UP,
  WALK_DOWN,
  ATTACK_LEFT,
  ATTACK_RIGHT,
  DEATH;

  @Override
  public boolean isDeath() {
    return this == DEATH;
  }

  @Override
  public boolean isWalk() {
    return this == WALK_LEFT || this == WALK_RIGHT || this == WALK_UP || this == WALK_DOWN;
  }
}
