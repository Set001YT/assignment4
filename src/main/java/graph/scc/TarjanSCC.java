package graph.scc;

import graph.Graph;
import graph.Metrics;
import java.util.*;

/**
 * Tarjan's algorithm for finding Strongly Connected Components
 */
public class TarjanSCC {
    private Graph graph;
    private Metrics metrics;

    // Tarjan algorithm variables
    private int[] low;      // lowest point reachable
    private int[] disc;     // discovery time
    private boolean[] onStack;
    private Stack<Integer> stack;
    private int time;

    // Results
    private List<List<Integer>> sccs;

    /**
     * Find all SCCs in the graph
     */
    public List<List<Integer>> findSCCs(Graph g, Metrics m) {
        this.graph = g;
        this.metrics = m;
        int n = g.getN();

        // Initialize arrays
        low = new int[n];
        disc = new int[n];
        onStack = new boolean[n];
        stack = new Stack<>();
        sccs = new ArrayList<>();
        time = 0;

        // Initialize discovery times to -1
        Arrays.fill(disc, -1);

        metrics.start();

        // Run DFS from each unvisited vertex
        for (int i = 0; i < n; i++) {
            if (disc[i] == -1) {
                dfs(i);
            }
        }

        metrics.stop();

        return sccs;
    }

    /**
     * DFS function for Tarjan's algorithm
     */
    private void dfs(int u) {
        // Set discovery time and low value
        disc[u] = low[u] = time++;
        stack.push(u);
        onStack[u] = true;

        metrics.incrementDFSVisits();

        // Visit all neighbors
        for (Graph.Edge edge : graph.getNeighbors(u)) {
            int v = edge.to;

            if (disc[v] == -1) {
                // If v is not visited, recurse
                dfs(v);
                // Update low value of u
                low[u] = Math.min(low[u], low[v]);
            } else if (onStack[v]) {
                // Update low value if v is on stack (back edge)
                low[u] = Math.min(low[u], disc[v]);
            }
        }

        // If u is a root node, pop the stack and create SCC
        if (low[u] == disc[u]) {
            List<Integer> scc = new ArrayList<>();
            int v;
            do {
                v = stack.pop();
                onStack[v] = false;
                scc.add(v);
                metrics.incrementOperations();
            } while (v != u);

            sccs.add(scc);
        }
    }

    /**
     * Get the list of SCCs
     */
    public List<List<Integer>> getSCCs() {
        return sccs;
    }
}
