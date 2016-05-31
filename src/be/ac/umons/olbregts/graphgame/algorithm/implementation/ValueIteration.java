/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.umons.olbregts.graphgame.algorithm.implementation;

import be.ac.umons.olbregts.graphgame.algorithm.Algorithm;
import be.ac.umons.olbregts.graphgame.algorithm.strategy.EscapeStrategy;
import be.ac.umons.olbregts.graphgame.algorithm.strategy.Strategy;
import be.ac.umons.olbregts.graphgame.exception.IllegalGraphException;
import be.ac.umons.olbregts.graphgame.model.Game;
import be.ac.umons.olbregts.graphgame.model.Graph;
import be.ac.umons.olbregts.graphgame.model.implementation.games.ReachibilityGame;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Simon
 */
public class ValueIteration implements Algorithm {

    private Map<String, Integer> vertexValues;
    private Map<String, String> mainStrat;
    private Map<String, String> escapeStrat;
    private boolean ended;
    private ReachibilityGame game;
    private String[] targets;
    private int minBorder;
    private int W;

    @Override
    public void reset(Game game) throws IllegalGraphException {
        if (!(game instanceof ReachibilityGame)) {
            throw new IllegalGraphException("The objectif does not match. A ReachibilityGame is needed");
        }
        this.game = (ReachibilityGame) game;

        targets = this.game.getWiningCondition().toArray(new String[0]);
        int vertexCount = game.getGraph().getVertexCount();
        vertexValues = new HashMap<>(vertexCount);
        mainStrat = new HashMap<>(vertexCount);
        escapeStrat = new HashMap<>(vertexCount);
        for (String vertexId : game.getGraph().getVertexsId()) {
            vertexValues.put(vertexId, Integer.MAX_VALUE);
        }
        for (String t : targets) {
            vertexValues.put(t, 0);
        }
        W = Integer.MIN_VALUE;
        for (String vertexId : game.getGraph().getVertexsId()) {
            for (int succWeight : game.getGraph().getSuccessorsWeight(vertexId)) {
                if (Math.abs(succWeight) > W) {
                    W = Math.abs(succWeight);
                }
            }
        }
        minBorder = -(vertexCount - 1) * W;
        ended = false;
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

    private boolean isTarget(String vertexId) {
        for (String target : targets) {
            if (target.equals(vertexId))
                return true;
        }
        return false;
    }

    @Override
    public void computeAStep() {
        ended = true;
        Map<String, Integer> previous = clonePreviousResult();
        for (String vertexId : game.getGraph().getVertexsId()) {
            if (!isTarget(vertexId)) {
                if (game.getGraph().getPlayer(vertexId) == Graph.PLAYER1) {
                    int min = Integer.MAX_VALUE;
                    String argMin = null;
                    String[] succ = game.getGraph().getSuccessors(vertexId);
                    int[] succWeight = game.getGraph().getSuccessorsWeight(vertexId);
                    for (int i = 0; i < succ.length; i++) {
                        int succValue = add(previous.get(succ[i]), succWeight[i]);
                        if (succValue < min) {
                            min = succValue;
                            argMin = succ[i];
                        }
                    }
                    vertexValues.put(vertexId, min);
                    if (!vertexValues.get(vertexId).equals(previous.get(vertexId))) {
                        ended = false;
                        mainStrat.put(vertexId, argMin);
                        if (previous.get(vertexId) == Integer.MAX_VALUE) {
                            escapeStrat.put(vertexId, argMin);
                        }
                    }
                } else {
                    int max = Integer.MIN_VALUE;
                    String argMax = null;

                    String[] succ = game.getGraph().getSuccessors(vertexId);
                    int[] succWeight = game.getGraph().getSuccessorsWeight(vertexId);
                    for (int i = 0; i < succ.length; i++) {
                        int succValue = add(previous.get(succ[i]), succWeight[i]);
                        if (succValue > max) {
                            max = succValue;
                            argMax = succ[i];
                        }
                    }
                    vertexValues.put(vertexId, max);
                    if (!vertexValues.get(vertexId).equals(previous.get(vertexId))) {
                        ended = false;
                        mainStrat.put(vertexId, argMax);
                    }
                }
                if (vertexValues.get(vertexId) < minBorder) {
                    vertexValues.put(vertexId, Integer.MIN_VALUE);
                }
            }
        }
        if (ended) {
            computeInfiniteStrategy();
        }
    }

    @Override
    public Strategy getStrategy(String vertexId) {
        int distance = Integer.MAX_VALUE;
        if (escapeStrat.get(vertexId) != null) {
            distance = vertexValues.get(vertexId) - (game.getGraph().getVertexCount() - 1) * W;
        }
        return new EscapeStrategy(mainStrat.get(vertexId), escapeStrat.get(vertexId), distance);
    }

    @Override
    public boolean isInWinningRegion(String vertexId) {
        return (vertexValues.get(vertexId) != Integer.MAX_VALUE && vertexValues.get(vertexId) != Integer.MIN_VALUE);
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
        int value = vertexValues.get(vertexId);
        if (value == Integer.MAX_VALUE) {
            return "+ inf";
        }
        if (value == Integer.MIN_VALUE) {
            return "- inf";
        }
        return "" + value;
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
        EscapeStrategy strategy = (EscapeStrategy) getStrategy(srcId);
        if (targetId.equals(strategy.getMainChoose())) {
            return Color.GREEN;
        }
        if (targetId.equals(strategy.getEscapeChoose())) {
            return Color.GREEN.darker();
        }
        return null;
    }

    private Map<String, Integer> clonePreviousResult() {
        Map<String, Integer> clone = new HashMap<>(vertexValues.size());
        for (Map.Entry<String, Integer> entry : vertexValues.entrySet()) {
            clone.put(entry.getKey(), entry.getValue());
        }
        return clone;
    }

    private void computeInfiniteStrategy() {
        try {
            Attractor attractor = new Attractor();
            attractor.reset(game);
            attractor.compute();
            for (String vertexId : game.getGraph().getVertexsId()) {
                if (!mainStrat.containsKey(vertexId)) {
                    String[] attrStrat = attractor.getStrategy(vertexId).getSelectedEdge();
                    if (attrStrat.length >= 1) {
                        mainStrat.put(vertexId, attrStrat[0]);
                    }
                }
            }
        } catch (IllegalGraphException ex) {
            //Can't happen. Cause if the graph is valid for ValueIteration it's valid for Attractor.
        }
    }

    private int add(int a, int b) {
        if (b == Integer.MAX_VALUE || b == Integer.MIN_VALUE) {
            return b;
        }
        if (a != Integer.MAX_VALUE && a != Integer.MIN_VALUE) {
            a += b;
        }
        return a;
    }
}
