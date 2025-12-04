package edu.asu.cse464.graphparser;

import java.util.*;

/**
 * Random Walk Search implementation.
 * At each step, randomly picks ONE unvisited neighbor and moves to it.
 * This is a true random walk (single active path), not a frontier-based search.
 */
public class RandomWalkSearch extends AbstractGraphSearch {

    private final Random random;
    private static final int MAX_STEPS = 1000; // safety bound against infinite walks

    public RandomWalkSearch(GraphParser graphParser) {
        super(graphParser);
        this.random = new Random();
    }

    /**
     * Constructor with seed for deterministic tests / demos.
     */
    public RandomWalkSearch(GraphParser graphParser, long seed) {
        super(graphParser);
        this.random = new Random(seed);
    }

    /**
     * Random-walk-based search. Overrides the template method because
     * random walk does not use a frontier of multiple nodes like BFS / DFS.
     *
     * It still reuses:
     *  - isValidInput(...) from AbstractGraphSearch
     *  - reconstructPath(...) from AbstractGraphSearch
     *  - graphParser.getNeighbors(...)
     */
    @Override
    public List<String> search(String src, String dst) {
        System.out.println();
        System.out.println("=== Random Walk Search: " + src + " -> " + dst + " ===");

        // 1. Validate input
        if (!isValidInput(src, dst)) {
            System.out.println("Invalid input for random walk search.");
            return null;
        }

        // 2. Trivial case: src == dst
        if (src.equals(dst)) {
            List<String> path = new ArrayList<>();
            path.add(src);
            System.out.println("Trivial path: " + path);
            return path;
        }

        // 3. Random walk from src
        Set<String> visited = new HashSet<>();
        Map<String, String> predecessorMap = new HashMap<>();

        String current = src;
        visited.add(current);

        for (int step = 1; step <= MAX_STEPS; step++) {
            System.out.println("  Step " + step + ": at node " + current);

            // Reached destination
            if (current.equals(dst)) {
                System.out.println("  Reached destination.");
                List<String> path = reconstructPath(dst, predecessorMap);
                System.out.println("Path found: " + path);
                return path;
            }

            // Get neighbors from GraphParser
            Set<String> neighbors = graphParser.getNeighbors(current);

            // Filter to unvisited neighbors
            List<String> unvisitedNeighbors = new ArrayList<>();
            for (String neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    unvisitedNeighbors.add(neighbor);
                }
            }

            // Dead end: no unvisited neighbors to walk to
            if (unvisitedNeighbors.isEmpty()) {
                System.out.println("  Dead end: no unvisited neighbors.");
                return null;
            }

            // Choose one neighbor at random
            String chosen = unvisitedNeighbors.get(random.nextInt(unvisitedNeighbors.size()));
            System.out.println("    Unvisited neighbors: " + unvisitedNeighbors);
            System.out.println("    Chosen next node: " + chosen);

            // Move to chosen neighbor
            visited.add(chosen);
            predecessorMap.put(chosen, current);
            current = chosen;
        }

        // Safety stop: too many steps without reaching dst
        System.out.println("  Gave up after " + MAX_STEPS + " steps without reaching destination.");
        return null;
    }

    // These are unused for random walk (we override search), but must be
    // implemented because AbstractGraphSearch declares them abstract.

    @Override
    protected void initializeFrontier(String src) {
        // Not used in random walk
    }

    @Override
    protected boolean isFrontierEmpty() {
        // Not used in random walk
        return true;
    }

    @Override
    protected String getNextNode() {
        // Not used in random walk
        return null;
    }

    @Override
    protected void addToFrontier(String node) {
        // Not used in random walk
    }
}
