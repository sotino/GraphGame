/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.umons.olbregts.graphgame.model.implementation.objectoriented;

/**
 * @author Simon
 */
public class Edge {

    private Vertex source;
    private Vertex target;
    private int cost;

    public Edge(Vertex source, Vertex target, int cost) {
        this.source = source;
        this.target = target;
        this.cost = cost;
    }

    public Vertex getTarget() {
        return target;
    }

    public Vertex getSource() {
        return source;
    }

    public int getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return "E{" + source + "->" + target + " c=" + cost + '}';
    }

}