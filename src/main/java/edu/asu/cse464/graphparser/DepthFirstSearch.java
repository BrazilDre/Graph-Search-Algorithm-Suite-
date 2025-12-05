package edu.asu.cse464.graphparser;

import java.util.*;

/**
 * Depth-First Search implementation that extends AbstractGraphSearch.
 * Uses a Stack (LIFO) for the frontier to ensure depth-first traversal.
 */
public class DepthFirstSearch extends AbstractGraphSearch {
    private Deque<String> stack;

    public DepthFirstSearch(GraphParser graphParser) {
        super(graphParser);
    }

    @Override
    protected void initializeFrontier(String src) {
        stack = new ArrayDeque<>();
        stack.push(src);
    }

    @Override
    protected boolean isFrontierEmpty() {
        return stack.isEmpty();
    }

    @Override
    protected String getNextNode() {
        return stack.pop(); // LIFO behavior
    }

    @Override
    protected void addToFrontier(String node) {
        stack.push(node);
    }

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
            printPath(path, false);
            printPath(path, true);
            return path;
        }

        // Step 3: Initialize data structures
        Set<String> visited = new HashSet<>();
        Map<String, String> predecessorMap = new HashMap<>();

        // Step 4: Initialize the frontier
        initializeFrontier(src);
        visited.add(src);

        // Print initial path
        List<String> currentPath = new ArrayList<>();
        currentPath.add(src);
        printPath(currentPath, false);

        // Step 5: Main search loop
        while (!isFrontierEmpty()) {
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

                    // Build and print path to this neighbor
                    List<String> pathToNeighbor = buildPath(neighbor, predecessorMap);
                    printPath(pathToNeighbor, false);
                }
            }
        }

        // Step 6: Check if destination was reached
        if (!predecessorMap.containsKey(dst)) {
            return null;
        }

        // Step 7: Reconstruct and print final path
        List<String> finalPath = reconstructPath(dst, predecessorMap);
        printPath(finalPath, true);
        return finalPath;
    }


    private List<String> buildPath(String node, Map<String, String> predecessorMap) {
        List<String> path = new ArrayList<>();
        String current = node;
        while (current != null) {
            path.add(current);
            current = predecessorMap.get(current);
        }
        Collections.reverse(path);
        return path;
    }

    private void printPath(List<String> path, boolean isFinal) {
        StringBuilder sb = new StringBuilder();
        if (!isFinal) {
            sb.append("visiting ");
        }
        sb.append("Path{nodes=[");
        for (int i = 0; i < path.size(); i++) {
            sb.append("Node{").append(path.get(i)).append("}");
            if (i < path.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]}");
        System.out.println(sb.toString());
    }
}
