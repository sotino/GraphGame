/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.umons.olbregts.graphgame.algorithm.implementation;

import be.ac.umons.olbregts.graphgame.algorithm.MemoryLessStrategy;
import be.ac.umons.olbregts.graphgame.algorithm.PathAlgorithm;
import be.ac.umons.olbregts.graphgame.algorithm.Strategy;
import be.ac.umons.olbregts.graphgame.exception.IllegalGraphException;
import be.ac.umons.olbregts.graphgame.model.Game;
import be.ac.umons.olbregts.graphgame.model.Graph;
import be.ac.umons.olbregts.graphgame.model.implementation.games.ReachibilityGame;

import java.util.ArrayList;

/**
 * @author Simon
 */
public class Attractor implements PathAlgorithm {

    private boolean[] attractor;
    private ReachibilityGame game;
    private int[] strat;
    private boolean ended;

    @Override
    public void reset(Game game) throws IllegalGraphException {
        if (!(game instanceof ReachibilityGame)) {
            throw new IllegalGraphException("The objectif does not match. A ReachibilityGame is needed");
        }
        this.game = (ReachibilityGame) game.getGraph();
        attractor = new boolean[game.getGraph().getVertexCount()];
        strat = new int[attractor.length];
        for (int i = 0; i < strat.length; i++) {
            strat[i] = -1;
        }
        ended = false;
        for (Integer index : this.game.getWiningCondition()) {
            attractor[index] = true;
        }
    }

    @Override
    public boolean isEnded() {
        return ended;
    }

    @Override
    public void compute() {
        while (!ended) {
            computeAStep();
        }
    }

    @Override
    public void computeAStep() {
        ended = true;
        for (int vertexId = 0; vertexId < game.getGraph().getVertexCount(); vertexId++) {
            if (!attractor[vertexId]) {
                int[] successor = game.getGraph().getSuccessors(vertexId);
                if (game.getGraph().getPlayer(vertexId) == Graph.PLAYER1) {
                    for (int succId : successor) {
                        if (attractor[succId]) {
                            attractor[vertexId] = true;
                            strat[vertexId] = succId;
                            ended = false;
                            break;
                        }
                    }
                } else {
                    int escape = -1;
                    for (int succId : successor) {
                        if (!attractor[succId]) {
                            escape = succId;
                            break;
                        }
                    }
                    if (escape == -1) {
                        attractor[vertexId] = true;
                        ended = false;
                        if (game.getGraph().hasSuccessors(vertexId)) {
                            strat[vertexId] = game.getGraph().getSuccessors(vertexId)[0];
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getLastSelected() {
        return -1;
    }

    @Override
    public int getDistance(int index) {
        if (attractor[index]) {
            return 0;
        } else {
            return Integer.MAX_VALUE;
        }
    }

    @Override
    public Strategy getStrategy(int vertesId) {
        int selected = -1;
        if (attractor[vertesId]) {
            selected = strat[vertesId];
        } else {
            if (ended) {
                int[] successor = game.getGraph().getSuccessors(vertesId);
                for (int succId : successor) {
                    if (!attractor[succId]) {
                        selected = succId;
                        break;
                    }
                }
            }
        }
        return new MemoryLessStrategy(selected);
    }

    @Override
    public ArrayList<Integer> getBlockedEdge(int index) {
        return new ArrayList<>();
    }
}
