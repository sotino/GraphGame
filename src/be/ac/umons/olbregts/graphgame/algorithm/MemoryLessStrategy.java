/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.umons.olbregts.graphgame.algorithm;

/**
 * @author Simon
 */
public class MemoryLessStrategy implements Strategy {

    private String vertexId;

    public MemoryLessStrategy() {
        vertexId = null;
    }

    public MemoryLessStrategy(String vertexId) {
        this.vertexId = vertexId;
    }

    public String getChoice(){
        return vertexId;
    }

    @Override
    public String printChoose() {
        if (vertexId != null) {
            return "-> " + "[V" + vertexId + ']';
        } else {
            return "/";
        }
    }

    @Override
    public String[] getSelectedEdge() {
        String[] choose = new String[(vertexId != null ? 1 : 0)];
        if (vertexId != null) {
            choose[0] = vertexId;
        }
        return choose;
    }
}
