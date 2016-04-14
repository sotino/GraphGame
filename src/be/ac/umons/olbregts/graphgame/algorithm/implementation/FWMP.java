package be.ac.umons.olbregts.graphgame.algorithm.implementation;

import be.ac.umons.olbregts.graphgame.algorithm.Algorithm;
import be.ac.umons.olbregts.graphgame.algorithm.MemoryLessStrategy;
import be.ac.umons.olbregts.graphgame.algorithm.Strategy;
import be.ac.umons.olbregts.graphgame.exception.IllegalGraphException;
import be.ac.umons.olbregts.graphgame.model.Game;
import be.ac.umons.olbregts.graphgame.model.Graph;
import be.ac.umons.olbregts.graphgame.model.implementation.games.ReachibilityGame;
import be.ac.umons.olbregts.graphgame.model.implementation.games.WindowingQuantitativeGame;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by simon on 1/04/16.
 */
public class FWMP implements Algorithm {

    private WindowingQuantitativeGame game;
    private Graph g;
    private boolean ended;
    private DirectFWMP directFWMP;
    private Attractor attractor;
    private Map<String,Strategy> strat;
    private Map<String,String> label;

    @Override
    public void reset(Game game) throws IllegalGraphException {
        if (!(game instanceof WindowingQuantitativeGame)) {
            throw new IllegalGraphException("The objectif does not match. A WindowingQuantitativeGame is needed");
        }
        this.game = (WindowingQuantitativeGame) game;
        g = game.getGraph().clone();
        ended = false;
        strat = new HashMap<>(g.getVertexCount());
        label = new HashMap<>(g.getVertexCount());
        directFWMP = new DirectFWMP();
        attractor = new Attractor();
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
            directFWMP.reset(new WindowingQuantitativeGame(g, game.getWiningCondition(), game.getWindowsSize()));
            directFWMP.compute();
            String[] wd = directFWMP.getWinningRegion();
            for(String winningVertex: wd){
                strat.put(winningVertex,directFWMP.getStrategy(winningVertex));
            }
            ReachibilityGame attrGame = new ReachibilityGame(g, Arrays.asList(wd));
            attractor.reset(attrGame);
            attractor.compute();
            String[] subGraph = g.getVertexsId();
            boolean attrIsEmpty = true;
            for (String vertexId : subGraph) {
                if (attractor.isInWinningRegion(vertexId)) {
                    attrIsEmpty = false;
                    g.deleteVertex(vertexId);
                    label.put(vertexId,directFWMP.getLabel(vertexId));
                    if (!strat.containsKey(vertexId))
                        strat.put(vertexId, attractor.getStrategy(vertexId));
                }
            }
            if(g.getVertexCount() == 0 || attrIsEmpty){
                ended = true;
            }
        } catch (IllegalGraphException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isInWinningRegion(String vertexId) {
        return !g.contains(vertexId);
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
        return strat.get(vertexId);
    }

    @Override
    public String getLabel(String vertexId) {
        return label.get(vertexId);
    }

    @Override
    public Color getVertexColor(String vertexId) {
        return null;
    }

    @Override
    public Color getEdgeColor(String srcId, String targetId) {
        Strategy s = strat.get(srcId);
        if(s!= null && s.getSelectedEdge().length>0){
            if(targetId.equals(s.getSelectedEdge()[0])){
                return Color.GREEN;
            }
        }
        return null;
    }
}
