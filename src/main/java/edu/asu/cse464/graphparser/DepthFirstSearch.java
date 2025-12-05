package edu.asu.cse464.graphparser;

import java.util.*;

public class DepthFirstSearch implements GraphSearch {
    private final GraphParser graph;
    public DepthFirstSearch(GraphParser graph) {
        this.graph = graph;
    }

    @Override
    public List<String> search(String src, String dst) {
        Stack<String> stack = new Stack<>();
        Set<String> visited = new HashSet<>();
        Map<String, String> prev = new HashMap<>();
        List<String> dfsPath = new ArrayList<>();


        //Check for valid inputs
        if (src == null || dst == null) {
            return null;
        }
        //Check if the src and dst nodes exists
        if (!graph.doesNodeExists(src) || !graph.doesNodeExists(dst)){
            return null;
        }
        //Return the node if source == destination
        if( src.equals(dst)){
            dfsPath.add(src);
            return dfsPath;
        }

        //Start Logic
        stack.push(src);
        visited.add(src);
        while (!stack.isEmpty()){
            String currentNode = stack.pop();

            //Check if the currentNode is the destination
            if(currentNode.equals(dst)){
                break;
            }

            for (String neighbor : graph.getNeighbors(currentNode)){
                if(!visited.contains(neighbor)){
                    stack.push(neighbor);
                    visited.add(neighbor);
                    prev.put(neighbor, currentNode);
                }
            }
        }

        //Check if destination was reached
        if(!prev.containsKey(dst)){
            return null;
        }

        //Find the DFS
        String current = dst;
        while(current != null){
            dfsPath.add(current);
            current = prev.get(current);
        }

        //Reverse the path so it goes from src to dst
        Collections.reverse(dfsPath);
        return dfsPath;

    }
}