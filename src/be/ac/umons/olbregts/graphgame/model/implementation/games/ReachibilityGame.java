/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.umons.olbregts.graphgame.model.implementation.games;

import be.ac.umons.olbregts.graphgame.model.Game;
import be.ac.umons.olbregts.graphgame.model.Graph;

import java.util.List;

/**
 * @author Simon
 */
public class ReachibilityGame implements Game<List<String>> {

    private Graph graph;
    private List<String> targetVertexs;

    public ReachibilityGame(Graph graph, List<String> targetVertexs) {
        this.graph = graph;
        this.targetVertexs = targetVertexs;
    }

    @Override
    public Graph getGraph() {
        return graph;
    }

    @Override
    public List<String> getWiningCondition() {
        return targetVertexs;
    }

}
