package com.bambooland.bambolandtrees;

import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.Material;

import java.util.*;

public class TreeFinder {

    private static final int MAX_LOGS = 1024;
    private static final int MAX_LEAVES = 2000;
    private static final int MAX_LEAF_DIST = 6;
    private static final int MAX_HORIZONTAL_BUDGET = 30;
    private static final int COST_DOWN = 1;

    public static class TreeResult {
        public final List<Block> woodBlocks = new ArrayList<>();
        public final List<Block> leafBlocks = new ArrayList<>();
    }

    private static class WoodNode {
        final Block block;
        final int budget;

        WoodNode(Block block, int budget) {
            this.block = block;
            this.budget = budget;
        }
    }

    /**
     * Verifica si el bloque es parte de un árbol (tiene hojas cerca).
     */
    public static boolean isTree(Block origin) {
        Set<Block> visited = new HashSet<>();
        Queue<Block> queue = new LinkedList<>();
        queue.add(origin);
        visited.add(origin);

        int checked = 0;
        while (!queue.isEmpty() && checked < 64) {
            Block current = queue.poll();
            checked++;

            for (Block neighbor : get26Neighbors(current)) {
                if (TreeMaterials.isLeaf(neighbor.getType())) return true;
            }

            for (Block neighbor : getFaceNeighbors(current)) {
                if (!visited.contains(neighbor) && TreeMaterials.isWood(neighbor.getType())) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        return false;
    }

    public static TreeResult findTree(Block origin) {
        TreeResult result = new TreeResult();
        Map<Block, Integer> visitedWood = new HashMap<>();
        Queue<WoodNode> woodQueue = new LinkedList<>();

        woodQueue.add(new WoodNode(origin, 0));
        visitedWood.put(origin, 0);

        int originY = origin.getY();

        while (!woodQueue.isEmpty() && result.woodBlocks.size() < MAX_LOGS) {
            WoodNode node = woodQueue.poll();
            Block current = node.block;
            int currentBudget = node.budget;

            result.woodBlocks.add(current);

            for (Block neighbor : get26Neighbors(current)) {
                if (!TreeMaterials.isWood(neighbor.getType())) continue;

                int dy = neighbor.getY() - current.getY();
                int moveCost = (dy > 0) ? 0 : (dy == 0) ? 1 : COST_DOWN;
                int newBudget = currentBudget + moveCost;

                if (newBudget > MAX_HORIZONTAL_BUDGET) continue;

                Integer prevBudget = visitedWood.get(neighbor);
                if (prevBudget != null && prevBudget <= newBudget) continue;

                if (neighbor.getY() < originY && isIndependentTreeBase(neighbor)) continue;

                visitedWood.put(neighbor, newBudget);
                woodQueue.add(new WoodNode(neighbor, newBudget));
            }
        }

        Set<Block> finalWoodSet = visitedWood.keySet();

        // Fase 2: Hojas
        Set<Block> visitedLeaves = new HashSet<>(finalWoodSet);
        Queue<Block> leafQueue = new LinkedList<>();

        for (Block wood : finalWoodSet) {
            for (Block neighbor : get26Neighbors(wood)) {
                if (!visitedLeaves.contains(neighbor) && TreeMaterials.isLeaf(neighbor.getType()) && leafBelongsToTree(neighbor, finalWoodSet)) {
                    visitedLeaves.add(neighbor);
                    leafQueue.add(neighbor);
                    result.leafBlocks.add(neighbor);
                }
            }
        }

        while (!leafQueue.isEmpty() && result.leafBlocks.size() < MAX_LEAVES) {
            Block leaf = leafQueue.poll();
            for (Block neighbor : get26Neighbors(leaf)) {
                if (!visitedLeaves.contains(neighbor) && TreeMaterials.isLeaf(neighbor.getType()) && leafBelongsToTree(neighbor, finalWoodSet)) {
                    visitedLeaves.add(neighbor);
                    leafQueue.add(neighbor);
                    result.leafBlocks.add(neighbor);
                }
            }
        }
        return result;
    }

    private static boolean isIndependentTreeBase(Block block) {
        Block below = block.getRelative(0, -1, 0);
        Material belowType = below.getType();
        return belowType.isSolid() && !TreeMaterials.isWood(belowType);
    }

    private static boolean leafBelongsToTree(Block leaf, Set<Block> woodSet) {
        BlockData data = leaf.getBlockData();
        int minDist = chebyshevMinDistance(leaf, woodSet);
        if (data instanceof Leaves leavesData) {
            return minDist <= leavesData.getDistance() + 1;
        }
        return minDist <= MAX_LEAF_DIST;
    }

    private static int chebyshevMinDistance(Block leaf, Set<Block> woodSet) {
        int min = Integer.MAX_VALUE;
        for (Block wood : woodSet) {
            int dist = Math.max(Math.max(Math.abs(wood.getX() - leaf.getX()), Math.abs(wood.getY() - leaf.getY())), Math.abs(wood.getZ() - leaf.getZ()));
            if (dist < min) min = dist;
            if (min == 1) break;
        }
        return min;
    }

    private static List<Block> getFaceNeighbors(Block block) {
        return List.of(block.getRelative(1,0,0), block.getRelative(-1,0,0), block.getRelative(0,1,0), block.getRelative(0,-1,0), block.getRelative(0,0,1), block.getRelative(0,0,-1));
    }

    private static List<Block> get26Neighbors(Block block) {
        List<Block> neighbors = new ArrayList<>(26);
        for (int dx = -1; dx <= 1; dx++)
            for (int dy = -1; dy <= 1; dy++)
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    neighbors.add(block.getRelative(dx, dy, dz));
                }
        return neighbors;
    }
}