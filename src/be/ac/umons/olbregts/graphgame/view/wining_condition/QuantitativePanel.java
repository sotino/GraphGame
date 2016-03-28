package be.ac.umons.olbregts.graphgame.view.wining_condition;

import be.ac.umons.olbregts.graphgame.model.Game;
import be.ac.umons.olbregts.graphgame.model.implementation.games.QuantitativeGame;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Simon on 26-01-16.
 */
public class QuantitativePanel extends WinningPanel {

    private JSpinner spinner;

    @Override
    public void initUI() {
        setLayout(new BorderLayout());
        SpinnerNumberModel model = new SpinnerNumberModel();
        model.setValue(0);
        spinner = new JSpinner(model);
        add(spinner, BorderLayout.CENTER);
    }

    @Override
    public Game getGame() {
        return new QuantitativeGame(getGraph(), (Integer) spinner.getValue());
    }

    @Override
    public boolean canExtractGame() {
        return true;
    }
}
