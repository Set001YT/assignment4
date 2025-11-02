package graph;

import graph.scc.*;
import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

/**
 * Unit tests for Tarjan's SCC algorithm
 */
public class TarjanSCCTest {

    @Test
    public void testSimpleCycle() {
        // Graph: 0→1→2→0 (one SCC with 3 vertices)
        Graph g = new Graph(3, true);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 0, 1);

        Metrics m = new Metrics();
        TarjanSCC tarjan = new TarjanSCC();
        List<List<Integer>> sccs = tarjan.findSCCs(g, m);

        // Should find exactly 1 SCC
        assertEquals(1, sccs.size());
        // That SCC should contain all 3 vertices
        assertEquals(3, sccs.get(0).size());
    }

    @Test
    public void testPureDAG() {
        // Graph: 0→1→2 (no cycles, each vertex is its own SCC)
        Graph g = new Graph(3, true);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);

        Metrics m = new Metrics();
        TarjanSCC tarjan = new TarjanSCC();
        List<List<Integer>> sccs = tarjan.findSCCs(g, m);

        // Should find 3 SCCs (one per vertex)
        assertEquals(3, sccs.size());
        // Each SCC should have size 1
        for (List<Integer> scc : sccs) {
            assertEquals(1, scc.size());
        }
    }

    @Test
    public void testMultipleSCCs() {
        // Two separate cycles: 0→1→2→0 and 3→4→5→3
        Graph g = new Graph(6, true);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 0, 1);
        g.addEdge(3, 4, 1);
        g.addEdge(4, 5, 1);
        g.addEdge(5, 3, 1);

        Metrics m = new Metrics();
        TarjanSCC tarjan = new TarjanSCC();
        List<List<Integer>> sccs = tarjan.findSCCs(g, m);

        // Should find exactly 2 SCCs
        assertEquals(2, sccs.size());
        // Each should have 3 vertices
        for (List<Integer> scc : sccs) {
            assertEquals(3, scc.size());
        }
    }

    @Test
    public void testEmptyGraph() {
        // Graph with 5 vertices but no edges
        Graph g = new Graph(5, true);

        Metrics m = new Metrics();
        TarjanSCC tarjan = new TarjanSCC();
        List<List<Integer>> sccs = tarjan.findSCCs(g, m);

        // Each vertex is its own SCC
        assertEquals(5, sccs.size());
        for (List<Integer> scc : sccs) {
            assertEquals(1, scc.size());
        }
    }

    @Test
    public void testSingleVertex() {
        // Graph with just 1 vertex
        Graph g = new Graph(1, true);

        Metrics m = new Metrics();
        TarjanSCC tarjan = new TarjanSCC();
        List<List<Integer>> sccs = tarjan.findSCCs(g, m);

        // Should find 1 SCC with 1 vertex
        assertEquals(1, sccs.size());
        assertEquals(1, sccs.get(0).size());
    }

    @Test
    public void testComplexGraph() {
        // More complex graph with mixed structure
        // 0→1→2→3→1 (cycle with 1,2,3) and 0,4 separate
        Graph g = new Graph(5, true);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 2);
        g.addEdge(2, 3, 1);
        g.addEdge(3, 1, 1);
        g.addEdge(0, 4, 3);

        Metrics m = new Metrics();
        TarjanSCC tarjan = new TarjanSCC();
        List<List<Integer>> sccs = tarjan.findSCCs(g, m);

        // Should find 3 SCCs: {1,2,3}, {0}, {4}
        assertEquals(3, sccs.size());

        // Check that one SCC has 3 vertices (the cycle)
        boolean foundCycle = false;
        for (List<Integer> scc : sccs) {
            if (scc.size() == 3) {
                foundCycle = true;
                break;
            }
        }
        assertTrue("Should find cycle with 3 vertices", foundCycle);
    }
}
