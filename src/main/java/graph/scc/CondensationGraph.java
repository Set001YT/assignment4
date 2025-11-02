package graph.scc;

import graph.Graph;
import java.util.*;

/**
 * Build condensation graph (DAG of SCCs)
 */
public class CondensationGraph {
    private Graph original;
    private List<List<Integer>> sccs;
    private Graph condensation;
    private int[] vertexToSCC; // maps original vertex to SCC index

    /**
     * Build condensation graph from SCCs
     */
    public Graph build(Graph g, List<List<Integer>> sccs) {
        this.original = g;
        this.sccs = sccs;
        int numSCCs = sccs.size();

        // Create mapping from vertex to SCC index
        vertexToSCC = new int[g.getN()];
        for (int i = 0; i < sccs.size(); i++) {
            for (int v : sccs.get(i)) {
                vertexToSCC[v] = i;
            }
        }

        // Create new graph with SCC nodes
        condensation = new Graph(numSCCs, true);

        // Add edges between different SCCs
        Set<String> addedEdges = new HashSet<>();

        for (int u = 0; u < g.getN(); u++) {
            int sccU = vertexToSCC[u];

            for (Graph.Edge edge : g.getNeighbors(u)) {
                int v = edge.to;
                int sccV = vertexToSCC[v];

                // Only add edge if vertices are in different SCCs
                if (sccU != sccV) {
                    String edgeKey = sccU + "-" + sccV;
                    if (!addedEdges.contains(edgeKey)) {
                        condensation.addEdge(sccU, sccV, edge.weight);
                        addedEdges.add(edgeKey);
                    }
                }
            }
        }

        return condensation;
    }

    /**
     * Get condensation graph
     */
    public Graph getCondensation() {
        return condensation;
    }

    /**
     * Get SCC index for a vertex
     */
    public int getVertexSCC(int v) {
        return vertexToSCC[v];
    }
}
