package fr.upem.algo.metamorph;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.nio.file.Paths;
import java.awt.Color;
import java.io.IOException;

import fr.upem.algo.metamorph.graph.AdjGraph;
import fr.upem.algo.metamorph.graph.Graphs;

public class ImageMixer {
	private Image left;
	private Image right;
	private Image mask;
	private Image mixed;
	private AdjGraph graph;
	private AdjGraph graphCopy;
	private int source;
	private int target;
	
	/**
	 * 
	 * @param left
	 * @param right
	 * @param mask
	 */
	public ImageMixer(Image left, Image right, Image mask) {
		this.left 	= Objects.requireNonNull(left);
		this.right 	= Objects.requireNonNull(right);
		this.mask 	= Objects.requireNonNull(mask);
	}
	
	/**
	 * 
	 */
	public void makeGraph() {
		graph = new AdjGraph(left.getWidth() * left.getHeight());
		for(int i=0; i<left.getWidth()-1; i++) {
			for(int j=0; j<left.getHeight()-1; j++) {
				//System.out.println("i="+i+", j="+j);
				if(i-1 >= 0) {
					graph.addEdge(conv(i, j), conv(i, j), (int) Image.capacity(left.get(i, j).getRGB(), right.get(i, j).getRGB(), left.get(i-1, j).getRGB(), right.get(i-1, j).getRGB())); // add haut
					if(j-1 >= 0) graph.addEdge(conv(i, j), conv(i, j), (int) Image.capacity(left.get(i, j).getRGB(), right.get(i, j).getRGB(), left.get(i-1, j-1).getRGB(), right.get(i-1, j-1).getRGB())); // add haut gauche
					if(j-2 < left.getWidth()) graph.addEdge(conv(i, j), conv(i, j), (int) Image.capacity(left.get(i, j).getRGB(), right.get(i, j).getRGB(), left.get(i-1, j+1).getRGB(), right.get(i-1, j+1).getRGB())); // add haut droit
				}
				if(i+1 < left.getHeight()) {
					graph.addEdge(conv(i, j), conv(i, j), (int) Image.capacity(left.get(i, j).getRGB(), right.get(i, j).getRGB(), left.get(i+1, j).getRGB(), right.get(i+1, j).getRGB())); // add bas
					if(j-1 >= 0) graph.addEdge(conv(i, j), conv(i, j), (int) Image.capacity(left.get(i, j).getRGB(), right.get(i, j).getRGB(), left.get(i+1, j-1).getRGB(), right.get(i+1, j-1).getRGB())); // add bas gauche
					if(j-2 < left.getWidth()) graph.addEdge(conv(i, j), conv(i, j), (int) Image.capacity(left.get(i, j).getRGB(), right.get(i, j).getRGB(), left.get(i+1, j+1).getRGB(), right.get(i+1, j+1).getRGB())); // add bas droit
				}
				if(j-1 >= 0) graph.addEdge(conv(i, j), conv(i, j), (int) Image.capacity(left.get(i, j).getRGB(), right.get(i, j).getRGB(), left.get(i, j-1).getRGB(), right.get(i, j-1).getRGB())); // add gauche
				if(j-2 < left.getWidth()) graph.addEdge(conv(i, j), conv(i, j), (int) Image.capacity(left.get(i, j).getRGB(), right.get(i, j).getRGB(), left.get(i, j+1).getRGB(), right.get(i, j+1).getRGB())); // add droit
			}
		}
		source = graph.addNodeToGraph();
		target = graph.addNodeToGraph();
		List<Integer> sourceList = new ArrayList<>();
		List<Integer> targetList = new ArrayList<>();
		for(int i=0; i<graph.numberOfVertices()-3; i++) {
			if(mask.getGreen(i) > 200) continue;
			if(mask.getBlue(i)  > 200) sourceList.add(i);
			if(mask.getRed(i)   > 200) targetList.add(i);
		}
		Graphs.makeMultiFlowGraph(graph,
				sourceList.stream().mapToInt(i -> i).toArray(),
				targetList.stream().mapToInt(i -> i).toArray(),
				source,
				target);
		graphCopy = (AdjGraph) graph.createCopy();
	}
	
	private int conv(int i, int j) {
		return i*left.getWidth() + j;
	}
	
	/**
	 * 
	 */
	public void processMinimalCut() {
		Graphs.EdmonsKarp(graph, source, target);
	}
	
	public void processMixedImage() {	
		mixed = Image.newImage(left.getWidth(), left.getHeight());
		for(int i=0; i<graph.numberOfVertices()-3; i++) {
			int i2 = i;
			graph.forEachEdge(i, e -> {
				if(e.getValue() == graphCopy.getValue(e.getStart(), e.getEnd())) {
					mixed.set(i2, new Color(Image.blur(left.get(i2).getRGB(), right.get(i2).getRGB(), 0.7)));
				} else if(mask.get(i2).getGreen() < 100 && mask.get(i2).getRed() > mask.get(i2).getBlue() ) {
					mixed.set(i2, right.get(i2));
				} else {
					mixed.set(i2, left.get(i2));
				}
			});
		}				
	}
	
	public void writeFile(String path, String filename) throws IOException {
		mixed.printToFile(path + "/" + filename);
	}
	
	/**
	 * 
	 * @param promptedMessage
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private static Image promptUserForImagePath(String promptedMessage) throws IOException {
		System.out.print(promptedMessage);
		String pathString = null;
		try(Scanner scanner = new Scanner(System.in)) {
			while(scanner.hasNext()) {
				pathString = scanner.next();
				if(!(pathString.length()<20)) break;
			}
		}
		System.out.println();
		return Image.newImage(Paths.get(pathString));
	}
}
