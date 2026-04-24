package id.uphdungeon.entity.animation;

// Enumeration for Rat enemy animation states.
// Rat uses WALK_DOWN as the initial spawn state (shows B2.png, the second frame).
// After first move, holds the last walk frame as directional idle.
public enum RatAnimationState implements EnemyAnimationState {
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
        return this == WALK_LEFT || this == WALK_RIGHT
                || this == WALK_UP || this == WALK_DOWN;
    }
}