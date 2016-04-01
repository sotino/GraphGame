package be.ac.umons.olbregts.graphgame.view.wining_condition;

import be.ac.umons.olbregts.graphgame.model.Game;
import be.ac.umons.olbregts.graphgame.model.Graph;

import javax.swing.*;

/**
 * Created by Simon on 19-01-16.
 */
public abstract class WinningPanel extends JPanel {

    private Graph graph;

    public WinningPanel() {
        super();
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
        initUI();
    }

    public abstract void initUI();

    public abstract Game getGame();

    public abstract Game getDefaultGame(Graph graph);

    public abstract boolean canExtractGame();


}
