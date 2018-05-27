package fr.upem.algo.metamorph;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import java.nio.file.Paths;
import java.io.IOException;

import fr.upem.algo.metamorph.graph.AdjGraph;
import fr.upem.algo.metamorph.graph.Edge;
import fr.upem.algo.metamorph.graph.Graph;
import fr.upem.algo.metamorph.graph.Graphs;

public class ImageMixer {
	private Image left;
	private Image right;
	private Image mask;
	private Image mixed;
	private Graph graph;
	private List<Edge> cutEdges;
	
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
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		//Image left = promptUserForImagePath("Chemin de l'image de gauche : ");
		//Image right = promptUserForImagePath("Chemin de l'image de droite : ");
		//Image mask = promptUserForImagePath("Chemin du masque : ");
		Image left = Image.newImage(Paths.get("res/testImg/test1/trump2.png"));
		Image right = Image.newImage(Paths.get("res/testImg/test1/fruits.jpg"));
		Image mask = Image.newImage(Paths.get("res/testImg/test1/mask-face.png"));
		if(!(left.getWidth() == right.getWidth()) && !(left.getHeight() == right.getHeight()) && !(left.getWidth() == mask.getWidth()) && !(left.getHeight() == mask.getHeight())) {
			throw new IllegalStateException("main: images and mask don't have the same size");
		}
		ImageMixer im = new ImageMixer(left, right, mask);
		System.out.print("Making graph...");
		im.makeGraph();
		System.out.print("OK\nEdmons Karp...");
		im.processMinimalCut();
		System.out.print("OK\nCreate mixed image...");
		im.processMixedImage();
		System.out.print("OK\nWriting in file...");
		im.writeFile("res/testImg", "result");
		System.out.print("OK");
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
	}
	
	private int conv(int i, int j) {
		return i*left.getWidth() + j;
	}
	
	/**
	 * 
	 */
	public void processMinimalCut() {
		List<Integer> sourceList = new ArrayList<>();
		List<Integer> targetList = new ArrayList<>();
		for(int i=0; i<graph.numberOfVertices(); i++) {
			if(mask.getGreen(i) > 200) continue;
			if(mask.getBlue(i)  > 200) sourceList.add(i);
			if(mask.getRed(i)   > 200) targetList.add(i);
		}
		int[] source = new int[sourceList.size()];
		int[] target = new int[targetList.size()];
		for(int i=0; i<source.length; i++) {
			source[i] = sourceList.get(i);
		}
		for(int i=0; i<target.length; i++) {
			target[i] = targetList.get(i);
		}
		this.cutEdges = Graphs.EdmonsKarpEdges(graph, source, target);
	}
	
	public void processMixedImage() {
		mixed = Image.newImage(left.getWidth(), left.getHeight());
		if(cutEdges == null || cutEdges.isEmpty()) {
			throw new IllegalStateException("EdmonsKarpEdges result is empty");
		}
		for(Edge e : cutEdges) {
			mixed.set(e.getStart(), left.get(e.getStart()));
			mixed.set(e.getEnd(), right.get(e.getEnd()));
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
