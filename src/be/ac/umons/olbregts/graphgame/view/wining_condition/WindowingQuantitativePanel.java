package be.ac.umons.olbregts.graphgame.view.wining_condition;

import be.ac.umons.olbregts.graphgame.model.Game;
import be.ac.umons.olbregts.graphgame.model.Graph;
import be.ac.umons.olbregts.graphgame.model.implementation.games.WindowingQuantitativeGame;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Simon on 26-01-16.
 */
public class WindowingQuantitativePanel extends WinningPanel {

    private JSpinner windowSpinner;

    @Override
    public void initUI() {
        setLayout(new BorderLayout());
        SpinnerNumberModel model = new SpinnerNumberModel();
        model.setValue(0);
        windowSpinner = new JSpinner(model);
        add(windowSpinner, BorderLayout.CENTER);
    }

    @Override
    public Game getGame() {
        return new WindowingQuantitativeGame(getGraph(), 0, (Integer) windowSpinner.getValue());
    }

    @Override
    public Game getDefaultGame(Graph graph) {
        int windowSize = 5;// (int) (graph.getVertexCount() * 0.25);
        return new WindowingQuantitativeGame(graph, 0, windowSize);
    }

    @Override
    public boolean canExtractGame() {
        return true;
    }
}
