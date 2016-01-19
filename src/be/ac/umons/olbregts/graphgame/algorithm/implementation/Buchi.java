package be.ac.umons.olbregts.graphgame.algorithm.implementation;

import be.ac.umons.olbregts.graphgame.algorithm.Algorithm;
import be.ac.umons.olbregts.graphgame.algorithm.Strategy;
import be.ac.umons.olbregts.graphgame.exception.IllegalGraphException;
import be.ac.umons.olbregts.graphgame.model.Game;
import be.ac.umons.olbregts.graphgame.model.Graph;
import be.ac.umons.olbregts.graphgame.model.implementation.games.ReachibilityGame;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by Simon on 19-01-16.
 */
public class Buchi implements Algorithm {

    private List<Integer> targets;
    private ReachibilityGame game;
    private Graph g;

    @Override
    public void reset(Game game) throws IllegalGraphException {

    }

    @Override
    public boolean isEnded() {
        return false;
    }

    @Override
    public void compute() {

    }

    @Override
    public void computeAStep() {

    }

    @Override
    public Strategy getStrategy(int index) {
        return null;
    }

    @Override
    public String getLabel(int vertexId) {
        return null;
    }

    @Override
    public Color getVertexColor(int vertexId) {
        return null;
    }

    @Override
    public Color getEdgeColor(int originId, int destinationId) {
        return null;
    }
}
