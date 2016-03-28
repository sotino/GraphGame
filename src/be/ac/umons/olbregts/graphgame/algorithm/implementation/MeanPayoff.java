package be.ac.umons.olbregts.graphgame.algorithm.implementation;

import be.ac.umons.olbregts.graphgame.algorithm.Algorithm;
import be.ac.umons.olbregts.graphgame.algorithm.MemoryLessStrategy;
import be.ac.umons.olbregts.graphgame.algorithm.Strategy;
import be.ac.umons.olbregts.graphgame.exception.IllegalGraphException;
import be.ac.umons.olbregts.graphgame.model.Game;
import be.ac.umons.olbregts.graphgame.model.Graph;
import be.ac.umons.olbregts.graphgame.model.implementation.games.QuantitativeGame;

import java.awt.*;

/**
 * Created by Simon on 26-01-16.
 */
public class MeanPayoff implements Algorithm {

    private double[] value;
    private int[] strat;
    private int k;
    private int kMax;
    private QuantitativeGame game;

    @Override
    public void reset(Game game) throws IllegalGraphException {
        if (!(game instanceof QuantitativeGame)) {
            throw new IllegalGraphException("The objectif does not match. A QuantitativeGame is needed");
        }
        this.game = (QuantitativeGame) game;
        value = new double[game.getGraph().getVertexCount()];
        strat = new int[game.getGraph().getVertexCount()];
        int d = Integer.MIN_VALUE;
        for (int v = 0; v < value.length; v++) {
            int[] succWeight = game.getGraph().getSuccessorsWeight(v);
            if (succWeight.length < 1) {
                throw new IllegalGraphException("No dead end allowed. The vertex " + v + " have no successor");
            }
            for (int w : succWeight) {
                if (d < Math.abs(w)) {
                    d = Math.abs(w);
                }
            }
            value[v] = 0;
            strat[v] = -1;
        }
        k = 0;
        int n = game.getGraph().getVertexCount();
        kMax = 4 * d * n * n * n;
    }

    @Override
    public boolean isEnded() {
        return k >= kMax;
    }

    @Override
    public void compute() {
        while (!isEnded()) {
            computeAStep();
        }
    }

    @Override
    public void computeAStep() {
        double[] temp = new double[value.length];
        for (int v = 0; v < game.getGraph().getVertexCount(); v++) {
            int[] succ = game.getGraph().getSuccessors(v);
            int[] succWeight = game.getGraph().getSuccessorsWeight(v);
            int player = game.getGraph().getPlayer(v);
            double newValue = (player == Graph.PLAYER1 ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
            for (int i = 0; i < succ.length; i++) {
                double s = succWeight[i] + value[succ[i]];
                if (player == Graph.PLAYER1) {
                    if (newValue < s) {
                        newValue = s;
                        strat[v] = succ[i];
                    }
                } else {
                    if (newValue > s) {
                        newValue = s;
                        strat[v] = succ[i];
                    }
                }
            }
            temp[v] = newValue;
        }
        value = temp;
        k++;
        if (isEnded()) {
            int n = game.getGraph().getVertexCount();
            for (int i = 0; i < value.length; i++) {
                double v = value[i] / k;
                double upperBound = v + (1 * 1. / (2 * n * (n - 1)));
                double lowerBound = v - (1 * 1. / (2 * n * (n - 1)));
                for (int l = 1; l <= n; l++) {
                    double t = Math.ceil(v * l) / l;
                    if (lowerBound < t && t < upperBound) {
                        value[i] = t;
                        break;
                    }
                    t = Math.floor(v * l) / l;
                    if (lowerBound < t && t < upperBound) {
                        value[i] = t;
                        break;
                    }
                }
            }
        }
    }

    @Override
    public boolean isInWinningRegion(int vertexId) {
        return value[vertexId] >= game.getWiningCondition();
    }

    @Override
    public Strategy getStrategy(int index) {
        return new MemoryLessStrategy(strat[index]);
    }

    @Override
    public String getLabel(int vertexId) {
        if (value[vertexId] == Long.MAX_VALUE) {
            return "+ inf";
        } else if (value[vertexId] == Long.MIN_VALUE) {
            return "- inf";
        } else {
            return "" + value[vertexId];
        }
    }

    @Override
    public Color getVertexColor(int vertexId) {
        return null;
    }

    @Override
    public Color getEdgeColor(int originId, int destinationId) {
        if (strat[originId] == destinationId) {
            return Color.GREEN;
        }
        return null;
    }
}
