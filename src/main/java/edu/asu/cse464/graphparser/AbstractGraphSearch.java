package edu.asu.cse464.graphparser;

import java.util.*;

/**
 * Abstract base class implementing the Template Pattern for graph search algorithms.
 * This class defines the common algorithm structure while allowing subclasses to
 * implement specific node retrieval strategies (BFS uses Queue, DFS uses Stack, etc.).
 */
public abstract class AbstractGraphSearch implements GraphSearch {
    protected final GraphParser graphParser;

    public AbstractGraphSearch(GraphParser graphParser) {
        this.graphParser = graphParser;
    }

    /**
     * Template method that defines the overall search algorithm structure.
     * This method implements the common steps shared by all graph search algorithms.
     */
    @Override
    public List<String> search(String src, String dst) {
        // Step 1: Validate inputs
        if (!isValidInput(src, dst)) {
            return null;
        }

        // Step 2: Check if source equals destination
        if (src.equals(dst)) {
            List<String> path = new ArrayList<>();
            path.add(src);
            return path;
        }

        // Step 3: Initialize data structures
        Set<String> visited = new HashSet<>();
        Map<String, String> predecessorMap = new HashMap<>();

        // Step 4: Initialize the frontier (algorithm-specific)
        initializeFrontier(src);
        visited.add(src);

        // Step 5: Main search loop
        while (!isFrontierEmpty()) {
            // Get next node (algorithm-specific: BFS uses queue, DFS uses stack)
            String currentNode = getNextNode();

            // Check if we reached the destination
            if (currentNode.equals(dst)) {
                break;
            }

            // Process neighbors
            for (String neighbor : graphParser.getNeighbors(currentNode)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    predecessorMap.put(neighbor, currentNode);
                    addToFrontier(neighbor);
                }
            }
        }

        // Step 6: Check if destination was reached
        if (!predecessorMap.containsKey(dst)) {
            return null;
        }

        // Step 7: Reconstruct path
        return reconstructPath(dst, predecessorMap);
    }

    /**
     * Validates that the input parameters are valid.
     *
     * @param src source node
     * @param dst destination node
     * @return true if inputs are valid, false otherwise
     */
    protected boolean isValidInput(String src, String dst) {
        if (src == null || dst == null) {
            return false;
        }
        if (!graphParser.doesNodeExists(src) || !graphParser.doesNodeExists(dst)) {
            return false;
        }
        return true;
    }

    /**
     * Reconstructs the path from source to destination using the predecessor map.
     *
     * @param dst destination node
     * @param predecessorMap map of node to its predecessor
     * @return list of nodes representing the path from source to destination
     */
    protected List<String> reconstructPath(String dst, Map<String, String> predecessorMap) {
        List<String> path = new ArrayList<>();
        String current = dst;
        while (current != null) {
            path.add(current);
            current = predecessorMap.get(current);
        }
        Collections.reverse(path);
        return path;
    }

    // Abstract methods to be implemented by subclasses (algorithm-specific behavior)

    /**
     * Initializes the frontier data structure with the starting node.
     * BFS will use a Queue, DFS will use a Stack.
     *
     * @param src source node to start the search
     */
    protected abstract void initializeFrontier(String src);

    /**
     * Checks if the frontier is empty.
     *
     * @return true if frontier is empty, false otherwise
     */
    protected abstract boolean isFrontierEmpty();

    /**
     * Gets and removes the next node from the frontier.
     * BFS: removes from front of queue (FIFO)
     * DFS: pops from stack (LIFO)
     *
     * @return the next node to process
     */
    protected abstract String getNextNode();

    /**
     * Adds a node to the frontier.
     *
     * @param node node to add to the frontier
     */
    protected abstract void addToFrontier(String node);
}
