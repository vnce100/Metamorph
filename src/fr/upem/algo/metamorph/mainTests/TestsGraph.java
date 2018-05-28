package fr.upem.algo.metamorph.mainTests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import fr.upem.algo.metamorph.graph.AdjGraph;
import fr.upem.algo.metamorph.graph.Graph;
import fr.upem.algo.metamorph.graph.Graphs;

public class TestsGraph {
	private final static Pattern patternFlow = Pattern.compile(
			"source \\[([0-9]*)\\] : .*\n" +
					"cible \\[([0-9]*)\\] : .*\n" +
					"valeur de la coupe min: ([0-9]*)"
	);
	private final static Pattern patternFlowMultiTarget = Pattern.compile(
			"source \\[(([0-9]*, )*[0-9]*)\\] : .*\n" +
					"cible \\[(([0-9]*, )*[0-9]*)\\] : .*\n" +
					"valeur de la coupe min: ([0-9]*)"
	);

	private final String pathFlow;
	private final String pathFlowMultiTarget;

	public TestsGraph() {
		this.pathFlow = "res/testFlow/";
		this.pathFlowMultiTarget = "res/testFlowMultiTarget/";
	}

	/*public TestsGraph(String pathFlow, String pathFlowMultiTarget) {
		this.pathFlow = pathFlow;
		this.pathFlowMultiTarget = pathFlowMultiTarget;
	}*/

	@Test
	public void testFlow() {
		System.out.println(
				"" +
						"************************************************************************" +
						'\n' +
						"************************* Start of Flow tests: *************************" +
						'\n'
		);
		try {
			Files.newDirectoryStream(Paths.get(pathFlow), path -> path.toString().endsWith(".mat"))
					.forEach(pathStr -> {
						System.out.println("Testing: " + pathStr);

						/* Getting cut information from .mat file */
						String text = "";
						try {
							text = String.join("\n", Files.readAllLines(pathStr));
						} catch (IOException e) {
							e.printStackTrace();
						}
						Matcher m = patternFlow.matcher(text);
						if (!m.find()) {
							throw new IllegalStateException("No match found");
						}

						/* Make graph from .mat file */
						Graph graph = null;
						try {
							graph = Graphs.makeGraphFromMatrixFile(pathStr, AdjGraph::new);
						} catch (IOException e) {
							e.printStackTrace();
						}

						/* Test EdmonsKarp on graph with .mat information */
						System.out.println("  Source: " + m.group(1) + ", Target: " + m.group(2));
						int minCut = Graphs.EdmonsKarp(graph, Integer.valueOf(m.group(1)), Integer.valueOf(m.group(2)));
						System.out.println("  Expected min cut: " + m.group(3) + ", Computed min cut: " + minCut + '\n');
						assertEquals(Integer.valueOf(m.group(3)).intValue(), minCut);
					});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testFlowMultiTarget() {
		System.out.println(
				"" +
						"************************************************************************" +
						'\n' +
						"******************* Start of Flow Multi Target tests: ******************" +
						'\n'
		);
		try {
			Files.newDirectoryStream(Paths.get(pathFlowMultiTarget), path -> path.toString().endsWith(".mat"))
					.forEach(pathStr -> {
						System.out.println("Testing: " + pathStr);

						/* Getting cut information from .mat file */
						String text = "";
						try {
							text = String.join("\n", Files.readAllLines(pathStr));
						} catch (IOException e) {
							e.printStackTrace();
						}
						Matcher m = patternFlowMultiTarget.matcher(text);
						if (!m.find()) {
							throw new IllegalStateException("No match found");
						}

						/* Make graph from .mat file */
						AdjGraph graph = null;
						try {
							graph = (AdjGraph) Graphs.makeGraphFromMatrixFile(pathStr, AdjGraph::new);
						} catch (IOException e) {
							e.printStackTrace();
						}

						/* Add a source node with an edge to all start nodes
						and a target node with an edge from all end nodes */
						int source = graph.addNodeToGraph();
						int target = graph.addNodeToGraph();
						String[] stringListStarts = m.group(1).split(", ");
						List<Integer> listSourceStarts = Arrays.stream(stringListStarts).map(Integer::parseInt).collect(Collectors.toList());
						String[] stringListEnds = m.group(3).split(", ");
						List<Integer> listSourceEnds = Arrays.stream(stringListEnds).map(Integer::parseInt).collect(Collectors.toList());
						Graphs.makeMultiFlowGraph(graph,
								listSourceStarts.stream().mapToInt(i -> i).toArray(),
								listSourceEnds.stream().mapToInt(i -> i).toArray(),
								source,
								target);

						/* Test EdmonsKarp on graph with .mat information */
						System.out.println("  Source(s): [" + m.group(1) + "], Target(s): [" + m.group(3) + "]");
						int minCut = Graphs.EdmonsKarp(graph, source, target);
						System.out.println("  Expected min cut: " + m.group(5) + ", Computed min cut: " + minCut + '\n');
						assertEquals(Integer.valueOf(m.group(5)).intValue(), minCut);
					});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

