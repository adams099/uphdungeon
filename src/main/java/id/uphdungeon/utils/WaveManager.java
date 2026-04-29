package id.uphdungeon.utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import id.uphdungeon.GamePanel;
import id.uphdungeon.entity.Dragon;
import id.uphdungeon.entity.Entity;
import id.uphdungeon.entity.Rat;
import id.uphdungeon.entity.Skeleton;

public class WaveManager {
  private final GamePanel gamePanel;
  private final Random random = new Random();
  private final List<WaveConfig> waves = new ArrayList<>();

  private int currentWaveIndex = 0;
  private int countdown = 0;
  private boolean isWaitingForWave = false;
  private boolean waveInProgress = false;

  private static class WaveConfig {
    final int rats, skeletons, dragons;
    final boolean isDefaultSpawn;

    WaveConfig(int rats, int skeletons, int dragons, boolean isDefaultSpawn) {
      this.rats = rats;
      this.skeletons = skeletons;
      this.dragons = dragons;
      this.isDefaultSpawn = isDefaultSpawn;
    }
  }

  public WaveManager(GamePanel gamePanel) {
    // Waves:
    // Level 1: 2 rats, 1 skeleton
    // Level 2: 2 rats, 2 skeleton
    // Level 3: 1 rats, 3 skeleton
    // Level 4: 4 skeleton
    // Level 5: 1 boss dragon
    this.gamePanel = gamePanel;
    waves.add(new WaveConfig(2, 1, 0, true));
    waves.add(new WaveConfig(2, 2, 0, false));
    waves.add(new WaveConfig(1, 3, 0, false));
    waves.add(new WaveConfig(0, 4, 0, false));
    waves.add(new WaveConfig(0, 0, 1, false));
  }

  public void startFirstWave() {
    gamePanel.addLogMessage("Wave 1: Start!", Color.RED);
    spawnWave(waves.get(0));
    waveInProgress = true;
  }

  public void update() {
    if (isWaitingForWave || !waveInProgress) return;

    boolean enemiesRemaining = gamePanel.entities.stream()
        .anyMatch(entity -> !(entity instanceof id.uphdungeon.entity.Player) && !entity.isDead);

    if (!enemiesRemaining && !gamePanel.entities.isEmpty()) {
      waveInProgress = false;
      if (currentWaveIndex + 1 < waves.size()) {
        isWaitingForWave = true;
        countdown = 5;
        gamePanel.addLogMessage("All enemies defeated!", Color.YELLOW);
        gamePanel.addLogMessage("Next wave in " + countdown + " initiatives...", Color.YELLOW);
      } else {
        gamePanel.addLogMessage("Dungeon Cleared!", Color.GREEN);
      }
    }
  }

  public void tickCountdown() {
    if (!isWaitingForWave) return;

    if (--countdown > 0) {
      gamePanel.addLogMessage("Wave approaching in " + countdown + "...", Color.YELLOW);
    } else {
      isWaitingForWave = false;
      currentWaveIndex++;
      gamePanel.addLogMessage("Wave " + (currentWaveIndex + 1) + " approaching!", Color.RED);
      spawnWave(waves.get(currentWaveIndex));
      waveInProgress = true;
      gamePanel.triggerScreenShake();
    }
  }

  private void spawnWave(WaveConfig config) {
    if (config.isDefaultSpawn) {
      gamePanel.entities
          .add(new Skeleton(gamePanel, gamePanel.tileSize * 5, gamePanel.tileSize * 5));
      gamePanel.entities.add(new Rat(gamePanel, gamePanel.tileSize * 8, gamePanel.tileSize * 2));
      gamePanel.entities.add(new Rat(gamePanel, gamePanel.tileSize * 10, gamePanel.tileSize * 10));
      return;
    }

    for (int i = 0; i < config.rats; i++)
      spawnAtEdge(new Rat(gamePanel, 0, 0));
    for (int i = 0; i < config.skeletons; i++)
      spawnAtEdge(new Skeleton(gamePanel, 0, 0));
    for (int i = 0; i < config.dragons; i++)
      spawnAtEdge(new Dragon(gamePanel, 0, 0));
  }

  private void spawnAtEdge(Entity entity) {
    int edgePosition = random.nextInt(4);
    int col = 0, row = 0;
    // 0 atas
    // 1 kanan
    // 2 bawah
    // 3 kiri
    switch (edgePosition) {
      case 0:
        col = random.nextInt(gamePanel.maxScreenCol);
        row = 0;
        break;
      case 1:
        col = gamePanel.maxScreenCol - 1;
        row = random.nextInt(gamePanel.maxScreenRow);
        break;
      case 2:
        col = random.nextInt(gamePanel.maxScreenCol);
        row = gamePanel.maxScreenRow - 1;
        break;
      case 3:
        col = 0;
        row = random.nextInt(gamePanel.maxScreenRow);
        break;
    }

    entity.x = col * gamePanel.tileSize;
    entity.y = row * gamePanel.tileSize;
    gamePanel.entities.add(entity);
  }

  public boolean isWaitingForWave() {
    return isWaitingForWave;
  }
}
