package fr.upem.algo.metamorph.main;

import java.util.Objects;
import java.util.Scanner;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.net.URI;

import fr.upem.algo.metamorph.graph.AdjGraph;
import fr.upem.algo.metamorph.graph.Graph;

public class ImageMixer {
	private Image left;
	private Image right;
	private Image mask;
	private Graph graph;
	
	/**
	 * 
	 * @param left
	 * @param right
	 * @param mask
	 */
	public ImageMixer(Image left, Image right) {
		this.left 	= Objects.requireNonNull(left);
		this.right 	= Objects.requireNonNull(right);
	}

	/**
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Image left = promptUserForImagePath("Chemin de l'image de gauche : ");
		Image right = promptUserForImagePath("Chemin de l'image de droite : ");
		if(!(left.getWidth() == right.getWidth()) && !(left.getHeight() == right.getHeight())) {
			throw new IllegalStateException("main: images don't have the same size");
		}
		ImageMixer im = new ImageMixer(left, right);
		im.makeGraph();
		im.processMinimalCut();
	}
	
	/**
	 * 
	 */
	public void makeGraph() {
		graph = new AdjGraph(left.getWidth() * left.getHeight());
		for(int i=0; i<left.getWidth(); i++) {
			for(int j=0; j<left.getHeight(); j++) {
				if(i-1 >= 0) {
					graph.addEdge(i-left.getWidth(), j, (int) Image.capacity(left.get(i, j).getRGB(), right.get(i, j).getRGB(), left.get(i-1, j).getRGB(), right.get(i-1, j).getRGB())); // add haut
					if(j-1 >= 0) graph.addEdge(i-left.getWidth(), j, (int) Image.capacity(left.get(i, j).getRGB(), right.get(i, j).getRGB(), left.get(i-1, j-1).getRGB(), right.get(i-1, j-1).getRGB())); // add haut gauche
					if(j-2 < left.getWidth()) graph.addEdge(i-left.getWidth(), j, (int) Image.capacity(left.get(i, j).getRGB(), right.get(i, j).getRGB(), left.get(i-1, j+1).getRGB(), right.get(i-1, j+1).getRGB())); // add haut droit
				}
				if(i+1 < left.getHeight()) {
					graph.addEdge(i-left.getWidth(), j, (int) Image.capacity(left.get(i, j).getRGB(), right.get(i, j).getRGB(), left.get(i+1, j).getRGB(), right.get(i+1, j).getRGB())); // add bas
					if(j-1 >= 0) graph.addEdge(i-left.getWidth(), j, (int) Image.capacity(left.get(i, j).getRGB(), right.get(i, j).getRGB(), left.get(i+1, j-1).getRGB(), right.get(i+1, j-1).getRGB())); // add bas gauche
					if(j-2 < left.getWidth()) graph.addEdge(i-left.getWidth(), j, (int) Image.capacity(left.get(i, j).getRGB(), right.get(i, j).getRGB(), left.get(i+1, j+1).getRGB(), right.get(i+1, j+1).getRGB())); // add bas droit
				}
				if(j-1 >= 0) graph.addEdge(i-left.getWidth(), j, (int) Image.capacity(left.get(i, j).getRGB(), right.get(i, j).getRGB(), left.get(i, j-1).getRGB(), right.get(i, j-1).getRGB())); // add gauche
				if(j-2 < left.getWidth()) graph.addEdge(i-left.getWidth(), j, (int) Image.capacity(left.get(i, j).getRGB(), right.get(i, j).getRGB(), left.get(i, j+1).getRGB(), right.get(i, j+1).getRGB())); // add droit
			}
		}
	}
	
	public void processMinimalCut() {
		
	}
	
	/**
	 * 
	 * @param promptedMessage
	 * @return
	 * @throws IOException
	 */
	private static Image promptUserForImagePath(String promptedMessage) throws IOException {
		System.out.print(promptedMessage);
		String pathString = null;
		try(Scanner scanner = new Scanner(System.in)) {
			if(scanner.hasNext()) {
				pathString = scanner.next();
			}
		}
		System.out.println();
		return Image.newImage(Paths.get(URI.create(pathString)));
	}
}
