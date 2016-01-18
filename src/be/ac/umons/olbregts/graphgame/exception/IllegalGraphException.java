/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.umons.olbregts.graphgame.exception;

/**
 * @author Simon
 */
public class IllegalGraphException extends Exception {

    /**
     * Creates a new instance of
     * <code>IllegalGraphException</code> without detail message.
     */
    public IllegalGraphException() {
    }

    /**
     * Constructs an instance of
     * <code>IllegalGraphException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public IllegalGraphException(String msg) {
        super(msg);
    }
}
