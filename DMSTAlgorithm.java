package cs6301.g45;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import cs6301.g45.DMSTGraph.DMSTEdge;
import cs6301.g45.DMSTGraph.DMSTVertex;
import cs6301.g45.Graph.Edge;
import cs6301.g45.Graph.Vertex;

public class DMSTAlgorithm {
	public void minGraph(DMSTGraph graph) {
		for(Vertex v: graph) {
			DMSTVertex dv = graph.getVertex(v);
			if(dv.DMSTrevAdj != null) {
				int minValue = findMinEdge(dv.DMSTrevAdj);
				for(DMSTGraph.DMSTEdge e: dv.DMSTrevAdj) {
					e.setWeight(e.getWeight() - minValue);
				}
				//TODO: IF all of  incoming edges are non-zero, disable that vertex
				//But it can not be root.
			}
		}
	}
	
	public int findMinEdge(List<DMSTEdge> revAdjList) {
		
		if(revAdjList.isEmpty())
			return 0;
		int minEdge = revAdjList.get(0).getWeight();
		for(Graph.Edge e: revAdjList) {
			if(minEdge > e.getWeight())
				minEdge = e.getWeight();		
		}
		return minEdge;
	}
	

	public DFS findDFSOnZeroEdge(DMSTGraph dg, Vertex src, boolean ZeroEdge, LinkedList<DMSTGraph.Vertex> decFinList) {
		dg.isZeroEdge = ZeroEdge;
		DFS d = new DFS(dg);
		d.reinitialize(dg);										//Initialize all vertices as not seen    		
    		d.DFSVisit(src, decFinList, 0, 0, 0);
		return d;
		
	}
	
	public void printGraph(DMSTGraph dg) {
		//System.out.println("------------------------------------");
		System.out.println("Node : Edges : weight");
	    for(Vertex v: dg) {
	    		System.out.print("  " + v + "  :   ");
	    		DMSTVertex dv = (DMSTVertex) v;
		    for(Edge e: dv) {
		    		DMSTEdge de = (DMSTEdge)e;
		    		System.out.print(de +" : "+ de.getWeight() + " "+ de.disabled);
		    }
		    //System.out.print("<------");
		    /*for (int i = 0; i < dv.DMSTrevAdj.size(); i++) {
			    System.out.print( dv.DMSTrevAdj.get(i)+ " : "+ dv.DMSTrevAdj.get(i).disabled + " ");

			}*/
		   

		    System.out.println();
	    }
		System.out.println("------------------------------------");

	}
	
	public void shrinkingOfGraph(DMSTGraph dg, Set<DMSTVertex> vertexInCycleSet, Vertex src, DMSTVertex pseudoVertex) {
		List<DMSTEdge> EdgesInsideCycle = new ArrayList<>();
		Set<DMSTEdge> OutGoingEdgesFrmCycle = new HashSet<>();
		Set<DMSTEdge> InComingEdgesToCycle = new HashSet<>();
		//Required to handle multiple incoming edges to the cycle from same vertex
		HashMap<DMSTVertex, DMSTEdge> inEdgeToCyleVertex = new HashMap<>(); 
		Iterator<DMSTVertex> itr = vertexInCycleSet.iterator();
		
		while(itr.hasNext()) {
			//Added edges inside Cycle and outgoing edges to the cycle to the list
			DMSTVertex vertx = itr.next();
			Iterator<DMSTEdge> itrEdge = vertx.DMSTadj.iterator();
			while(itrEdge.hasNext()) {
				DMSTEdge de = itrEdge.next();
				if(vertexInCycleSet.contains(de.otherEnd(vertx)))
					EdgesInsideCycle.add(de);
				else {
					OutGoingEdgesFrmCycle.add(de);
					//pseudoVertex.DMSTadj.add(de);
				}
			}
			Iterator<DMSTEdge> itrRevEdge = vertx.DMSTrevAdj.iterator();
			while(itrRevEdge.hasNext()) {
				DMSTEdge de = itrRevEdge.next();
				if(de.disabled == false) {
					DMSTVertex sourceVert = (DMSTVertex) de.otherEnd(vertx);
					if(!vertexInCycleSet.contains(sourceVert)) {		
						/*
						 * If the map already contains edge from the same vertex, save only the edge having minimum weight.
						 */
						if (inEdgeToCyleVertex.containsKey(sourceVert)) {
							DMSTEdge minEdge = inEdgeToCyleVertex.get(sourceVert);
							if (minEdge.getWeight() > de.getWeight()) {
								inEdgeToCyleVertex.put(sourceVert, de);
								InComingEdgesToCycle.remove(minEdge);
								InComingEdgesToCycle.add(de);
							}
						} else {
							inEdgeToCyleVertex.put(sourceVert, de);
							InComingEdgesToCycle.add(de);
						}
					}
				}
			}
		}
	/*	System.out.println("---- InComingEdgesToCycle Start---");
		for (Iterator<DMSTEdge> it = InComingEdgesToCycle.iterator(); it.hasNext(); ) {
				DMSTEdge e = it.next();
				DMSTVertex v = (DMSTVertex)e.fromVertex();
				if (v.isPseudoVertex)
					System.out.println(e);
	    }
		System.out.println("---- InComingEdgesToCycle End---");
		
		System.out.println("---- OutGoingEdgesToCycle Start---");
		for (Iterator<DMSTEdge> it = OutGoingEdgesFrmCycle.iterator(); it.hasNext(); ) {
				DMSTEdge e = it.next();
				DMSTVertex v = (DMSTVertex)e.fromVertex();
				if (v.isPseudoVertex)
					System.out.println(e);
	    }
		System.out.println("---- OutGoingEdgesToCycle End---");*/
	
		
		//Adding all Outgoing Edges to pseudoVertex
		Iterator<DMSTEdge> itr3 = OutGoingEdgesFrmCycle.iterator(); 
		while(itr3.hasNext()) {
			DMSTEdge changedEdge = itr3.next();
			if(changedEdge.disabled == false) {
				DMSTVertex toVertex = (DMSTVertex)changedEdge.toVertex();
				DMSTEdge newEdge = new DMSTEdge(pseudoVertex, toVertex , changedEdge.getWeight());//from,to,weight
				newEdge.corrspondingEdge = changedEdge;
				pseudoVertex.DMSTadj.add(newEdge);
				toVertex.DMSTrevAdj.remove(changedEdge);
				toVertex.DMSTrevAdj.add(newEdge);	
			}
		}
		
		//Adding all Incoming Edges to pseudoVertex
		Iterator<DMSTEdge> itr4 = InComingEdgesToCycle.iterator();
		while(itr4.hasNext()) {
			DMSTEdge changedEdge = itr4.next();
			if(changedEdge.disabled == false) {
				DMSTVertex fromVertex = (DMSTVertex)changedEdge.fromVertex();
				DMSTEdge newEdge = new DMSTEdge(fromVertex, pseudoVertex, changedEdge.getWeight());//from,to,weight
				newEdge.corrspondingEdge = changedEdge;
				pseudoVertex.DMSTrevAdj.add(newEdge);
				fromVertex.DMSTadj.remove(changedEdge);
				fromVertex.DMSTadj.add(newEdge);
			}
		}
		
		/*Squeeze the cycle*/
		dg.xv[pseudoVertex.getName()] = pseudoVertex; // Added new pseudoVertex
		dg.n++;
		pseudoVertex.disabled = false;
		Iterator<DMSTVertex> itr2 = vertexInCycleSet.iterator();
		while(itr2.hasNext()) {
			DMSTVertex dmstVert = itr2.next();
			for(DMSTEdge edge: dmstVert.DMSTadj)
				edge.disabled = true;
			for(DMSTEdge edge: dmstVert.DMSTrevAdj)
				edge.disabled = true;
			dmstVert.disabled = true;
			pseudoVertex.sccVertexList.add(dmstVert); //added all the vertices belongs to pseudoVertex
			pseudoVertex.withinSccEdges.addAll(EdgesInsideCycle);
		}
		
		/*//Disable all the edges inside the cycle
		for(DMSTEdge edge: EdgesInsideCycle) {
			edge.disabled = true;
		}*/
	}
	
	public void expansionOfGraph(DMSTGraph dg, DMSTVertex superVertex, Set<DMSTEdge> mstEdgeList) {
		
		Set<DMSTVertex> expandVList = superVertex.sccVertexList;
		Vertex source = null;
		
		for(DMSTVertex vtx: expandVList) {
			vtx.disabled = false; //enable within vertices
			for(DMSTEdge e: vtx.DMSTrevAdj)
				/*if(e.fromVertex() == parentInBFS) {
					//System.out.println("M the rev edge for expaned vertex :"+ e);
					source = vtx;
				}*/
				if (e.partOfBFS) {
					source = vtx;
					//System.out.println(" the rev edge for expaned vertex :"+ e + " :source vertex"+vtx);
				}
			
		}
		
		//disable all vertex
		Iterator<Vertex> itr = dg.iterator();
		while(itr.hasNext()){
			DMSTVertex vertex = (DMSTVertex) itr.next();
			if(expandVList.contains(vertex)) { //disable all except expanded list vertices
				vertex.disabled = false;
			/*	for(DMSTEdge e: vertex.DMSTrevAdj)
					if(e.disabled = false)
						System.out.println("The rev edge for expaned vertex :"+ e);*/
				
			}else
				vertex.disabled = true;
		}
		
		for(DMSTVertex vert: expandVList) {
			
			for(DMSTEdge e: vert.DMSTadj) 
				e.disabled = false; //enable within edges
		}
		for(DMSTEdge edge : superVertex.withinSccEdges) { //enabled the edges disabled within SCC during pseudoVertex creation
			edge.disabled = false;
		}
		//start bfs with src
		LinkedList<DMSTGraph.Vertex> decFinList = new LinkedList<>();
		//System.out.println("The source :"+ source);
		DFS d = new DFS(dg);
		d.reinitialize(dg);										//Initialize all vertices as not seen    		
    		d.DFSVisit(source, decFinList, 0, 0, 0);
		findDFSOnZeroEdge(dg, source, true, decFinList);
		addFrmBFStoMSTList(dg, decFinList, mstEdgeList, source);
	}
		
		
	//}
	
	//find num of node in xv //delete if not needed
	public int numNodesInXV(DMSTVertex[] xv) {
		int count = 0;
		while(xv[count] != null) {
			count++;
		}
		return count;
	}
	
	public void printSCC(Graph dg, SCC cc) {
		//System.out.println("Input Graph has " + nc + " components:");

		for(Graph.Vertex u: dg) {
		    System.out.print(u + " [ " + cc.getCCVertex(u).cno + " ] :");
		    for(Graph.Edge e: u.adj) {
		    		Graph.Vertex v = e.otherEnd(u);
		    		System.out.print(e + " ");
		    }
		    System.out.println();
		}
	}
	public void addFrmBFStoMSTList(DMSTGraph dg, LinkedList<DMSTGraph.Vertex> decFinList, Set<DMSTEdge> mstEdgeList, Vertex src){
		ListIterator<DMSTGraph.Vertex> listIterator = decFinList.listIterator();
		while (listIterator.hasNext()) {
			DMSTGraph.Vertex vert = listIterator.next();{
			if(vert != src) {
				for(DMSTEdge edge: dg.getVertex(vert).DMSTrevAdj) {
					//TODO: Should we change reve edge iterator to handle these cases ??
					if (!edge.disabled && edge.getWeight() == 0) {
						while(edge.corrspondingEdge != null) {
							edge = edge.corrspondingEdge;
							edge.partOfBFS = true;
						}
						mstEdgeList.add(edge);
					}
				}
			}
			//System.out.println(mstEdgeList.toString());

			}
		}
	
	}
	

}
