/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.umons.olbregts.graphgame.model;

/**
 * @author Simon
 */
public interface Game<E> {

    public Graph getGraph();

    public E getWiningCondition();
}
