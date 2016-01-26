package be.ac.umons.olbregts.graphgame.model.implementation.games;

import be.ac.umons.olbregts.graphgame.model.Game;
import be.ac.umons.olbregts.graphgame.model.Graph;

/**
 * Created by Simon on 26-01-16.
 */
public class QuantitativeGame implements Game<Integer> {

    private Graph graph;
    private int target;

    public QuantitativeGame(Graph graph, int target) {
        this.graph = graph;
        this.target = target;
    }

    @Override
    public Graph getGraph() {
        return graph;
    }

    @Override
    public Integer getWiningCondition() {
        return target;
    }
}
