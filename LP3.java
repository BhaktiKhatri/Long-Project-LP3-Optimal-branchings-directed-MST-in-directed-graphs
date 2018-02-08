
// Starter code for LP3
// Do not rename this file or move it away from cs6301/g??

// change following line to your group number
package cs6301.g45;

import java.util.Scanner;
import java.util.Set;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.ListIterator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import cs6301.g45.SCC;
import cs6301.g45.Timer;
import cs6301.g45.DFS;
import cs6301.g45.Graph;
import cs6301.g45.Graph.Edge;
import cs6301.g45.Graph.Vertex;
import cs6301.g45.DMSTGraph.DMSTEdge;
import cs6301.g45.DMSTGraph.DMSTVertex;

public class LP3 {
	static int VERBOSE = 0;

	public static void main(String[] args) throws FileNotFoundException {
		Scanner in;
		if (args.length > 0) {
			File inputFile = new File(args[0]);
			in = new Scanner(inputFile);
		} else {
			in = new Scanner(System.in);
		}
		if (args.length > 1) {
			VERBOSE = Integer.parseInt(args[1]);
		}

		int start = in.nextInt(); // root node of the MST
		Graph g = Graph.readDirectedGraph(in);
		Vertex startVertex = g.getVertex(start);
		List<Edge> dmst = new ArrayList<>();

		Timer timer = new Timer();
		int wmst = directedMST(g, startVertex, dmst);
		timer.end();

		System.out.println(wmst);
		if (VERBOSE > 0) {
			System.out.println("_________________________");
			for (Edge e : dmst) {
				System.out.print(e);
			}
			System.out.println();
			System.out.println("_________________________");
		}
		System.out.println(timer);

	}
	
	/**
	 * TO DO: List dmst is an empty list. When your algorithm finishes, it should
	 * have the edges of the directed MST of g rooted at the start vertex. Edges
	 * must be ordered based on the vertex into which it goes, e.g.,
	 * {(7,1),(7,2),null,(2,4),(3,5),(5,6),(3,7)}. In this example, 3 is the start
	 * vertex and has no incoming edges. So, the list has a null corresponding to
	 * Vertex 3. The function should return the total weight of the MST it found.
	 */
	public static int directedMST(Graph g, Vertex start, List<Edge> dmst) {
		DMSTGraph xg = new DMSTGraph(g);
		int sumOfmstVertex = LP3.driverMethod(xg, start, dmst);
		dmst.add(start.getName(), null);
		return sumOfmstVertex;
	}

	public static int driverMethod(DMSTGraph dg, Vertex src, List<Edge> dmst) {
		DMSTVertex src1 = (DMSTVertex) dg.getVertex(src);
		DMSTAlgorithm da = new DMSTAlgorithm();
		int numPseudoVertex = 0;
		LinkedList<DMSTGraph.Vertex> decFinList = new LinkedList<>();
		int sumOfMST = 0;
		DFS d;
		for (DMSTEdge edge : src1.DMSTrevAdj) {
			edge.disabled = true;
		}

		while (true) {
			da.minGraph(dg);
			decFinList.clear();
			d = da.findDFSOnZeroEdge(dg, src1, true, decFinList);
			Set<DMSTVertex> dfsList = new HashSet<>();
			ListIterator<DMSTGraph.Vertex> listIterator = decFinList.listIterator();
			while (listIterator.hasNext()) {
				Graph.Vertex v = listIterator.next();
				dfsList.add(dg.getVertex(v));
			}
			// We need to get the real size through iterator that takes care of disabled
			// edges.
			if (dfsList.size() == dg.sizeWDisabled()) {
				break;
			} else {
				/* Now Run scc algorithm */
				Set<DMSTVertex>[] ccInfoHashSet = new HashSet[dg.size() + 1];

				// Initialize each HashSet
				for (int i = 1; i < ccInfoHashSet.length; i++) {
					ccInfoHashSet[i] = new HashSet<DMSTVertex>();
				}
				// run the scc on graph

				SCC cc = new SCC(dg);
				int nc = cc.stronglyConnectedComponents(dg);
				//System.out.println("Input Graph has " + nc + " components:");
				//da.printGraph(dg);

				dg.isZeroEdge = false;

				// print the scc information
				//da.printSCC(dg, cc);
				// Assign each vertex to their respecting HashSet it belongs
				for (Graph.Vertex vertx : dg) {
					dg.getVertex(vertx).sccBelongTo = cc.getCCVertex(vertx).cno;
					Set<DMSTVertex> hs = ccInfoHashSet[cc.getCCVertex(vertx).cno];
					hs.add(dg.getVertex(vertx));
				}

				// Contraction Phase starts
				List<DMSTVertex> vertxListToDisable = new ArrayList<>();
				for (int i = 1; i < ccInfoHashSet.length; i++) {
					Set<DMSTVertex> set = ccInfoHashSet[i];
					if (set.size() > 1) {
						//System.out.println(set);
						DMSTVertex pseudoVertex = new DMSTVertex(new Vertex(dg.size()));
						pseudoVertex.isPseudoVertex = true;
						numPseudoVertex++;
						da.shrinkingOfGraph(dg, set, src, pseudoVertex);
					}

				}
			}
		}

		// Expansion Phase starts
		// System.out.println("Last compressed Graph");
		// da.printGraph(dg);
		int sizeOfOrigionalGraph = dg.size() - 1;
		Set<DMSTEdge> mstEdgeList = new HashSet<>();
		da.addFrmBFStoMSTList(dg, decFinList, mstEdgeList, src);

		// Find the incoming edge to the final super vertex

		// System.out.println("sizeOfOrigionalGraph :"+ sizeOfOrigionalGraph + "
		// numOfNodes : "+ numPseudoVertex );

		int count = 0;
		while (count < numPseudoVertex) {
			DMSTVertex superVertex = dg.xv[sizeOfOrigionalGraph - count];
			da.expansionOfGraph(dg, superVertex, mstEdgeList);
			count++;
		}

		for (DMSTEdge e : mstEdgeList) {
			sumOfMST += e.getOriWeight();
		}
		
		for(int i = 1; i < dg.size(); i++) {
			for (DMSTEdge e : mstEdgeList) {
				if(e.fromVertex() == src)
					dmst.add(null);
				else if(e.toVertex().equals(dg.getVertex(i)))
					dmst.add(e);
			}
		}

		return sumOfMST;

	}
	
	/*public static void printdmst(List<Edge> dmst, int src) {
		int count = 0;
		System.out.print("[");
		for(Edge e: dmst) {
			if(count == src)
				System.out.print("null");
			else {
				System.out.print(e);
			}
			count++;
			if(count < dmst.size())
				System.out.print(",");
		}
		System.out.print("] ");

		System.out.println();
	}*/
}
