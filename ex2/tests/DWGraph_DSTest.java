import api.DWGraph_DS;
import api.NodeData;
import api.directed_weighted_graph;
import api.node_data;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class DWGraph_DSTest {

    @Test
    void getNode() {
        directed_weighted_graph g = graph();
        assertEquals(3, g.getNode(3).getKey());
        assertEquals(6, g.getNode(6).getKey());
        assertNull(g.getNode(16));
        assertNull(g.getNode(-1));
    }

    @Test
    void getEdge() {
        directed_weighted_graph g = graph();
        assertEquals(1,g.getEdge(2,4).getWeight());
        assertNull(g.getEdge(4,2));
        assertNull(g.getEdge(11,6));
        assertEquals(17,g.getEdge(5,12).getWeight());
        assertNull(g.getEdge(12,5));
        assertEquals(13,g.getEdge(12,13).getDest());
        assertEquals(12,g.getEdge(12,13).getSrc());
        assertEquals(2,g.getEdge(12,13).getWeight());
        assertEquals(4,g.getEdge(10,11).getWeight());
        assertNull(g.getEdge(1,15));
        assertNull(g.getEdge(1,3));
    }

    @Test
    void addNode() {
        directed_weighted_graph g = new DWGraph_DS();
        assertNull(g.getNode(1));
        assertNull(g.getNode(2));
        assertNull(g.getNode(3));
        assertNull(g.getNode(-1));
        assertNull(g.getNode(4));
        node_data n1 = new NodeData(1);
        g.addNode(n1);
        node_data n2 = new NodeData(2);
        g.addNode(n2);
        node_data n3 = new NodeData(3);
        g.addNode(n3);
        node_data n5 = new NodeData(5);
        g.addNode(n5);
        assertEquals(1,g.getNode(1).getKey());
        assertEquals(2,g.getNode(2).getKey());
        assertEquals(3,g.getNode(3).getKey());
        assertEquals(4,g.nodeSize());
        assertNull(g.getNode(4));
    }

    @Test
    void connect() {
        directed_weighted_graph g = new DWGraph_DS();
        for (int i = 0; i < 5; i++) {
            node_data n = new NodeData(i);
            g.addNode(n);
        }
        g.connect(0, 1, 10);
        g.connect(1, 2, 20);
        g.connect(2, 3, 30);
        g.connect(3, 4, 40);
        g.connect(3, 4, 50);
        g.connect(3, 3, 60);
        assertNotNull(g.getEdge(0, 1));
        assertNotNull(g.getEdge(1, 2));
        assertNull(g.getEdge(0, 2));
        assertNull(g.getEdge(0, 3));
        assertNull(g.getEdge(0, 5));
        assertEquals(10, g.getEdge(0, 1).getWeight());
        assertEquals(20, g.getEdge(1, 2).getWeight());
        assertEquals(30, g.getEdge(2, 3).getWeight());
        assertEquals(50, g.getEdge(3, 4).getWeight());
        assertNull(g.getEdge(3, 3));
    }

    @Test
    void getV() {
        directed_weighted_graph g = graph();
        Iterator<node_data> itr = g.getV().iterator();
        for (int i = 1; i <= g.nodeSize(); i++) {
            assertEquals(g.getNode(i), itr.next());
        }
    }

    @Test
    void getE() {
        directed_weighted_graph g = graph();
        assertTrue(g.getE(5).contains(g.getEdge(5,4)));
        assertTrue(g.getE(5).contains(g.getEdge(5,6)));
        assertFalse(g.getE(5).contains(g.getEdge(4,5)));
        assertFalse(g.getE(5).contains(g.getEdge(6,5)));
        assertTrue(g.getE(4).isEmpty());
    }

    @Test
    void removeNode() {
        directed_weighted_graph g = graph();
        assertEquals(5,g.getNode(5).getKey());
        assertEquals(14,g.nodeSize());
        assertEquals(19,g.edgeSize());
        assertEquals(33,g.getMC());
        assertNotNull(g.removeNode(5));
        assertNull(g.getNode(5));
        assertNull(g.getEdge(5,12));
    }

    @Test
    void removeEdge() {
        directed_weighted_graph g = graph();
        assertNotNull(g.getEdge(1,2));
        assertNull(g.getEdge(2,1));
        assertNotNull(g.getEdge(5,12));
        assertNull(g.getEdge(4,5));
        assertEquals(19,g.edgeSize());
        g.removeEdge(1,2);
        g.removeEdge(5,12);
        g.removeEdge(5,4 );
        assertNull(g.getEdge(1,2));
        assertNull(g.getEdge(2,1));
        assertNull(g.getEdge(5,12));
        assertNull(g.getEdge(4,5));
        assertNotNull(g.getEdge(10,11));
        assertEquals(16,g.edgeSize());
    }

    @Test
    void nodeSize() {
        directed_weighted_graph g = graph();
        assertEquals(14,g.nodeSize());
    }

    @Test
    void edgeSize() {
        directed_weighted_graph g = graph();
        assertEquals(19,g.edgeSize());
        g.removeEdge(1,2);
        g.removeEdge(2,1);
        g.removeNode(5);
        assertEquals(14,g.edgeSize());
    }

    @Test
    void getMC() {
        directed_weighted_graph g = graph();
        directed_weighted_graph h = new DWGraph_DS();
        assertEquals(33,g.getMC());
        assertEquals(0,h.getMC());
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