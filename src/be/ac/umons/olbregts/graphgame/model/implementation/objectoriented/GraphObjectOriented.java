/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.umons.olbregts.graphgame.model.implementation.objectoriented;

import be.ac.umons.olbregts.graphgame.model.Graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * @author Simon
 */
public class GraphObjectOriented implements Graph {

    private HashMap<String, Vertex> vertexs;

    public GraphObjectOriented() {
        vertexs = new HashMap<>();
    }

    public GraphObjectOriented(List<Integer> head, List<Integer> player, List<Integer> succ, List<Integer> cost) {
        vertexs = new HashMap<>(head.size() - 1);
        for (int i = 0; i < head.size() - 1; i++) {
            int p = player.get(i) == 0 ? 1 : player.get(i);
            Vertex v = new Vertex("V" + i, p);
            vertexs.put("V" + i, v);
        }
        for (int i = 0; i < head.size() - 1; i++) {
            for (int j = head.get(i) - 1; j < head.get(i + 1) - 1; j++) {
                Edge e = new Edge(vertexs.get("V" + i), vertexs.get("V" + (succ.get(j) - 1)), cost.get(j));
                e.getTarget().addPred(e);
                e.getSource().addSucc(e);
            }
        }
    }

    public String[] getVertexsId() {
        return vertexs.keySet().toArray(new String[getVertexCount()]);
    }

    public boolean contains(String vertexId) {
        return vertexs.containsKey(vertexId);
    }

    public Collection<Vertex> getVertexs() {
        return vertexs.values();
    }

    public void addVertex(String vertexId, int player) {
        Vertex v = new Vertex(vertexId, player);
        vertexs.put(vertexId, v);
    }

    public void deleteVertex(String vertexId) {
        Vertex old = vertexs.get(vertexId);
        vertexs.remove(vertexId);
        for (Vertex vertex : vertexs.values()) {
            vertex.removePred(old);
            vertex.removeSucc(old);
        }
    }

    public void deleteEdge(String srcId, String targetId) {
        if (!vertexs.containsKey(srcId)) {
            throw new IllegalArgumentException("The vertex (" + srcId + ") is not in the graph.");
        }
        if (!vertexs.containsKey(targetId)) {
            throw new IllegalArgumentException("The vertex (" + targetId + ") is not in the graph.");
        }
        Vertex src = vertexs.get(srcId);
        Vertex target = vertexs.get(targetId);
        target.removePred(src);
        src.removeSucc(target);
    }

    public boolean addEdge(String srcId, String targetId, int cost) {
        if (!vertexs.containsKey(srcId)) {
            throw new IllegalArgumentException("The vertex (" + srcId + ") is not in the graph.");
        }
        if (!vertexs.containsKey(targetId)) {
            throw new IllegalArgumentException("The vertex (" + targetId + ") is not in the graph.");
        }
        Vertex src = vertexs.get(srcId);
        Vertex target = vertexs.get(targetId);
        Edge e = new Edge(src, target, cost);
        src.addSucc(e);
        return target.addPred(e);
    }

    @Override
    public int getVertexCount() {
        return vertexs.size();
    }

    @Override
    public int getSuccessorCount(String vertexId) {
        return vertexs.get(vertexId).getSucc().size();
    }

    @Override
    public int getPredecessorCount(String vertexId) {
        return vertexs.get(vertexId).getPred().size();
    }

    @Override
    public String[] getSuccessors(String vertexId) {
        int nbSucc = this.getSuccessorCount(vertexId);
        String[] successors = new String[nbSucc];
        List<Edge> succ = vertexs.get(vertexId).getSucc();
        for (int i = 0; i < nbSucc; i++) {
            successors[i] = succ.get(i).getTarget().getId();
        }
        return successors;
    }

    @Override
    public int[] getSuccessorsWeight(String vertexId) {
        int nbSucc = this.getSuccessorCount(vertexId);
        int[] successorsWeight = new int[nbSucc];
        List<Edge> succ = vertexs.get(vertexId).getSucc();
        for (int i = 0; i < nbSucc; i++) {
            successorsWeight[i] = succ.get(i).getCost();
        }
        return successorsWeight;
    }

    @Override
    public String[] getPredecessor(String vertexId) {
        int nbPred = this.getPredecessorCount(vertexId);
        String[] predecessors = new String[nbPred];
        List<Edge> pred = vertexs.get(vertexId).getPred();
        for (int i = 0; i < nbPred; i++) {
            predecessors[i] = pred.get(i).getSource().getId();
        }
        return predecessors;
    }

    @Override
    public int[] getPredecessorWeight(String vertexId) {
        int nbPred = this.getPredecessorCount(vertexId);
        int[] predecessors = new int[nbPred];
        List<Edge> pred = vertexs.get(vertexId).getPred();
        for (int i = 0; i < nbPred; i++) {
            predecessors[i] = pred.get(i).getCost();
        }
        return predecessors;
    }

    @Override
    public int getPlayer(String vertexId) {
        return vertexs.get(vertexId).getPlayer();
    }

    @Override
    public boolean hasSuccessors(String vertexId) {
        return !vertexs.get(vertexId).getSucc().isEmpty();
    }

    @Override
    public boolean hasPredecessors(String vertexId) {
        return !vertexs.get(vertexId).getPred().isEmpty();
    }

    @Override
    public Graph getSubgraph(String[] vertexsId) {
        GraphObjectOriented subGraphe = new GraphObjectOriented();
        for (Vertex vertex : vertexs.values()) {
            subGraphe.addVertex(vertex.getId(), vertex.getPlayer());
        }
        for (String sourceId : vertexsId) {
            for (Edge succ : vertexs.get(sourceId).getSucc()) {
                if (contains(vertexsId, succ.getTarget().getId())) {
                    subGraphe.addEdge(sourceId, succ.getTarget().getId(), succ.getCost());
                }
            }
        }
        return subGraphe;
    }

    private boolean contains(String[] array, String v) {
        for (String a : array) {
            if (a.equals(v)) {
                return true;
            }
        }
        return false;
    }

    public Graph clone() {
        GraphObjectOriented clone = new GraphObjectOriented();
        for (Vertex v : vertexs.values()) {
            clone.addVertex(v.getId(), v.getPlayer());
        }
        for (Vertex v : vertexs.values()) {
            for (Edge e : v.getSucc()) {
                clone.addEdge(v.getId(), e.getTarget().getId(), e.getCost());
            }
        }
        return clone;
    }

}
