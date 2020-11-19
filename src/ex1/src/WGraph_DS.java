package ex1.src;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;


/**
 * This class represents an weighted graph.
 * It should support a large number of nodes (over 10^6, with average degree of 10).
 * The implementation should be based on an efficient compact representation
 * @author Alex Baranov
 */
public class WGraph_DS implements weighted_graph, Serializable {
    private int mc, edgeSize;
    private final HashMap<Integer, node_info> keys;
    private final HashMap<Integer, HashMap<node_info, Double>> edges;


    /**
     * Default constructor
     */
    public WGraph_DS() {

        mc = 0;
        edgeSize = 0;
        this.keys = new HashMap<>();
        this.edges = new HashMap<>();

    }

    /**
     * Copy constructor. Deep copy of a graph
     * @param g graph - the desired graph to copy
     */
    public WGraph_DS(weighted_graph g) {
        node_info n1 = new NodeInfo(1);
        node_info n2 = new NodeInfo(1);
        this.keys = new HashMap<>();
        this.edges = new HashMap<>();
        if (g != null) {
            for (node_info n : g.getV()) { // loop and create new nodes and copy content from each node
                node_info a = new NodeInfo(n);
                this.keys.put(a.getKey(), a);
            }
            for (node_info node : g.getV()) { // loop through the nodes in the graph
                for (node_info n : g.getV(node.getKey())) { // loop through the neighbors of each node
                    if (g.hasEdge(node.getKey(), n.getKey())) { // check if already two nodes has an edge
                        int node1 = node.getKey();
                        int node2 = n.getKey();
                        connect(getNode(node1).getKey(), getNode(node2).getKey(), g.getEdge(node1, node2)); // connect an edge with two nodes
                    }
                }
            }
            this.edgeSize = g.edgeSize();
            this.mc = g.getMC();
        }
    }

    /**
     * return the node_data by the key,
     *
     * @param key the node key
     * @return the node_info by the key, null if none.
     */
    @Override
    public node_info getNode(int key) {
        return keys.get(key);
    }

    /**
     * return true if and only if there is an edge between node1 and node2
     *
     * @param node1 int , node with key 1
     * @param node2 int , node with key 2
     * @return boolean true if nodes are connected else false.
     */
    @Override
    public boolean hasEdge(int node1, int node2) {
        node_info nodeOne = keys.get(node1);
        node_info nodeTwo = keys.get(node2);
        if (    nodeOne != null &&
                nodeTwo != null &&
                edges.containsKey(node1)){
            return edges.get(node1).containsKey(nodeTwo);
        }
        return false;
    }

    /**
     * return the value of the edge which is connection node1 and node2
     * return -1 if no edge
     *
     * @param node1 - key of node 1
     * @param node2 - key of node 2
     * @return double - weight of the edge connecting the two nodes
     */


    @Override
    public double getEdge(int node1, int node2) {
        node_info nodeOne = keys.get(node1);
        node_info nodeTwo = keys.get(node2);
        if (nodeOne != null && nodeTwo != null && edges.containsKey(node1) && edges.get(node1).containsKey(nodeTwo)) {
            return edges.get(node1).get(nodeTwo);
        }
        return -1;
    }

    /**
     * adds new node to the graph with given key.
     * only adds new node if graph do not contains the same key.
     * @param key the unique key which is associated with the node
     */

    @Override
    public void addNode(int key) {
        if (!keys.containsKey(key)) {
            node_info n = new NodeInfo(key);
            keys.put(key, n);
            mc++;
        }
    }

    /**
     * Connect an edge between node1 and node2 with the given weight.
     * If there is already edge between the nodes or one or two of the nodes in null
     * it simply does nothing.
     *
     * @param node1 node with key 1
     * @param node2 node with key 2
     * @param w the weight of the edge which is connecting the two nodes
     */

    @Override
    public void connect(int node1, int node2, double w) {
        node_info nodeOne = keys.get(node1);
        node_info nodeTwo = keys.get(node2);
        if (nodeOne != null && nodeTwo != null && w >= 0 && node1 != node2) {
            if (!edges.containsKey(node1)) {
                HashMap<node_info, Double> map = new HashMap<>();
                edges.put(node1, map);
            }
            if (!edges.containsKey(node2)) {
                HashMap<node_info, Double> map = new HashMap<>();
                edges.put(node2, map);
            }
            if (!edges.get(node1).containsKey(nodeTwo)) {
                edges.get(node1).put(nodeTwo, w);
                edges.get(node2).put(nodeOne, w);
                edgeSize++;
                mc++;
            }
            else if (edges.get(node1).get(nodeTwo) != w){
                edges.get(node1).replace(nodeTwo, w); // if problems then change replace with put
                edges.get(node2).replace(nodeOne, w);
                mc++;
            }
        }
    }

    /**
     * This method return a pointer for the
     * collection representing all the nodes in the graph.
     *
     * @return Collection<node_info> - all the nodes in the graph
     */

    @Override
    public Collection<node_info> getV() {
        return keys.values();
    }

    /**
     * This method returns a collection containing all the
     * nodes connected to node_id
     *
     * @return Collection<node_info> - of all the nodes in the graph
     */

    @Override
    public Collection<node_info> getV(int node_id) {
        if (!edges.containsKey(node_id)) {
            HashMap<Integer, node_info> map = new HashMap<>();
            return map.values();
        }
        return edges.get(node_id).keySet();
    }

    /**
     * Delete the node (by the given key) from the graph.
     * Removes all edges which are connected with this node.
     *
     * @param key int - the node you wish to delete
     * @return node_info, the removed node (null if none).
     */

    @Override
    public node_info removeNode(int key) {
        node_info node = keys.get(key);
        if (node != null) {
            int size = getV(key).size();
            for (int i = 0; i < size; i++) {
                removeEdge(key,getV(key).iterator().next().getKey());
            }
            keys.remove(key);
            mc++;
            return node;
        }
        return null;
    }

    /**
     * Delete the edge which is connected to node1 and node2.
     *
     * @param node1 int - key of node 1
     * @param node2 int - key of node 2
     */

    @Override
    public void removeEdge(int node1, int node2) {
        node_info nodeOne = keys.get(node1);
        node_info nodeTwo = keys.get(node2);
        if (nodeOne != null && nodeTwo != null) {
            if (edges.get(node1).containsKey(nodeTwo) && edges.get(node2).containsKey(nodeOne)) {
                edges.get(node1).remove(nodeTwo);
                edges.get(node2).remove(nodeOne);
                edgeSize--;
                mc++;
            }
        }
    }

    /**
     * Equals function, overrides the object equals method and implements a new equals method.
     * This new equals method runs on both graphs and checks if they both got the same values.
     * It checks the key, weight values, also checks if all the connections are the same,also checks node, mc and edge sizes.
     *
     * @param obj The graph you wish to compare it to,
     * @return boolean true if both graphs are the same, false if even 1 param is different.
     */

    @Override
    public boolean equals (Object obj){
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        final WGraph_DS graph = (WGraph_DS) obj;
        if (this.edgeSize != graph.edgeSize() && this.keys.size() != graph.nodeSize()){
            return false;
        }
        if(this.edgeSize != graph.edgeSize() && this.keys.size() != graph.nodeSize()){
            return false;
        }
        return keys.equals(graph.keys) && edges.equals(graph.edges);
    }

    /**
     * return the number of nodes in the graph.
     *
     * @return int - number of nodes.
     */

    @Override
    public int nodeSize() {
        return keys.size();
    }

    /**
     * return the number of edges in the graph.
     *
     * @return int - number of edges
     */

    @Override
    public int edgeSize() {
        return edgeSize;
    }

    /**
     * return the Mode Count - for testing changes in the graph.
     * Any change in the inner state of the graph should cause an increment in the ModeCount
     *
     * @return int -  number of changed in the graph.
     */

    @Override
    public int getMC() {
        return mc;
    }

    /**
     * Overrides the hashCode method, a must if equals method is overridden.
     * @return int - new hashCode.
     */
    @Override
    public int hashCode(){
        return Objects.hash(this.keys.values().iterator().next().getKey() * 17 * 37, this.edgeSize, this.mc);
    }

    /**
     * This class represents the set of functions applicable
     * on a node in an weighted graph.
     * @author Alex Baranov
     */

    private static class NodeInfo implements node_info, Serializable {
        private final int key;
        private double tag;
        private String info;


        /**
         * Constructor, init the key value with the given key.
         */
        public NodeInfo(int key) {
            this.key = key;
        }

        /**
         * Copy constructor, copy the values of the given node.
         *
         * @param n - Node which is copied from.
         */

        public NodeInfo(node_info n) {
            this.key = n.getKey();
            this.tag = n.getTag();
            this.info = n.getInfo();
        }

        /**
         * Return the key (id) associated with this node.
         * Each node has unique key.
         * @return int - key of node
         */

        @Override
        public int getKey() {
            return this.key;
        }

        /**
         * return the data associated with the node.
         * @return String
         */

        @Override
        public String getInfo() {
            return this.info;
        }

        /**
         * Set the data associated with this node.
         * @param s String
         */

        @Override
        public void setInfo(String s) {
            this.info = s;
        }

        /**
         * Return the "tag" that was associated with this node.
         * Used in algorithms.
         * @return int - tag
         */

        @Override
        public double getTag() {
            return this.tag;
        }

        /**
         * Allow setting the "tag" value for temporal marking an node - common
         * Used in algorithms.
         * @param t int
         */


        @Override
        public void setTag(double t) {
            this.tag = t;
        }

        /**
         * Basic toString method
         * @return String of key
         */

        @Override
        public String toString() {
            return "" + this.key;
        }

        /**
         * Overrides equals method, compares the two graphs if they are both equal by values.
         *
         * @param obj graph which is being compared to
         * @return boolean - true if all values are same else false.
         */

        @Override
        public boolean equals(Object obj){
            if (obj == null) {
                return false;
            }
            if (obj.getClass() != this.getClass()) {
                return false;
            }
            final node_info node = (NodeInfo) obj;
            return this.key == node.getKey();
        }

        /**
         * Overrides hashCode method, a must if equals method is overridden
         * @return int -  new hashCode.
         */
        @Override
        public int hashCode(){
            return Objects.hash( 37 + this.key * 17);
        }
    }
}