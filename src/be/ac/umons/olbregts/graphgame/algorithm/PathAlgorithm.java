/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.umons.olbregts.graphgame.algorithm;

import be.ac.umons.olbregts.graphgame.exception.IllegalGraphException;
import be.ac.umons.olbregts.graphgame.model.Game;

import java.util.ArrayList;

/**
 * @author Simon
 */
public interface PathAlgorithm {

    public void reset(Game game) throws IllegalGraphException;

    public boolean isEnded();

    public void compute();

    public void computeAStep();

    public int getLastSelected();

    public int getDistance(int index);

    public Strategy getStrategy(int index);

    public ArrayList<Integer> getBlockedEdge(int index);
}
