package be.ac.umons.olbregts.graphgame.algorithm.implementation;

import be.ac.umons.olbregts.graphgame.algorithm.Algorithm;
import be.ac.umons.olbregts.graphgame.algorithm.Strategy;
import be.ac.umons.olbregts.graphgame.exception.IllegalGraphException;
import be.ac.umons.olbregts.graphgame.model.Game;
import be.ac.umons.olbregts.graphgame.model.Graph;
import be.ac.umons.olbregts.graphgame.model.implementation.games.ReachibilityGame;

import java.awt.*;
import java.util.*;

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
    public Strategy getStrategy(String vertexId) {
        return buchi.getStrategy(vertexId);
    }

    @Override
    public boolean isInWinningRegion(String vertexId) {
        return !buchi.isInWinningRegion(vertexId);
    }

    @Override
    public String[] getWinningRegion(){
        java.util.List<String> winningRegion = new ArrayList<>();
        for(String vertexId:game.getGraph().getVertexsId()){
            if(isInWinningRegion(vertexId)){
                winningRegion.add(vertexId);
            }
        }
        return winningRegion.toArray(new String[winningRegion.size()]);
    }

    @Override
    public String getLabel(String vertexId) {
        if (buchi.isInWinningRegion(vertexId)) {
            return "Not co buchi";
        } else {
            return "co buchi";
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
        return buchi.getEdgeColor(srcId, targetId);
    }
}
