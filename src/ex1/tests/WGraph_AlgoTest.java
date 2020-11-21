package ex1.tests;


import ex1.src.WGraph_Algo;
import ex1.src.WGraph_DS;
import ex1.src.node_info;
import ex1.src.weighted_graph;
import ex1.src.weighted_graph_algorithms;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;


/**
 * This is a TEST class for the weighted_graph_algorithm, check most if not all, possible outcomes with the class.
 * @author Alex Baranov
 */
class WGraph_AlgoTest {
    private static Random _rand;
    private static int _seed;


    @BeforeAll
    static void seedInit() {
        _seed = 1;
    }

    /**
     * Simple runtime test, builds a random graph with 1,000,000 vertexes and 5,000,000 edges connected randomly.
     * If run time is over 10 seconds then it wont pass the test.
     * On my machine i tested this runtime test and ran it for 4.5 seconds in average.
     */

    @Test
    void graphRuntimeTest() {
        int v = 1000000, e = 5000000;
        double startTime = System.currentTimeMillis();
        weighted_graph g = graphCreator(v, e, 100, _seed);
        double endTime = System.currentTimeMillis();
        double runTime = endTime - startTime;
        assertTrue(10000 > runTime);
    }

    @Test
    void init() {
        weighted_graph g = graphCreator(10, 10, 50, _seed);
        weighted_graph_algorithms ga = new WGraph_Algo();
        ga.init(null);
        assertNull(ga.getGraph());
        ga.init(g);
        assertEquals(g, ga.getGraph());
    }


    @Test
    void copy() {
        weighted_graph g = graphCreator(10, 10, 50, _seed);
        weighted_graph_algorithms ga = new WGraph_Algo();
        ga.init(g);
        weighted_graph h = ga.copy();
        assertEquals(h.edgeSize(), g.edgeSize());
        assertEquals(h.nodeSize(), g.nodeSize());
        assertEquals(h.getMC(), g.getMC());
        for (node_info n : g.getV()) {
            assertNotSame(n, h.getNode(n.getKey()));
            assertEquals(n, h.getNode(n.getKey()));
        }
        for (node_info n : g.getV()) {
            for (node_info k : g.getV(n.getKey())) {
                assertTrue(h.getV(n.getKey()).contains(h.getNode(k.getKey())));
                assertEquals(g.getEdge(n.getKey(), k.getKey()), h.getEdge(n.getKey(), k.getKey()));
            }
        }
    }


    @Test
    void equalsTest(){
        weighted_graph g1 = graph();
        weighted_graph g2 = graph();
        assertEquals(g1,g2);
        assertEquals(g2,g1);
        g1.removeEdge(1,2);
        g1.removeEdge(3,4);
        g1.removeEdge(2,3);
        g1.removeEdge(2,4);
        g1.removeEdge(8,3);
        g1.removeEdge(3,7);
        g1.removeEdge(4,7);
        assertNotEquals(g1,g2);
        assertNotEquals(g2,g1);
        g1.connect(1,2,4);
        g1.connect(8,3,6);
        g1.connect(7,3,2);
        g1.connect(4,3,10);
        g1.connect(2,3,2);
        g1.connect(2,4,1);
        g1.connect(7,4,2);
        assertNotEquals(g1,g2);
        assertNotEquals(g2,g1);
        g1.connect(1,2,5);
        assertEquals(g1,g2);
        assertEquals(g2,g1);
        weighted_graph n1 = new WGraph_DS();
        weighted_graph n2 = new WGraph_DS();
        assertEquals(n1,n2);
        assertEquals(n2,n1);
    }

    @Test
    void isConnectedTest() {
        weighted_graph g = connectedGraphCreator(10, 30);
        weighted_graph_algorithms ga = new WGraph_Algo();
        ga.init(g);
        assertTrue(ga.isConnected());
        g.removeEdge(3, 4);
        assertFalse(ga.isConnected());
        g = new WGraph_DS();
        ga.init(g);
        assertTrue(ga.isConnected());
        g.addNode(1);
        assertTrue(ga.isConnected());
        g = graph();
        ga.init(g);
        assertTrue(ga.isConnected());
        g.removeEdge(4,5);
        g.removeEdge(9,5);
        assertFalse(ga.isConnected());
        g.connect(4,5,10);
        g.connect(9,5,10);
        assertTrue(ga.isConnected());
    }

    @Test
    void shortestPathDistTest() {
        weighted_graph h = graph();
        weighted_graph_algorithms ha = new WGraph_Algo();
        ha.init(h);
        ha.shortestPathDist(1,2);
        int vSize = 50;
        int weight = 1;
        weighted_graph g = connectedGraphCreator(vSize, weight);
        weighted_graph_algorithms ga = new WGraph_Algo();
        ga.init(g);
        assertEquals((vSize - 1) * weight, ga.shortestPathDist(0, vSize - 1));
        g.removeEdge(vSize / 2, (vSize / 2) + 1);
        assertEquals(-1, ga.shortestPathDist(0, vSize - 1));
        g = graph();
        ga.init(g);
        assertEquals(29,ga.shortestPathDist(8,10));
        assertEquals(0,ga.shortestPathDist(5,5));
        assertEquals(8,ga.shortestPathDist(9,2));
        assertEquals(17,ga.shortestPathDist(13,5));
        assertEquals(17,ga.shortestPathDist(5,13));
        assertEquals(33,ga.shortestPathDist(13,8));
        assertEquals(33,ga.shortestPathDist(8,13));
        assertEquals(15,ga.shortestPathDist(14,2));
        assertEquals(15,ga.shortestPathDist(2,14));
        assertEquals(16,ga.shortestPathDist(9,10));
        assertEquals(16,ga.shortestPathDist(10,9));
        assertEquals(10,ga.shortestPathDist(5,4));
        assertEquals(10,ga.shortestPathDist(4,5));
        assertEquals(-1,ga.shortestPathDist(10,15));

        g.removeEdge(4,5);
        g.removeEdge(9,5);
        assertEquals(-1,ga.shortestPathDist(1,5));
        assertEquals(8,ga.shortestPathDist(1,7));
    }
    @Test
    void shortestPath(){
        weighted_graph g = graph();
        weighted_graph_algorithms ga = new WGraph_Algo();
        ga.init(g);
        List<node_info> lst = new ArrayList<>();
        assertNull(ga.shortestPath(1,17));
        lst.add(g.getNode(1));
        lst.add(g.getNode(2));
        lst.add(g.getNode(4));
        lst.add(g.getNode(7));
        lst.add(g.getNode(9));
        lst.add(g.getNode(5));
        lst.add(g.getNode(6));
        assertEquals(lst, ga.shortestPath(1,6));
        Collections.reverse(lst);
        assertEquals(lst,ga.shortestPath(6,1));
        lst.clear();
        lst.add(g.getNode(13));
        lst.add(g.getNode(12));
        lst.add(g.getNode(10));
        lst.add(g.getNode(11));
        lst.add(g.getNode(6));
        lst.add(g.getNode(5));
        lst.add(g.getNode(9));
        lst.add(g.getNode(14));
        assertEquals(lst,ga.shortestPath(13,14));
        lst.clear();
        lst.add(g.getNode(13));
        assertEquals(lst,ga.shortestPath(13,13));
        g.removeEdge(4,5);
        g.removeEdge(9,5);
        assertNull(ga.shortestPath(13,14));
    }
    @Test
    void saveAndLoadTests(){
        weighted_graph g1 = graph();
        weighted_graph g2 = graph();
        weighted_graph_algorithms ga = new WGraph_Algo();

        assertTrue(ga.save("Hello.txt"));
        assertTrue(ga.load("Hello.txt"));

        ga.init(g1);
        assertTrue(ga.save("Hello.txt"));
        assertTrue(ga.load("Hello.txt"));
        assertEquals(g1,g2);
        assertEquals(g2,g1);
        assertFalse(ga.save(null));
        assertFalse(ga.load(null));
    }


    public static void initSeed(int seed) {
        _seed = seed;
        _rand = new Random(_seed);
    }

    private static int randNum(int min, int max) {
        double num1 = _rand.nextDouble();
        double dx = min + max;
        double ans = dx * num1 + min;
        return (int) (ans);
    }

    private static weighted_graph graphCreator(int vSize, int eSize, int weight, int seed) {
        weighted_graph graph = new WGraph_DS();
        initSeed(seed);
        for (int i = 0; i < vSize; i++) {
            graph.addNode(i);
        }
        while (graph.edgeSize() < eSize) {
            int a = randNum(0, vSize);
            int b = randNum(0, vSize);
            int k = randNum(0, weight);
            graph.connect(a, b, k);
        }
        return graph;
    }

    private static weighted_graph connectedGraphCreator(int vSize, int weight) {
        weighted_graph graph = new WGraph_DS();
        for (int i = 0; i < vSize; i++) {
            graph.addNode(i);
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
        g.connect(5, 12, 17);
        g.connect(12, 13, 2);
        g.connect(8,3,6);
        g.connect(10,11,4);
        g.connect(10,12,2);
        return g;
    }
}
