package ex1.tests;

import ex1.src.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JunitTest_GraphAlgo {

    public weighted_graph graphBuilder(int size) {
        weighted_graph g = new WGraph_DS();
        for (int i = 0; i < size; ++i)
            g.addNode(i);
        return g;
    }


    @Test
    public void test_copy() {
        weighted_graph graph = graphBuilder(10);
        graph.connect(0,1,2);
        graph.connect(1,2,2);
        graph.connect(1,2,3);
        graph.connect(9,2,3);
        graph.connect(0,7,1);
        WGraph_Algo algo = new WGraph_Algo();
        algo.init(graph);
        weighted_graph copy = algo.copy();

        Assertions.assertEquals(graph.nodeSize(), copy.nodeSize());
        Assertions.assertEquals(graph.edgeSize(), copy.edgeSize());
        Assertions.assertEquals(graph.getV(0).size(), copy.getV(0).size());
    }



    @Test
    public void test_connected() {
        weighted_graph graph = graphBuilder(10);
        for (int i = 0; i < 9; ++i)
            graph.connect(i, i + 1, i/2+1);
        WGraph_Algo algo = new WGraph_Algo();
        algo.init(graph);
        Assertions.assertTrue(algo.isConnected());
        Assertions.assertEquals(10, algo.shortestPath(0,9).size());
        //printPath(algo.shortestPath(0,5));
    }


    @Test
    public void test_shortestPath1() {
        weighted_graph graph = graphBuilder(10);
        for (int i = 0; i<99;i = i + 2){
            graph.connect(i, i + 2, i+1);
        }
        WGraph_Algo algo = new WGraph_Algo();
        algo.init(graph);
        Assertions.assertNotNull(algo.shortestPath(0,2));
        Assertions.assertNull(algo.shortestPath(0,1));
        Assertions.assertFalse((algo.isConnected()));
        //printPath(algo.shortestPath(0,1));
        //System.out.println("distance is: " + algo.shortestPathDist(0,1));
        //System.out.println(algo.toString(algo.getGraph()));
    }


    @Test
    public void test_saveLoad (){
        weighted_graph graph = graphBuilder(10);
        graph.connect(0,1,1);
        graph.connect(0,2,1);
        graph.connect(5,2,9);
        graph.connect(2,5,5);
        WGraph_Algo algo = new WGraph_Algo();
        algo.init(graph);
        Assertions.assertTrue(algo.save("graph1"));
        System.out.println(algo.toString(algo.getGraph()));

        Assertions.assertTrue(algo.load("graph1"));
        weighted_graph graph1 = algo.getGraph();
        System.out.println(algo.toString(algo.getGraph()));
        Assertions.assertTrue(algo.save("graph1_new"));
        Assertions.assertEquals(graph.nodeSize(), graph1.nodeSize());
        assertEquals(graph.edgeSize(), graph1.edgeSize());          ///////////

    }


    @Test
    void test_Time() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(10000), () -> {
            weighted_graph g = graphBuilder(1000000);
            for (int i = 0; i < g.nodeSize(); i++)
                for (int j = i; j < i+10; j++) {
                    g.connect(i, j, 1);
                }
        });
    }


    @Test
    void test_copyTime() {
        weighted_graph g = graphBuilder(1000000);
        for (int i = 0; i < g.nodeSize(); i++) {
            for (int j = i; j < i+4; j++) {
                g.connect(i, j, 1);
            }
        }

        WGraph_Algo algo = new WGraph_Algo();
        algo.init(g);
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(10000), () -> {
            weighted_graph gc = algo.copy();
        });
    }



    @Test
    void test_MC_edgeSize () {
        weighted_graph g = new WGraph_DS();
        for (int i = 0; i < 50; ++i)
            g.addNode(i);
        for (int i = 0; i < g.nodeSize(); i++) {
            for (int j = 0; j < 15; j++) {
                g.connect(i, j, 1);
            }
        }
        Assertions.assertEquals(680, g.getMC());
        Assertions.assertEquals(630, g.edgeSize());
        WGraph_Algo algo = new WGraph_Algo();
        algo.init(g);
        Assertions.assertTrue(algo.isConnected());


    }


    //helper function:
    private void printPath(List<node_info> path) {
        if (path == null)
            return;
        for (int i = 0; i < path.size() - 1; ++i) {
            System.out.print(path.get(i).getKey() + ", ");
            System.out.print(path.get(path.size()-1).getKey());
        }
    }
}
