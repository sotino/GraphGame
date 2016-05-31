package be.ac.umons.olbregts.graphgame.algorithm.implementation;

import be.ac.umons.olbregts.graphgame.algorithm.Algorithm;
import be.ac.umons.olbregts.graphgame.algorithm.strategy.Strategy;
import be.ac.umons.olbregts.graphgame.exception.IllegalGraphException;
import be.ac.umons.olbregts.graphgame.model.Game;
import be.ac.umons.olbregts.graphgame.model.Graph;
import be.ac.umons.olbregts.graphgame.model.implementation.games.ReachibilityGame;

import java.awt.*;
import java.util.ArrayList;

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
    public Strategy getStrategy(String vertexId) {
        return attractor.getStrategy(vertexId);
    }

    @Override
    public boolean isInWinningRegion(String vertexId) {
        return !attractor.isInWinningRegion(vertexId);
    }

    @Override
    public String[] getWinningRegion() {
        java.util.List<String> winningRegion = new ArrayList<>();
        for (String vertexId : game.getGraph().getVertexsId()) {
            if (isInWinningRegion(vertexId)) {
                winningRegion.add(vertexId);
            }
        }
        return winningRegion.toArray(new String[winningRegion.size()]);
    }

    @Override
    public String getLabel(String vertexId) {
        if (attractor.isInWinningRegion(vertexId)) {
            return "Not Safe";
        } else {
            return "Safe";
        }
    }

    @Override
    public Color getVertexColor(String vertexId) {
        for (String target : game.getWiningCondition()) {
            if (vertexId.equals(target)) {
                return Color.YELLOW;
            }
        }
        return null;
    }

    @Override
    public Color getEdgeColor(String srcId, String targetId) {
        return attractor.getEdgeColor(srcId, targetId);
    }
}
