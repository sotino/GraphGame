package be.ac.umons.olbregts.graphgame.algorithm.implementation;

import be.ac.umons.olbregts.graphgame.algorithm.Algorithm;
import be.ac.umons.olbregts.graphgame.algorithm.Strategy;
import be.ac.umons.olbregts.graphgame.exception.IllegalGraphException;
import be.ac.umons.olbregts.graphgame.model.Game;
import be.ac.umons.olbregts.graphgame.model.Graph;
import be.ac.umons.olbregts.graphgame.model.implementation.games.ReachibilityGame;
import be.ac.umons.olbregts.graphgame.model.implementation.games.WindowingQuantitativeGame;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by simon on 30/03/16.
 */
public class DirectFWMP implements Algorithm {

    private WindowingQuantitativeGame game;
    private Graph g;
    private boolean ended;
    private GoodWin goodWin;
    private List<String> winGW;
    private Map<String,Strategy> removedStrat;
    private Map<String,String> removedLabel;

    @Override
    public void reset(Game game) throws IllegalGraphException {
        if (!(game instanceof WindowingQuantitativeGame)) {
            throw new IllegalGraphException("The objectif does not match. A WindowingQuantitativeGame is needed");
        }
        this.game = (WindowingQuantitativeGame) game;
        g = game.getGraph().clone();
        ended = false;
        winGW = new ArrayList<>(g.getVertexCount());
        removedStrat = new HashMap<>(g.getVertexCount());
        removedLabel = new HashMap<>(g.getVertexCount());
    }

    @Override
    public boolean isEnded() {
        return ended;
    }

    @Override
    public void compute() {
        while (!isEnded()) {
            computeAStep();
        }
    }

    @Override
    public void computeAStep() {
        try {
            goodWin = new GoodWin();
            goodWin.reset(new WindowingQuantitativeGame(g, game.getWiningCondition(), game.getWindowsSize()));
            goodWin.compute();
            winGW.clear();
            List<String> looseGW = new ArrayList<>();
            for(String vertexId:g.getVertexsId()){
                if (goodWin.isInWinningRegion(vertexId)) {
                    winGW.add(vertexId);
                } else {
                    looseGW.add(vertexId);
                }
            }
            if (winGW.size() == g.getVertexCount() || winGW.isEmpty()) {
                ended = true;
            } else {
                Safety safety = new Safety();
                safety.reset(new ReachibilityGame(g, looseGW));
                winGW.clear();
                for(String vertexId:g.getVertexsId()){
                    if (!safety.isInWinningRegion(vertexId)) {
                        removedStrat.put(vertexId,goodWin.getStrategy(vertexId));
                        removedLabel.put(vertexId,goodWin.getLabel(vertexId));
                        g.deleteVertex(vertexId);
                    }
                }
            }
        } catch (IllegalGraphException e) {
            //Can't happen because when the graph is valid for DirectFWMP it is valid for GoodWin
        }
    }

    @Override
    public boolean isInWinningRegion(String vertexId) {
        return winGW.contains(vertexId);
    }

    @Override
    public String[] getWinningRegion(){
        return winGW.toArray(new String[winGW.size()]);
    }

    @Override
    public Strategy getStrategy(String vertexId) {
        Strategy s = removedStrat.get(vertexId);
        if (s != null)
            return s;
        if (goodWin == null)
            return null;
        return goodWin.getStrategy(vertexId);
    }

    @Override
    public String getLabel(String vertexId) {
        String label = removedLabel.get(vertexId);
        if (label != null)
            return label;
        if (goodWin == null)
            return null;
        return goodWin.getLabel(vertexId);
    }

    @Override
    public Color getVertexColor(String vertexId) {
        if(! g.contains(vertexId)){
            return Color.RED;
        }
        return null;
    }

    @Override
    public Color getEdgeColor(String srcId, String targetId) {
        if(!g.contains(srcId) || !g.contains(targetId)){
            return Color.RED;
        }
        if (goodWin == null)
            return null;
        return goodWin.getEdgeColor(srcId, targetId);
    }
}
