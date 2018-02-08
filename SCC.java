package cs6301.g45;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import cs6301.g45.CC.CCVertex;

/**
 * Java Program to find the number of strongly connected components
 * @author Bhakti Khatri
 * @author Lopamudra Muduli
 * @author Sangeeta Kadambala
 * @author Gautam Gunda
 */


public class SCC extends GraphAlgorithm<SCC.SCCVertex> {
	
    // Class to store information about a vertex in this algorithm
	public class SCCVertex {
		boolean seen;			//To check if the vertex is seen or not
		Graph.Vertex parent;	//To keep track of parent of given vertex
		int inDegree;			//To keep track of in-degree of every vertex
		int top;				//To keep track of number of vertices
		int dis;				//To keep track of time
		int fin;				//To keep track of time
		public int cno;				//To keep track of components
		
		public SCCVertex(Graph.Vertex u) {
			seen = false;
		    parent = null;
		    inDegree = 0;
		    top = 0;
		    cno = -1;
		}
	} 
	
	public int count=0;
	public SCC(Graph g) {
		super(g);
		node = new SCCVertex[g.size()];
		
    	for(Graph.Vertex u: g) {
    	    node[u.getName()] = new SCCVertex(u);
    	}
	}
	
	/**
     * Function to implement Depth first search of the given graph
     * @param itr: Iterator<Graph.Vertex> - Iterator to iterate over the graph
     * @param g: Graph - Directed graph
     * @return decFinList: List<Graph.Vertex> - Linked list of vertices
     */
	private List<Graph.Vertex> DFS(Graph g, Iterator<Graph.Vertex> itr){
	    	int topNum = g.size();
	    	int time = 0;
	    	int cno = 0;
	    	List<Graph.Vertex> decFinList = new LinkedList<>();
	    	reinitialize(g);
	    	while(itr.hasNext()){
	    		Graph.Vertex v = itr.next();
	    		if(!getVertex(v).seen){
	    			cno++;
	    			DFSVisit(v, decFinList, time, cno, topNum);
	    		}
	    	}
	    	return decFinList;
    }
    
	/**
     * @param v          : Vertex - DFS root vertex
     * @param decFinList : List<Graph.Vertex> - List containing final order of the vertices
     * @param time     	 : int - records the time/sequence in which the vertex is start processing and end processing
     * @param cno	     : int - component number
     * @param topNum	 : int - number in decreasing finish time
     */
	private void DFSVisit(Graph.Vertex v, List<Graph.Vertex> decFinList, int time, int cno, int topNum){
    	SCCVertex tVertex = getVertex(v);
    	tVertex.seen = true;
    	tVertex.dis = ++time;
    	tVertex.cno = cno;
        for(Graph.Edge e: v) {
			Graph.Vertex u = e.otherEnd(v);
			if(!getVertex(u).seen) {
				tVertex.parent = v;
				DFSVisit(u, decFinList, time, cno, topNum);
			}
		}
        tVertex.fin = ++time;
        tVertex.top = topNum--;
        decFinList.add(v);
    }
    
    /**
     * Method allows running DFS many times, with different sources
     * @param g: Graph
     */
    public void reinitialize(Graph g) {
		for(Graph.Vertex u: g) {
			SCCVertex tu = getVertex(u);
		    tu.seen = false;
		}
    }

    /**
     * Method reverses the edges of graph g
     * @param g
     * @return g: Graph - reverse graph
     */
    public Graph getReverseGraph(Graph g) {
    	List<Graph.Edge> temp = new LinkedList<Graph.Edge>();
    	for(Graph.Vertex u: g) {
    		temp = u.revAdj;
    		u.revAdj = u.adj;
    		u.adj = temp;
    		getVertex(u).inDegree = u.revAdj.size();
    	}
        return g;
    }
    
    /**
     * Method to get the number of strongly connected components
     * @param g: Graph - Graph
     * @return n: int - number of strongly connected components
     */
    public int stronglyConnectedComponents(Graph g) {
	    	reinitialize(g);										//Initialize all vertices as not seen
	    	Iterator<Graph.Vertex> itr = g.iterator();		
	    	List<Graph.Vertex> decFinList = DFS(g, itr);			//Run DFS on graph g according to finish time order
	    	Graph gr = getReverseGraph(g);							//Reverse the edges of graph g
	    	reinitialize(gr);										//Initialize all vertices as not seen in the reverse graph
	    	Iterator<Graph.Vertex> itr1 = decFinList.iterator();
	    	List<Graph.Vertex> decFinList1 = DFS(gr, itr1);			//Run DFS again, going through nodes in decreasing finish time order of first DFS
	    	List<Integer> list = new ArrayList<>();
	    	int cnt = 0;
	    	for(Graph.Vertex v: decFinList1) {				
	    		if(cnt == 0) {
	    			list.add(getVertex(v).cno);
	    			count++;
	    		}
	    		cnt++;
	    		//System.out.println("Vertex "+(v.name+1)+" belongs to component: "+getVertex(v).cno);
	    		if(list.contains(getVertex(v).cno)) {
	    		} else {
	    			count++;
	    			list.add(getVertex(v).cno);
	    		}
	    	}
	    	return count;
    }
    
 // From Vertex to CCVertex (ugly)
    public SCCVertex getCCVertex(Graph.Vertex u) {
    		return node[u.name];
    }
}