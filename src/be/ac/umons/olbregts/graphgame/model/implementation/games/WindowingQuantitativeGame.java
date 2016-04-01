package be.ac.umons.olbregts.graphgame.model.implementation.games;

import be.ac.umons.olbregts.graphgame.model.Graph;

/**
 * Created by simon on 28/03/16.
 */
public class WindowingQuantitativeGame extends QuantitativeGame {

    private int windowsSize;

    public WindowingQuantitativeGame(Graph graph, int target, int windowsSize) {
        super(graph, target);
        this.windowsSize = windowsSize;
    }

    public int getWindowsSize(){
        return windowsSize;
    }
}
