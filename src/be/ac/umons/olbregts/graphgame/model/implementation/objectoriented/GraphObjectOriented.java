/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.umons.olbregts.graphgame.model.implementation.objectoriented;

import be.ac.umons.olbregts.graphgame.model.Graph;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon
 */
public class GraphObjectOriented implements Graph {

    private ArrayList<Vertex> vertexs;
    private Vertex destination;

    public GraphObjectOriented() {
        vertexs = new ArrayList<>();
        destination = null;
    }

    public GraphObjectOriented(List<Integer> head, List<Integer> player, List<Integer> succ, List<Integer> cost) {
        vertexs = new ArrayList<>(head.size() - 1);
        for (int i = 0; i < head.size() - 1; i++) {
            int p = player.get(i) == 0 ? 1 : player.get(i);
            Vertex v = new Vertex(i, p);
            if (player.get(i) == 0) {
                destination = v;
            }
            vertexs.add(v);
        }
        for (int i = 0; i < head.size() - 1; i++) {
            for (int j = head.get(i) - 1; j < head.get(i + 1) - 1; j++) {
                Edge e = new Edge(vertexs.get(i), vertexs.get(succ.get(j) - 1), cost.get(j));
                e.getTarget().addPred(e);
                e.getSource().addSucc(e);
            }
        }
    }

    public List<Vertex> getVertexs() {
        return vertexs;
    }

    public Vertex getDestination() {
        return destination;
    }

    public void setDestination(Vertex v) {
        if (vertexs.contains(v)) {
            destination = v;
        } else {
            throw new IllegalArgumentException("The vertex must be in the graph");
        }
    }

    public Vertex getVertex(int i) {
        return vertexs.get(i);
    }

    public Vertex getLastVertex() {
        return vertexs.get(vertexs.size() - 1);
    }

    public void addVertex(int player) {
        Vertex v = new Vertex(vertexs.size(), player);
        vertexs.add(v);
    }

    public void deleteVertex(int index) {
        Vertex old = vertexs.get(index);
        if (old == destination) {
            destination = null;
        }
        vertexs.remove(old);
        for (int i = 0; i < vertexs.size(); i++) {
            vertexs.get(i).setIndex(i);
            vertexs.get(i).removePred(old);
            vertexs.get(i).removeSucc(old);
        }
    }

    public void deleteEdge(int srcIndex, int targetIndex) {
        if (srcIndex >= vertexs.size()) {
            throw new IllegalArgumentException("The source index (" + srcIndex + ") is out of range.");
        }

        if (targetIndex >= vertexs.size()) {
            throw new IllegalArgumentException("The target index (" + targetIndex + ") is out of range.");
        }
        Vertex src = vertexs.get(srcIndex);
        Vertex target = vertexs.get(targetIndex);
        target.removePred(src);
        src.removeSucc(target);
    }

    public boolean addEdge(int srcIndex, int targetIndex, int cost) {
        if (srcIndex >= vertexs.size()) {
            throw new IllegalArgumentException("The source index (" + srcIndex + ") is out of range.");
        }
        if (targetIndex >= vertexs.size()) {
            throw new IllegalArgumentException("The target index (" + targetIndex + ") is out of range.");
        }
        Vertex src = vertexs.get(srcIndex);
        Vertex target = vertexs.get(targetIndex);
        Edge e = new Edge(src, target, cost);
        src.addSucc(e);
        return target.addPred(e);
    }

    public int getVertexNumber() {
        return vertexs.size();
    }

    @Override
    public int getVertexCount() {
        return vertexs.size();
    }

    @Override
    public int getSuccessorCount(int vertexId) {
        return vertexs.get(vertexId).getSucc().size();
    }

    @Override
    public int getPredecessorCount(int vertexId) {
        return vertexs.get(vertexId).getPred().size();
    }

    @Override
    public int[] getSuccessors(int vertexId) {
        int nbSucc = this.getSuccessorCount(vertexId);
        int[] successors = new int[nbSucc];
        List<Edge> succ = vertexs.get(vertexId).getSucc();
        for (int i = 0; i < nbSucc; i++) {
            successors[i] = succ.get(i).getTarget().getIndex();
        }
        return successors;
    }

    @Override
    public int[] getSuccessorsWeight(int vertexId) {
        int nbSucc = this.getSuccessorCount(vertexId);
        int[] successorsWeight = new int[nbSucc];
        List<Edge> succ = vertexs.get(vertexId).getSucc();
        for (int i = 0; i < nbSucc; i++) {
            successorsWeight[i] = succ.get(i).getCost();
        }
        return successorsWeight;
    }

    @Override
    public int[] getPredecessor(int vertexId) {
        int nbPred = this.getPredecessorCount(vertexId);
        int[] predecessors = new int[nbPred];
        List<Edge> pred = vertexs.get(vertexId).getPred();
        for (int i = 0; i < nbPred; i++) {
            predecessors[i] = pred.get(i).getSource().getIndex();
        }
        return predecessors;
    }

    @Override
    public int[] getPredecessorWeight(int vertexId) {
        int nbPred = this.getPredecessorCount(vertexId);
        int[] predecessors = new int[nbPred];
        List<Edge> pred = vertexs.get(vertexId).getPred();
        for (int i = 0; i < nbPred; i++) {
            predecessors[i] = pred.get(i).getCost();
        }
        return predecessors;
    }

    @Override
    public int getPlayer(int vertexId) {
        return vertexs.get(vertexId).getPlayer();
    }

    @Override
    public boolean hasSuccessors(int vertexId) {
        return !vertexs.get(vertexId).getSucc().isEmpty();
    }

    @Override
    public boolean hasPredecessors(int vertexId) {
        return !vertexs.get(vertexId).getPred().isEmpty();
    }


}
