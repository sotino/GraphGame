package be.ac.umons.olbregts.graphgame.algorithm.implementation;

import be.ac.umons.olbregts.graphgame.algorithm.Algorithm;
import be.ac.umons.olbregts.graphgame.algorithm.MemoryLessStrategy;
import be.ac.umons.olbregts.graphgame.algorithm.Strategy;
import be.ac.umons.olbregts.graphgame.exception.IllegalGraphException;
import be.ac.umons.olbregts.graphgame.model.Game;
import be.ac.umons.olbregts.graphgame.model.Graph;
import be.ac.umons.olbregts.graphgame.model.implementation.games.WindowingQuantitativeGame;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by simon on 28/03/16.
 */
public class GoodWin implements Algorithm {

    private WindowingQuantitativeGame game;
    private int l;
    private Map<String,int[]> c;
    private Map<String,String> strat;

    @Override
    public void reset(Game game) throws IllegalGraphException {
        if (!(game instanceof WindowingQuantitativeGame)) {
            throw new IllegalGraphException("The objectif does not match. A WindowingQuantitativeGame is needed");
        }
        this.game = (WindowingQuantitativeGame) game;
        int n = game.getGraph().getVertexCount();
        l = 0;
        c = new HashMap<>(n);
        strat = new HashMap<>(n);
        for(String vertexId: game.getGraph().getVertexsId()){
            c.put(vertexId,new int[this.game.getWindowsSize() + 1]);
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
            for(String vertexId: game.getGraph().getVertexsId()){
            //for (int u = 0; u < n; u++) {
                String[] succ = game.getGraph().getSuccessors(vertexId);
                int[] succWeight = game.getGraph().getSuccessorsWeight(vertexId);
                int[] cVertexId = c.get(vertexId);
                if (game.getGraph().getPlayer(vertexId) == Graph.PLAYER1) {
                    int max = Integer.MIN_VALUE;
                    for (int i = 0; i < succ.length; i++) {
                        int[] cSucc = c.get(succ[i]);
                        int current = succWeight[i] + Math.max(cSucc[l - 1], cSucc[0]);
                        if (max < current) {
                            max = current;
                            strat.put(vertexId,succ[i]);
                        }
                    }
                    cVertexId[l] = max;
                } else {
                    int min = Integer.MAX_VALUE;
                    for (int i = 0; i < succ.length; i++) {
                        int[] cSucc = c.get(succ[i]);
                        int current = succWeight[i] + Math.max(cSucc[l - 1], cSucc[0]);
                        if (min > current) {
                            min = current;
                            strat.put(vertexId,succ[i]);
                        }
                    }
                    cVertexId[l] = min;
                }
            }
        }
    }

    @Override
    public boolean isInWinningRegion(String vertexId) {
        return strat.containsKey(vertexId) && c.get(vertexId)[l] >= game.getWiningCondition();
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
        return "" + c.get(vertexId)[l];
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
