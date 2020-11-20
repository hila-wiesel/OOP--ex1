package ex1.src;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This class implement the interface graph.
 * Each object that created in this class has private fields: nodes, numEdge and numNode.
 * The class itself has one static field - ModeCount, that keeps the number of changes that happened in the graph.
 * Using the function: getNode(), hasEdge(), addNode(), connect(), getV(),removeNode(), removeEdge(), nodeSize(), edgeSize() and getMC()
 */
public class WGraph_DS implements weighted_graph {
 

    /**
     * This class id a inner class of WGraph_DS, implement the interface node_data.
     * Each object that created in this class has private fields: key, neighbors; info and tag.
     * Using the function: getKey(), getNi(), addNi(), removeNode(), getInfo(), setInfo(), getTag() and setTag().
     */
    private class NodeInfo implements node_info, Comparable<node_info>  {


        private int key;
        private HashMap<Integer, Double> neighbors;   // <key, weight>
        private String info;
        private double tag;


        public NodeInfo() {
            //key = numNode++;
            neighbors = new HashMap<Integer,Double>();
            info = "not visited";
            tag = Integer.MAX_VALUE;
        }

        public NodeInfo(node_info node) {
            this.key = node.getKey();
            neighbors = new HashMap<Integer,Double>();
            this.info = node.getInfo();
            this.tag = node.getTag();
        }

        public NodeInfo(int k) {
            key = k;
            neighbors = new HashMap<Integer,Double>();
            info = "not visited";
            tag = Integer.MAX_VALUE;
        }


        @Override
        public int getKey() {
            return key;
        }


        @Override
        public String getInfo() {
            return info;
        }


        @Override
        public void setInfo(String s) {
            info = s;
        }


        @Override
        public double getTag() {
            return tag;
        }


        @Override
        public void setTag(double t){
            tag = t;
        }


        public Collection<Integer> getNiByKey(){
            return  neighbors.keySet();
        }


        public boolean hasNi(int key) {
            if (neighbors.containsKey(key))
                return true;
            return false;
        }


        public void addNi(int key, double weight) {
            neighbors.put(key, weight);
        }

        public double getEdge(int key) {
            if (!hasNi(key))
                return -1;
            return this.neighbors.get(key); //get the weight
        }

        public void removeNode(int key) {
            neighbors.remove(key);
        }


        public int compareTo(node_info other) {
            if(this.equals(other))
                return 0;
            else if(this.tag > other.getTag())
                return 1;
            else return -1;

        }

    }




    private HashMap<Integer, NodeInfo> nodes;     //  Integer = key
    private int numEdge;
    private int numNode;
    private int ModeCount = 0;

    public WGraph_DS(){
        nodes = new HashMap<Integer,NodeInfo>();
        numEdge = 0;
        numNode = 0;
        ModeCount = 0;

    }


    public WGraph_DS(weighted_graph g){
        numEdge = 0;
        numNode = 0;
        ModeCount = 0;
        nodes = new HashMap<Integer,NodeInfo>();

        Collection<node_info> nodeG = g.getV();
        Iterator<node_info> i1 = nodeG.iterator();

        //creating copy of all the graph's nodes:
        while (i1.hasNext()) {
            node_info temp = i1.next();
            nodes.put(temp.getKey(), new NodeInfo(temp));
            ++numNode;
        }

        //creating all the edges:
        Iterator<node_info> i2 = nodeG.iterator();
        while (i2.hasNext()) {
            node_info temp = i2.next();
            Collection<node_info> ni = g.getV(temp.getKey());
            Iterator<node_info> i3 = ni.iterator();
            while (i3.hasNext()) {
                int k = i3.next().getKey();
                double w= g.getEdge(temp.getKey(), k);
                this.connect(temp.getKey(), k, w);
            }
        }

    }


    @Override
    public node_info getNode(int key) {
        if (!nodes.containsKey(key))
            return null;
        return nodes.get(key);
    }


    @Override
    public boolean hasEdge(int node1, int node2) {
        if(!nodes.containsKey(node1) || !nodes.containsKey(node2))
            return false;
        return nodes.get(node1).hasNi(node2);
    }


    @Override
    public double getEdge(int node1, int node2) {
        if (!hasEdge(node1, node2))
            return -1;
        return nodes.get(node1).getEdge(node2);
    }


    @Override
    public void addNode(int key) {
        if (nodes.containsKey(key))      //When key already existed
            return;
        nodes.put(key,new NodeInfo(key));
        ++numNode;
        ++ModeCount;
    }


    @Override
    public void connect(int node1, int node2, double w) {
        if (node1 == node2)
            return;
        if (nodes.containsKey(node1) & nodes.containsKey(node2)) {
            if (hasEdge(node1, node2)){
                if (getEdge(node1, node2)==w)
                    return;
                --numEdge;
            }
            nodes.get(node1).addNi(node2, w);
            nodes.get(node2).addNi(node1, w);
            ++ModeCount;
            ++numEdge;
        }
        return;
    }


    @Override
    public Collection<node_info> getV() {
        Collection<NodeInfo> nodesCol = nodes.values();
        Collection<node_info> ans = new ArrayList();
        Iterator<NodeInfo> iter = nodesCol.iterator();
        while (iter.hasNext()) {
            NodeInfo temp = iter.next();
            ans.add((node_info)temp);
        }
        return ans;
    }

    @Override
    public Collection<node_info> getV(int node_id) {
        Collection<Integer> neighbors = nodes.get(node_id).getNiByKey();
        Collection<node_info> ans = new ArrayList();
        Iterator<Integer> iter = neighbors.iterator();
        while (iter.hasNext()) {
            int temp = iter.next();
            ans.add((node_info)nodes.get(temp));
        }
        return ans;
    }


    @Override
    public node_info removeNode(int key) {

        if(nodes.containsKey(key)) {
            NodeInfo deleteNode = nodes.remove(key); 			//remove the node from the graph
            --numNode;
            ++ModeCount;
            Collection<Integer> ni = deleteNode.getNiByKey();		//remove the edges
            Iterator<Integer> iterator = ni.iterator();
            while (iterator.hasNext()) {
                int temp = iterator.next();
                iterator.remove();
                nodes.get(temp).removeNode(deleteNode.getKey());
                ++ModeCount;
                --numEdge;
            }
            return deleteNode;
        }
        return null;    }


    @Override
    public void removeEdge(int node1, int node2) {
        if(nodes.containsKey(node1) && nodes.containsKey(node2)){
            if(nodes.get(node1).hasNi(node2)) {
                nodes.get(node1).removeNode(node2);
                nodes.get(node2).removeNode(node1);
                ++ModeCount;
                --numEdge;
            }
        }

    }


    @Override
    public int nodeSize() {
        return numNode;
    }


    @Override
    public int edgeSize() {
        return numEdge;
    }


    @Override
    public int getMC() {
        return ModeCount;
    }


    /** This function comparador the graph with an other graph
     * @param other
     * @return true if this graph equals to the other
     */
    @Override
    public boolean equals (Object other){
        weighted_graph g = (weighted_graph)other;
        if ((this.nodeSize() != g.nodeSize()) || (this.edgeSize() != g.edgeSize()))
            return false;
        Collection<node_info> nodesG = g.getV();
        Iterator<node_info> iterG = nodesG.iterator();
        Collection<node_info> nodesThis = this.getV();
        Iterator<node_info> iterThis = nodesThis.iterator();

        while (iterG.hasNext()){
            node_info tempG = iterG.next();
            node_info tempThis = iterThis.next();
            if ((tempG.getKey() != tempThis.getKey()) || (tempG.getInfo() != tempThis.getInfo()) || (tempG.getTag() != tempThis.getTag()))
                return false;
            Collection<node_info> niThis = this.getV(tempG.getKey());
            Collection<node_info> niG = this.getV(tempThis.getKey());
            if (niThis.size() != niG.size())
                return false;
            Iterator<node_info> iterNiG = niG.iterator();
            Iterator<node_info> iterNiThis = niThis.iterator();
            while (iterNiG.hasNext()){
                node_info tempNiG = iterNiG.next();
                node_info tempNiThis = iterNiThis.next();
                if (tempNiG.getKey() !=tempNiThis.getKey())
                    return false;
            }
        }
        return true;
    }
}
