// change following line to your group number
package cs6301.g45;

import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * program to find the Euler path
 * @author Lopamudra Muduli
 * @author Bhakti Khatri
 * @author Gautam Gunda
 * @author Sangeeta Kadambala
 *
 */
public class Euler extends GraphAlgorithm<Euler.EulerVertex>{
    int VERBOSE;
    List<Graph.Edge> tour;
    static Graph graph;
	Graph.Vertex start; 

    
    // Constructor
    Euler(Graph g, Graph.Vertex startV) {
    		super(g);
    		node = new EulerVertex[g.size()];
    		// Create array for storing vertex properties
    		for(Graph.Vertex u: g) {
    		    node[u.getName()] = new EulerVertex(u);
    		}
		VERBOSE = 1;
		tour = new LinkedList<>();
		graph = g;
		start = startV;
    }
    
    /**
     * Class for extended vertex of Graph.Vertex
     */
    static class EulerVertex{
		List<Graph.Edge> tour;
		boolean tourVisited;
		Iterator<Graph.Edge> eulerVertexIterator;
		
		EulerVertex(Graph.Vertex u) {
		    tour = new LinkedList<>(); //list of tour corresponding to the vertex
		    tourVisited = false; // flag true if the tour of this vertex already completed.
		    eulerVertexIterator = u.iterator(); //iterator for EulerVertex
		}
		
    }

    // To do: function to find an Euler tour
    public List<Graph.Edge> findEulerTour() {
    		List<Graph.Edge> subTour = getVertex(start).tour;
    		findTours(getVertex(start), subTour);
    		for(Graph.Vertex v: graph) {
    			EulerVertex eVertx = getVertex(v);
    			if(eVertx.eulerVertexIterator.hasNext())
    				findTours(eVertx, eVertx.tour);
    		}
		if(VERBOSE > 9) {
			printTours();
		}
		stitchTours(tour);
		return tour;
    }

    /* To do: test if the graph is Eulerian.
     * If the graph is not Eulerian, it prints the message:
     * "Graph is not Eulerian" and one reason why, such as
     * "inDegree = 5, outDegree = 3 at Vertex 37" or
     * "Graph is not strongly connected"
     */
    boolean isEulerian() {
    		/*System.out.println("Graph is not Eulerian");
    		System.out.println("Reason: Graph is not strongly connected");*/
    		
    		DirectedGraphTestEulerian isEulerian = new DirectedGraphTestEulerian(graph);
    	    
    	    if (isEulerian.testEulerian(graph)) {
    	    		System.out.println("The Directed Graph is Eulerian!");
    	    		return true;
    	    } else {
    	        System.out.println("The Directed Graph is NOT Eulerian! ");
    	        return false;
    	    }
    }

    
    /**
     * Find tours starting at vertices with unexplored edges
     * @param tStart : start vertex for the next tour
     * @param subTour : tour corresponding to tStart vertex
     */
    void findTours(EulerVertex tStart, List<Graph.Edge> subTour) {
    		while(tStart.eulerVertexIterator.hasNext()) {
    			Graph.Edge edge = tStart.eulerVertexIterator.next();
    			Graph.Vertex otherEndVertex = edge.to;
    			subTour.add(edge);
    			tStart = getVertex(otherEndVertex);
    		}
    }

    /* Print tours found by findTours() using following format:
     * Start vertex of tour: list of edges with no separators
     * Example: lp2-in1.txt, with start vertex 3, following tours may be found.
     * 3: (3,1)(1,2)(2,3)(3,4)(4,5)(5,6)(6,3)
     * 4: (4,7)(7,8)(8,4)
     * 5: (5,7)(7,9)(9,5)
     *
     * Just use System.out.print(u) and System.out.print(e)
     */
    void printTours() {
    		for(Graph.Vertex u: graph) {
    			if(getVertex(u).tour.size() > 0) {
    				System.out.print(u + ": ");
    				for(Graph.Edge e: getVertex(u).tour) {
    					System.out.print(e + " ");
    				}
    				System.out.println();
    			}
    		}
    }

    // Stitch tours into a single tour using the algorithm discussed in class
    void stitchTours(List<Graph.Edge> tour) {
    		explore(start, tour);
    }
    
    /**
     * Helper recursive function for stitchTours. It explores each vertex and if a vertex have corresponding tour considered for stitching
     * @param u : Graph.Vertex : the vertex to be explored
     * @param tour : List<Graph.Edge> : The tour start from u
     * @return : List<Graph.Edge> tour
     */
    public List<Graph.Edge> explore(Graph.Vertex u, List<Graph.Edge> tour){
    		EulerVertex temp = getVertex(u);
    		Graph.Vertex lastVertex = u;
    		for(Graph.Edge e: temp.tour) {
    			temp.tourVisited = true;
    			tour.add(e);
    			temp = getVertex(e.otherEnd(lastVertex));
    			lastVertex = e.otherEnd(lastVertex);
    			if(!temp.tourVisited && temp.tour.size() > 0)
    				explore(lastVertex, tour);
    		}
    		return tour;
    }

    void setVerbose(int v) {
    		VERBOSE = v;
    }
}
