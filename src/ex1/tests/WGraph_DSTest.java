package ex1.tests;


import ex1.src.WGraph_DS;
import ex1.src.node_info;
import ex1.src.weighted_graph;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a TEST class for the weighted_graph, check most if not all, possible outcomes with the class.
 * @author Alex Baranov
 */
class WGraph_DSTest {

    @Test
    void getNode() {
        weighted_graph g = graph();
        assertEquals(3, g.getNode(3).getKey());
        assertEquals(6, g.getNode(6).getKey());
        assertNull(g.getNode(16));
        assertNull(g.getNode(-1));
    }

    @Test
    void hasEdge() {
        weighted_graph g = graph();
        assertTrue(g.hasEdge(1,2));
        assertTrue(g.hasEdge(2,1));
        assertTrue(g.hasEdge(6,11));
        assertTrue(g.hasEdge(5,9));
        assertTrue(g.hasEdge(14,9));
        assertFalse(g.hasEdge(1,3));
        assertFalse(g.hasEdge(14,7));
        assertFalse(g.hasEdge(-1,2));
        assertFalse(g.hasEdge(0,1));
        assertFalse(g.hasEdge(1,4));
        assertFalse(g.hasEdge(10,11));
    }

    @Test
    void getEdge() {
        weighted_graph g = graph();
        assertEquals(1,g.getEdge(2,4));
        assertEquals(1,g.getEdge(4,2));
        assertEquals(8,g.getEdge(11,6));
        assertEquals(5,g.getEdge(5,12));
        assertEquals(5,g.getEdge(12,5));
        assertEquals(7,g.getEdge(12,13));
        assertEquals(-1,g.getEdge(10,11));
        assertEquals(-1,g.getEdge(1,15));
        assertEquals(-1,g.getEdge(1,3));
    }

    @Test
    void addNode() {
        weighted_graph g = new WGraph_DS();
        assertNull(g.getNode(1));
        assertNull(g.getNode(2));
        assertNull(g.getNode(3));
        assertNull(g.getNode(-1));
        assertNull(g.getNode(4));
        g.addNode(1);
        g.getNode(1).setTag(3.0);
        g.addNode(2);
        g.addNode(3);
        g.addNode(-1);
        g.addNode(1);
        assertEquals(1,g.getNode(1).getKey());
        assertEquals(2,g.getNode(2).getKey());
        assertEquals(3,g.getNode(3).getKey());
        assertEquals(-1,g.getNode(-1).getKey());
        assertEquals(4,g.nodeSize());
        assertEquals(3.0,g.getNode(1).getTag());
        assertNull(g.getNode(4));
    }

    @Test
    void connect() {
        weighted_graph g = new WGraph_DS();
        for (int i = 0; i < 5; i++) {
            g.addNode(i);
        }
        g.connect(0,1,10);
        g.connect(1,2,20);
        g.connect(2,3,30);
        g.connect(3,4,40);
        g.connect(3,4,50);
        g.connect(3,3,60);
        assertTrue(g.hasEdge(0,1));
        assertTrue(g.hasEdge(1,2));
        assertFalse(g.hasEdge(0,2));
        assertFalse(g.hasEdge(0,3));
        assertFalse(g.hasEdge(0,5));
        assertEquals(10,g.getEdge(0,1));
        assertEquals(20,g.getEdge(1,2));
        assertEquals(30,g.getEdge(2,3));
        assertEquals(50,g.getEdge(3,4));
        assertEquals(-1,g.getEdge(3,3));
    }



    @Test
    void getV() {
        weighted_graph g = graph();
        Iterator<node_info> itr = g.getV().iterator();
        for (int i = 1; i <= g.nodeSize(); i++) {
            assertEquals(g.getNode(i), itr.next());
        }
    }
    @Test
    void getVNeighbors(){
        weighted_graph g = graph();
        assertTrue(g.getV(5).contains(g.getNode(9)));
        assertTrue(g.getV(5).contains(g.getNode(4)));
        assertTrue(g.getV(5).contains(g.getNode(6)));
        assertTrue(g.getV(5).contains(g.getNode(12)));
    }

    @Test
    void removeNode() {
        weighted_graph g = graph();
        assertEquals(5,g.getNode(5).getKey());
        assertEquals(5,g.removeNode(5).getKey());
        assertEquals(13,g.nodeSize());
        assertEquals(10,g.edgeSize());
        assertEquals(33,g.getMC());
        assertNull(g.getNode(5));
        assertFalse(g.hasEdge(5,12));
    }

    @Test
    void removeEdge() {
        weighted_graph g = graph();
        assertTrue(g.hasEdge(1,2));
        assertTrue(g.hasEdge(2,1));
        assertTrue(g.hasEdge(5,12));
        assertTrue(g.hasEdge(4,5));
        g.removeEdge(1,2);
        g.removeEdge(5,12);
        g.removeEdge(4,5);
        assertFalse(g.hasEdge(1,2));
        assertFalse(g.hasEdge(2,1));
        assertFalse(g.hasEdge(5,12));
        assertFalse(g.hasEdge(4,5));
        assertFalse(g.hasEdge(10,11));
        assertEquals(11,g.edgeSize());
    }

    @Test
    void nodeSize() {
        weighted_graph g = graph();
        assertEquals(14,g.nodeSize());
    }

    @Test
    void edgeSize() {
        weighted_graph g = graph();
        assertEquals(14,g.edgeSize());
    }

    @Test
    void getMC() {
        weighted_graph g = graph();
        weighted_graph h = new WGraph_DS();
        assertEquals(28,g.getMC());
        assertEquals(0,h.getMC());
    }
    private static weighted_graph graph() {
        weighted_graph g = new WGraph_DS();
        for (int i = 1; i < 15; i++) {
            g.addNode(i);
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
        g.connect(5, 12, 5);
        g.connect(12, 13, 7);
        return g;
    }
}
