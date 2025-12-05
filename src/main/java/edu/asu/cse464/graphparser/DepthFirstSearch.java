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
}