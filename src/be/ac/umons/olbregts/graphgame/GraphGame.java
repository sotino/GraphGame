/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.umons.olbregts.graphgame;

import be.ac.umons.olbregts.graphgame.algorithm.implementation.Buchi;
import be.ac.umons.olbregts.graphgame.algorithm.implementation.FWMP;
import be.ac.umons.olbregts.graphgame.exception.IllegalGraphException;
import be.ac.umons.olbregts.graphgame.io.GraphLoader;
import be.ac.umons.olbregts.graphgame.model.Graph;
import be.ac.umons.olbregts.graphgame.model.implementation.games.ReachibilityGame;
import be.ac.umons.olbregts.graphgame.model.implementation.games.WindowingQuantitativeGame;
import be.ac.umons.olbregts.graphgame.model.implementation.objectoriented.GraphObjectOriented;
import be.ac.umons.olbregts.graphgame.view.MainFrame;
import com.sun.org.apache.xpath.internal.SourceTree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.channels.Pipe;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author Simon
 */
public class GraphGame {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        findExemple();
        java.awt.EventQueue.invokeLater(() -> new MainFrame().setVisible(true));
    }

    private static void findExemple() {
        boolean finded = false;
        Graph g = null;
        Random rnd = new Random();
        FWMP b = new FWMP();
        while (!finded){
            g = generateGraph(rnd.nextInt(8)+8);
            int lMax = rnd.nextInt(4)+3;
            try {
                b.reset(new WindowingQuantitativeGame(g,0,lMax));
            } catch (IllegalGraphException e) {
                continue;
            }
            int limit = 3;
            int i =0;
            while (!b.isEnded() && i<limit) {
                b.computeAStep();
                i++;
            }
            System.out.println("Region:"+b.getWinningRegion().length);
            if(i==limit && b.getWinningRegion().length >= 2 && b.getWinningRegion().length <= 6){
                finded = true;
            }
            System.out.println("finded: "+finded);
            System.out.println("lMax:"+lMax);
        }
        try {
            File folder = new File("generated");
            if(!folder.exists()){
                folder.mkdir();
            }
            GraphLoader.saveGraph(g, "generated/exemple.graph");
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
            graph.addVertex(""+i,player);
        }

        /** Connect each edge to one of the previous
         *  to have a spanning tree
         *  to ensure to have a connected graph */
        for (int i = 1; i < size; i++) {
            int succ = (i == 1 ? 0 : rnd.nextInt(i - 1));
            graph.addEdge(""+i, ""+succ, rnd.nextInt(5)-2);
        }

        /** Add random edge for density */
        int minSucc = Math.max(1,1);
        int maxSucc = Math.max(1,3);
        int delta = maxSucc - minSucc;
        for (int i = 0; i < size; i++) {
            int succCount = minSucc + rnd.nextInt(delta + 1);
            for (int j = 0; j < succCount; j++) {
                int succ = rnd.nextInt(size);
                graph.addEdge(""+i,""+ succ, rnd.nextInt(5)-2);
            }
        }
        return graph;
    }
}
