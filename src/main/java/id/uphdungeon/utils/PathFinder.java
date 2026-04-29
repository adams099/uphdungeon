/*
 * Code based on:
 * https://github.com/watabou/PD-classes/blob/master/com/watabou/utils/PathFinder.java
 *
 * We use the referred code for some logic, and adjust logic based on our game repository.
 *
 * We embed the copyright below, but the code is not 1:1 of the referred code.
 */

/*
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>
 */

package id.uphdungeon.utils;

import id.uphdungeon.entity.Entity;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class PathFinder {
  public static int[] distance;
  private static int[] queue;
  private static int size = 0;
  private static int width = 0;
  private static int[] dir;

  public static void setMapSize(int width, int height) {
    int size = width * height;
    if (PathFinder.size != size) {
      PathFinder.size = size;
      PathFinder.width = width;
      distance = new int[size];
      queue = new int[size];
      dir = new int[] {-1, +1, -width, +width, -width - 1, -width + 1, +width - 1, +width + 1,};
    }
  }

  public static Path find(int from, int to, boolean[] passable) {
    if (from < 0 || from >= size || to < 0 || to >= size) {
      return null;
    }
    if (!buildDistanceMap(from, to, passable)) {
      return null;
    }
    Path result = new Path();
    int s = from;
    // From the starting position we are moving downwards (towards lower distance),
    // until we reach the target point
    do {
      int minD = distance[s];
      int mins = s;
      for (int i = 0; i < dir.length; i++) {
        int n = s + dir[i];
        if (n >= 0 && n < size && isAdjacent(s, n)) {
          int thisD = distance[n];
          if (thisD < minD) {
            minD = thisD;
            mins = n;
          }
        }
      }

      // nambah ini buat jaga2
      if (mins == s) break;

      s = mins;
      result.add(s);
    } while (s != to);
    return result;
  }

  private static boolean isAdjacent(int a, int b) {
    int ax = a % width;
    int ay = a / width;
    int bx = b % width;
    int by = b / width;
    return Math.abs(ax - bx) <= 1 && Math.abs(ay - by) <= 1;
  }

  private static boolean buildDistanceMap(int from, int to, boolean[] passable) {
    if (from == to) {
      return false;
    }
    Arrays.fill(distance, Integer.MAX_VALUE);
    boolean pathFound = false;
    int head = 0;
    int tail = 0;
    // Add to queue
    // ini bfs, mulai dari 'to'
    queue[tail++] = to;
    distance[to] = 0;
    while (head < tail) {
      // Remove from queue
      int step = queue[head++];
      if (step == from) {
        pathFound = true;
        break;
      }
      int nextDistance = distance[step] + 1;
      for (int i = 0; i < dir.length; i++) {
        int n = step + dir[i];
        if (n >= 0 && n < size && isAdjacent(step, n)) {
          if (n == from || (passable[n] && (distance[n] > nextDistance))) {
            // Add to queue
            queue[tail++] = n;
            distance[n] = nextDistance;
          }
        }
      }
    }
    return pathFound;
  }

  public static int getStep(int from, int to, boolean[] passable) {
    if (from < 0 || from >= size || to < 0 || to >= size) {
      return -1;
    }
    if (!buildDistanceMap(from, to, passable)) {
      return -1;
    }

    int minD = distance[from];
    int best = from;

    for (int i = 0; i < dir.length; i++) {
      int n = from + dir[i];
      if (n >= 0 && n < size && isAdjacent(from, n)) {
        int thisD = distance[n];
        if (thisD < minD) {
          minD = thisD;
          best = n;
        }
      }
    }

    return best == from ? -1 : best;
  }

  public static int getIndex(int x, int y, int tileSize, int width) {
    return (x / tileSize) + (y / tileSize) * width;
  }

  public static boolean[] buildPassableMap(int width, int height, int tileSize,
      List<? extends Entity> entities, Entity exclude) {
    boolean[] passable = new boolean[width * height];
    Arrays.fill(passable, true);
    for (Entity e : entities) {
      if (!e.isDead && e != exclude) {
        int index = getIndex(e.x, e.y, tileSize, width);
        if (index >= 0 && index < passable.length) passable[index] = false;
      }
    }
    return passable;
  }

  public static int getGridDistance(int x1, int y1, int x2, int y2, int tileSize) {
    int dx = Math.abs(x1 - x2) / tileSize;
    int dy = Math.abs(y1 - y2) / tileSize;
    return Math.max(dx, dy);
  }

  public static class Path extends LinkedList<Integer> {
    private static final long serialVersionUID = 1L;
  }
}
