/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.umons.olbregts.graphgame.io;

import be.ac.umons.olbregts.graphgame.model.Graph;
import be.ac.umons.olbregts.graphgame.model.implementation.objectoriented.Edge;
import be.ac.umons.olbregts.graphgame.model.implementation.objectoriented.GraphObjectOriented;
import be.ac.umons.olbregts.graphgame.model.implementation.objectoriented.Vertex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @author Simon
 */
public class GraphLoader {

    public static GraphObjectOriented loadGraph(String filePath) throws FileNotFoundException {

        Scanner fileScan = new Scanner(new File(filePath));

        ArrayList<Integer> head = new ArrayList<>();
        ArrayList<Integer> player = new ArrayList<>();
        ArrayList<Integer> succ = new ArrayList<>();
        ArrayList<Integer> cost = new ArrayList<>();


        int currentLine = 1;
        LineLoop:
        while (fileScan.hasNextLine()) {
            String line = fileScan.nextLine();
            Scanner lineScan = new Scanner(line);
            lineScan.useLocale(Locale.US);
            switch (currentLine) {
                case 1:
                    while (lineScan.hasNext()) {
                        head.add(lineScan.nextInt());
                    }
                    break;
                case 2:
                    while (lineScan.hasNext()) {
                        player.add(lineScan.nextInt());
                    }
                    break;
                case 3:
                    while (lineScan.hasNext()) {
                        succ.add(lineScan.nextInt());
                    }
                    break;
                case 4:
                    while (lineScan.hasNext()) {
                        cost.add(lineScan.nextInt());
                    }
                    break;
                default:
                    break LineLoop;
            }
            currentLine++;
        }

        return new GraphObjectOriented(head, player, succ, cost);
    }

    public static void saveGraph(Graph graph, String filePath) throws UnsupportedEncodingException, FileNotFoundException {

        //Map<String, ArrayList<String>> successor = new HashMap<>(graph.getVertexCount());
        String[] vertedsId = graph.getVertexsId();
        /*for(String vertexId: vertedsId){
            successor.put(vertexId,new ArrayList<>());
        }

        for(String vertexId: vertedsId){
            for(String succId:graph.getSuccessors(vertexId)){
                successor.get(vertexId).add(succId);
            }
        }*/

      /*  for (Vertex v : graph.getVertexs()) {
            for (Edge e : v.getPred()) {
                successor.get(e.getSource().getIndex()).add(e);
            }
        }*/

        PrintWriter writer = new PrintWriter(filePath, "UTF-8");
        //Head
        int current = 1;
        for(String vertexId:vertedsId){
            writer.print("" + current + " ");
            current += graph.getSuccessorCount(vertexId);
        }
        /*
        for (int i = 0; i < successor.size(); i++) {
            writer.print("" + current + " ");
            current += successor.get(i).size();
        }*/
        writer.println("" + current);

        //Player
        for(String vertexId:vertedsId){
            writer.print("" + graph.getPlayer(vertexId) + " ");
        }
        writer.println();

        //Succ
        for(String vertexId:vertedsId){
        //for (int i = 0; i < vertedsId.size(); i++) {
            for (String succ : graph.getSuccessors(vertexId)) {
                for(int i =0;i<vertedsId.length;i++){
                    if(vertedsId[i].equals(succ)){
                        writer.print("" + (i+1)+" ");
                    }
                }
                //writer.print("" + ( e.getTarget().getIndex() + 1) + " ");
            }
        }
        writer.println();

        //Cost
        for(String vertexId: vertedsId){
            for(int cost:graph.getSuccessorsWeight(vertexId)){
                writer.print(""+ cost+" ");
            }
        }
        /*
        for (int i = 0; i < successor.size(); i++) {
            for (Edge e : successor.get(i)) {
                writer.print("" + e.getCost() + " ");
            }
        }*/
        writer.println();
        writer.close();
    }
}
