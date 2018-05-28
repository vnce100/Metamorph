package fr.upem.algo.metamorph.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Consumer;

public class AdjGraph implements Graph {

	private final ArrayList<LinkedList<Edge>> adj;
	private int nodes; // number of nodes
	private int edges;

	/**
	 *
	 * @param nodes
	 */
	public AdjGraph(int nodes) {
		if(nodes < 1) {
			throw new IllegalArgumentException("Number of nodes should be positive (value: "+ nodes +")" );
		}
		this.nodes = nodes;
		this.adj = new ArrayList<>(nodes);
		for(int i=0; i<nodes; i++) {
			adj.add(new LinkedList<>());
		}
	}

	/**
	 *
	 */
	@Override
	public int numberOfEdges() {
		return edges;
	}

	/**
	 *
	 */
	@Override
	public int numberOfVertices() {
		return nodes;
	}

	/**
	 *
	 */
	@Override
	public void addEdge(int src, int dst, int value) {
		Iterator<Edge> it = Objects.requireNonNull(adj.get(checkNode(src))).iterator();
		checkNode(dst);
		checkValue(value);
		while(it.hasNext()) {
			if(it.next().getEnd() == dst) {
				it.remove();
				edges--;
				break;
			}
		}
		adj.get(src).add(new Edge(src, dst, value));
		edges++;
	}

	/**
	 *
	 */
	@Override
	public boolean isEdge(int src, int dst) {
		Iterator<Edge> it = Objects.requireNonNull(adj.get(checkNode(src))).iterator();
		checkNode(dst);
		while(it.hasNext()) {
			if(it.next().getEnd() == dst) return true;
		}
		return false;
	}

	/**
	 *
	 */
	@Override
	public int getValue(int src, int dst) {
		Iterator<Edge> it = Objects.requireNonNull(adj.get(checkNode(src))).iterator();
		checkNode(dst);
		while(it.hasNext()) {
			Edge e = it.next();
			if(e.getEnd() == dst) return e.getValue();
		}
		return Edge.NULL_VALUE;
	}

	/**
	 *
	 */
	@Override
	public Iterator<Edge> edgeIterator(int i) {
		return adj.get(checkNode(i)).iterator();
	}

	/**
	 *
	 */
	@Override
	public void forEachEdge(int i, Consumer<Edge> consumer) {
		adj.get(checkNode(i)).forEach(consumer);
	}

	private int checkNode(int n) {
		if(n < 0 || n > nodes) {
			System.out.println("Nodes = " + nodes + " et n test� = " + n);
			throw new IllegalArgumentException("Node is not in the matrix");
		}
		return n;
	}

	private int checkValue(int value) {
		/*if(value == 0) {
			throw new IllegalArgumentException("Value 0 is not allowed");
		}
		*/
		return value;
	}
	
	public void removeEdge(int src, int dst) {
		Iterator<Edge> it = Objects.requireNonNull(adj.get(checkNode(src))).iterator();
		checkNode(dst);
		while(it.hasNext()) {
			if(it.next().getEnd() == dst) {
				it.remove();
				edges--;
				return;
			}
		}
		throw new IllegalStateException("Try to remove non existing edge");
	}

	public int addNodeToGraph(){
		adj.add(new LinkedList<>());
		nodes++;
		return nodes - 1;
	}

	@Override
	public Graph createCopy() {
		AdjGraph copy = new AdjGraph(this.nodes);
		copy.edges = this.edges;
		int i=0;
		for(LinkedList<Edge> l : this.adj) {
			Collections.copy(copy.adj.get(i), l);
			i++;
		}
		return copy;
	}

	@Override
	public void setValue(int src, int dst, int value) {
		forEachEdge(src, e -> {
			if(e.getEnd() == dst) {
				e.setValue(value);
				return;
			}
		});
		throw new IllegalStateException("Try to set value on non existing node");
	}
}
