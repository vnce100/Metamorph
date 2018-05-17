package fr.upem.algo.metamorph.mainTests;

import fr.upem.algo.metamorph.graph.AdjGraph;
import fr.upem.algo.metamorph.graph.Graph;
import fr.upem.algo.metamorph.graph.Graphs;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestsGraph {
	private final static Pattern patternFlow = Pattern.compile(
			"source \\[([0-9]*)\\] : .*\n" +
					"cible \\[([0-9]*)\\] : .*\n" +
					"valeur de la coupe min: ([0-9]*)"
	);

	@Test
	public void testFlow() {
		try {
			Files.newDirectoryStream(Paths.get("res/testFlow/"), path -> path.toString().endsWith(".mat"))
					.forEach(pathStr -> {
						System.out.println("Testing: " + pathStr);
						Graph graph = null;
						try {
							graph = Graphs.makeGraphFromMatrixFile(pathStr, AdjGraph::new);
						} catch (IOException e) {
							e.printStackTrace();
						}
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
						System.out.println("  Source: " + m.group(1) + ", Target: " + m.group(2));
						int minCut = Graphs.EdmonsKarp(graph, Integer.valueOf(m.group(1)), Integer.valueOf(m.group(2)));
						System.out.println("  Expected min cut: " + m.group(3) + ", Computed min cut: " + minCut + '\n');
						assertEquals(Integer.valueOf(m.group(3)).intValue(), minCut);
					});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
