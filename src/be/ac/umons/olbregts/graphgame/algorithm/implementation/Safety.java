package be.ac.umons.olbregts.graphgame.algorithm.implementation;

import be.ac.umons.olbregts.graphgame.algorithm.Algorithm;
import be.ac.umons.olbregts.graphgame.algorithm.Strategy;
import be.ac.umons.olbregts.graphgame.exception.IllegalGraphException;
import be.ac.umons.olbregts.graphgame.model.Game;
import be.ac.umons.olbregts.graphgame.model.Graph;
import be.ac.umons.olbregts.graphgame.model.implementation.games.ReachibilityGame;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Simon on 19-01-16.
 */
public class Safety implements Algorithm {

    private Attractor attractor;
    private ReachibilityGame game;

    @Override
    public void reset(Game game) throws IllegalGraphException {
        if (!(game instanceof ReachibilityGame)) {
            throw new IllegalGraphException("The objectif does not match. A ReachibilityGame is needed");
        }
        this.game = (ReachibilityGame) game;
        attractor = new Attractor();
        attractor.setWinningPlayer(Graph.PLAYER2);
        attractor.reset(game);
    }

    @Override
    public boolean isEnded() {
        return attractor.isEnded();
    }

    @Override
    public void compute() {
        attractor.compute();
    }

    @Override
    public void computeAStep() {
        attractor.computeAStep();
    }

    @Override
    public Strategy getStrategy(int index) {
        return attractor.getStrategy(index);
    }

    @Override
    public String getLabel(int vertexId) {
        if (attractor.isAttractor(vertexId)) {
            return "Not Safe";
        } else {
            return "Safe";
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
        return attractor.getEdgeColor(originId,destinationId);
    }
}
