package api;

import java.util.Collection;
import java.util.HashMap;

public class DWGraph_DS implements directed_weighted_graph {
    private HashMap<Integer, node_data> nodes;
    private HashMap<Integer, HashMap<Integer, edge_data>> neighbors;
    private HashMap<Integer, HashMap<Integer, edge_data>> inEdges; // Point of this is to point to the existing edge and not create a new one
    private int edgeSize, mc;                                      // so in connect i also need to update this with the existing hashmap of
                                                                   // edges.


    public DWGraph_DS() {
        this.nodes = new HashMap<>();
        this.neighbors = new HashMap<>();
        this.inEdges = new HashMap<>();
        this.edgeSize = 0;
        this.mc = 0;
    }

    public DWGraph_DS(directed_weighted_graph g) {
        // Check if graph is null else copy
        if (g == null) {
            return;
        }
        this.nodes = new HashMap<>();
        this.neighbors = new HashMap<>();
        for (node_data n : g.getV()) { // loop and create new nodes and copy content from each node
            node_data a = new NodeData(n.getKey());
            this.nodes.put(a.getKey(), a);
        }
        for (node_data n : g.getV()) {
            for (edge_data edge: g.getE(n.getKey())) { // loop through the neighbors of each node
                    int nodeSrc = n.getKey();
                    int nodeDest = edge.getDest();
                    double weight = edge.getWeight();
                    connect(nodeSrc, nodeDest, weight); // connect an edge with two nodes
            }
        }
        this.edgeSize = g.edgeSize();
        this.mc = g.getMC();
    }

    @Override
    public node_data getNode(int key) {
        return nodes.get(key);
    }

    @Override
    public edge_data getEdge(int src, int dest) {
        node_data nodeSrc = nodes.get(src);
        node_data nodeDest = nodes.get(dest);
        if (nodeSrc != null && nodeDest != null) {
            if (neighbors.containsKey(src)) {
                return neighbors.get(src).get(dest);
            }
        }
        return null;
    }

    @Override
    public void addNode(node_data n) {
        if (!nodes.containsKey(n.getKey())) {
            nodes.put(n.getKey(), n);
            mc++;
        }
    }

    @Override
    public void connect(int src, int dest, double w) {
        node_data nodeSrc = nodes.get(src);
        node_data nodeDest = nodes.get(dest);
        if (nodeSrc != null && nodeDest != null && w >= 0 && nodeSrc != nodeDest) {
            if (!neighbors.containsKey(src)) {
                HashMap<Integer, edge_data> map = new HashMap<>();
                neighbors.put(src, map);
            }
            if (!inEdges.containsKey(dest)) {
                HashMap<Integer, edge_data> map = new HashMap<>();
                inEdges.put(dest, map);
            }
            if (!neighbors.get(src).containsKey(dest)) {
                edge_data edge = new EdgeData(src, dest, w);
                neighbors.get(src).put(dest, edge);
                inEdges.get(dest).put(src,edge);
                edgeSize++;
                mc++;
            } else if (neighbors.get(src).get(dest).getWeight() != w) {
                edge_data edge = new EdgeData(src, dest, w);
                neighbors.get(src).replace(dest, edge);
                inEdges.get(dest).replace(src,edge);
                mc++;
            }
        }
    }

    @Override
    public Collection<node_data> getV() {
        return nodes.values();
    }

    @Override
    public Collection<edge_data> getE(int node_id) {
        if (!neighbors.containsKey(node_id)) {
            return new HashMap<Integer, edge_data>().values();
        }
        return neighbors.get(node_id).values();
    }

    @Override
    public node_data removeNode(int key) {
        node_data node = nodes.get(key);
        if (node != null) {
            for (node_data n : getV()) {
                removeEdge(key,n.getKey());
            }
            for (edge_data edge : inEdges.get(key).values()) {
                removeEdge(edge.getSrc(),key);
            }
            nodes.remove(key);
            mc++;
            return node;
        }
        return null;
    }

    @Override
    public edge_data removeEdge(int src, int dest) {
        node_data nodeSrc = nodes.get(src);
        node_data nodeDest = nodes.get(dest);
        if (nodeDest != null && nodeSrc != null) {
            if (neighbors.containsKey(src) && neighbors.get(src).containsKey(dest)) {
                edgeSize--;
                mc++;
                return neighbors.get(src).remove(dest);
            }
        }
        return null;
    }

    @Override
    public int nodeSize() {
        return nodes.size();
    }

    @Override
    public int edgeSize() {
        return edgeSize;
    }

    @Override
    public int getMC() {
        return mc;
    }
}