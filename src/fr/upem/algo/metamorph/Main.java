package fr.upem.algo.metamorph;

import java.io.IOException;
import java.nio.file.Paths;

public class Main {

	public static void main(String args[]) throws IOException{
		String leftImg = "test2/jonsnow2.png";
		String rightImg = "test2/panda3.png";
		String maskImg = "test2/mask-face.png";
		Image left = Image.newImage(Paths.get("res/testImg/" 	+ leftImg));
		Image right = Image.newImage(Paths.get("res/testImg/" 	+ rightImg));
		Image mask = Image.newImage(Paths.get("res/testImg/" 	+ maskImg));
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
}
