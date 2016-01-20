package be.ac.umons.olbregts.graphgame.algorithm.implementation;

import be.ac.umons.olbregts.graphgame.algorithm.Algorithm;
import be.ac.umons.olbregts.graphgame.algorithm.MemoryLessStrategy;
import be.ac.umons.olbregts.graphgame.algorithm.Strategy;
import be.ac.umons.olbregts.graphgame.exception.IllegalGraphException;
import be.ac.umons.olbregts.graphgame.model.Game;
import be.ac.umons.olbregts.graphgame.model.Graph;
import be.ac.umons.olbregts.graphgame.model.implementation.games.ReachibilityGame;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by Simon on 19-01-16.
 */
public class Buchi implements Algorithm {

    private ReachibilityGame game;
    private Graph g;
    private boolean ended;
    private int[] strat;
    private boolean[] deleted;

    @Override
    public void reset(Game game) throws IllegalGraphException {
        if (!(game instanceof ReachibilityGame)) {
            throw new IllegalGraphException("The objectif does not match. A ReachibilityGame is needed");
        }
        this.game = (ReachibilityGame) game;
        g = game.getGraph().clone();
        strat = new int[g.getVertexCount()];
        deleted = new boolean[g.getVertexCount()];
        for (int i = 0; i < strat.length; i++) {
            strat[i] = -1;
            deleted[i] = false;
        }
        ended = false;
    }

    @Override
    public boolean isEnded() {
        return false;
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
            attr.compute();
            List<Integer> trapTargets = new ArrayList<>();
            for (int i = 0; i < g.getVertexCount(); i++) {
                if (!attr.isAttractor(i)) {
                    trapTargets.add(i);
                    System.out.println("trap target:" + i);
                }
            }
            ReachibilityGame trapGame = new ReachibilityGame(g, trapTargets);
            Attractor trap = new Attractor();
            trap.reset(trapGame);
            trap.setWinningPlayer(Graph.PLAYER2);
            trap.compute();
            for (int i = 0; i < g.getVertexCount(); i++) {
                if (trap.isAttractor(i) && !deleted[i]) {
                    ended = false;
                    deleted[i] = true;
                    int[] trapStrat = trap.getStrategy(i).getSelectedEdge();
                    if (trapStrat.length > 0) {
                        strat[i] = trapStrat[0];
                        if(game.getWiningCondition().contains(strat[i])){
                            System.out.println("strat is win");
                            for(int succId:g.getSuccessors(i)){
                                if(trap.isAttractor(succId) && ! game.getWiningCondition().contains(succId)){
                                    System.out.println("new start : "+succId);
                                    strat[i]=succId;
                                    break;
                                }
                            }
                        }
                    }
                    for (int succ : g.getSuccessors(i)) {
                        g.deleteEdge(i, succ);
                    }
                    for (int pred : g.getPredecessor(i)) {
                        g.deleteEdge(pred, i);
                    }
                }
            }
        } catch (IllegalGraphException e) {
            e.printStackTrace();
        }
        if (ended) {
            for (int i = 0; i < g.getVertexCount(); i++) {
                if (strat[i] == -1) {
                    if (g.hasSuccessors(i)) {
                        strat[i] = g.getSuccessors(i)[0];
                    }
                }
            }
        }
    }

    @Override
    public Strategy getStrategy(int index) {
        return new MemoryLessStrategy(strat[index]);
    }

    @Override
    public String getLabel(int vertexId) {
        if (deleted[vertexId]) {
            return "Not Buchi";
        } else {
            return "Buchi";
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
        if (strat[originId] == destinationId) {
            return Color.GREEN;
        }
        if(deleted[originId] || deleted[destinationId]){
            return Color.RED;
        }
        return null;
    }
}
