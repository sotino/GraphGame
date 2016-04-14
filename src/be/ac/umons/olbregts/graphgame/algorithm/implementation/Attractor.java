/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.umons.olbregts.graphgame.algorithm.implementation;

import be.ac.umons.olbregts.graphgame.algorithm.Algorithm;
import be.ac.umons.olbregts.graphgame.algorithm.MemoryLessStrategy;
import be.ac.umons.olbregts.graphgame.algorithm.Strategy;
import be.ac.umons.olbregts.graphgame.exception.IllegalGraphException;
import be.ac.umons.olbregts.graphgame.model.Game;
import be.ac.umons.olbregts.graphgame.model.Graph;
import be.ac.umons.olbregts.graphgame.model.implementation.games.ReachibilityGame;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Simon
 */
public class Attractor implements Algorithm {

    private Map<String, Boolean> attractor;
    private ReachibilityGame game;
    private Map<String, String> strat;
    private boolean ended;
    private int winningPlayer = Graph.PLAYER1;

    @Override
    public void reset(Game game) throws IllegalGraphException {
        if (!(game instanceof ReachibilityGame)) {
            throw new IllegalGraphException("The objectif does not match. A ReachibilityGame is needed");
        }
        this.game = (ReachibilityGame) game;
        int n = game.getGraph().getVertexCount();
        attractor = new HashMap<>(n);
        strat = new HashMap<>(n);
        ended = false;
        for (String vertex : this.game.getWiningCondition()) {
            attractor.put(vertex, true);
        }
    }

    public void setWinningPlayer(int winningPlayer) {
        this.winningPlayer = winningPlayer;
    }

    @Override
    public boolean isInWinningRegion(String vertexId) {
        boolean winning = attractor.containsKey(vertexId) && attractor.get(vertexId);
        return winning;
    }

    @Override
    public String[] getWinningRegion() {
        java.util.List<String> winningRegion = new ArrayList<>();
        for (Map.Entry<String, Boolean> attractors : attractor.entrySet()) {
            if (attractors.getValue()) {
                winningRegion.add(attractors.getKey());
            }
        }
        return winningRegion.toArray(new String[winningRegion.size()]);
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
        for (String vertexId : game.getGraph().getVertexsId()) {
            if (!isInWinningRegion(vertexId)) {
                String[] successor = game.getGraph().getSuccessors(vertexId);
                if (game.getGraph().getPlayer(vertexId) == winningPlayer) {
                    for (String succId : successor) {
                        if (isInWinningRegion(succId)) {
                            attractor.put(vertexId, true);
                            strat.put(vertexId, succId);
                            ended = false;
                            break;
                        }
                    }
                } else {
                    String escape = null;
                    for (String succId : successor) {
                        if (!isInWinningRegion(succId)) {
                            escape = succId;
                            break;
                        }
                    }
                    if (escape == null) {
                        attractor.put(vertexId, true);
                        ended = false;
                        if (game.getGraph().hasSuccessors(vertexId)) {
                            strat.put(vertexId, game.getGraph().getSuccessors(vertexId)[0]);
                        }
                    }
                }
            }
        }
        if (ended) {
            for (String vertexId : game.getGraph().getVertexsId()) {
                //for (int i = 0; i < game.getGraph().getVertexCount(); i++) {
                if (!strat.containsKey(vertexId)) {
                    if (isInWinningRegion(vertexId)) {
                        for (String succId : game.getGraph().getSuccessors(vertexId)) {
                            if (isInWinningRegion(succId)) {
                                strat.put(vertexId, succId);
                                break;
                            }
                        }
                    } else {
                        for (String succId : game.getGraph().getSuccessors(vertexId)) {
                            if (!isInWinningRegion(succId)) {
                                strat.put(vertexId, succId);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public Strategy getStrategy(String vertexId) {
        return new MemoryLessStrategy(strat.get(vertexId));
    }

    @Override
    public String getLabel(String vertexId) {
        if (isInWinningRegion(vertexId)) {
            return "Attr";
        } else {
            return "Not Attr";
        }
    }

    @Override
    public Color getVertexColor(String vertexId) {
        for (String target : game.getWiningCondition()) {
            if (vertexId.equals(target)) {
                return Color.YELLOW;
            }
        }
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
