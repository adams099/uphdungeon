package id.uphdungeon.entity;

import java.util.Random;

public class Entity {
    public int x, y;
    public int speed;
    
    public int maxHealth;
    public int health;
    public int minDamage;
    public int maxDamage;
    public boolean isDead = false;
    
    public void attack(Entity target) {
        Random rand = new Random();
        int damage = rand.nextInt(maxDamage - minDamage + 1) + minDamage;
        target.health -= damage;
        
        System.out.println(this.getClass().getSimpleName() + " attacked " + target.getClass().getSimpleName() + " for " + damage + " damage! (" + target.health + "/" + target.maxHealth + " HP left)");
        
        if (target.health <= 0) {
            target.isDead = true;
            System.out.println(target.getClass().getSimpleName() + " died!");
        }
    }
}