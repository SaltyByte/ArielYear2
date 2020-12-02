package tests;


import api.*;

public class DWGraph_DSTest {

    public static void main(String[] args) {
        directed_weighted_graph g = graph();
        dw_graph_algorithms ga = new DWGraph_Algo();
        ga.init(g);
        //System.out.println(ga.shortestPathDist(1,3));
        System.out.println(ga.shortestPath(9,13));

    }

    private static directed_weighted_graph graph() {
        directed_weighted_graph g = new DWGraph_DS();
        for (int i = 1; i < 15; i++) {
            node_data n = new NodeData(i);
            g.addNode(n);
        }
        g.connect(1, 2, 5);
        g.connect(2, 3, 2);
        g.connect(3, 2, 5);
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
    private static directed_weighted_graph smallGraph(){
        directed_weighted_graph g = new DWGraph_DS();
        for (int i = 1; i < 6; i++) {
            node_data n = new NodeData(i);
            g.addNode(n);
        }
        g.connect(1, 2, 5.1);
        g.connect(1, 5, 3.6);
        g.connect(2, 3, 1.5);
        g.connect(3, 4, 3.2);
        g.connect(4, 2, 2.7);



        return g;
    }
}
