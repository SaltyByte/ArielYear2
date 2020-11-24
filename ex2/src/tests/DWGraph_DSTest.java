package tests;


import api.*;

public class DWGraph_DSTest {

    public static void main(String[] args) {
        directed_weighted_graph graph = graph();
        dw_graph_algorithms algo = new DWGraph_Algo();
        algo.init(graph);
        directed_weighted_graph g1 = algo.copy();
//        System.out.println(graph.getE(4));
//        System.out.println(g1.getE(4));
//        System.out.println(graph.edgeSize());
//        System.out.println(g1.edgeSize());
//        System.out.println(graph.getV());
//        System.out.println(g1.getV());
//        System.out.println(graph.getE(6));
//        System.out.println(g1.getE(6));
        System.out.println(graph.getE(9));
        System.out.println(g1.getE(9));
        System.out.println(algo.isConnected());


    }

    private static directed_weighted_graph graph() {
        directed_weighted_graph g = new DWGraph_DS();
        for (int i = 1; i < 15; i++) {
            node_data n = new NodeData(i);
            g.addNode(n);
        }
        g.connect(1, 2, 5);
        g.connect(2, 3, 2);
        g.connect(2, 4, 1);
        g.connect(3, 4, 10);
        g.connect(5, 4, 12);
        g.connect(5, 6, 1);
        g.connect(6, 11, 8);
        g.connect(7, 3, 2);
        g.connect(7, 4, 2);
        g.connect(7, 9, 5);
        g.connect(9, 14, 7);
        g.connect(9, 5, 3);
        g.connect(5, 12, 17);
        g.connect(12, 13, 2);
        g.connect(8,3,6);
        g.connect(10,11,4);
        g.connect(10,12,2);
        return g;
    }
}
