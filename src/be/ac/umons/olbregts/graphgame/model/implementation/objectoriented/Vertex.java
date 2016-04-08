/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.umons.olbregts.graphgame.model.implementation.objectoriented;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon
 */
public class Vertex {

    private String id;
    private int player;
    private ArrayList<Edge> predecessor;
    private ArrayList<Edge> successor;
    private int availableEdge;

    public Vertex(String id, int player) {
        this.id = id;
        predecessor = new ArrayList<>();
        successor = new ArrayList<>();
        this.player = player;
        availableEdge = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPlayer() {
        return player;
    }

    public int getAvailableEdge() {
        return availableEdge;
    }

    public List<Edge> getPred() {
        return predecessor;
    }

    public boolean addPred(Edge pred) {
        if (isPred(pred.getSource())) {
            return false;
        }
        return predecessor.add(pred);
    }

    public List<Edge> getSucc() {
        return successor;
    }

    public boolean addSucc(Edge succ) {
        if (isSucc(succ.getTarget())) {
            return false;
        }
        boolean result = successor.add(succ);
        if (result) {
            availableEdge++;
        }
        return result;
    }

    public void removePred(Vertex pred) {
        for (int i = 0; i < predecessor.size(); i++) {
            Edge e = predecessor.get(i);
            if (e.getSource() == pred) {
                predecessor.remove(i);
                i--;
            }
        }
    }

    public void removeSucc(Vertex succ) {
        for (int i = 0; i < successor.size(); i++) {
            Edge e = successor.get(i);
            if (e.getTarget() == succ) {
                successor.remove(i);
                i--;
            }
        }
    }

    public boolean isPred(Vertex src) {
        for (Edge e : predecessor) {
            if (e.getSource() == src) {
                return true;
            }
        }
        return false;
    }

    public boolean isSucc(Vertex target) {
        for (Edge e : successor) {
            if (e.getTarget() == target) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "[V" + id + ']';
    }
}