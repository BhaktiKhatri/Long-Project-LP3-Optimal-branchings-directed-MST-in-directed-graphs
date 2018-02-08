package cs6301.g45;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import cs6301.g45.Graph;
import cs6301.g45.GraphAlgorithm;

/**
 * Java Program to check if the directed graph is Eulerian or not
 * @author Bhakti Khatri
 * @author Lopamudra Muduli
 * @author Sangeeta Kadambala
 * @author Gautam Gunda
 */


public class DirectedGraphTestEulerian extends GraphAlgorithm<DirectedGraphTestEulerian.TestEulerian> {
	
    // Class to store information about a vertex in this algorithm
	static class TestEulerian {
		boolean seen;			//To check if the vertex is seen or not
		Graph.Vertex parent;	//To keep track of parent of given vertex
		int inDegree;			//To keep track of in-degree of every vertex
		int top;				//To keep track of number of vertices
		int dis;				//To keep track of time
		int fin;				//To keep track of time
		int cno;				//To keep track of components
		
		public TestEulerian(Graph.Vertex u) {
			seen = false;
		    parent = null;
		    inDegree = 0;
		    top = 0;
		}
	} 
	
	public DirectedGraphTestEulerian(Graph g) {
		super(g);
		node = new TestEulerian[g.size()];
		
    	for(Graph.Vertex u: g) {
    	    node[u.getName()] = new TestEulerian(u);
    	}
	}
	
	/**
     * Function to implement Depth first search of the given graph
     * @param itr: Iterator<Graph.Vertex> - Iterator to iterate over the graph
     * @param g: Graph - Directed graph
     * @return decFinList: List<Graph.Vertex> - Linked list of vertices
     */
	private List<Graph.Vertex> DFS(Graph g, Iterator<Graph.Vertex> itr) {
    	int topNum = g.size();
    	int time = 0;
    	int cno = 0;
    	List<Graph.Vertex> decFinList = new LinkedList<>();
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
    
    /**
     * Recursive helper function for DFS
     * @param v          : Vertex - DFS root vertex
     * @param decFinList : List<Graph.Vertex> - List containing final order of the vertices
     * @param time     	 : int - records the time/sequence in which the vertex is start processing and end processing
     * @param cno	     : int - component number
     * @param topNum	 : int - number in decreasing finish time
     */
    private void DFSVisit(Graph.Vertex v, List<Graph.Vertex> decFinList, int time, int cno, int topNum) {
    	TestEulerian te = getVertex(v);
    	te.seen = true;
    	te.dis = ++time;
    	te.cno = cno;
        for(Graph.Edge e: v) {
			Graph.Vertex u = e.otherEnd(v);
			if(!getVertex(u).seen) {
				te.parent = v;
				DFSVisit(u, decFinList, time, cno, topNum);
			}
		}
        te.fin = ++time;
        te.top = topNum--;
        decFinList.add(v);
    }
    
    /**
     * Method to check if given vertex is seen or not
     * @param u: Graph.Vertex - a vertex of graph
     * @return true if the vertex is seen, false otherwise
     */
    boolean seen(Graph.Vertex u) {
    	return getVertex(u).seen;
    }
    
    /**
     * Method allows running DFS many times, with different sources
     * @param g: Graph
     */
    void reinitialize(Graph g) {
		for(Graph.Vertex u: g) {
			TestEulerian tu = getVertex(u);
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
     * Method to check if the directed graph is strongly connected
     * @param g: Graph - Graph
     * @return flag: boolean - true if the directed graph is strongly connected, false otherwise
     */
    public boolean isSC(Graph g) {
    	reinitialize(g);								//Initialize all vertices as not seen
    	boolean flag = false;						
    	Iterator<Graph.Vertex> itr = g.iterator();		
    	List<Graph.Vertex> decFinList = DFS(g, itr);	//Run DFS on graph g according to finish time order
    	Graph gr = getReverseGraph(g);					//Reverse the edges of graph g
    	reinitialize(gr);								//Initialize all vertices as not seen in the reverse graph
    	Iterator<Graph.Vertex> itr1 = decFinList.iterator();
    	
    	List<Graph.Vertex> decFinList1 = DFS(gr, itr1);	//Run DFS again, going through nodes in decreasing finish time order of first DFS
    	for(Graph.Vertex v: decFinList1) {				//If DFS traversal doesn’t visit all vertices, then return false. Otherwise return true.
    		if(!seen(v)) {
    			flag = false;
    			break;
    		}
    		else {
    			flag = true;
    		}
    	}
    	if (!flag){
    		System.out.println("Graph is not strongly connected");
    	}
    	return flag;
    }
    
    /**
     * Method to check if in-degree of every vertex is equal to its out-degree
     * @param g: Graph - Graph
     * @return true if in-degree of every vertex is equal to its out-degree, false otherwise
     */
    boolean checkDegree(Graph g) {
    	boolean dflag=false;
    	String vertexName = "";
    	for(Graph.Vertex v: g) {
    		if(v.adj.size() == getVertex(v).inDegree)  {
    			dflag = true;
    		} else {
    			dflag = false;
    			vertexName = v.getName()+"";
    			break;
    		}
    	}
    	if (!dflag){
    		System.out.println("Indegree is not equal to outdegree at vertex "+vertexName);
    	}
    	return dflag;
    }
    
    /**
     * Method to check if the directed graph is Eulerian or not
     * @param g: Graph - Graph
     * @return true if given directed graph is Eulerian, false otherwise
     */
    boolean testEulerian(Graph g) {
    	if(isSC(g) && checkDegree(g)) 
    		return true;
    	else 
    		return false;
    }
    
        
}