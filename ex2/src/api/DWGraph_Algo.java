package api;

import src.node_info;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class DWGraph_Algo implements dw_graph_algorithms {
    private directed_weighted_graph graph;

    @Override
    public void init(directed_weighted_graph g) {
        this.graph = g;
    }

    @Override
    public directed_weighted_graph getGraph() {
        return this.graph;
    }

    @Override
    public directed_weighted_graph copy() {
        if (this.graph != null){
            return new DWGraph_DS(this.graph);

        }
        return null;
    }

    @Override
    public boolean isConnected() {
        if (graph == null || graph.getV().size() <= 1) {
            return true;
        }
        node_data node = graph.getV().iterator().next();
        Dijkstra(node.getKey());
        for (node_data n : graph.getV()) {
            if (n.getWeight() == Double.POSITIVE_INFINITY) {
                return false;
            }
        }
        return true;
    }

    @Override
    public double shortestPathDist(int src, int dest) {
        return 0;
    }

    @Override
    public List<node_data> shortestPath(int src, int dest) {
        return null;
    }

    @Override
    public boolean save(String file) {
        return false;
    }

    @Override
    public boolean load(String file) {
        return false;
    }

    private void Dijkstra(int src){
        for(node_data n : graph.getV()){
            n.setInfo("");
            n.setWeight(Double.POSITIVE_INFINITY);
        }
        graph.getNode(src).setWeight(0);
        node_data nodeOne = graph.getNode(src);
        Queue<node_data> q = new PriorityQueue<>(new WeightComparator());
        q.add(nodeOne);
        while (!q.isEmpty()) {
            node_data node = q.peek();
            for (edge_data edge : graph.getE(node.getKey())) {
                node_data neighbor = graph.getNode(edge.getDest());
                if (node.getWeight() < neighbor.getWeight() && !q.contains(neighbor)) {
                    q.add(neighbor);
                }
                double weight = graph.getEdge(node.getKey(), neighbor.getKey()).getWeight();
                if (node.getWeight() + weight < neighbor.getWeight()) {
                    neighbor.setWeight(node.getWeight() + weight);
                    neighbor.setInfo("" + node.getKey());
                }
            }
            q.remove();
        }
    }
    private static class WeightComparator implements Comparator<node_data> {

        /**
         * Overrides compare method by tag (weight), if node1 > node2 return 1, if node1 < node2 return -1, else return 0.
         * used in the priorityQueue
         * @param node1 - node1 to compare
         * @param node2 - node2 to compare
         * @return - int if node1 > node2 return 1, if node1 < node2 return -1, else return 0.
         */

        @Override
        public int compare(node_data node1, node_data node2) {
            if (node1.getTag() > node2.getTag()) {
                return 1;
            } else if (node1.getTag() < node2.getTag()) {
                return -1;
            }
            return 0;
        }
    }
}
