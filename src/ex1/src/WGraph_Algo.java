package ex1.src;

import java.io.*;
import java.util.*;

/**
 * This class contains set of algorithms which the initialized graph operates on, including:
 * 0. copy; (Deep copy of a graph).
 * 1. init(graph); (Initialize the graph to the Graph_Algo class).
 * 2. isConnected(); (Check if all nodes in graph are connected).
 * 3. int shortestPathDist(int src, int dest); (Return the shortest path of two nodes).
 * 4. List<node_data> shortestPath(int src, int dest);(Return a list of the shortest path of two nodes).
 * 5. Save(file);
 * 6. Load(file);
 */

public class WGraph_Algo implements weighted_graph_algorithms, Serializable {

    private weighted_graph g;


    /**
     * Initialize the graph on which this set of algorithms operates on.
     * @param g graph object
     */

    @Override
    public void init(weighted_graph g) {
        this.g = g;
    }

    /**
     * returns the graph associated with the graph_algo class
     * @return weighted_graph , the associated graph in graph_algo class.
     */

    @Override
    public weighted_graph getGraph() {
        return g;
    }

    /**
     * Compute a deep copy of this graph.
     * @return g1 - the new deep copied graph
     */

    @Override
    public weighted_graph copy() {
        if (this.g == null) {
            return null;
        }
        return new WGraph_DS(this.g);
    }

    /**
     * Returns true if and only if ALL the nodes in the graph are connected.
     * @return boolean - true if connected else false.
     */

    @Override
    public boolean isConnected() {
        if (g == null || g.getV().size() <= 1) {
            return true;
        }
        node_info node = g.getV().iterator().next();
        DijkstraAlgo(node.getKey());
        for (node_info n : g.getV()) {
            if (n.getTag() == Double.MAX_VALUE) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the length of the shortest path between two nodes.
     * Note: if no such path --> returns -1
     * @param src - start node
     * @param dest - end (target) node
     * @return int - the sum of all the weights in the shortest path found
     */

    @Override
    public double shortestPathDist(int src, int dest) {
        if (g == null || g.getNode(src) == null || g.getNode(dest) == null) {
            return -1;
        }
        if (src == dest) {
            return 0;
        }
        DijkstraAlgo(src,dest);
        if (g.getNode(dest).getTag() == Double.MAX_VALUE) {
            return -1;
        }
        return g.getNode(dest).getTag();
    }

    /**
     * Returns the the shortest path between two nodes - as an ordered List of nodes:
     * src--> n1-->n2-->dest
     * Note if no such path --> returns null;
     * @param src - start node
     * @param dest - end (target) node
     * @return List<node_info> - the ordered list with the shortest path.
     */

    @Override
    public List<node_info> shortestPath(int src, int dest) {
        if (shortestPathDist(src, dest) == -1 || g.getV().isEmpty()) {
            return null;
        }
        List<node_info> list = new ArrayList<>();
        if (src == dest) {
            list.add(g.getNode(src));
            return list;
        }
        node_info srcNode = g.getNode(src);
        node_info destNode = g.getNode(dest);
        list.add(destNode);
        boolean finished = false;
        int nextNodeIndex = 0;
        while (!finished) { // while loop, adds shortest path from dest to src
            node_info node = list.get(nextNodeIndex);
            for (node_info n : g.getV(node.getKey())) {
                if (n.getKey() == srcNode.getKey()) {
                    list.add(n);
                    finished = true;
                } else if (Integer.parseInt(node.getInfo()) == n.getKey() && !list.contains(n)) {
                    list.add(n);
                    nextNodeIndex++;
                }
            }
        }
        Collections.reverse(list);
        return list;
    }

    @Override
    public boolean save(String file) {
        if(file == null){
            return false;
        }
        try {
            FileOutputStream fout = new FileOutputStream(file);
            ObjectOutputStream obj = new ObjectOutputStream(fout);
            obj.writeObject(g);
            obj.close();
            fout.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }


    @Override
    public boolean load(String file) {
        if(file == null){
            return false;
        }
        try {
            FileInputStream fin = new FileInputStream(file);
            ObjectInputStream obj = new ObjectInputStream(fin);
            g = (weighted_graph) obj.readObject();
            obj.close();
            fin.close();
        } catch (IOException | ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    /**
     * Dijkstra method.
     * Dijkstra is a algorithm used to go over all the nodes in the graph by lowest weight connected.
     * In this method, it iterates over the nodes with the lowest weight in the graph and marks them with 'tag'.
     * The tag is then used to count how much far is every node in the graph.
     * This method should run in a O(n+v * log(v)) time complexity (n = number of nodes in the graph, v = number of edges in the graph).
     * @param node1 int - arbitrary node to perform the BTS algorithm.
     */

    private void DijkstraAlgo(int node1) {
        for (node_info n : g.getV()) {
            n.setTag(Double.MAX_VALUE);
            n.setInfo("");
        }
        g.getNode(node1).setTag(0);
        node_info nodeOne = g.getNode(node1);
        Queue<node_info> q = new PriorityQueue<>(new WeightComparator());
        q.add(nodeOne);
        while (!q.isEmpty()) {
            node_info node = q.peek();
            for (node_info n : g.getV(node.getKey())) {
                if (n.getTag() > node.getTag() && !q.contains(n)) {
                    q.add(n);
                }
                double edge = g.getEdge(node.getKey(), n.getKey());
                if (node.getTag() + edge < n.getTag()) {
                    n.setTag(node.getTag() + edge);
                    n.setInfo("" + node.getKey());
                }
            }
            q.remove();
        }
    }

    /**
     * Same as the first DijkstraAlgo method but stops when it encounters the dest node.
     * Instead of running all over the graph, it stops on the dest node.
     * Because we use priority queue it must be the shortest path.
     * @param src - source node to start the algorithm
     * @param dest - destination node which stops the algorithm
     */
    private void DijkstraAlgo(int src, int dest) {
        for (node_info n : g.getV()) {
            n.setTag(Double.MAX_VALUE);
            n.setInfo("");
        }
        g.getNode(src).setTag(0);
        node_info nodeOne = g.getNode(src);
        node_info nodeTwo = g.getNode(dest);
        Queue<node_info> q = new PriorityQueue<>(new WeightComparator());
        q.add(nodeOne);
        while (!q.isEmpty()) {
            node_info node = q.peek();
            for (node_info n : g.getV(node.getKey())) {
                if (node.getTag() < n.getTag() && !q.contains(n)) {
                    q.add(n);
                }
                double edge = g.getEdge(node.getKey(), n.getKey());
                if (node.getTag() + edge < n.getTag()) {
                    n.setTag(node.getTag() + edge);
                    n.setInfo("" + node.getKey());
                }
                if(q.peek() == nodeTwo){
                    return;
                }
            }
            q.remove();
        }
    }

    /**
     * Comparator class with one method
     */
    private static class WeightComparator implements Comparator<node_info> {

        /**
         * Overrides compare method by tag (weight), if node1 > node2 return 1, if node1 < node2 return -1, else return 0.
         * used in the priorityQueue
         * @param node1 - node1 to compare
         * @param node2 - node2 to compare
         * @return - int if node1 > node2 return 1, if node1 < node2 return -1, else return 0.
         */

        @Override
        public int compare(node_info node1, node_info node2) {
            if (node1.getTag() > node2.getTag()) {
                return 1;
            } else if (node1.getTag() < node2.getTag()) {
                return -1;
            }
            return 0;
        }
    }
}

