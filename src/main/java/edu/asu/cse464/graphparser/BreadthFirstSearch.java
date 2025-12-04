package edu.asu.cse464.graphparser;

import java.util.*;

/**
 * Breadth-First Search implementation that extends AbstractGraphSearch.
 * Uses a Queue (FIFO) for the frontier to ensure level-by-level traversal.
 */
public class BreadthFirstSearch extends AbstractGraphSearch {
    private Queue<String> queue;

    public BreadthFirstSearch(GraphParser graphParser) {
        super(graphParser);
    }

    @Override
    protected void initializeFrontier(String src) {
        queue = new LinkedList<>();
        queue.add(src);
    }

    @Override
    protected boolean isFrontierEmpty() {
        return queue.isEmpty();
    }

    @Override
    protected String getNextNode() {
        return queue.remove();
    }

    @Override
    protected void addToFrontier(String node) {
        queue.add(node);
    }
}