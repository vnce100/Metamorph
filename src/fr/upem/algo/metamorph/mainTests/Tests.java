package fr.upem.algo.metamorph.mainTests;

import java.io.IOException;
import java.nio.file.Paths;

import fr.upem.algo.metamorph.graph.AdjGraph;
import fr.upem.algo.metamorph.graph.Graph;
import fr.upem.algo.metamorph.graph.Graphs;
import fr.upem.algo.metamorph.graph.MatGraph;

public class Tests {
	public static void main(String[] args) throws IOException {
        MatGraph matGraph = new MatGraph(8);
        matGraph.addEdge(0, 1, 3);
        matGraph.addEdge(0, 2, 3);
        matGraph.addEdge(0, 3, 3);
        matGraph.addEdge(1, 4, 3);
        matGraph.addEdge(1, 5, 3);
        matGraph.addEdge(3, 7, 3);
        matGraph.addEdge(5, 6, 3);
        matGraph.addEdge(5, 7, 3);


        //System.out.println(matGraph.numberOfEdges());
        //System.out.println(matGraph.numberOfVertices());
        //System.out.println(matGraph.isEdge(1, 3));
        //System.out.println(matGraph.isEdge(3, 4));
        //System.out.println(matGraph.getWeight(1, 3));
        //System.out.println(matGraph.getWeight(3, 4));
        //System.out.println(Graphs.DFS(matGraph));
        //System.out.println(Graphs.BFS(matGraph));
        //Graphs.bellmanFord(matGraph, 2).printShortestPathTo(4);

        /*AdjGraph adjGraph = new AdjGraph(7);
        adjGraph.addEdge(0, 1, 3);
        adjGraph.addEdge(0, 2, 3);
        adjGraph.addEdge(0, 4, 3);
        adjGraph.addEdge(1, 3, 3);
        adjGraph.addEdge(1, 5, 3);
        adjGraph.addEdge(2, 6, 3);
        adjGraph.addEdge(4, 5, 3);

        System.out.println(adjGraph.numberOfEdges());
        System.out.println(adjGraph.numberOfVertices());
        System.out.println(adjGraph.isEdge(1, 2));
        System.out.println(adjGraph.isEdge(3, 4));
        System.out.println(adjGraph.getWeight(1,2));
        System.out.println(adjGraph.getWeight(3, 4));
        System.out.println(Graphs.DFS(adjGraph));
        System.out.println(Graphs.BFS(adjGraph));*/
        
        
        Graph g2 = Graphs.makeGraphFromMatrixFile(Paths.get("/home/6ir2/vvivier/Téléchargements/td3_exo1.mat"), i -> new AdjGraph(i));
        System.out.println(Graphs.BFS(g2, 0, 9));
    }
}
