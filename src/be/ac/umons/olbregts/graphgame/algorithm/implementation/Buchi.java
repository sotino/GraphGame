package be.ac.umons.olbregts.graphgame.algorithm.implementation;

import be.ac.umons.olbregts.graphgame.algorithm.Algorithm;
import be.ac.umons.olbregts.graphgame.algorithm.strategy.MemoryLessStrategy;
import be.ac.umons.olbregts.graphgame.algorithm.strategy.Strategy;
import be.ac.umons.olbregts.graphgame.exception.IllegalGraphException;
import be.ac.umons.olbregts.graphgame.model.Game;
import be.ac.umons.olbregts.graphgame.model.Graph;
import be.ac.umons.olbregts.graphgame.model.implementation.games.ReachibilityGame;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Simon on 19-01-16.
 */
public class Buchi implements Algorithm {

    private ReachibilityGame game;
    private Graph g;
    private boolean ended;
    private Map<String, String> strat;
    private int winningPlayer = Graph.PLAYER1;

    @Override
    public void reset(Game game) throws IllegalGraphException {
        if (!(game instanceof ReachibilityGame)) {
            throw new IllegalGraphException("The objectif does not match. A ReachibilityGame is needed");
        }
        this.game = (ReachibilityGame) game;
        g = game.getGraph().clone();
        strat = new HashMap<>(g.getVertexCount());
        ended = false;
    }

    public void setWinningPlayer(int winningPlayer) {
        this.winningPlayer = winningPlayer;
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
        Attractor attr = new Attractor();
        try {
            ReachibilityGame attrGame = new ReachibilityGame(g, game.getWiningCondition());
            attr.reset(attrGame);
            attr.setWinningPlayer(winningPlayer);
            attr.compute();
            List<String> trapTargets = new ArrayList<>();
            for (String vertexId : g.getVertexsId()) {
                System.out.println(vertexId + ": " + attr.isInWinningRegion(vertexId));
                if (!attr.isInWinningRegion(vertexId)) {
                    trapTargets.add(vertexId);
                }
            }

            ReachibilityGame trapGame = new ReachibilityGame(g, trapTargets);
            Attractor trap = new Attractor();
            trap.reset(trapGame);
            trap.setWinningPlayer((winningPlayer == Graph.PLAYER1 ? Graph.PLAYER2 : Graph.PLAYER1));
            trap.compute();
            for (String toRemoveId : trap.getWinningRegion()) {
                ended = false;
                String[] trapStrat = trap.getStrategy(toRemoveId).getSelectedEdge();
                if (trapStrat.length > 0) {
                    strat.put(toRemoveId, trapStrat[0]);
                    /** The traping strategy can force to visit a vertex of the winning condition
                     * if one the winning vertex is in the winning region of the player 2
                     */
                    if (game.getWiningCondition().contains(trapStrat[0])) {
                        for (String succId : g.getSuccessors(toRemoveId)) {
                            if (trap.isInWinningRegion(succId) && !game.getWiningCondition().contains(succId)) {
                                strat.put(toRemoveId, succId);
                            }
                        }
                    }
                }
                g.deleteVertex(toRemoveId);
            }
            if (g.getVertexCount() == 0) {
                ended = true;
            }

        } catch (IllegalGraphException e) {
            //Can't happen cause if the graph is valid for Buchi it's valid for Attractor.
        }
        if (ended) {
            /**
             * When ended the remaining vertex is in the winning region of player 1
             * And all remaining edge allow to stay in this region
             */
            for (String vertexId : g.getVertexsId()) {
                if (!strat.containsKey(vertexId) && g.hasSuccessors(vertexId)) {
                    strat.put(vertexId, g.getSuccessors(vertexId)[0]);
                }
            }
        }
    }

    @Override
    public Strategy getStrategy(String vertexId) {
        return new MemoryLessStrategy(strat.get(vertexId));
    }

    @Override
    public boolean isInWinningRegion(String vertexId) {
        return g.contains(vertexId);
    }

    @Override
    public String[] getWinningRegion() {
        return g.getVertexsId();
    }

    @Override
    public String getLabel(String vertexId) {
        if (isInWinningRegion(vertexId)) {
            return "Buchi";
        } else {
            return "Not Buchi";
        }
    }

    @Override
    public Color getVertexColor(String vertexId) {
        for (String targetId : game.getWiningCondition()) {
            if (vertexId.equals(targetId)) {
                return Color.YELLOW;
            }
        }
        return null;
    }

    @Override
    public Color getEdgeColor(String srcId, String targetId) {
        if (targetId.equals(strat.get(srcId))) {
            return Color.GREEN;
        }
        if (!isInWinningRegion(srcId) || !isInWinningRegion(targetId)) {
            return Color.RED;
        }
        return null;
    }
}
