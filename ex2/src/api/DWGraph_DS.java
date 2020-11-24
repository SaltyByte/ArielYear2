package api;

import java.util.Collection;
import java.util.HashMap;

public class DWGraph_DS implements directed_weighted_graph {
    private HashMap<Integer, node_data> nodes;
    private HashMap<Integer, HashMap<node_data, edge_data>> edges;
    private int edgeSize, mc;


    public DWGraph_DS() {
        this.nodes = new HashMap<>();
        this.edges = new HashMap<>();
        this.edgeSize = 0;
        this.mc = 0;
    }

    public DWGraph_DS(directed_weighted_graph g) {
        // Check if graph is null else copy
        if (g == null) {
            return;
        }
        this.nodes = new HashMap<>();
        this.edges = new HashMap<>();
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
            if (edges.containsKey(src)) {
                return edges.get(src).get(nodeDest);
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
            if (!edges.containsKey(src)) {
                HashMap<node_data, edge_data> map = new HashMap<>();
                edges.put(src, map);
            }
            if (!edges.get(src).containsKey(nodeSrc)) {
                edge_data edge = new EdgeData(src, dest, w);
                edges.get(src).put(nodeDest, edge);
                edgeSize++;
                mc++;
            } else if (edges.get(src).get(nodeDest).getWeight() != w) {
                edge_data edge = new EdgeData(src, dest, w);
                edges.get(src).replace(nodeDest, edge);
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
        if (!edges.containsKey(node_id)) {
            HashMap<Integer, edge_data> map = new HashMap<>();
            return map.values();
        }
        return edges.get(node_id).values();
    }

    @Override
    public node_data removeNode(int key) {
        node_data node = nodes.get(key);
        if (node != null) {
            for (edge_data n : getE(key)) {
                removeEdge(key, n.getDest());
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
            if (edges.containsKey(src) && edges.get(src).containsKey(nodeDest)) {
                return edges.get(src).remove(nodeDest);
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


    private class EdgeData implements edge_data {
        private int src, dest, tag;
        private double weight;
        private String info;

        public EdgeData(int src, int dest, double weight) {
            this.src = src;
            this.dest = dest;
            this.weight = weight;
        }

        @Override
        public int getSrc() {
            return this.src;
        }

        @Override
        public int getDest() {
            return this.dest;
        }

        @Override
        public double getWeight() {
            return this.weight;
        }

        @Override
        public String getInfo() {
            return this.info;
        }

        @Override
        public void setInfo(String s) {
            this.info = s;
        }

        @Override
        public int getTag() {
            return this.tag;
        }

        @Override
        public void setTag(int t) {
            this.tag = t;
        }

        public String toString() {
            return "|Src: " + this.src + " Dest: " + this.dest + " Weight: " + this.weight + "|";
        }
    }
}