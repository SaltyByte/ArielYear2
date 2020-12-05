package api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * This class implements dw_graph_algorithms interface that represents
 * a Directed (positive) Weighted Graph Theory Algorithms including:
 * 0. clone(); (copy)
 * 1. init(graph);
 * 2. isConnected(); // strongly (all ordered pais connected)
 * 3. double shortestPathDist(int src, int dest);
 * 4. List<node_data> shortestPath(int src, int dest);
 * 5. Save(file); // JSON file
 * 6. Load(file); // JSON file
 */
public class DWGraph_Algo implements dw_graph_algorithms {

	private directed_weighted_graph graph;
	private HashMap<Integer, TagWeight> tags;

	/**
	 * Init the graph on which this set of algorithms operates on.
	 * @param directed_weighted_graph g - the graph needed to be initialized
	 */
	@Override
	public void init(directed_weighted_graph g) {
		this.graph = g;
	}

	/**
	 * Returns the underlying graph of which this class works.
	 * @return directed_weighted_graph - get graph
	 */
	@Override
	public directed_weighted_graph getGraph() {
		return this.graph;
	}

	/**
	 * Compute a deep copy of this weighted graph.
	 * @return directed_weighted_graph - copied graph
	 */
	@Override
	public directed_weighted_graph copy() {
		if (this.graph != null) {
			return new DWGraph_DS(this.graph);

		}
		return null;
	}

	/**
	 * Returns true if and only if there is a valid path from each node to each
	 * other node. NOTE: assume directional graph (all n*(n-1) ordered pairs).
	 * @return boolean - true if graph is strongly connected and false if not connected
	 */
	@Override
	public boolean isConnected() {
		if (graph == null || graph.getV().size() <= 1) {
			return true;
		}
		for (node_data node : graph.getV()) {
			Dijkstra(node.getKey(),null);
			if (tags.size() != graph.nodeSize()){
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the length of the shortest path between src to dest,
	 * If no such path --> returns -1.
	 * @param src - start node
	 * @param dest - end (target) node
	 * @return double - the shortest distance between src and dest
	 */
	@Override
	public double shortestPathDist(int src, int dest) {
		if (graph == null || graph.getNode(src) == null || graph.getNode(dest) == null){
			return -1;
		}
		if (src == dest) {
			return 0;
		}
		Dijkstra(src,dest);
		if (!tags.containsKey(dest)) {
			return -1;
		}
		return tags.get(dest).getSumWeight();
	}

	/**
	 * Returns the shortest path between src to dest - as an ordered List of nodes:
	 * src--> n1-->n2-->...dest
	 * If no such path --> returns null.
	 * @param src - start node
	 * @param dest - end (target) node
	 * @return List<node_data>
	 */
	@Override
	public List<node_data> shortestPath(int src, int dest) {
		if (shortestPathDist(src, dest) == -1 || graph.getV().isEmpty()) {
			return null;
		}
		List<node_data> list = new ArrayList<>();
		if (src == dest) {
			list.add(graph.getNode(src));
			return list;
		}
		node_data destNode = graph.getNode(dest);
		list.add(destNode);
		boolean finished = false;
		int nextNodeIndex = 0;
		// while loop, adds shortest path from dest to src
		while (!finished) {
			node_data node = list.get(nextNodeIndex);
			list.add(tags.get(node.getKey()).getParent());
			nextNodeIndex++;
			if(list.get(list.size() - 1) == graph.getNode(src)){
				finished = true;
			}
		}
		Collections.reverse(list);
		return list;
	}

	/** 
	 * Private function that uses Dijkstra algorithm to find the shortest path
	 * according to the weight.
	 * @param int src - starting node
	 * @param Integer dest - end (target) node
	 */
	private void Dijkstra(int src, Integer dest) {
		tags = new HashMap<>();

		node_data nodeSrc = graph.getNode(src);
		Queue<node_data> q = new PriorityQueue<>(new WeightComparator());
		q.add(nodeSrc);
		TagWeight tag = new TagWeight(null, 0);
		tags.put(src, tag);
		while (!q.isEmpty()) {
			node_data node = q.poll();
			for (edge_data edge : graph.getE(node.getKey())) {
				double sumWeight = edge.getWeight() + tags.get(node.getKey()).getSumWeight();
				node_data neighbor = graph.getNode(edge.getDest());
				if (!tags.containsKey(neighbor.getKey()) && !q.contains(neighbor)) {
					TagWeight t = new TagWeight(node, sumWeight);
					tags.put(edge.getDest(), t);
					q.add(neighbor);
				}
				else if (sumWeight < tags.get(edge.getDest()).getSumWeight()) {
					TagWeight t = new TagWeight(node, sumWeight);
					tags.put(edge.getDest(), t);
				}
				if (dest != null && node.getKey() == dest){
					return;
				}
			}
		}
	}

	/**
	 * Saves this weighted (directed) graph to the given
	 * file name - in JSON format.
	 * @param file - the file name (may include a relative path)
	 * @return true - if and only if the file was successfully saved
	 */
	@Override
	public boolean save(String file) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(this.graph);	
		// Write JSON to file
		try
		{
			PrintWriter pw = new PrintWriter(new File(file));
			pw.write(json);
			pw.close();
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * This method load a graph to this graph algorithm.
	 * if the file was successfully loaded - the underlying graph
	 * of this class will be changed (to the loaded one), in case the
	 * graph was not loaded the original graph should remain "as is".
	 * @param file - file name of JSON file
	 * @return true - if and only if the graph was successfully loaded
	 */
	@Override
	public boolean load(String file) {
		try 
		{
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.setLenient();
			gsonBuilder.registerTypeAdapter(node_data.class, new InterfaceAdapter<NodeData>(NodeData.class));
			gsonBuilder.registerTypeAdapter(edge_data.class, new InterfaceAdapter<EdgeData>(EdgeData.class));
			
			Gson gson = gsonBuilder.create();
			FileReader reader = new FileReader(file);
			this.graph = gson.fromJson(reader, DWGraph_DS.class);	
			System.out.println(this.graph);
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private class InterfaceAdapter<T> implements JsonDeserializer<T> {

		private Class<T> targetClass;

		public InterfaceAdapter(Class<T> targetClass) {
			this.targetClass = targetClass;
		}

		@Override
		public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			return context.deserialize(json.getAsJsonObject(), targetClass);
		}
	}

	/**
	 * This class implements Comparator interface and compare weights
	 * that are used to save the distance from src.
	 * This is needed for the priority queue.
	 */
	private class WeightComparator implements Comparator<node_data> {

		/**
		 * Overrides compare method by tag (weight), if node1 > node2 return 1, if node1 < node2 return -1, else return 0.
		 * used in the priorityQueue.
		 * @param node1 - node1 to compare
		 * @param node2 - node2 to compare
		 * @return - int if node1 > node2 return 1, if node1 < node2 return -1, else return 0.
		 */
		@Override
		public int compare(node_data node1, node_data node2) {
			double sum1 = tags.get(node1.getKey()).getSumWeight();
			double sum2 = tags.get(node2.getKey()).getSumWeight();
			if (sum1 > sum2) {
				return 1;
			} else if (sum1 <sum2) {
				return -1;
			}
			return 0;
		}
	}

	private class TagWeight {
		private double sumWeight;
		private node_data parent;

		public TagWeight(node_data parent, double sumWeight){
			this.parent = parent;
			this.sumWeight = sumWeight;
		}
		public double getSumWeight(){
			return sumWeight;
		}

		public node_data getParent() {
			return parent;
		}
	}
}