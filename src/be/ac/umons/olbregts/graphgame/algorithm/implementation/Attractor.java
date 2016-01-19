/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.umons.olbregts.graphgame.algorithm.implementation;

import be.ac.umons.olbregts.graphgame.algorithm.MemoryLessStrategy;
import be.ac.umons.olbregts.graphgame.algorithm.Algorithm;
import be.ac.umons.olbregts.graphgame.algorithm.Strategy;
import be.ac.umons.olbregts.graphgame.exception.IllegalGraphException;
import be.ac.umons.olbregts.graphgame.model.Game;
import be.ac.umons.olbregts.graphgame.model.Graph;
import be.ac.umons.olbregts.graphgame.model.implementation.games.ReachibilityGame;

import java.awt.*;

/**
 * @author Simon
 */
public class Attractor implements Algorithm {

    private boolean[] attractor;
    private ReachibilityGame game;
    private int[] strat;
    private boolean ended;
    private int winningPlayer = Graph.PLAYER1;

    @Override
    public void reset(Game game) throws IllegalGraphException {
        if (!(game instanceof ReachibilityGame)) {
            throw new IllegalGraphException("The objectif does not match. A ReachibilityGame is needed");
        }
        this.game = (ReachibilityGame) game;
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

    public void setWinningPlayer(int winningPlayer){
        this.winningPlayer = winningPlayer;
    }

    public boolean isAttractor(int vertexId){
        return attractor[vertexId];
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
                if (game.getGraph().getPlayer(vertexId) == winningPlayer) {
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
    public String getLabel(int vertexId) {
        if (attractor[vertexId]) {
            return "Attr";
        } else {
            return "Not Attr";
        }
    }

    @Override
    public Color getVertexColor(int vertexId) {
        for(int target : game.getWiningCondition()){
            if(vertexId == target){
                return Color.YELLOW;
            }
        }
        return null;
    }

    @Override
    public Color getEdgeColor(int originId, int destinationId) {
        int[] selected = getStrategy(originId).getSelectedEdge();
        for(int v : selected){
            if(v == destinationId){
                return Color.GREEN;
            }
        }
        return null;
    }

}
