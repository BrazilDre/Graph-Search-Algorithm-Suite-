package edu.asu.cse464.graphparser;
import java.util.*;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        GraphParser gp = new GraphParser();

        try {
            System.out.println("=== CSE 464 Graph Parser Demo ===\n");

            // Feature 1: Parse Graph
            System.out.println("Feature 1: Parsing graph from input.dot");
            gp.parseGraph("src/main/resources/input.dot");
            System.out.println(gp.toString());

            // Feature 2: Add Nodes
            System.out.println("\nFeature 2: Adding nodes x and y");
            gp.addNode("x");
            gp.addNode("y");
            System.out.println("Node count after adding: " + gp.getNodeCount());

            // Feature 3: Add Edge
            System.out.println("\nFeature 3: Adding edge x -> y");
            gp.addEdge("x", "y");
            System.out.println("Edge count after adding: " + gp.getEdgeCount());

            // Feature 4: Output
            System.out.println("\nFeature 4: Outputting graph");
            gp.outputDOTGraph("output.dot");
            gp.outputGraphics("output.png", "png");
            System.out.println("Created: output.dot and output.png");
             
            //Feature 5: BFS Algorithm
            System.out.println("\nFeature 5: Using BFS algorithm");
            BreadthFirstSearch bfs = new BreadthFirstSearch(gp);
            List<String> BFSresult = bfs.search("A", "E");
            if(BFSresult == null) {
                System.out.println("No path found");
            } else {
                System.out.println("BFS path:" + BFSresult);
            }
          
            //Feature 6: DFS algorithm
            System.out.println("\nFeature 6: Using DFS algorithm");
            DepthFirstSearch dfs = new DepthFirstSearch(gp);
            List<String> DFSresult = dfs.search("A", "E");
            if(DFSresult == null) {
                System.out.println("No path found");
            } else {
                System.out.println("DFS path:" + DFSresult);
            }

            //Complete
            System.out.println("\n=== Demo Complete ===");

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}