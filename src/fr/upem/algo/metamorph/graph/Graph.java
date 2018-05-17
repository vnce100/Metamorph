package fr.upem.algo.metamorph.graph;

import java.util.Iterator;
import java.util.function.Consumer;

public interface Graph {

	int numberOfEdges();

	int numberOfVertices();

	void addEdge(int i, int j, int value);

	boolean isEdge(int i, int j);

	int getWeight(int i, int j);

	Iterator<Edge> edgeIterator(int i);

	void forEachEdge(int i, Consumer<Edge> consumer);
	
	// Graph create(URI path) throws IOException;

	default String toGraphviz() {
		StringBuilder sb = new StringBuilder();
		sb.append("digraph G {\n");
		for(int i=0; i<numberOfVertices(); i++) {
			sb.append(i + ";\n");
			forEachEdge(i, e -> {
				sb.append(e.getStart()).append(" -> ").append(e.getEnd()).append(" [ label=\"").append(e.getValue()).append("\" ];\n");
			});
		}
		return sb.toString();
	}
}
