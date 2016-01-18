/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.umons.olbregts.graphgame.view;

/**
 * @author Simon
 */
public class MemoryLessRenderer implements ResultRenderer {

    private GraphView graphView;

    public MemoryLessRenderer(GraphView graphView) {
        this.graphView = graphView;
    }

    @Override
    public void render() {
        graphView.updateGraph();
    }

    @Override
    public void reset() {
        graphView.resetGraph();
    }

}
