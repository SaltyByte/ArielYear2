package api;
import java.util.Collection;
/**
 * This interface represents a directional weighted graph.
 * The interface has a road-system or communication network in mind - 
 * and should support a large number of nodes (over 100,000).
 * The implementation should be based on an efficient compact representation 
 * (should NOT be based on a n*n matrix).
 *
 */

public interface directed_weighted_graph {
	/**
	 * returns the node_data by the node_id,
	 * @param key - the node_id
	 * @return the node_data by the node_id, null if none.
	 */
	public node_data getNode(int key);
	/**
	 * returns the data of the edge (src,dest), null if none.
	 * Note: this method should run in O(1) time.
	 * @param src - the source node
	 * @param dest - the destination node
	 * @return edge_data - the edge between the source and destination
	 */
	public edge_data getEdge(int src, int dest);
	/**
	 * adds a new node to the graph with the given node_data.
	 * Note: this method should run in O(1) time.
	 * @param n - node_data
	 */
	public void addNode(node_data n);
/**
 * Connects an edge with weight w between node src to node dest.
 * * Note: this method should run in O(1) time.
 * @param src - the source of the edge.
 * @param dest - the destination of the edge.
 * @param w - positive weight representing the cost (aka time, price, etc) between src--dest.
 */
	public void connect(int src, int dest, double w);
	/**
	 * This method returns a pointer (shallow copy) for the
	 * collection representing all the nodes in the graph. 
	 * Note: this method should run in O(1) time.
	 * @return Collection of node_data
	 */
	public Collection<node_data> getV();
	/**
	 * This method returns a pointer (shallow copy) for the
	 * collection representing all the edges getting out of 
	 * the given node (all the edges starting (source) at the given node). 
	 * Note: this method should run in O(k) time, k being the collection size.
	 * @param node_id int
	 * @return Collection of edge_data
	 */
	public Collection<edge_data> getE(int node_id);
	/**
	 * Deletes the node (with the given ID) from the graph -
	 * and removes all edges which starts or ends at this node.
	 * This method should run in O(k), V.degree=k, as all the edges should be removed.
	 * @return the data of the removed node (null if none). 
	 * @param key - the node id
	 */
	public node_data removeNode(int key);
	/**
	 * Deletes the edge from the graph,
	 * Note: this method should run in O(1) time.
	 * @param src - the source node
	 * @param dest - the destination node
	 * @return the data of the removed edge (null if none).
	 */
	public edge_data removeEdge(int src, int dest);
	/** Returns the number of vertices (nodes) in the graph.
	 * Note: this method should run in O(1) time. 
	 * @return int node size
	 */
	public int nodeSize();
	/** 
	 * Returns the number of edges (assume directional graph).
	 * Note: this method should run in O(1) time.
	 * @return int edge size
	 */
	public int edgeSize();
/**
 * Returns the Mode Count - for testing changes in the graph.
 * @return int mode counter
 */
	public int getMC();
}
