/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.umons.olbregts.graphgame.algorithm;

/**
 * @author Simon
 */
public class MemoryLessStrategy implements Strategy {

    private int vertexId;

    public MemoryLessStrategy() {
        vertexId = -1;
    }

    public MemoryLessStrategy(int vertexId) {
        this.vertexId = vertexId;
    }

    @Override
    public String printChoose() {
        if (vertexId >= 0) {
            return "-> " + "[V" + (vertexId + 1) + ']';
        } else {
            return "/";
        }
    }

    @Override
    public int[] getSelectedEdge() {
        int[] choose = new int[(vertexId >= 0 ? 1 : 0)];
        if (vertexId >= 0) {
            choose[0] = vertexId;
        }
        return choose;
    }
}
