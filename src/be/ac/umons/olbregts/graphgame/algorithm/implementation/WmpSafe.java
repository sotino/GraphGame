package be.ac.umons.olbregts.graphgame.algorithm.implementation;

import be.ac.umons.olbregts.graphgame.algorithm.Algorithm;
import be.ac.umons.olbregts.graphgame.algorithm.strategy.Strategy;
import be.ac.umons.olbregts.graphgame.exception.IllegalGraphException;
import be.ac.umons.olbregts.graphgame.model.Game;
import be.ac.umons.olbregts.graphgame.model.Graph;
import be.ac.umons.olbregts.graphgame.model.implementation.games.ReachibilityGame;
import be.ac.umons.olbregts.graphgame.model.implementation.games.WindowReachGame;
import be.ac.umons.olbregts.graphgame.model.implementation.games.WindowingQuantitativeGame;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by simon on 27/05/16.
 */
public class WmpSafe implements Algorithm {

    private WindowReachGame game;
    private Graph g;
    private Map<String, Strategy> strats;
    private Map<String, String> labels;
    private List<String> winningRegion;
    private int step;

    @Override
    public void reset(Game game) throws IllegalGraphException {
        if (!(game instanceof WindowReachGame)) {
            throw new IllegalGraphException("The objectif does not match. A WindowingQuantitativeGame is needed");
        }
        this.game = (WindowReachGame) game;
        int n = game.getGraph().getVertexCount();
        g = game.getGraph().clone();
        strats = new HashMap<>(n);
        labels = new HashMap<>(n);
        winningRegion = new ArrayList<>(n);
        step = 0;
    }

    @Override
    public boolean isEnded() {
        return step > 1;
    }

    @Override
    public void compute() {
        while (!isEnded()) {
            computeAStep();
        }
    }

    @Override
    public void computeAStep() {
        switch (step) {
            case 0:
                Safety safety = new Safety();
                try {
                    safety.reset(new ReachibilityGame(game.getGraph(), game.getTargetVertexs()));
                    safety.compute();
                    for (String vertexId : game.getGraph().getVertexsId()) {
                        if (!safety.isInWinningRegion(vertexId)) {
                            g.deleteVertex(vertexId);
                            strats.put(vertexId, safety.getStrategy(vertexId));
                            labels.put(vertexId, "Not safe");
                        }
                    }
                } catch (IllegalGraphException e) {
                    e.printStackTrace();
                }
                step = 1;
                break;
            case 1:
                DirectFWMP dfwmp = new DirectFWMP();
                try {
                    dfwmp.reset(new WindowingQuantitativeGame(g, 0, game.getWindowsSize()));
                    dfwmp.compute();
                    for (String vertexId : g.getVertexsId()) {
                        if (dfwmp.isInWinningRegion(vertexId)) {
                            winningRegion.add(vertexId);
                        }
                        strats.put(vertexId, dfwmp.getStrategy(vertexId));
                        labels.put(vertexId, dfwmp.getLabel(vertexId));
                    }
                } catch (IllegalGraphException e) {
                    e.printStackTrace();
                }
                step = 2;
                break;
        }
    }

    @Override
    public boolean isInWinningRegion(String vertexId) {
        return winningRegion.contains(vertexId);
    }

    @Override
    public String[] getWinningRegion() {
        return winningRegion.toArray(new String[winningRegion.size()]);
    }

    @Override
    public Strategy getStrategy(String vertexId) {
        return strats.get(vertexId);
    }

    @Override
    public String getLabel(String vertexId) {
        return labels.get(vertexId);
    }

    @Override
    public Color getVertexColor(String vertexId) {
        if (!isEnded() && !g.contains(vertexId)) {
            return Color.RED;
        }
        if (game.getTargetVertexs().contains(vertexId)) {
            return Color.YELLOW;
        }
        return null;
    }

    @Override
    public Color getEdgeColor(String srcId, String targetId) {
        if (!isEnded() && (!g.contains(srcId) || !g.contains(targetId))) {
            return Color.RED;
        }
        if (strats.get(srcId) != null && Arrays.stream(strats.get(srcId).getSelectedEdge()).anyMatch(targetId::equals)) {
            return Color.GREEN;
        }
        return null;
    }
}
