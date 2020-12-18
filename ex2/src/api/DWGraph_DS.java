package api;

import java.util.Collection;
import java.util.HashMap;

/**
 * This class implements directed_weighted_graph interface
 * that represents a directional weighted graph.
 * The interface has a road-system or communication network in mind.
 */
public class DWGraph_DS implements directed_weighted_graph {

	// This HashMap holds the nodes of this graph
	private HashMap<Integer, node_data> nodes = new HashMap<>();
	// This HashMap holds the node neighbors and the edges between them 
	private HashMap<Integer, HashMap<Integer, edge_data>> neighbors = new HashMap<>();
	// This HashMap hold the parents of nodes in the graph
	private HashMap<Integer, HashMap<Integer, edge_data>> parents = new HashMap<>();
	private int edgeSize, mc;
	
	/**
	 * Default constructor.
	 */
	public DWGraph_DS() {
		this.edgeSize = 0;
		this.mc = 0;
	}

	/**
	 * Deep copy constructor that copies
	 * an existing graph and creates a new graph.
	 * @param g - directed_weighted_graph
	 */
	public DWGraph_DS(directed_weighted_graph g) {
		// Check if graph is null else copy
		if (g == null) {
			return;
		}
		// Loop and create new nodes and copy content from each node
		for (node_data n : g.getV()) {
			int srcKey = n.getKey();
			this.nodes.put(srcKey, new NodeData(srcKey));
		}
		// Loop over the nodes and create new nodes, then copy content from each node
		for (node_data node : g.getV()) {
			int srcKey = node.getKey();
			// Loop through the edges of each node
			for (edge_data edge: g.getE(srcKey)) {
				int destKey = edge.getDest();
				double weight = edge.getWeight();
				// Connect an edge with two nodes and their weight
				connect(srcKey, destKey, weight);
			}
		}
		// Set the mode counter and edge size to the same value as the copied graph
		this.edgeSize = g.edgeSize();
		this.mc = g.getMC();
	}

	/**
	 * Returns the node_data by the node_id,
	 * @param key - the node_id
	 * @return the node_data by the node_id, null if none.
	 */
	@Override
	public node_data getNode(int key) {
		return nodes.get(key);
	}

	/**
	 * Returns the data of the edge (src,dest), null if none.
	 * @param src - the start node
	 * @param dest - end (target) node
	 * @return edge_data edge
	 */
	@Override
	public edge_data getEdge(int src, int dest) {
		// Check if node src and dest exists in the graph
		if (src != dest && nodes.get(src) != null && nodes.get(dest) != null) {
			if (neighbors.containsKey(src)) {
				return neighbors.get(src).get(dest);
			}
		}
		return null;
	}

	/**
	 * Adds a new node to the graph with the given node_data.
	 * @param n - node needed to be added to the graph
	 */
	@Override
	public void addNode(node_data n) {
		if (!nodes.containsKey(n.getKey())) {
			nodes.put(n.getKey(), n);
			mc++;
		}
	}

	/**
	 * Connects an edge with weight w between node src to node dest.
	 * @param src - the source of the edge.
	 * @param dest - the destination of the edge.
	 * @param w - positive weight representing the cost (aka time, price, etc) between src--dest.
	 */
	@Override
	public void connect(int src, int dest, double w) {
		// Check if src is not equal to dest and if they exist in the graph
		// and if the weight is positive, then connect
		if (src != dest && nodes.get(src) != null && nodes.get(dest) != null && w >= 0) {
			// If src does not have neighbors, then create a new inner hash map and put it in neighbors
			if (!neighbors.containsKey(src)) {
				HashMap<Integer, edge_data> map = new HashMap<>();
				neighbors.put(src, map);
			}
			// If dest does not have parents, then create a new inner hash map and put it in parents
			if (!parents.containsKey(dest)) {
				HashMap<Integer, edge_data> map = new HashMap<>();
				parents.put(dest, map);
			}
			// If src and dest are not neighbors, then connect them
			if (!neighbors.get(src).containsKey(dest)) {
				edge_data edge = new EdgeData(src, dest, w);
				neighbors.get(src).put(dest, edge);
				parents.get(dest).put(src, edge);
				edgeSize++;
				mc++;	
			}
			// If src and dest are already neighbors, then update the weight
			else if (neighbors.get(src).get(dest).getWeight() != w) {
				edge_data edge = new EdgeData(src, dest, w);
				neighbors.get(src).replace(dest, edge);
				mc++;
			}
		}
	}

	/**
	 * This method returns a pointer (shallow copy) for the
	 * collection representing all the nodes in the graph. 
	 * @return Collection of node_data
	 */
	@Override
	public Collection<node_data> getV() {
		return nodes.values();
	}

	/**
	 * This method returns a pointer (shallow copy) for the
	 * collection representing all the edges getting out of 
	 * the given node (all the edges starting (source) at the given node). 
	 * Note: this method should run in O(k) time, k being the collection size.
	 * @return Collection of edge_data
	 */
	@Override
	public Collection<edge_data> getE(int node_id) {
		if (!neighbors.containsKey(node_id)) {
			return new HashMap<Integer, edge_data>().values();
		}
		return neighbors.get(node_id).values();
	}

	/**
	 * Deletes the node (with the given ID) from the graph -
	 * and removes all edges which starts or ends at this node.
	 * This method should run in O(k), V.degree=k, as all the edges should be removed.
	 * @return the data of the removed node (null if none). 
	 * @param key - the key of the node that needed to be removed from the graph
	 */
	@Override
	public node_data removeNode(int key) {
		node_data node = nodes.get(key);
		// If node is not in graph, then remove node
		if (node != null) {
			int size = getE(key).size();
			// Loop over the edges size and remove the edge between key and dest
			for (int i = 0; i < size; i++) {
				removeEdge(key,getE(key).iterator().next().getDest());
			}
			// Loop over the src from the parents map and remove their edges
			for (int src : parents.get(key).keySet()) {
				removeEdge(src, key);
			}
			// Remove the node from the list of nodes in the graph
			nodes.remove(key);
			mc++;
			return node;
		}
		return null;
	}

	/**
	 * Deletes the edge from the graph,
	 * @param src - start node
	 * @param dest - end (target) node
	 * @return edge_data - the data of the removed edge (null if none).
	 */
	@Override
	public edge_data removeEdge(int src, int dest) {
		// Check if src is not equal to dest and if they exist in the graph
		if (src != dest && nodes.get(dest) != null && nodes.get(src) != null) {
			// Check if src has neighbors and if dest is its neighbor
			if (neighbors.containsKey(src) && neighbors.get(src).containsKey(dest)) {
				edgeSize--;
				mc++;
				parents.get(dest).remove(src);
				return neighbors.get(src).remove(dest);
			}
		}
		return null;
	}

	/** 
	 * Returns the number of vertices (nodes) in the graph.
	 * @return int node size - the number of nodes in the graph
	 */
	@Override
	public int nodeSize() {
		return nodes.size();
	}

	/** 
	 * Returns the number of edges (assume directional graph).
	 * @return int edge size - the number of edges in the graph
	 */
	@Override
	public int edgeSize() {
		return edgeSize;
	}

	/**
	 * Returns the Mode Count - for testing changes in the graph.
	 * @return int mode counter - the count of any changes in the graph
	 */
	@Override
	public int getMC() {
		return mc;
	}
}