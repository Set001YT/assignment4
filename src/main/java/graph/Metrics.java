package graph;

/**
 * Interface for tracking algorithm performance metrics
 */
public class Metrics {
    private int dfsVisits;
    private int edgeRelaxations;
    private int operationCount;
    private long startTime;
    private long endTime;

    /**
     * Start timing
     */
    public void start() {
        this.startTime = System.nanoTime();
        this.dfsVisits = 0;
        this.edgeRelaxations = 0;
        this.operationCount = 0;
    }

    /**
     * Stop timing
     */
    public void stop() {
        this.endTime = System.nanoTime();
    }

    /**
     * Increment DFS visit counter
     */
    public void incrementDFSVisits() {
        this.dfsVisits++;
    }

    /**
     * Increment edge relaxation counter
     */
    public void incrementRelaxations() {
        this.edgeRelaxations++;
    }

    /**
     * Increment general operation counter
     */
    public void incrementOperations() {
        this.operationCount++;
    }

    /**
     * Get elapsed time in milliseconds
     */
    public double getElapsedTimeMs() {
        return (endTime - startTime) / 1_000_000.0;
    }

    /**
     * Get DFS visits count
     */
    public int getDFSVisits() {
        return dfsVisits;
    }

    /**
     * Get relaxations count
     */
    public int getRelaxations() {
        return edgeRelaxations;
    }

    /**
     * Get operations count
     */
    public int getOperations() {
        return operationCount;
    }

    @Override
    public String toString() {
        return String.format("Time: %.3f ms, DFS Visits: %d, Relaxations: %d, Operations: %d",
                getElapsedTimeMs(), dfsVisits, edgeRelaxations, operationCount);
    }
}
