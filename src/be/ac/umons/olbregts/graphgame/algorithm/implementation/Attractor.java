/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.umons.olbregts.graphgame.algorithm.implementation;

import be.ac.umons.olbregts.graphgame.algorithm.Algorithm;
import be.ac.umons.olbregts.graphgame.algorithm.strategy.MemoryLessStrategy;
import be.ac.umons.olbregts.graphgame.algorithm.strategy.Strategy;
import be.ac.umons.olbregts.graphgame.exception.IllegalGraphException;
import be.ac.umons.olbregts.graphgame.model.Game;
import be.ac.umons.olbregts.graphgame.model.Graph;
import be.ac.umons.olbregts.graphgame.model.implementation.games.ReachibilityGame;

import java.awt.*;
import java.util.*;
import java.util.Queue;

/**
 * @author Simon
 */
public class Attractor implements Algorithm {

    private ReachibilityGame game;
    private Map<String, Boolean> attractor;
    private Map<String, String> strat;
    private Map<String, Integer> succCount;
    private boolean ended;
    private int winningPlayer = Graph.PLAYER1;
    private Queue<String> toProcess;

    @Override
    public void reset(Game game) throws IllegalGraphException {
        if (!(game instanceof ReachibilityGame)) {
            throw new IllegalGraphException("The objectif does not match. A ReachibilityGame is needed");
        }
        this.game = (ReachibilityGame) game;
        int n = game.getGraph().getVertexCount();
        attractor = new HashMap<>(n);
        strat = new HashMap<>(n);
        succCount = new HashMap<>(n);
        toProcess = new LinkedList<>();
        ended = false;
        toProcess.addAll(this.game.getWiningCondition());
        for (String vertexId : game.getGraph().getVertexsId()) {
            succCount.put(vertexId, game.getGraph().getSuccessorCount(vertexId));
        }
    }

    public void setWinningPlayer(int winningPlayer) {
        this.winningPlayer = winningPlayer;
    }

    @Override
    public boolean isInWinningRegion(String vertexId) {
        return attractor.containsKey(vertexId) && attractor.get(vertexId);
    }

    @Override
    public String[] getWinningRegion() {
        java.util.List<String> winningRegion = new ArrayList<>();
        for (Map.Entry<String, Boolean> attractors : attractor.entrySet()) {
            if (attractors.getValue()) {
                winningRegion.add(attractors.getKey());
            }
        }
        return winningRegion.toArray(new String[winningRegion.size()]);
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
        Queue<String> nextProcess = new LinkedList<>();
        while (!toProcess.isEmpty()) {
            String vertexId = toProcess.remove();
            if (!attractor.containsKey(vertexId)) {
                attractor.put(vertexId, true);
                for (String predecessor : game.getGraph().getPredecessor(vertexId)) {
                    if (game.getGraph().getPlayer(predecessor) == winningPlayer) {
                        if (!strat.containsKey(predecessor)) {
                            strat.put(predecessor, vertexId);
                        }
                        nextProcess.add(predecessor);
                    } else if (succCount.get(predecessor) == 1) {
                        nextProcess.add(predecessor);
                    } else {
                        succCount.put(predecessor, succCount.get(predecessor) - 1);
                    }
                }
            }
        }
        toProcess = nextProcess;
        ended = nextProcess.isEmpty();
        if (ended) {
            for (String vertexId : game.getGraph().getVertexsId()) {
                if (!strat.containsKey(vertexId)) {
                    if (isInWinningRegion(vertexId)) {
                        for (String succId : game.getGraph().getSuccessors(vertexId)) {
                            if (isInWinningRegion(succId)) {
                                strat.put(vertexId, succId);
                                break;
                            }
                        }
                    } else {
                        for (String succId : game.getGraph().getSuccessors(vertexId)) {
                            if (!isInWinningRegion(succId)) {
                                strat.put(vertexId, succId);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public Strategy getStrategy(String vertexId) {
        return new MemoryLessStrategy(strat.get(vertexId));
    }

    @Override
    public String getLabel(String vertexId) {
        if (isInWinningRegion(vertexId)) {
            return "Attr";
        } else {
            return "Not Attr";
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
        if (ended || attractor.containsKey(srcId)) {
            if (targetId.equals(strat.get(srcId))) {
                return Color.GREEN;
            }
        }
        return null;
    }

}