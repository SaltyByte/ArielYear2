package api;

/**
 * This class represents a position on the graph (a relative position
 * on an edge - between two consecutive nodes).
 */
public interface edge_location {
    /**
     * Returns the edge on which the location is.
     * @return edge_data - the edge data
     */
    public edge_data getEdge();
    /**
     * Returns the relative ration [0,1] of the location between src and dest.
     * @return double - the ratio
     */
    public double getRatio();
}
