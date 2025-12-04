package edu.asu.cse464.graphparser;
import java.util.*;

import java.io.IOException;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static void runBfsDemo(GraphParser gp, String start, String dest) {
        System.out.println("\n=======Starting BFS algorithm=======");
        BreadthFirstSearch bfs = new BreadthFirstSearch(gp);
        List<String> BFSresult = bfs.search(start, dest);
        if(BFSresult == null) {
            System.out.println("No path found");
        }
        System.out.println("====================================\n");
    }

    private static void runDfsDemo(GraphParser gp, String start, String dest) {
        System.out.println("\n=======Starting DFS algorithm=======");
        DepthFirstSearch dfs = new DepthFirstSearch(gp);
        List<String> DFSresult = dfs.search(start, dest);
        if(DFSresult == null) {
            System.out.println("No path found");
        }
        System.out.println("====================================\n");
    }

    private static void runRandomWalkDemo(GraphParser gp, String start, String dest) {
        System.out.println("\n========== Random Walk Search ==========");
        System.out.println("Running Random Walk multiple times:\n");

        RandomWalkSearch rws = new RandomWalkSearch(gp);
        int successCount = 0;
        int maxAttempts = 5;

        for (int i = 1; i <= maxAttempts; i++) {
            rws.setAttemptNumber(i);
            List<String> result = rws.search(start, dest);

            if (result != null) {
                successCount++;
            }
        }

        System.out.println("\nTotal successful attempts: " + successCount);
        System.out.println("========================================\n");
    }

    public static void main(String[] args) {
        GraphParser gp = new GraphParser();

        try {
            System.out.println("=== CSE 464 Graph Parser Demo ===\n");



            // Feature 1: Parse Graph
            System.out.println("Feature 1: Parsing graph from input.dot");
            gp.parseGraph("src/main/resources/input2.dot");
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

            //Getting source and destination node input
            System.out.println("\n================ Select Nodes ================");

            //Check for non-null inputs and existing nodes for the source
            String sourceNode ="";
            while (sourceNode.isEmpty() || !gp.doesNodeExists(sourceNode)){
                System.out.println("\nEnter source node:");
                sourceNode = scanner.nextLine().trim().toUpperCase();

                if(sourceNode.isEmpty()){
                    System.out.println("⚠️  Source node cannot be empty. Please try again.");
                }else if (!gp.doesNodeExists(sourceNode)) {
                    System.out.println("⚠️  Node '" + sourceNode + "' does not exist in the graph. Please try again.");
                    sourceNode = "";
                }
            }

            //Check for non-null inputs and existing nodes for the source
            String destinationNode = "";
            while (destinationNode.isEmpty() || !gp.doesNodeExists(destinationNode)) {
                System.out.println("\nEnter destination node:");
                destinationNode = scanner.nextLine().trim().toUpperCase();

                if (destinationNode.isEmpty()) {
                    System.out.println("⚠️  Destination node cannot be empty. Please try again.");
                } else if (!gp.doesNodeExists(destinationNode)) {
                    System.out.println("⚠️  Node '" + destinationNode + "' does not exist in the graph. Please try again.");
                    destinationNode = ""; // Reset to continue loop
                }
            }

            System.out.println("\n✓ Valid nodes selected: " + sourceNode + " → " + destinationNode);


            boolean running = true;
            while (running) {
                //Prompt user for algorithm selection
                System.out.println("\n================ Algorith Selection ================");
                System.out.println("\nSelect search algorithm to run");
                System.out.println("1. BFS");
                System.out.println("2. DFS");
                System.out.println("3. Random Walk (Multiple Runs)");
                System.out.println("4. All of them at once");
                System.out.println("5. Exit");
                System.out.print("Enter choice: ");
                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1":
                        runBfsDemo(gp, sourceNode, destinationNode);
                        break;
                    case "2":
                        runDfsDemo(gp, sourceNode, destinationNode);
                        break;
                    case "3":
                        runRandomWalkDemo(gp, sourceNode, destinationNode);
                        break;
                    case "4":
                        System.out.println("\n========== Running All Algorithms ==========\n");
                        runBfsDemo(gp, sourceNode, destinationNode);
                        runDfsDemo(gp, sourceNode, destinationNode);
                        runRandomWalkDemo(gp, sourceNode, destinationNode);
                        System.out.println("===========================================\n");
                        break;
                    case "5":
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter 1, 2, 3, 4, or 5.");
                }
            }

            //Complete
            System.out.println("\n=== Demo Complete ===");

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}