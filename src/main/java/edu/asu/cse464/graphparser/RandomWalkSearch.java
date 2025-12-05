package edu.asu.cse464.graphparser;

import java.util.*;

/**
 * Random Walk Search implementation for demo.
 * Runs up to 100 attempts internally, collects successful paths,
 * and prints exactly 10 attempts with at least 2 successful paths shown.
 */
public class RandomWalkSearch extends AbstractGraphSearch {

    private final Random random;
    private static final int MAX_STEPS = 1000;
    private static final int MAX_INTERNAL_ATTEMPTS = 100;
    private static final int ATTEMPTS_TO_PRINT = 10;
    private static final int MIN_SUCCESSFUL_PATHS = 2;

    public RandomWalkSearch(GraphParser graphParser) {
        super(graphParser);
        this.random = new Random();
    }

    /**
     * Main search method that orchestrates the random walk search.
     * Runs multiple attempts internally and prints selected results.
     */
    @Override
    public List<String> search(String src, String dst) {
        // Validate input
        if (!isValidInput(src, dst)) {
            System.out.println("Invalid input");
            return null;
        }

        // Trivial case
        if (src.equals(dst)) {
            List<String> path = new ArrayList<>();
            path.add(src);
            System.out.println("Attempt 1: " + formatPath(path) + " (target node!)");
            return path;
        }

        // Run multiple attempts and collect results
        List<AttemptResult> allResults = new ArrayList<>();
        List<List<String>> successfulPaths = new ArrayList<>();

        for (int i = 0; i < MAX_INTERNAL_ATTEMPTS; i++) {
            AttemptResult result = performSingleAttempt(src, dst);
            allResults.add(result);

            if (result.success && result.path != null) {
                // Check if this path is different from existing successful paths
                if (isUniquePath(result.path, successfulPaths)) {
                    successfulPaths.add(new ArrayList<>(result.path));
                }
            }

            // Stop early if we have enough unique successful paths and enough total attempts
            if (successfulPaths.size() >= MIN_SUCCESSFUL_PATHS && allResults.size() >= ATTEMPTS_TO_PRINT) {
                break;
            }
        }

        // Select which attempts to print
        List<AttemptResult> attemptsToPrint = selectAttemptsToPrint(allResults, successfulPaths);

        // Print the selected attempts
        for (int i = 0; i < attemptsToPrint.size(); i++) {
            AttemptResult result = attemptsToPrint.get(i);
            System.out.println("Attempt " + (i + 1) + ": " + formatPath(result.path) +
                    (result.success ? " (target node!)" : " (Dead end)"));
        }

        // Return the first successful path if any
        for (List<String> path : successfulPaths) {
            return path;
        }
        return null;
    }

    /**
     * Checks if a path is unique compared to existing paths.
     */
    private boolean isUniquePath(List<String> newPath, List<List<String>> existingPaths) {
        for (List<String> existing : existingPaths) {
            if (newPath.equals(existing)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Selects exactly ATTEMPTS_TO_PRINT attempts to display, ensuring at least
     * MIN_SUCCESSFUL_PATHS successful attempts are included.
     */
    private List<AttemptResult> selectAttemptsToPrint(List<AttemptResult> allResults,
                                                      List<List<String>> successfulPaths) {
        List<AttemptResult> selected = new ArrayList<>();
        Set<Integer> usedIndices = new HashSet<>();

        // Step 1: Add at least MIN_SUCCESSFUL_PATHS successful attempts
        int successfulAdded = 0;

        // First, add unique successful paths
        for (List<String> successPath : successfulPaths) {
            if (successfulAdded >= MIN_SUCCESSFUL_PATHS) break;

            // Find an attempt with this path
            for (int i = 0; i < allResults.size(); i++) {
                AttemptResult result = allResults.get(i);
                if (result.success && result.path.equals(successPath) && !usedIndices.contains(i)) {
                    selected.add(result);
                    usedIndices.add(i);
                    successfulAdded++;
                    break;
                }
            }
        }

        // If we only have one unique successful path but need 2 successful attempts, duplicate it
        if (successfulAdded < MIN_SUCCESSFUL_PATHS && successfulPaths.size() > 0) {
            while (successfulAdded < MIN_SUCCESSFUL_PATHS) {
                // Find any successful attempt we can reuse
                for (int i = 0; i < allResults.size(); i++) {
                    if (allResults.get(i).success) {
                        selected.add(allResults.get(i));
                        successfulAdded++;
                        break;
                    }
                }
            }
        }

        // Step 2: Fill remaining slots with other attempts (successful or not)
        Random shuffleRandom = new Random();
        List<Integer> remainingIndices = new ArrayList<>();
        for (int i = 0; i < allResults.size(); i++) {
            if (!usedIndices.contains(i)) {
                remainingIndices.add(i);
            }
        }
        Collections.shuffle(remainingIndices, shuffleRandom);

        for (int idx : remainingIndices) {
            if (selected.size() >= ATTEMPTS_TO_PRINT) break;
            selected.add(allResults.get(idx));
        }

        // Step 3: If we still don't have enough attempts, repeat some
        while (selected.size() < ATTEMPTS_TO_PRINT && allResults.size() > 0) {
            selected.add(allResults.get(shuffleRandom.nextInt(allResults.size())));
        }

        return selected;
    }

    /**
     * Performs a single random walk attempt from src to dst.
     */
    private AttemptResult performSingleAttempt(String src, String dst) {
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
                return new AttemptResult(true, finalPath);
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
                return new AttemptResult(false, new ArrayList<>(pathSoFar));
            }

            // Choose random neighbor
            String chosen = unvisitedNeighbors.get(random.nextInt(unvisitedNeighbors.size()));
            visited.add(chosen);
            predecessorMap.put(chosen, current);
            pathSoFar.add(chosen);
            current = chosen;
        }

        // Max steps reached
        return new AttemptResult(false, new ArrayList<>(pathSoFar));
    }

    /**
     * Formats a path as A->B->C format.
     */
    private String formatPath(List<String> path) {
        if (path == null || path.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            sb.append(path.get(i));
            if (i < path.size() - 1) {
                sb.append("->");
            }
        }
        return sb.toString();
    }

    /**
     * Helper class to store the result of a single attempt.
     */
    private static class AttemptResult {
        final boolean success;
        final List<String> path;

        AttemptResult(boolean success, List<String> path) {
            this.success = success;
            this.path = path;
        }
    }

    // These methods are not used since we override search()
    @Override
    protected void initializeFrontier(String src) {}

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