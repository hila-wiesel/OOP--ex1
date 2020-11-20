package ex1.src;

import java.io.*;
import java.util.*;

/**
 * This class implement the interface graph_algorithms.
 * Each object that created in this class has private field - graph, that the object can do changes on it.
 * Using the function: init(), copy(), isConnected(), shortestPathDist(), shortestPath(),
 * saving to a file and load a graph from a file.
 */

public class WGraph_Algo implements weighted_graph_algorithms {
    private weighted_graph graph;


    @Override
    /** initialize the graph_algorithms object a specific graph to work on */
    public void init(weighted_graph g) {
        this.graph = g;
    }

    @Override
    public weighted_graph getGraph() {
        return graph;
    }


    @Override
    /** Making a deep copying for the graph, by copy constructor that coping all the nodes first, and than creating the fitting edges. */
    public weighted_graph copy() {
        return new WGraph_DS(this.graph);
    }

    @Override
    /**  This function return whether the graph is connect or not.
     * using the helper function- Dijkstra to get the distance from the first node to all of the rest,
     * and checking if there in a node that the distance between them is infinite (no way between them).
     */
    public boolean isConnected() {
        Collection<node_info> nodes = graph.getV();
        Iterator<node_info> iterator = nodes.iterator();
        if(nodes.size()==0 )        //  empty graph is connected
            return true;

        node_info first = iterator.next();
        Dijkstra(graph, first.getKey());
        boolean ans = true;

        while (iterator.hasNext()){
            node_info temp = iterator.next();
            if(temp.getTag()==Integer.MAX_VALUE)
                ans=false;
        }
        //      restart(nodes);
        return ans;
    }

    /** This function return the length of the shortest path from src node to dest node
     * using the helper function- Dijkstra to get the distance from src to dest
     * return -1 if there is no path between the two.
     */
    @Override
    public double shortestPathDist(int src, int dest) {
        Dijkstra(graph,src);
        double ans = graph.getNode(dest).getTag();
        if (ans ==Integer.MAX_VALUE)    // no path
            return -1;
        return ans;
    }




    @Override
    /** This function return the shortest path from src node to dest node
     * using the helper function- Dijkstra to get the the previously node from each node that in the shortest path back from dest to src
     */
    public List<node_info> shortestPath(int src, int dest) {
        if (graph.getNode(src) == null || graph.getNode(dest) == null)
            return null;

        HashMap<Integer,Integer> parents = Dijkstra(graph, src);
        if (graph.getNode(dest).getTag()==Integer.MAX_VALUE) {        //	there is no way to get dest from src
            return null;
        }

        List<node_info> path = new ArrayList<node_info>();
        if (src==dest) {
            path.add(graph.getNode(src));
            return path;
        }

        int p = dest;
        while(parents.get(p) != null) {	 	//there is a node before him in the path
            path.add(graph.getNode(p));
            p = parents.get(p);
        }
        path.add(graph.getNode(p));     //add the lest node (src)

        Collections.reverse(path);
        return path;

    }


    @Override
    /** This function save the graph in to a new file in the given string name file. */
    public boolean save(String file) {
        try{
            File newFile = new File(file);
            FileWriter fw =new FileWriter(newFile);
            fw.write(toString(graph));
            fw.close();
            return true;
        }
        catch (IOException e){
            return false;
        }
    }


    @Override
    /** This function load the details from the given file and create a new graph in accordance to it,
     * and init the graph to the WGraph_Algo. */
    public boolean load(String file) {

        String line = "";
        weighted_graph graph = new WGraph_DS();     //creat new graph

        try{
            BufferedReader br = new BufferedReader(new FileReader(file));
            for(int i=0; i<4;++i)
                line=br.readLine();      // skip 3 lines

            //creating the nodes:
            String[] graphInfo = line.split(",");
            for(int i=0; i<graphInfo.length; ++i ){
                graph.addNode(Integer.parseInt(graphInfo[i]));
            }

            int k=0;
            while ((line=br.readLine()) != null) {
                line = br.readLine();     // skip line
                if (line != null) {
                    String[] ni = line.split(",");
                    for (int i = 0; i < ni.length-1; i = i + 2) {
                        graph.connect(k, Integer.parseInt(ni[i]), Double.parseDouble(ni[i + 1]));
                    }
                    ++k;
                }
            }
            init(graph);
            return true;
        }
        catch (IOException e){          //  currect exception?
            return false;
        }
    }


    //helper functions:

    /**
     * this private function use in this class for help an other algorithms get the shortest path between two nodes, the shortest path length and decide whether the graph is connect or not
     * by moving from the src node to its neighbors and set their tag (that save distance from src) in accordance to the weight between them,
     * and then moving to each of their neighbors and set their tag accordance to the weight between them plus their "parents" tag cetera.
     * at the same time Dijkstra saves the parent (previously node) for each node, for using in in other function.
     * @param src
     * @return parents
     */
    private HashMap<Integer,Integer> Dijkstra (weighted_graph g, int src){
        restart(graph.getV());
        PriorityQueue<node_info> pQueue = new PriorityQueue<node_info>();
        HashMap<Integer, Integer> parents = new  HashMap<Integer, Integer>();
        parents.put(src, null);
        g.getNode(src).setTag(0.0);   //  the distance between node to himself

        pQueue.add(g.getNode(src));
        while(!pQueue.isEmpty()){
            node_info pred = pQueue.poll();
            Collection<node_info> ni = g.getV(pred.getKey())   ;
            Iterator<node_info> iter1 = ni.iterator();
            while (iter1.hasNext()) {           //add the queue all the neighbors
                node_info temp = iter1.next();
                if (temp.getInfo() != "visited")
                    pQueue.add(temp);
            }
            Iterator<node_info> iter2 = ni.iterator();
            while (iter2.hasNext()) {               //  updating the right distance (in tag) in the neighbors
                node_info temp = iter2.next();
                //if (temp.getInfo() != "visited") {
                double distance = g.getNode(pred.getKey()).getTag() + g.getEdge(temp.getKey(), pred.getKey());
                if (distance < temp.getTag()) {
                    temp.setTag(distance);
                    parents.put(temp.getKey(), pred.getKey());     //לעדכן את ההורה הנכון..
                }
                //}
            }
            pred.setInfo("visited");
        }
        return parents;
    }


    /**restart the info and the tag - necessary before using the Dijkstra algorithm (that change tag and info)
     *@param nodes
     */
    private void restart (Collection<node_info> nodes) {
        Iterator<node_info> iter = nodes.iterator();
        while (iter.hasNext()) {
            node_info temp = iter.next();
            temp.setTag(Integer.MAX_VALUE);
            temp.setInfo("not visited");
        }
    }


    /** toString function:
     * @param g
     * @return the important details of the graph as a string.
     */
    public String toString(weighted_graph g) {
        StringBuilder sb = new StringBuilder();
        sb.append("Num of nodes: " + this.graph.nodeSize() + "\n");
        sb.append("Number of edegs: " + this.graph.edgeSize());
        sb.append("\nNodes (by key):\n");               //add a list of all the nodes
        Collection<node_info> nodes = g.getV();
        Iterator<node_info> iter1 = nodes.iterator();
        while (iter1.hasNext()) {
            node_info temp = iter1.next();
            sb.append(temp.getKey());
            if (iter1.hasNext())
                sb.append(",");
        }

        Iterator<node_info> iter2 = nodes.iterator();
        while (iter2.hasNext()) {              //add a list of all the neighbors:
            node_info temp = iter2.next();
            sb.append("\nNeighbors of node with key " + temp.getKey() + " (key of niNode , weight) :\n");
            Collection<node_info> ni = g.getV(temp.getKey());
            Iterator<node_info> iterNi = ni.iterator();
            while (iterNi.hasNext()) {
                node_info tempNi = iterNi.next();
                sb.append(tempNi.getKey() + "," +  g.getEdge((temp.getKey()), tempNi.getKey()) );
                if (iterNi.hasNext()) {
                    sb.append(",");
                }
            }
        }
        return sb.toString();
    }

}
