package edu.asu.cse464.graphparser;

import java.util.*;


public class BreadthFirstSearch implements GraphSearch {
    private final GraphParser graph;

    public BreadthFirstSearch(GraphParser graph) {
        this.graph = graph;
    }

    @Override
    public List<String> search(String src, String dst){
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        Map<String, String> prev = new HashMap<>();

        List<String> BFSpath = new ArrayList<>();

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
            BFSpath.add(src);
            return BFSpath;
        }

        //Start Logic
        queue.add(src);
        visited.add(src);
        while (!queue.isEmpty()){
            //Remove Node in the queue
            String currentNode = queue.remove();


            //Check if arrived at the destination
            if (currentNode.equals(dst)){
                break;
            }
            for (String neighbor : graph.getNeighbors(currentNode)){
                if (!visited.contains(neighbor)){
                    visited.add(neighbor);
                    prev.put(neighbor, currentNode);
                    queue.add(neighbor);
                }

            }
        }

        //Check if it reached the destination
        if (!prev.containsKey(dst)){
            return null;
        }

        //Find the BFS
        String current = dst;
        while (current != null){
            BFSpath.add(current);
            current = prev.get(current);
        }

        //Reverse the path so it goes from src to dst
        Collections.reverse(BFSpath);
        return BFSpath;


    }


}