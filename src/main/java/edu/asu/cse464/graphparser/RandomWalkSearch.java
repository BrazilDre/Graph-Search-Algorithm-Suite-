package edu.asu.cse464.graphparser;

import java.util.*;

/**
 * Random Walk Search implementation for demo.
 * Outputs in format: Attempt X: A->B->C (Dead end) or (target node!)
 */
public class RandomWalkSearch extends AbstractGraphSearch {

    private final Random random;
    private static final int MAX_STEPS = 1000;
    private int attemptNumber = 0;
    private boolean silentMode = false;

    public RandomWalkSearch(GraphParser graphParser) {
        super(graphParser);
        this.random = new Random();
    }

    /**
     * Reset attempt counter (useful for multiple runs in demo).
     */
    public void resetAttemptCounter() {
        attemptNumber = 0;
    }

    /**
     * Set attempt number manually (for demo control).
     */
    public void setAttemptNumber(int num) {
        attemptNumber = num;
    }

    @Override
    public List<String> search(String src, String dst) {
        attemptNumber++;

        // Validate input
        if (!isValidInput(src, dst)) {
            System.out.println("Attempt " + attemptNumber + ": Invalid input");
            return null;
        }

        // Trivial case
        if (src.equals(dst)) {
            List<String> path = new ArrayList<>();
            path.add(src);
            printAttempt(path, true);
            return path;
        }

        // Random walk
        Set<String> visited = new HashSet<>();
        Map<String, String> predecessorMap = new HashMap<>();
        List<String> pathSoFar = new ArrayList<>();

        String current = src;
        visited.add(current);
        pathSoFar.add(current);

        for (int step = 1; step <= MAX_STEPS; step++) {
            // Reached destination
            if (current.equals(dst)) {
                List<String> finalPath = reconstructPath(dst, predecessorMap);
                printAttempt(finalPath, true);  // Print successful attempt
                return finalPath;
            }

            // Get unvisited neighbors
            Set<String> neighbors = graphParser.getNeighbors(current);
            List<String> unvisitedNeighbors = new ArrayList<>();
            for (String neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    unvisitedNeighbors.add(neighbor);
                }
            }

            // Dead end
            if (unvisitedNeighbors.isEmpty()) {
                printAttempt(pathSoFar, false);  // Print failed attempt
                return null;
            }

            // Choose random neighbor
            String chosen = unvisitedNeighbors.get(random.nextInt(unvisitedNeighbors.size()));
            visited.add(chosen);
            predecessorMap.put(chosen, current);
            pathSoFar.add(chosen);
            current = chosen;
        }

        // Max steps reached
        printAttempt(pathSoFar, false);  // Print failed attempt
        return null;
    }

    /**
     * Print attempt in format: Attempt X: A->B->C->D (Dead end) or (target node!)
     */
    private void printAttempt(List<String> path, boolean success) {
        if (silentMode) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Attempt ").append(attemptNumber - 1).append(": ");

        for (int i = 0; i < path.size(); i++) {
            sb.append(path.get(i));
            if (i < path.size() - 1) {
                sb.append("->");
            }
        }

        if (success) {
            sb.append(" (target node!)");
        } else {
            sb.append(" (Dead end)");
        }

        System.out.println(sb.toString());
    }

    // These methods are not used since we override search()
    @Override
    protected void initializeFrontier(String src) {}

    public void setSilentMode(boolean silent) {
        this.silentMode = silent;
    }

    @Override
    protected boolean isFrontierEmpty() {
        return true;
    }

    @Override
    protected String getNextNode() {
        return null;
    }

    @Override
    protected void addToFrontier(String node) {}
}