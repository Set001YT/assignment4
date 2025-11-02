package graph;

import graph.topo.*;
import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

/**
 * Unit tests for Topological Sort (Kahn's algorithm)
 */
public class TopologicalSortTest {

    @Test
    public void testSimpleDAG() {
        // Simple chain: 0→1→2
        Graph g = new Graph(3, true);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);

        Metrics m = new Metrics();
        TopologicalSort topo = new TopologicalSort();
        List<Integer> order = topo.sort(g, m);

        // Should return valid order
        assertNotNull(order);
        assertEquals(3, order.size());

        // 0 must come before 1, 1 before 2
        assertTrue(order.indexOf(0) < order.indexOf(1));
        assertTrue(order.indexOf(1) < order.indexOf(2));
    }

    @Test
    public void testCycleDetection() {
        // Graph with cycle: 0→1→2→0
        Graph g = new Graph(3, true);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 0, 1);

        Metrics m = new Metrics();
        TopologicalSort topo = new TopologicalSort();
        List<Integer> order = topo.sort(g, m);

        // Should return null because of cycle
        assertNull("Topological sort should return null for cyclic graph", order);
    }

    @Test
    public void testDiamondDAG() {
        // Diamond structure: 0→1, 0→2, 1→3, 2→3
        Graph g = new Graph(4, true);
        g.addEdge(0, 1, 1);
        g.addEdge(0, 2, 1);
        g.addEdge(1, 3, 1);
        g.addEdge(2, 3, 1);

        Metrics m = new Metrics();
        TopologicalSort topo = new TopologicalSort();
        List<Integer> order = topo.sort(g, m);

        assertNotNull(order);
        assertEquals(4, order.size());

        // 0 must be first
        assertEquals(0, (int)order.get(0));
        // 3 must be last
        assertEquals(3, (int)order.get(3));

        // 0 before both 1 and 2
        assertTrue(order.indexOf(0) < order.indexOf(1));
        assertTrue(order.indexOf(0) < order.indexOf(2));

        // Both 1 and 2 before 3
        assertTrue(order.indexOf(1) < order.indexOf(3));
        assertTrue(order.indexOf(2) < order.indexOf(3));
    }

    @Test
    public void testDisconnectedDAG() {
        // Two separate chains: 0→1 and 2→3
        Graph g = new Graph(4, true);
        g.addEdge(0, 1, 1);
        g.addEdge(2, 3, 1);

        Metrics m = new Metrics();
        TopologicalSort topo = new TopologicalSort();
        List<Integer> order = topo.sort(g, m);

        assertNotNull(order);
        assertEquals(4, order.size());

        // 0 before 1, 2 before 3
        assertTrue(order.indexOf(0) < order.indexOf(1));
        assertTrue(order.indexOf(2) < order.indexOf(3));
    }

    @Test
    public void testSingleVertex() {
        // Graph with single vertex
        Graph g = new Graph(1, true);

        Metrics m = new Metrics();
        TopologicalSort topo = new TopologicalSort();
        List<Integer> order = topo.sort(g, m);

        assertNotNull(order);
        assertEquals(1, order.size());
        assertEquals(0, (int)order.get(0));
    }

    @Test
    public void testEmptyGraph() {
        // Graph with vertices but no edges
        Graph g = new Graph(3, true);

        Metrics m = new Metrics();
        TopologicalSort topo = new TopologicalSort();
        List<Integer> order = topo.sort(g, m);

        assertNotNull(order);
        assertEquals(3, order.size());
    }

    @Test
    public void testExpandToOriginal() {
        // Test the expandToOriginal method
        List<List<Integer>> sccs = new ArrayList<>();
        sccs.add(Arrays.asList(1, 2, 3));  // SCC 0
        sccs.add(Arrays.asList(0));         // SCC 1
        sccs.add(Arrays.asList(4, 5));      // SCC 2

        List<Integer> sccOrder = Arrays.asList(1, 0, 2);

        TopologicalSort topo = new TopologicalSort();
        List<Integer> taskOrder = topo.expandToOriginal(sccOrder, sccs);

        // Should contain all 6 vertices
        assertEquals(6, taskOrder.size());

        // Order should be: [0], [1,2,3], [4,5]
        assertEquals(0, (int)taskOrder.get(0));
        assertTrue(taskOrder.containsAll(Arrays.asList(1, 2, 3)));
        assertTrue(taskOrder.containsAll(Arrays.asList(4, 5)));
    }
}
