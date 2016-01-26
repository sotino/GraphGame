/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.umons.olbregts.graphgame.algorithm.implementation;

import be.ac.umons.olbregts.graphgame.algorithm.EscapeStrategy;
import be.ac.umons.olbregts.graphgame.algorithm.Algorithm;
import be.ac.umons.olbregts.graphgame.algorithm.Strategy;
import be.ac.umons.olbregts.graphgame.exception.IllegalGraphException;
import be.ac.umons.olbregts.graphgame.model.Game;
import be.ac.umons.olbregts.graphgame.model.Graph;
import be.ac.umons.olbregts.graphgame.model.Heap;
import be.ac.umons.olbregts.graphgame.model.implementation.games.ReachibilityGame;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author Simon
 */
public class ValueIteration implements Algorithm {

    private ArrayList<Integer> vertexValues;
    private int[] mainStrat;
    private int[] escapeStrat;
    private boolean ended;
    private ReachibilityGame game;
    private Integer[] targets;
    private int minBorder;
    private int W;

    @Override
    public void reset(Game game) throws IllegalGraphException {
        if (!(game instanceof ReachibilityGame)) {
            throw new IllegalGraphException("The objectif does not match. A ReachibilityGame is needed");
        }
        this.game = (ReachibilityGame) game;

        targets = this.game.getWiningCondition().toArray(new Integer[0]);
        int vertexCount = game.getGraph().getVertexCount();
        vertexValues = new ArrayList<>(vertexCount);
        mainStrat = new int[vertexCount];
        escapeStrat = new int[vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            vertexValues.add(Integer.MAX_VALUE);
            mainStrat[i] = -1;
            escapeStrat[i] = -1;
        }
        for (int i : targets) {
            vertexValues.set(i, 0);
        }
        W = Integer.MIN_VALUE;
        for (int i = 0; i < vertexCount; i++) {
            for (int succWeight : game.getGraph().getSuccessorsWeight(i)) {
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

    private boolean isTarget(int vertexId) {
        for (int target : targets) {
            if (target == vertexId)
                return true;
        }
        return false;
    }

    @Override
    public void computeAStep() {
        ended = true;
        ArrayList<Integer> previous = clonePreviousResult();
        for (int v = 0; v < game.getGraph().getVertexCount(); v++) {
            if (!isTarget(v)) {
                if (game.getGraph().getPlayer(v) == Graph.PLAYER1) {
                    int min = Integer.MAX_VALUE;
                    int argMin = -1;
                    int[] succ = game.getGraph().getSuccessors(v);
                    int[] succWeight = game.getGraph().getSuccessorsWeight(v);
                    for (int i = 0; i < succ.length; i++) {
                        int succValue = add(previous.get(succ[i]), succWeight[i]);
                        if (succValue < min) {
                            min = succValue;
                            argMin = succ[i];
                        }
                    }
                    vertexValues.set(v, min);
                    if (!vertexValues.get(v).equals(previous.get(v))) {
                        ended = false;
                        mainStrat[v] = argMin;
                        if (previous.get(v) == Integer.MAX_VALUE) {
                            escapeStrat[v] = argMin;
                        }
                    }
                } else {
                    int max = Integer.MIN_VALUE;
                    int argMax = -1;

                    int[] succ = game.getGraph().getSuccessors(v);
                    int[] succWeight = game.getGraph().getSuccessorsWeight(v);
                    for (int i = 0; i < succ.length; i++) {
                        int succValue = add(previous.get(succ[i]), succWeight[i]);
                        if (succValue > max) {
                            max = succValue;
                            argMax = succ[i];
                        }
                    }
                    vertexValues.set(v, max);
                    if (!vertexValues.get(v).equals(previous.get(v))) {
                        ended = false;
                        mainStrat[v] = argMax;
                    }
                }
                if (vertexValues.get(v) < minBorder) {
                    vertexValues.set(v, Integer.MIN_VALUE);
                }
            }
        }
        if (ended) {
            computeInfiniteStrategy();
        }
    }

    @Override
    public Strategy getStrategy(int index) {
        int distance = Integer.MAX_VALUE;
        if (escapeStrat[index] != -1) {
            distance = vertexValues.get(index) - (game.getGraph().getVertexCount() - 1) * W;
        }
        return new EscapeStrategy(mainStrat[index], escapeStrat[index], distance);
    }

    @Override
    public boolean isInWinningRegion(int vertexId){
        return (vertexValues.get(vertexId) != Integer.MAX_VALUE && vertexValues.get(vertexId) != Integer.MIN_VALUE);
    }

    @Override
    public String getLabel(int vertexId) {
        int value = vertexValues.get(vertexId);
        if(value == Integer.MAX_VALUE){
            return "+ inf";
        }
        if(value == Integer.MIN_VALUE){
            return "- inf";
        }
        return "" + value;
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
        EscapeStrategy strategy = (EscapeStrategy) getStrategy(originId);
        if(destinationId == strategy.getMainChoose()){
            return Color.GREEN;
        }
        if(destinationId == strategy.getEscapeChoose()){
            return Color.GREEN.darker();
        }
        return null;
    }

    private ArrayList<Integer> clonePreviousResult() {
        ArrayList<Integer> clone = new ArrayList<>(vertexValues.size());
        for (int i : vertexValues) {
            clone.add(i);
        }
        return clone;
    }

    private void computeInfiniteStrategy() {
        try {
            Attractor attractor = new Attractor();
            attractor.reset(game);
            attractor.compute();
            for (int i = 0; i < mainStrat.length; i++) {
                if (mainStrat[i] == -1) {
                    int[] attrStrat = attractor.getStrategy(i).getSelectedEdge();
                    if (attrStrat.length >= 1) {
                        mainStrat[i] = attrStrat[0];
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
