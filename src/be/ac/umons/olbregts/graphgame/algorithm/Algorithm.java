/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.umons.olbregts.graphgame.algorithm;

import be.ac.umons.olbregts.graphgame.exception.IllegalGraphException;
import be.ac.umons.olbregts.graphgame.model.Game;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author Simon
 */
public interface Algorithm {

    public void reset(Game game) throws IllegalGraphException;

    public boolean isEnded();

    public void compute();

    public void computeAStep();

    public boolean isInWinningRegion(int vertexId);

    public Strategy getStrategy(int index);

    public String getLabel(int vertexId);

    public Color getVertexColor(int vertexId);

    public Color getEdgeColor(int originId, int destinationId);

}
