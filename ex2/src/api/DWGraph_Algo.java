package api;

import java.util.*;

public class DWGraph_Algo implements dw_graph_algorithms {
    private directed_weighted_graph graph;
    private HashMap<Integer, TagWeight> tags;


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
        for (node_data node : graph.getV()) {
            Dijkstra(node.getKey(),null);
            if (tags.size() != graph.nodeSize()){
                return false;
            }
        }
        return true;
    }

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
        while (!finished) { // while loop, adds shortest path from dest to src
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
                node_data neighbor = graph.getNode(edge.getDest());
                if (!tags.containsKey(neighbor.getKey()) && !q.contains(neighbor)) {
                    TagWeight t = new TagWeight(node, edge.getWeight() + tags.get(node.getKey()).getSumWeight());
                    tags.put(edge.getDest(), t);
                    q.add(neighbor);
                }
                else if (edge.getWeight() + tags.get(edge.getSrc()).getSumWeight() < tags.get(edge.getDest()).getSumWeight()) {
                    TagWeight t = new TagWeight(node, edge.getWeight() + tags.get(node.getKey()).getSumWeight());
                    tags.put(edge.getDest(), t);
                }
                if (dest != null && neighbor.getKey() == dest){
                    return;
                }
            }
        }
    }

    @Override
    public boolean save(String file) {
        return false;
    }

    @Override
    public boolean load(String file) {
        return false;
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
