/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.umons.olbregts.graphgame;

import be.ac.umons.olbregts.graphgame.algorithm.Algorithm;
import be.ac.umons.olbregts.graphgame.algorithm.implementation.WmpReach;
import be.ac.umons.olbregts.graphgame.algorithm.implementation.WmpSafe;
import be.ac.umons.olbregts.graphgame.exception.IllegalGraphException;
import be.ac.umons.olbregts.graphgame.io.GraphLoader;
import be.ac.umons.olbregts.graphgame.model.Graph;
import be.ac.umons.olbregts.graphgame.model.implementation.games.WindowReachGame;
import be.ac.umons.olbregts.graphgame.model.implementation.objectoriented.GraphObjectOriented;
import be.ac.umons.olbregts.graphgame.view.MainFrame;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
 * @author Simon
 */
public class GraphGame {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //findExample();
        java.awt.EventQueue.invokeLater(() -> new MainFrame().setVisible(true));
    }

    private static void findExample() {
        boolean finded = false;
        Graph g = null;
        Random rnd = new Random();
        Algorithm alg = new WmpReach();
        long exempleCount = 0;
        while (!finded) {
            exempleCount++;
            System.out.println("exemple:" + exempleCount);
            g = generateGraph(rnd.nextInt(6) + 8);
            int lMax = rnd.nextInt(3) + 2;
            try {
                String[] targets = new String[2];
                System.out.print("targets: ");
                for (int i = 0; i < 2; i++) {
                    targets[i] = g.getVertexsId()[i];
                    System.out.print(targets[i] + " ");
                }
                System.out.println();
                alg.reset(new WindowReachGame(g, 0, lMax, targets));
            } catch (IllegalGraphException e) {
                continue;
            }
            int limit = 7;
            int i = 0;
            while (!alg.isEnded() && i < limit) {
                alg.computeAStep();
                i++;
            }
            System.out.println("Region:" + alg.getWinningRegion().length);
            if (i > 1 && i <= limit && alg.getWinningRegion().length >= 2 && g.getVertexCount() -alg.getWinningRegion().length >= 2) {
                finded = true;
            }
            System.out.println("finded: " + finded);
            System.out.println("lMax:" + lMax);

        }
        try {
            File folder = new File(GraphLoader.getDirectoryPath(),"generated");
            if (!folder.exists()) {
                folder.mkdir();
            }
            GraphLoader.saveGraph(g, GraphLoader.getDirectoryPath()+"/generated/exemple.graph");
        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Graph generateGraph(int size) {
        GraphObjectOriented graph = new GraphObjectOriented();
        Random rnd = new Random();
        /** Generation of the vertex */
        for (int i = 0; i < size; i++) {
            int player = (rnd.nextInt(2) == 0 ? Graph.PLAYER1 : Graph.PLAYER2);
            graph.addVertex("" + i, player);
        }

        /** Connect each edge to one of the previous
         *  to have a spanning tree
         *  to ensure to have a connected graph */
        for (int i = 1; i < size; i++) {
            int succ = (i == 1 ? 0 : rnd.nextInt(i - 1));
            graph.addEdge("" + i, "" + succ, rnd.nextInt(5) - 2);
        }

        /** Add random edge for density */
        int minSucc = 1;
        int maxSucc = 2;
        int delta = maxSucc - minSucc;
        for (int i = 0; i < size; i++) {
            int succCount = minSucc + rnd.nextInt(delta + 1);
            for (int j = 0; j < succCount; j++) {
                int succ = rnd.nextInt(size);
                graph.addEdge("" + i, "" + succ, rnd.nextInt(5) - 2);
            }
        }
        return graph;
    }
}
