package be.ac.umons.olbregts.graphgame.algorithm.implementation;

import be.ac.umons.olbregts.graphgame.algorithm.Algorithm;
import be.ac.umons.olbregts.graphgame.algorithm.Strategy;
import be.ac.umons.olbregts.graphgame.exception.IllegalGraphException;
import be.ac.umons.olbregts.graphgame.model.Game;
import be.ac.umons.olbregts.graphgame.model.Graph;
import be.ac.umons.olbregts.graphgame.model.implementation.games.ReachibilityGame;

import java.awt.*;

/**
 * Created by Simon on 21-01-16.
 */
public class CoBuchi implements Algorithm {

    private Buchi buchi;
    private ReachibilityGame game;

    @Override
    public void reset(Game game) throws IllegalGraphException {
        if (!(game instanceof ReachibilityGame)) {
            throw new IllegalGraphException("The objectif does not match. A ReachibilityGame is needed");
        }
        this.game = (ReachibilityGame) game;
        buchi = new Buchi();
        buchi.setWinningPlayer(Graph.PLAYER2);
        buchi.reset(game);
    }

    @Override
    public boolean isEnded() {
        return buchi.isEnded();
    }

    @Override
    public void compute() {
        buchi.compute();
    }

    @Override
    public void computeAStep() {
        buchi.computeAStep();
    }

    @Override
    public Strategy getStrategy(int index) {
        return buchi.getStrategy(index);
    }

    @Override
    public boolean isInWinningRegion(int vertexId) {
        return !buchi.isInWinningRegion(vertexId);
    }

    @Override
    public String getLabel(int vertexId) {
        if (buchi.isInWinningRegion(vertexId)) {
            return "Not co buchi";
        } else {
            return "co buchi";
        }
    }

    @Override
    public Color getVertexColor(int vertexId) {
        for (int target : game.getWiningCondition()) {
            if (vertexId == target) {
                return Color.YELLOW;
            }
        }
        return null;
    }

    @Override
    public Color getEdgeColor(int originId, int destinationId) {
        return buchi.getEdgeColor(originId, destinationId);
    }
}
