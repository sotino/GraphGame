package be.ac.umons.olbregts.graphgame.algorithm.implementation;

import be.ac.umons.olbregts.graphgame.algorithm.Algorithm;
import be.ac.umons.olbregts.graphgame.algorithm.strategy.Strategy;
import be.ac.umons.olbregts.graphgame.algorithm.strategy.UnionStrategy;
import be.ac.umons.olbregts.graphgame.exception.IllegalGraphException;
import be.ac.umons.olbregts.graphgame.model.Game;
import be.ac.umons.olbregts.graphgame.model.implementation.games.WindowReachGame;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by simon on 23/05/16.
 */
public class GDEnd implements Algorithm {

    private WindowReachGame game;
    private List<String> x;
    private Map<String, UnionStrategy> strats;
    private Map<String, Boolean> isInX;
    private boolean ended;
    private ICWEnd icwEnd;

    @Override
    public void reset(Game game) throws IllegalGraphException {
        if (!(game instanceof WindowReachGame)) {
            throw new IllegalGraphException("The objectif does not match. A WindowReachGame is needed");
        }
        this.game = (WindowReachGame) game;
        int n = game.getGraph().getVertexCount();
        strats = new HashMap<>(n);
        isInX = new HashMap<>(n);
        ended = false;
        x = new ArrayList<>(this.game.getTargetVertexs());
        for (String vertedId : game.getGraph().getVertexsId()) {
            strats.put(vertedId, new UnionStrategy());
        }
        for (String vertexId : this.game.getTargetVertexs()) {
            isInX.put(vertexId, true);
        }
        icwEnd = new ICWEnd();
    }

    @Override
    public boolean isInWinningRegion(String vertexId) {
        return x.contains(vertexId);
    }

    @Override
    public String[] getWinningRegion() {
        return x.toArray(new String[x.size()]);
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

    @Override
    public void computeAStep() {
        ended = true;
        try {
            icwEnd.reset(new WindowReachGame(game.getGraph(), 0, game.getWindowsSize(), x));
            icwEnd.compute();
            String[] x2 = icwEnd.getWinningRegion();
            for (String vertexId : x2) {
                if (!isInX.containsKey(vertexId)) {
                    x.add(vertexId);
                    isInX.put(vertexId, true);
                    strats.get(vertexId).addStrategy(icwEnd.getStrategy(vertexId));
                    ended = false;
                }
            }
            if (ended) {
                for (String vertexId : game.getGraph().getVertexsId()) {
                    if (!isInX.containsKey(vertexId)) {
                        strats.get(vertexId).addStrategy(icwEnd.getStrategy(vertexId));
                    }
                }
            }
        } catch (IllegalGraphException e) {
            //Can't happen because if the game is valid for GDEnd then it's valid for ICWEnd
        }
    }

    @Override
    public Strategy getStrategy(String vertexId) {
        return strats.get(vertexId);
    }

    @Override
    public String getLabel(String vertexId) {
        return null;
    }

    @Override
    public Color getVertexColor(String vertexId) {
        if (game.getTargetVertexs().contains(vertexId)) {
            return Color.YELLOW;
        }
        return null;
    }

    @Override
    public Color getEdgeColor(String srcId, String targetId) {
        if (strats != null && strats.get(srcId) != null) {
            if (Arrays.stream(strats.get(srcId).getSelectedEdge()).anyMatch(targetId::equals)) {
                return Color.GREEN;
            }
        }
        return null;
    }
}
