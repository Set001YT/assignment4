package graph;

import graph.dagsp.*;
import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

/**
 * Unit tests for DAG Shortest/Longest Path algorithms
 */
public class DAGShortestPathTest {

    @Test
    public void testShortestPathSimple() {
        // Simple chain: 0→1(5)→2(3)→3(1)
        Graph g = new Graph(4, true);
        g.addEdge(0, 1, 5);
        g.addEdge(1, 2, 3);
        g.addEdge(2, 3, 1);

        Metrics m = new Metrics();
        DAGShortestPath dagSP = new DAGShortestPath();
        int[] dist = dagSP.shortestPaths(g, 0, m);

        assertNotNull(dist);
        assertEquals(0, dist[0]);
        assertEquals(5, dist[1]);
        assertEquals(8, dist[2]);  // 5+3
        assertEquals(9, dist[3]);  // 5+3+1
    }

    @Test
    public void testShortestPathWithAlternatives() {
        // Graph with two paths: 0→1(5)→3(2) and 0→2(3)→3(1)
        Graph g = new Graph(4, true);
        g.addEdge(0, 1, 5);
        g.addEdge(0, 2, 3);
        g.addEdge(1, 3, 2);
        g.addEdge(2, 3, 1);

        Metrics m = new Metrics();
        DAGShortestPath dagSP = new DAGShortestPath();
        int[] dist = dagSP.shortestPaths(g, 0, m);

        assertNotNull(dist);
        assertEquals(0, dist[0]);
        assertEquals(5, dist[1]);
        assertEquals(3, dist[2]);
        assertEquals(4, dist[3]);  // Shortest is 0→2→3 (3+1=4)
    }

    @Test
    public void testShortestPathUnreachable() {
        // Disconnected graph: 0→1 and 2→3 (separate)
        Graph g = new Graph(4, true);
        g.addEdge(0, 1, 5);
        g.addEdge(2, 3, 3);

        Metrics m = new Metrics();
        DAGShortestPath dagSP = new DAGShortestPath();
        int[] dist = dagSP.shortestPaths(g, 0, m);

        assertNotNull(dist);
        assertEquals(0, dist[0]);
        assertEquals(5, dist[1]);

        // Vertices 2 and 3 are unreachable from 0
        assertEquals(Integer.MAX_VALUE / 2, dist[2]);
        assertEquals(Integer.MAX_VALUE / 2, dist[3]);
    }

    @Test
    public void testLongestPathSimple() {
        // Simple chain: 0→1(2)→2(3)→3(1)
        Graph g = new Graph(4, true);
        g.addEdge(0, 1, 2);
        g.addEdge(1, 2, 3);
        g.addEdge(2, 3, 1);

        Metrics m = new Metrics();
        DAGShortestPath dagSP = new DAGShortestPath();
        DAGShortestPath.PathResult result = dagSP.longestPath(g, m);

        assertNotNull(result);
        assertEquals(6, result.length);  // 2+3+1
        assertEquals(4, result.path.size());  // Path: 0→1→2→3

        // Check path is correct
        assertEquals(0, (int)result.path.get(0));
        assertEquals(3, (int)result.path.get(3));
    }

    @Test
    public void testLongestPathWithBranches() {
        // Graph with branches: 0→1(2), 0→2(5), 1→3(4), 2→3(1)
        Graph g = new Graph(4, true);
        g.addEdge(0, 1, 2);
        g.addEdge(0, 2, 5);
        g.addEdge(1, 3, 4);
        g.addEdge(2, 3, 1);

        Metrics m = new Metrics();
        DAGShortestPath dagSP = new DAGShortestPath();
        DAGShortestPath.PathResult result = dagSP.longestPath(g, m);

        assertNotNull(result);
        // Longest path is 0→1→3 (2+4=6) or 0→2→3 (5+1=6)
        assertEquals(6, result.length);
    }

    @Test
    public void testLongestPathMultipleSources() {
        // Multiple sources: 0→2(3), 1→2(4), 2→3(2)
        Graph g = new Graph(4, true);
        g.addEdge(0, 2, 3);
        g.addEdge(1, 2, 4);
        g.addEdge(2, 3, 2);

        Metrics m = new Metrics();
        DAGShortestPath dagSP = new DAGShortestPath();
        DAGShortestPath.PathResult result = dagSP.longestPath(g, m);

        assertNotNull(result);
        // Longest is from 1: 1→2→3 (4+2=6)
        assertEquals(6, result.length);
    }

    @Test
    public void testSingleVertex() {
        // Graph with single vertex
        Graph g = new Graph(1, true);

        Metrics m = new Metrics();
        DAGShortestPath dagSP = new DAGShortestPath();

        // Shortest path
        int[] dist = dagSP.shortestPaths(g, 0, m);
        assertNotNull(dist);
        assertEquals(0, dist[0]);

        // Longest path
        DAGShortestPath.PathResult result = dagSP.longestPath(g, new Metrics());
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    public void testDisconnectedComponents() {
        // Two separate chains: 0→1(3) and 2→3(4)
        Graph g = new Graph(4, true);
        g.addEdge(0, 1, 3);
        g.addEdge(2, 3, 4);

        Metrics m = new Metrics();
        DAGShortestPath dagSP = new DAGShortestPath();
        DAGShortestPath.PathResult result = dagSP.longestPath(g, m);

        assertNotNull(result);
        // Longest is 2→3 (4)
        assertEquals(4, result.length);
    }

    @Test
    public void testEmptyGraph() {
        // Graph with vertices but no edges
        Graph g = new Graph(3, true);

        Metrics m = new Metrics();
        DAGShortestPath dagSP = new DAGShortestPath();

        // Shortest from vertex 0
        int[] dist = dagSP.shortestPaths(g, 0, m);
        assertNotNull(dist);
        assertEquals(0, dist[0]);
        assertEquals(Integer.MAX_VALUE / 2, dist[1]);
        assertEquals(Integer.MAX_VALUE / 2, dist[2]);

        // Longest path should be 0 (no edges)
        DAGShortestPath.PathResult result = dagSP.longestPath(g, new Metrics());
        assertNotNull(result);
        assertEquals(0, result.length);
    }
}
