package be.ac.umons.olbregts.graphgame.algorithm.implementation;

import be.ac.umons.olbregts.graphgame.algorithm.Algorithm;
import be.ac.umons.olbregts.graphgame.algorithm.strategy.Strategy;
import be.ac.umons.olbregts.graphgame.algorithm.strategy.WindowStrategy;
import be.ac.umons.olbregts.graphgame.exception.IllegalGraphException;
import be.ac.umons.olbregts.graphgame.model.Game;
import be.ac.umons.olbregts.graphgame.model.Graph;
import be.ac.umons.olbregts.graphgame.model.implementation.games.WindowingQuantitativeGame;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by simon on 28/03/16.
 */
public class GoodWin implements Algorithm {

    private WindowingQuantitativeGame game;
    private int l;
    private Map<String, int[]> c;
    private Map<String, WindowStrategy> strats;

    @Override
    public void reset(Game game) throws IllegalGraphException {
        if (!(game instanceof WindowingQuantitativeGame)) {
            throw new IllegalGraphException("The objectif does not match. A WindowingQuantitativeGame is needed");
        }
        this.game = (WindowingQuantitativeGame) game;
        int n = game.getGraph().getVertexCount();
        l = 0;
        c = new HashMap<>(n);
        strats = new HashMap<>(n);
        for (String vertexId : game.getGraph().getVertexsId()) {
            c.put(vertexId, new int[this.game.getWindowsSize() + 1]);
            strats.put(vertexId, new WindowStrategy(this.game.getWindowsSize()));
        }
    }

    @Override
    public boolean isEnded() {
        return l >= game.getWindowsSize();
    }

    @Override
    public void compute() {
        while (!isEnded()) {
            computeAStep();
        }
    }

    @Override
    public void computeAStep() {
        if (!isEnded()) {
            l++;
            for (String vertexId : game.getGraph().getVertexsId()) {
                String[] succ = game.getGraph().getSuccessors(vertexId);
                int[] succWeight = game.getGraph().getSuccessorsWeight(vertexId);
                int[] cVertexId = c.get(vertexId);
                if (game.getGraph().getPlayer(vertexId) == Graph.PLAYER1) {
                    int max = Integer.MIN_VALUE;
                    for (int i = 0; i < succ.length; i++) {
                        int[] cSucc = c.get(succ[i]);
                        int current = oplus(succWeight[i], Math.max(cSucc[l - 1], cSucc[0]));
                        if (max < current || i == 0) {
                            max = current;
                            strats.get(vertexId).setStrategies(game.getWindowsSize() - l, succ[i]);
                        }
                    }
                    cVertexId[l] = max;
                } else {
                    int min = Integer.MAX_VALUE;
                    for (int i = 0; i < succ.length; i++) {
                        int[] cSucc = c.get(succ[i]);
                        int current = oplus(succWeight[i], Math.max(cSucc[l - 1], cSucc[0]));
                        if (min > current || i == 0) {
                            min = current;
                            strats.get(vertexId).setStrategies(game.getWindowsSize() - l, succ[i]);
                        }
                    }
                    cVertexId[l] = min;
                }
            }
        }
    }

    private int oplus(int a, int b) {
        if (a == Integer.MAX_VALUE || a == Integer.MIN_VALUE) {
            return a;
        }
        if (b == Integer.MAX_VALUE || b == Integer.MIN_VALUE) {
            return b;
        }
        int sum = a + b;
        if (sum < 0) {
            return Integer.MIN_VALUE;
        }
        return sum;
    }

    @Override
    public boolean isInWinningRegion(String vertexId) {
        return c.get(vertexId)[l] >= game.getWiningCondition();
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
    public Strategy getStrategy(String vertexId) {
        return strats.get(vertexId);
    }

    @Override
    public String getLabel(String vertexId) {
        int value = c.get(vertexId)[l];
        if (value == Integer.MAX_VALUE) {
            return "inf";
        }
        if (value == Integer.MIN_VALUE) {
            return "- inf";
        }
        return "" + value;
    }

    @Override
    public Color getVertexColor(String vertexId) {
        return null;
    }

    @Override
    public Color getEdgeColor(String srcId, String targetId) {
        if (Arrays.stream(strats.get(srcId).getSelectedEdge()).anyMatch(targetId::equals)) {
            return Color.GREEN;
        }
        return null;
    }
}
