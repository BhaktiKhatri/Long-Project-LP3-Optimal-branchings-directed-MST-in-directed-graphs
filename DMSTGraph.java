package cs6301.g45;
import java.util.ArrayList;
import java.util.HashSet;

/** @author rbk
 *  Ver 1.0: 2017/09/29
 *  Example to extend Graph/Vertex/Edge classes to implement algorithms in which nodes and edges
 *  need to be disabled during execution.  Design goal: be able to call other graph algorithms
 *  without changing their codes to account for disabled elements.
 *
 *  Ver 1.1: 2017/10/09
 *  Updated iterator with boolean field ready. Previously, if hasNext() is called multiple
 *  times, then cursor keeps moving forward, even though the elements were not accessed
 *  by next().  Also, if program calls next() multiple times, without calling hasNext()
 *  in between, same element is returned.  Added UnsupportedOperationException to remove.
 **/

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.LinkedList;
import cs6301.g45.ArrayIterator;
import cs6301.g45.Graph;

public class DMSTGraph extends Graph{
    static boolean isZeroEdge;

    public static class DMSTVertex extends Vertex {
		boolean disabled;
		List<DMSTEdge> DMSTadj;
		List<DMSTEdge> DMSTrevAdj;
		Set<DMSTVertex> sccVertexList; // to store the corresponding scc components it belongs
		
		int sccBelongTo;
		List<DMSTEdge> withinSccEdges; //Inside SCC, the disabled edges while creating pseudoVertex
		boolean connectedToRoot = false; //which vertex is connected to src
		boolean isPseudoVertex;
		
		DMSTVertex(Vertex u) {
		    super(u);
		    disabled = false;
		    DMSTadj = new LinkedList<>();
		    DMSTrevAdj = new LinkedList<>();
		    sccVertexList = new HashSet<>();
		    sccBelongTo = -1;
		    withinSccEdges = new ArrayList<>();
		    isPseudoVertex = false;
		}

		boolean isDisabled() { return disabled; }
	
		void disable() { disabled = true; }
	

		@Override
		public Iterator<Edge> iterator() { return new DMSTVertexIterator(this); }
	
		class DMSTVertexIterator implements Iterator<Edge> {
			DMSTEdge cur;
		    Iterator<DMSTEdge> it;
		    boolean ready;
	
		    DMSTVertexIterator(DMSTVertex u) {
				this.it = u.DMSTadj.iterator();
				ready = false;
		    }
	
		    public boolean hasNext() {
				if(ready) { 
					return true;
				}
				if(!it.hasNext()) {
					return false; 
				}
				cur = it.next();
				
				if(isZeroEdge == false) {
					while(cur.isDisabled() && it.hasNext()) {
					    cur = it.next();
					}
					ready = true;
					return !cur.isDisabled();
				}else {
					while((cur.getWeight() != 0 || cur.isDisabled()) && it.hasNext()) {
					    cur = it.next();
					    //System.out.println("next :" + cur);
					}
					ready = true;
					return cur.getWeight() == 0 && !cur.isDisabled();
				}
		    }
	
		    public Edge next() {
			if(!ready) {
			    if(!hasNext()) {
				throw new java.util.NoSuchElementException();
			    }
			}
			ready = false;
			return cur;
		    }
	
		    public void remove() {
		    		throw new java.lang.UnsupportedOperationException();
		    }
		}
    }

    static class DMSTEdge extends Edge {
		boolean disabled;
		DMSTEdge corrspondingEdge; //represent its corresponding original graph edge
		int originalWeight;
		boolean partOfBFS;
	
		DMSTEdge(DMSTVertex from, DMSTVertex to, int weight) {
		    super(from, to, weight);
		    disabled = false;
		    corrspondingEdge = null;
		    originalWeight = weight;
		    partOfBFS = false;
		}
		int getOriWeight() {
			return originalWeight;
		}
		boolean isDisabled() {
			DMSTVertex xfrom = (DMSTVertex) fromVertex();
			DMSTVertex xto = (DMSTVertex) toVertex();
			return disabled || xfrom.isDisabled() || xto.isDisabled();
		}
    }

    DMSTVertex[] xv; // vertices of graph
    int n; // Keep vertices size for new vertices 
    
    public DMSTGraph(Graph g) {
    		super(g);
    		xv = new DMSTVertex[2*g.size()];  // Extra space is allocated in array for nodes to be added later
        for(Vertex u: g) {
            xv[u.getName()] = new DMSTVertex(u);
        }
        n = g.size();
		// Make copy of edges
		for(Vertex u: g) {
		    for(Edge e: u) {
				Vertex v = e.otherEnd(u);
				DMSTVertex x1 = getVertex(u);
				DMSTVertex x2 = getVertex(v);
				DMSTEdge edge = new DMSTEdge(x1, x2, e.getWeight());
				//edge.origionalEdgeVal = edge.getWeight(); //setting the original value of each edge
				x1.DMSTadj.add(edge);
				x2.DMSTrevAdj.add(edge);
				
		    }
		}
    }
    
    @Override
    public int size() {
    		return n;
    }
    public int sizeWDisabled() {
    		Iterator<Vertex> itr = iterator();
    		int count = 0;
    		while(itr.hasNext())
    			count++;
    		return count;
    }
    
    @Override
    public Iterator<Vertex> iterator() { return new DMSTGraphIterator(this); }

    class DMSTGraphIterator implements Iterator<Vertex> {
		Iterator<DMSTVertex> it;
		DMSTVertex xcur;
		
		DMSTGraphIterator(DMSTGraph xg) {
		    this.it = new ArrayIterator<DMSTVertex>(xg.xv, 0, xg.size()-1);  // Iterate over existing elements only
	}
	

	public boolean hasNext() {
	    if(!it.hasNext()) { return false; }
	    xcur = it.next();
	    while(xcur.isDisabled() && it.hasNext()) {
	    		xcur = it.next();
	    }
	    return !xcur.isDisabled();
	}

	public Vertex next() {
	    return xcur;
	}

	public void remove() {
	}
	    
    }


    @Override
    public Vertex getVertex(int n) {
        return xv[n-1];
    }

    DMSTVertex getVertex(Vertex u) {
    		return Vertex.getVertex(xv, u);
    }

    void disable(int i) {
    		DMSTVertex u = (DMSTVertex) getVertex(i);
    		u.disable();
    }
    
}
