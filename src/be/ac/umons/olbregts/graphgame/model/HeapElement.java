/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.umons.olbregts.graphgame.model;

/**
 * @author Simon
 */
public interface HeapElement<E> extends Comparable<E> {
    int getHeapIndex();

    void setHeapIndex(int index);
}
