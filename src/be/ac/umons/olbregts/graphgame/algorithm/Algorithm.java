/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.umons.olbregts.graphgame.algorithm;

import be.ac.umons.olbregts.graphgame.exception.IllegalGraphException;
import be.ac.umons.olbregts.graphgame.model.Game;

import java.awt.*;

/**
 * @author Simon
 */
public interface Algorithm {

    void reset(Game game) throws IllegalGraphException;

    boolean isEnded();

    void compute();

    void computeAStep();

    boolean isInWinningRegion(int vertexId);

    Strategy getStrategy(int index);

    String getLabel(int vertexId);

    Color getVertexColor(int vertexId);

    Color getEdgeColor(int originId, int destinationId);

}
