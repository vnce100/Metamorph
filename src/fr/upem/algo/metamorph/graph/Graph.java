package fr.upem.algo.metamorph.graph;

import java.util.Iterator;
import java.util.function.Consumer;

public interface Graph {
	
	final static int NULL_VALUE = 0;

	int numberOfEdges();

	int numberOfVertices();

	void addEdge(int i, int j, int value);
	
	void removeEdge(int i, int j);

	boolean isEdge(int i, int j);

	int getValue(int i, int j);
	
	void setValue(int i, int j, int value);

	Iterator<Edge> edgeIterator(int i);

	void forEachEdge(int i, Consumer<Edge> consumer);
	
	Graph createCopy();
	
	// Graph create(URI path) throws IOException;

	default String toGraphviz() {
		StringBuilder sb = new StringBuilder();
		sb.append("digraph G {\n");
		for(int i=0; i<numberOfVertices(); i++) {
			sb.append(i + ";\n");
			forEachEdge(i, e -> {
				if(e.getValue() != NULL_VALUE) {
					sb.append(e.getStart()).append(" -> ").append(e.getEnd()).append(" [ label=\"").append(e.getValue()).append("\" ];\n");
				}
			});
		}
		sb.append("}");
		return sb.toString();
	}
}
