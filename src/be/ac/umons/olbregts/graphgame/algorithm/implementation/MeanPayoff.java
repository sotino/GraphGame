package be.ac.umons.olbregts.graphgame.algorithm.implementation;

import be.ac.umons.olbregts.graphgame.algorithm.Algorithm;
import be.ac.umons.olbregts.graphgame.algorithm.MemoryLessStrategy;
import be.ac.umons.olbregts.graphgame.algorithm.Strategy;
import be.ac.umons.olbregts.graphgame.exception.IllegalGraphException;
import be.ac.umons.olbregts.graphgame.model.Game;
import be.ac.umons.olbregts.graphgame.model.Graph;
import be.ac.umons.olbregts.graphgame.model.implementation.games.QuantitativeGame;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Simon on 26-01-16.
 */
public class MeanPayoff implements Algorithm {

    private Map<String,Double> value;
    private Map<String,String> strat;
    private long k;
    private long kMax;
    private QuantitativeGame game;

    @Override
    public void reset(Game game) throws IllegalGraphException {
        if (!(game instanceof QuantitativeGame)) {
            throw new IllegalGraphException("The objectif does not match. A QuantitativeGame is needed");
        }
        this.game = (QuantitativeGame) game;
        int n = game.getGraph().getVertexCount();
        value = new HashMap<>(n);
        strat = new HashMap<>(n);
        int d = Integer.MIN_VALUE;
        for(String vertexId : game.getGraph().getVertexsId()){
            int[] succWeight = game.getGraph().getSuccessorsWeight(vertexId);
            if (succWeight.length < 1) {
                throw new IllegalGraphException("No dead end allowed. The vertex " + vertexId + " have no successor");
            }
            for (int w : succWeight) {
                d = Math.max(d,Math.abs(w));
            }
            value.put(vertexId,0.);
        }
        k = 0;
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
        Map<String,Double> temp = new HashMap<>(value.size());
        for(String vertexId: game.getGraph().getVertexsId()){
        //for (int v = 0; v < game.getGraph().getVertexCount(); v++) {
            String[] succ = game.getGraph().getSuccessors(vertexId);
            int[] succWeight = game.getGraph().getSuccessorsWeight(vertexId);
            int player = game.getGraph().getPlayer(vertexId);
            double newValue = (player == Graph.PLAYER1 ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
            for (int i = 0; i < succ.length; i++) {
                double s = succWeight[i] + value.get(succ[i]);
                if (player == Graph.PLAYER1) {
                    if (newValue < s) {
                        newValue = s;
                        strat.put(vertexId,succ[i]);
                    }
                } else {
                    if (newValue > s) {
                        newValue = s;
                        strat.put(vertexId,succ[i]);
                    }
                }
            }
            temp.put(vertexId,newValue);
        }
        value = temp;
        //TODO check if k++ before the is ended and if v=.../k is correct
        k++;
        if (isEnded()) {
            int n = game.getGraph().getVertexCount();
            for(Map.Entry<String,Double> valueEntry : value.entrySet()){
            //for (int i = 0; i < value.length; i++) {
                double v = valueEntry.getValue() / k;
                double upperBound = v + (1 * 1. / (2 * n * (n - 1)));
                double lowerBound = v - (1 * 1. / (2 * n * (n - 1)));
                for (int l = 1; l <= n; l++) {
                    double t = Math.ceil(v * l) / l;
                    if (lowerBound < t && t < upperBound) {
                        valueEntry.setValue(t);
                        //value[i] = t;
                        break;
                    }
                    t = Math.floor(v * l) / l;
                    if (lowerBound < t && t < upperBound) {
                        valueEntry.setValue(t);
                        //value[i] = t;
                        break;
                    }
                }
            }
        }
    }

    @Override
    public boolean isInWinningRegion(String vertexId) {
        return value.get(vertexId) >= game.getWiningCondition();
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
    public Strategy getStrategy(String vertexId) {
        return new MemoryLessStrategy(strat.get(vertexId));
    }

    @Override
    public String getLabel(String vertexId) {
        double v = value.get(vertexId);
        if (v == Long.MAX_VALUE) {
            return "+ inf";
        } else if (v == Long.MIN_VALUE) {
            return "- inf";
        } else {
            return "" + v;
        }
    }

    @Override
    public Color getVertexColor(String vertexId) {
        return null;
    }

    @Override
    public Color getEdgeColor(String srcId, String targetId) {
        if (targetId.equals(strat.get(srcId))) {
            return Color.GREEN;
        }
        return null;
    }
}
