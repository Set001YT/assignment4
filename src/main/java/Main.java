import graph.*;
import graph.scc.*;
import graph.topo.*;
import graph.dagsp.*;
import com.google.gson.*;
import java.io.*;
import java.util.*;

/**
 * Main class to run all algorithms on datasets
 */
public class Main {

    // CSV writer for metrics
    private static PrintWriter csvWriter;

    public static void main(String[] args) {
        String[] datasets = {
                "small_1", "small_2", "small_3",
                "medium_1", "medium_2", "medium_3",
                "large_1", "large_2", "large_3"
        };

        System.out.println("Assignment 4: Smart City Scheduling");
        System.out.println("====================================\n");

        // Initialize CSV file
        try {
            csvWriter = new PrintWriter(new FileWriter("metrics.csv"));
            writeCSVHeader();
        } catch (IOException e) {
            System.err.println("Error creating CSV file: " + e.getMessage());
            return;
        }

        // Process all datasets
        for (String dataset : datasets) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("Processing: " + dataset + ".json");
            System.out.println("=".repeat(60));

            try {
                processDataset(dataset + ".json", dataset);
            } catch (Exception e) {
                System.err.println("Error processing " + dataset + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Close CSV file
        csvWriter.close();
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Metrics saved to: metrics.csv");
        System.out.println("=".repeat(60));
    }

    /**
     * Write CSV header
     */
    private static void writeCSVHeader() {
        csvWriter.println("dataset,vertices,edges,sccs,scc_time_ms,scc_dfs_visits," +
                "topo_time_ms,topo_operations,has_source,shortest_path_time_ms," +
                "shortest_path_relaxations,longest_path_length,longest_path_time_ms," +
                "longest_path_relaxations");
        csvWriter.flush();
    }

    private static void processDataset(String filename, String datasetName) throws IOException {
        // Read JSON file using Gson
        Gson gson = new Gson();
        JsonObject json = gson.fromJson(new FileReader(filename), JsonObject.class);

        int n = json.get("n").getAsInt();
        JsonArray edges = json.getAsJsonArray("edges");

        // Build original graph
        Graph g = new Graph(n, true);
        for (int i = 0; i < edges.size(); i++) {
            JsonObject edge = edges.get(i).getAsJsonObject();
            int u = edge.get("u").getAsInt();
            int v = edge.get("v").getAsInt();
            int w = edge.get("w").getAsInt();
            g.addEdge(u, v, w);
        }

        System.out.println("Original Graph: " + n + " vertices, " + edges.size() + " edges");

        // Variables for CSV metrics
        int numSCCs = 0;
        double sccTime = 0;
        int sccVisits = 0;
        double topoTime = 0;
        int topoOps = 0;
        boolean hasSource = json.has("source");
        double spTime = 0;
        int spRelaxations = 0;
        int longestPathLength = 0;
        double lpTime = 0;
        int lpRelaxations = 0;

        // ==========================================
        // 1. Find Strongly Connected Components
        // ==========================================
        System.out.println("\n[1] Finding Strongly Connected Components (Tarjan)...");
        Metrics sccMetrics = new Metrics();
        TarjanSCC tarjan = new TarjanSCC();
        List<List<Integer>> sccs = tarjan.findSCCs(g, sccMetrics);

        numSCCs = sccs.size();
        sccTime = sccMetrics.getElapsedTimeMs();
        sccVisits = sccMetrics.getDFSVisits();

        System.out.println("Found " + sccs.size() + " SCC(s):");
        for (int i = 0; i < sccs.size(); i++) {
            List<Integer> scc = sccs.get(i);
            System.out.println("  SCC " + i + ": " + scc + " (size: " + scc.size() + ")");
        }
        System.out.println("Performance: " + sccMetrics);

        // ==========================================
        // 2. Build Condensation Graph (DAG of SCCs)
        // ==========================================
        System.out.println("\n[2] Building Condensation Graph...");
        CondensationGraph condGraph = new CondensationGraph();
        Graph dag = condGraph.build(g, sccs);

        // Count edges in condensation
        int condEdges = 0;
        for (int i = 0; i < dag.getN(); i++) {
            condEdges += dag.getNeighbors(i).size();
        }

        System.out.println("Condensation DAG: " + dag.getN() + " nodes (SCCs), " + condEdges + " edges");

        // ==========================================
        // 3. Topological Sort of Condensation
        // ==========================================
        System.out.println("\n[3] Topological Sort of Condensation DAG...");
        Metrics topoMetrics = new Metrics();
        TopologicalSort topo = new TopologicalSort();
        List<Integer> sccOrder = topo.sort(dag, topoMetrics);

        topoTime = topoMetrics.getElapsedTimeMs();
        topoOps = topoMetrics.getOperations();

        if (sccOrder != null) {
            System.out.println("SCC Topological Order: " + sccOrder);

            // Expand to original task order
            List<Integer> taskOrder = topo.expandToOriginal(sccOrder, sccs);
            System.out.println("Task Execution Order: " + taskOrder);
            System.out.println("Performance: " + topoMetrics);
        } else {
            System.out.println("ERROR: Condensation has a cycle (should not happen!)");
        }

        // ==========================================
        // 4. Shortest Paths from Source (if provided)
        // ==========================================
        if (json.has("source")) {
            int originalSource = json.get("source").getAsInt();

            // Map original source vertex to its SCC index
            int sccSource = condGraph.getVertexSCC(originalSource);

            System.out.println("\n[4] Shortest Paths in Condensation DAG...");
            System.out.println("Original source vertex: " + originalSource +
                    " → SCC " + sccSource);

            Metrics spMetrics = new Metrics();
            DAGShortestPath dagSP = new DAGShortestPath();
            int[] dist = dagSP.shortestPaths(dag, sccSource, spMetrics);

            spTime = spMetrics.getElapsedTimeMs();
            spRelaxations = spMetrics.getRelaxations();

            if (dist != null) {
                System.out.println("Shortest distances from SCC " + sccSource + ":");
                for (int i = 0; i < dist.length; i++) {
                    if (dist[i] == Integer.MAX_VALUE / 2) {
                        System.out.println("  To SCC " + i + ": UNREACHABLE");
                    } else {
                        System.out.println("  To SCC " + i + ": " + dist[i]);
                    }
                }
                System.out.println("Performance: " + spMetrics);
            } else {
                System.out.println("ERROR: Could not compute shortest paths (cycle detected)");
            }
        }

        // ==========================================
        // 5. Longest Path (Critical Path)
        // ==========================================
        System.out.println("\n[5] Finding Longest Path (Critical Path) in Condensation...");
        Metrics lpMetrics = new Metrics();
        DAGShortestPath dagSP = new DAGShortestPath();
        DAGShortestPath.PathResult result = dagSP.longestPath(dag, lpMetrics);

        lpTime = lpMetrics.getElapsedTimeMs();
        lpRelaxations = lpMetrics.getRelaxations();

        if (result != null && result.path.size() > 0) {
            longestPathLength = result.length;

            System.out.println("Critical Path (SCC indices): " + result.path);
            System.out.println("Critical Path Length: " + result.length);

            // Map SCC path back to original vertices
            System.out.print("Critical Path (original vertices): ");
            for (int sccIdx : result.path) {
                System.out.print(sccs.get(sccIdx) + " ");
            }
            System.out.println();

            System.out.println("Performance: " + lpMetrics);
        } else {
            System.out.println("No path found or graph is empty");
            longestPathLength = 0;
        }

        System.out.println("\n" + "-".repeat(60));

        // ==========================================
        // Write metrics to CSV
        // ==========================================
        writeCSVRow(datasetName, n, edges.size(), numSCCs, sccTime, sccVisits,
                topoTime, topoOps, hasSource, spTime, spRelaxations,
                longestPathLength, lpTime, lpRelaxations);
    }

    /**
     * Write a row to CSV file
     */
    private static void writeCSVRow(String dataset, int vertices, int edges,
                                    int sccs, double sccTime, int sccVisits,
                                    double topoTime, int topoOps, boolean hasSource,
                                    double spTime, int spRelaxations,
                                    int longestPathLength, double lpTime, int lpRelaxations) {
        csvWriter.printf("%s,%d,%d,%d,%.3f,%d,%.3f,%d,%b,%.3f,%d,%d,%.3f,%d%n",
                dataset, vertices, edges, sccs, sccTime, sccVisits,
                topoTime, topoOps, hasSource, spTime, spRelaxations,
                longestPathLength, lpTime, lpRelaxations);
        csvWriter.flush();
    }
}
