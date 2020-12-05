package tests;

import api.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DWGraph_AlgoTest {

    @Test
    void init() {
        directed_weighted_graph g = graph();
        dw_graph_algorithms ga = new DWGraph_Algo();
        ga.init(null);
        assertNull(ga.getGraph());
        ga.init(g);
        assertEquals(g, ga.getGraph());
    }

    @Test
    void getGraph() {
    }

    @Test
    void copy() {
        directed_weighted_graph g = graph();
        dw_graph_algorithms ga = new DWGraph_Algo();
        ga.init(g);
        directed_weighted_graph h = ga.copy();
        assertEquals(h.edgeSize(), g.edgeSize());
        assertEquals(h.nodeSize(), g.nodeSize());
        assertEquals(h.getMC(), g.getMC());
        for (node_data n : g.getV()) {
            assertNotSame(n, h.getNode(n.getKey()));
            assertEquals(n.getKey(), h.getNode(n.getKey()).getKey());
        }
        for (node_data n : g.getV()) {
            for (edge_data edge : g.getE(n.getKey())) {
                assertTrue(h.getE(n.getKey()).contains(h.getEdge(edge.getSrc(),edge.getDest())));
                assertEquals(g.getEdge(n.getKey(), edge.getDest()).getSrc(), h.getEdge(n.getKey(), edge.getDest()).getSrc());
                assertEquals(g.getEdge(n.getKey(), edge.getDest()).getDest(), h.getEdge(n.getKey(), edge.getDest()).getDest());
            }
        }
    }

    @Test
    void isConnected() {
        directed_weighted_graph g = new DWGraph_DS();
        dw_graph_algorithms ga = new DWGraph_Algo();
        ga.init(g);
        assertTrue(ga.isConnected());
        g = graph();
        ga.init(g);
        assertFalse(ga.isConnected());
        g.connect(2,1,10);
        g.connect(3,8,10);
        g.connect(14,9,10);
        g.connect(13,12,10);
        assertFalse(ga.isConnected());
        g.connect(4,2,10);
        g.connect(6,5,10);
        g.connect(4,7,10);
        assertFalse(ga.isConnected());
        g.connect(12,5,10);
        assertTrue(ga.isConnected());
    }

    @Test
    void shortestPathDist() {
        directed_weighted_graph g = fullGraph();
        dw_graph_algorithms ga = new DWGraph_Algo();
        ga.init(g);
        assertEquals(21.5,ga.shortestPathDist(8,10));
        assertEquals(0,ga.shortestPathDist(5,5));
        assertEquals(21,ga.shortestPathDist(9,2));
        assertEquals(9.4,ga.shortestPathDist(13,5));
        assertEquals(13,ga.shortestPathDist(5,13));
        assertEquals(32.599999999999994,ga.shortestPathDist(13,8));
        assertEquals(25.5,ga.shortestPathDist(8,13));
        assertEquals(14,ga.shortestPathDist(14,2));
        assertEquals(9.2,ga.shortestPathDist(2,14));
        assertEquals(12,ga.shortestPathDist(9,10));
        assertEquals(12,ga.shortestPathDist(5,4));
        assertEquals(4.2,ga.shortestPathDist(4,5));
        assertEquals(-1,ga.shortestPathDist(10,15));
        g.removeEdge(5,4);
        g.removeEdge(9,5);
        assertEquals(3,ga.shortestPathDist(1,5));
        assertEquals(-1,ga.shortestPathDist(4,5));
        assertEquals(-1,ga.shortestPathDist(13,4));
        assertEquals(-1,ga.shortestPathDist(1,7));
    }

    @Test
    void shortestPath() {
        directed_weighted_graph g = fullGraph();
        dw_graph_algorithms ga = new DWGraph_Algo();
        ga.init(g);
        List<node_data> lst = new ArrayList<>();
        assertNull(ga.shortestPath(2,7));
        lst.add(g.getNode(12));
        lst.add(g.getNode(13));
        lst.add(g.getNode(5));
        lst.add(g.getNode(6));
        lst.add(g.getNode(11));
        lst.add(g.getNode(10));
        g.connect(12,10,30);
        assertEquals(lst, ga.shortestPath(12,10));
        lst.clear();
        lst.add(g.getNode(10));
        lst.add(g.getNode(12));
        assertEquals(lst,ga.shortestPath(10,12));
        lst.clear();
        lst.add(g.getNode(13));
        lst.add(g.getNode(5));
        lst.add(g.getNode(4));
        lst.add(g.getNode(9));
        lst.add(g.getNode(14));
        g.connect(5,14,30);
        assertEquals(lst,ga.shortestPath(13,14));
        lst.clear();
        lst.add(g.getNode(13));
        assertEquals(lst,ga.shortestPath(13,13));
        g.removeEdge(4,9);
        g.removeEdge(5,14);
        assertNull(ga.shortestPath(13,14));
    }

    @Test
    void save() {
    }

    @Test
    void load() {
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
    private static directed_weighted_graph connectedGraphCreator(int vSize, int weight) {
        directed_weighted_graph graph = new DWGraph_DS();
        for (int i = 0; i < vSize; i++) {
            node_data n = new NodeData(i);
            graph.addNode(n);
        }
        int a = 0;
        int b = 1;
        while (graph.edgeSize() < vSize - 1) {
            graph.connect(a, b, weight);
            a++;
            b++;
        }
        return graph;
    }
    private static directed_weighted_graph fullGraph() {
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
        g.connect(4,9,1.2);
        g.connect(3,9,3.5);
        g.connect(13,5,9.4);
        g.connect(1,5,3);
        g.connect(14,8,3);
        return g;
    }
}