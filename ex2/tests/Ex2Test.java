import api.DWGraph_DS;
import api.NodeData;
import api.directed_weighted_graph;
import api.node_data;
import gameClient.Ex2;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

public class Ex2Test {

    @Test
    void calcDiffLocation() {
        directed_weighted_graph graph = graph();
        assertEquals(graph.getNode(5), Ex2.nextNode(graph,9,11));
        assertEquals(graph.getNode(9), Ex2.nextNode(graph,7,14));
        assertEquals(graph.getNode(10), Ex2.nextNode(graph,11,13));
        assertEquals(graph.getNode(2), Ex2.nextNode(graph,1,4));
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
        g.connect(6, 11, 4);
        g.connect(7, 3, 2);
        g.connect(7, 4, 2);
        g.connect(7, 9, 5);
        g.connect(9, 14, 7);
        g.connect(9, 5, 3);
        g.connect(5, 12, 17);
        g.connect(12, 13, 2);
        g.connect(8,3,6);
        g.connect(10,11,4);
        g.connect(11,10,4);
        g.connect(10,12,2);
        return g;
    }

}
