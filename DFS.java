package cs6301.g45;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * Java Program to print depth first search traversal of a given graph
 * @author Bhakti Khatri
 * @author Lopamudra Muduli
 * @author Sangeeta Kadambala
 * @author Gautam Gunda
 */

public class DFS extends GraphAlgorithm<DFS.DFSVertex> {

	// Class to store information about a vertex in this algorithm
	static class DFSVertex {
		public boolean seen;			//To check if the vertex is seen or not
		public Graph.Vertex parent;		//To keep track of parent of given vertex
		public int inDegree;			//To keep track of in-degree of every vertex
		public int top;					//To keep track of number of vertices
		public int dis;					//To keep track of time
		public int fin;					//To keep track of time
		public int cno;					//To keep track of components
			
		public DFSVertex(Graph.Vertex u) {
			seen = false;
		    parent = null;
		    inDegree = 0;
		    top = 0;
		}
		
	} 
		
	public Graph.Vertex getParent(Graph.Vertex u) {
		return getVertex(u).parent;
	}
	public int count=0;
		
	public DFS(Graph g) {
		super(g);
		node = new DFSVertex[g.size()];
			
	   	for(Graph.Vertex u: g) {
	   	    node[u.getName()] = new DFSVertex(u);
	   	}
	}

	/**
     * Function to implement Depth first search of the given graph
     * @param itr: Iterator<Graph.Vertex> - Iterator to iterate over the graph
     * @param g: Graph - Directed graph
     * @return decFinList: List<Graph.Vertex> - Linked list of vertices
     */
	public List<Graph.Vertex> dFS(Graph g, Iterator<Graph.Vertex> itr){
    	int topNum = g.size();
    	int time = 0;
    	int cno = 0;
    	LinkedList<Graph.Vertex> decFinList = new LinkedList<>();
    	reinitialize(g);
    	while(itr.hasNext()) {
    		Graph.Vertex v = itr.next();
    		if(!getVertex(v).seen) {
    			cno++;
    			DFSVisit(v, decFinList, time, cno, topNum);
    		}
    	}
    	return decFinList;
    }
	
/*	*//**
     * Function to implement Depth first search of the given graph
     * @param itr: Iterator<Graph.Vertex> - Iterator to iterate over the graph
     * @param g: Graph - Directed graph
     * @return decFinList: List<Graph.Vertex> - Linked list of vertices
     *//*
	public List<Graph.Vertex> dFS(Graph g, Graph.Vertex src, Iterator<Graph.Edge> itr){
    	int topNum = g.size();
    	int time = 0;
    	int cno = 0;
    	LinkedList<Graph.Vertex> decFinList = new LinkedList<>();
    	reinitialize(g);
    	while(itr.hasNext()) {
    		Graph.Vertex v = itr.next();
    		if(!getVertex(v).seen) {
    			cno++;
    			DFSVisit(v, decFinList, time, cno, topNum);
    		}
    	}
    	return decFinList;
    }*/
	
	/**
     * @param v          : Vertex - DFS root vertex
     * @param decFinList : List<Graph.Vertex> - List containing final order of the vertices
     * @param time     	 : int - records the time/sequence in which the vertex is start processing and end processing
     * @param cno	     : int - component number
     * @param topNum	 : int - number in decreasing finish time
     */
	public void DFSVisit(Graph.Vertex v, LinkedList<Graph.Vertex> decFinList, int time, int cno, int topNum){
    	DFSVertex tVertex = getVertex(v);
    	tVertex.seen = true;
    	tVertex.dis = ++time;
    	tVertex.cno = cno;
    	//System.out.print(v.name+" ");
        for(Graph.Edge e: v) {
			Graph.Vertex u = e.otherEnd(v);
			if(!getVertex(u).seen) {
				getVertex(u).parent = v;
				DFSVisit(u, decFinList, time, cno, topNum);
			}
			tVertex.fin = ++time;
	        tVertex.top = topNum--;
		}
        decFinList.addFirst(v);
    }
        
    /**
     * Method allows running DFS many times, with different sources
     * @param g: Graph
     */
    public void reinitialize(Graph g) {
		for(Graph.Vertex u: g) {
			DFSVertex tu = getVertex(u);
		    tu.seen = false;
		}
    }

    public static void main(String args[]) {
    	Scanner in;
    	try {
	        if (args.length > 0) {
	        	String path = args[0];
	            File inputFile = new File(path);
	            in = new Scanner(inputFile);
	        } else {
	            in = new Scanner(System.in);
	        }
	        
	        Graph g = Graph.readGraph(in,true);
	        
	        System.out.println("Depth First Traversal:");
	        DFS dfs = new DFS(g);
	    	Iterator<Graph.Vertex> itr = g.iterator();		
	    	List<Graph.Vertex> decFinList = dfs.dFS(g, itr);
	    	System.out.println("DFS: "+decFinList);
    	} 
    	catch(Exception e) {
    		System.out.println("Exception: "+e);
    	}
        
    }
    
}
