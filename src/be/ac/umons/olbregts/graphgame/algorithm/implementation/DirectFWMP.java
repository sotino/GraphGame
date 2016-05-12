package be.ac.umons.olbregts.graphgame.algorithm.implementation;

import be.ac.umons.olbregts.graphgame.algorithm.Algorithm;
import be.ac.umons.olbregts.graphgame.algorithm.MemoryLessStrategy;
import be.ac.umons.olbregts.graphgame.algorithm.Strategy;
import be.ac.umons.olbregts.graphgame.algorithm.WindowStrategy;
import be.ac.umons.olbregts.graphgame.exception.IllegalGraphException;
import be.ac.umons.olbregts.graphgame.model.Game;
import be.ac.umons.olbregts.graphgame.model.Graph;
import be.ac.umons.olbregts.graphgame.model.implementation.games.ReachibilityGame;
import be.ac.umons.olbregts.graphgame.model.implementation.games.WindowingQuantitativeGame;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by simon on 30/03/16.
 */
public class DirectFWMP implements Algorithm {

    private WindowingQuantitativeGame game;
    private Graph g;
    private boolean ended;
    private GoodWin goodWin;
    private List<String> winGW;
    private Map<String,Strategy> goodwinStrat;
    private Map<String,String> goodwinLabel;

    @Override
    public void reset(Game game) throws IllegalGraphException {
        if (!(game instanceof WindowingQuantitativeGame)) {
            throw new IllegalGraphException("The objectif does not match. A WindowingQuantitativeGame is needed");
        }
        this.game = (WindowingQuantitativeGame) game;
        g = game.getGraph().clone();
        ended = false;
        winGW = new ArrayList<>(g.getVertexCount());
        goodwinStrat = new HashMap<>(g.getVertexCount());
        goodwinLabel = new HashMap<>(g.getVertexCount());
        goodWin = null;
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
                goodwinStrat.put(vertexId,goodWin.getStrategy(vertexId));
                goodwinLabel.put(vertexId,goodWin.getLabel(vertexId));
            }
            if (winGW.size() == g.getVertexCount() || winGW.isEmpty()) {
                ended = true;
            } else {
                Safety safety = new Safety();
                safety.reset(new ReachibilityGame(g, looseGW));
                safety.compute();
                //winGW.clear();
                for(String vertexId:g.getVertexsId()){
                    if (!safety.isInWinningRegion(vertexId)) {
                        WindowStrategy ws = (WindowStrategy) goodwinStrat.get(vertexId);
                        MemoryLessStrategy safetyStrat = (MemoryLessStrategy) safety.getStrategy(vertexId);
                        if(!looseGW.contains(vertexId)) {
                            // if the windows is opened in [vertexId] then try to reach safety
                            ws.setStrategies(0, safetyStrat.getChoice());
                        }
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
        return goodwinStrat.get(vertexId);
    }

    @Override
    public String getLabel(String vertexId) {
        return goodwinLabel.get(vertexId);
    }

    @Override
    public Color getVertexColor(String vertexId) {
        if(! isEnded() && ! g.contains(vertexId)){
            return Color.RED;
        }
        return null;
    }

    @Override
    public Color getEdgeColor(String srcId, String targetId) {
        if(! isEnded() && (!g.contains(srcId) || !g.contains(targetId))){
            return Color.RED;
        }
        if(goodwinStrat.get(srcId) != null && Arrays.stream(goodwinStrat.get(srcId).getSelectedEdge()).anyMatch(targetId::equals)){
            return Color.GREEN;
        }
        return null;
    }
}
