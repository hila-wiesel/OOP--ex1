package ex1.tests;

import ex1.src.WGraph_DS;
import ex1.src.weighted_graph;
import org.junit.jupiter.api.Assertions;
import org.testng.annotations.Test;

import java.time.Duration;


public class JunitTest_GraphDS {

    public weighted_graph graphBuilder(int size) {
        weighted_graph g = new WGraph_DS();
        for (int i = 0; i < size; ++i)
            g.addNode(i);
        return g;
    }

    @Test
    public void test_getNode() {
        weighted_graph g = graphBuilder(10);
        for (int i = 0; i < 10; ++i)
            Assertions.assertNotNull(g.getNode(i));
    }

    @Test
    public void test_getNullNode() {
        weighted_graph g = graphBuilder(0);
        Assertions.assertNull(g.getNode(0));
    }

    @Test
    public void connect1() {
        weighted_graph g = graphBuilder(10);
        for (int i = 0; i<10;i = i + 2){
            g.connect(i, i + 2, i);
        }
        for (int j = 0, k = 1; j < 8 && k < 10; j = j + 2, k = k + 2) {
            Assertions.assertTrue(g.hasEdge(j, j + 2));
            Assertions.assertFalse(g.hasEdge(k, k + 2));
        }
    }

    @Test
    public void connectSame() {
        weighted_graph g = graphBuilder(10);
        g.connect(0, 1, 1);
        g.connect(0, 2, 1);
        g.connect(0, 3, 1);
        int expect = g.getV(0).size();
        g.connect(0, 0, 1);
        g.connect(0, 1, 2);
        Assertions.assertEquals(expect, g.getV(0).size());
    }

    @Test
    public void connectNotExist() {
        weighted_graph g = graphBuilder(1);
        g.connect(0, 1, 2);
        Assertions.assertEquals(1, g.getMC());
    }

    @Test
    public void remove1(){
        weighted_graph g = graphBuilder(10);
        g.connect(0, 1, 1);
        g.connect(0, 2, 1);
        g.connect(0, 3, 1);
        g.removeNode(0);
        Assertions.assertEquals(0, g.edgeSize());
    }

    @Test
    public void remove2(){
        weighted_graph g = graphBuilder(0);
        g.removeNode(0);
    }

    @Test
    public void remove3(){
        weighted_graph g = graphBuilder(10);
        g.connect(0, 1, 1);
        g.connect(0, 2, 1);
        g.connect(0, 3, 1);
        g.removeEdge(0,1);
        g.removeEdge(2,3);
        Assertions.assertEquals(2, g.edgeSize());

    }


    @Test
    public  void graphTest_BuildRunTime()
    {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(10000), () -> {

            weighted_graph g=graphBuilder(1000000);
            for (int i = 0; i <g.nodeSize() ; i++)
                for (int j = 0; j <11 ; j++)
                    g.connect(i,j,1);
        });
    }
}