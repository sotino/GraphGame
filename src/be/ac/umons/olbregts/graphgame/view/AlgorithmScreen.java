/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.umons.olbregts.graphgame.view;

import be.ac.umons.olbregts.graphgame.algorithm.Algorithm;
import be.ac.umons.olbregts.graphgame.algorithm.AlgorithmInfo;
import be.ac.umons.olbregts.graphgame.algorithm.AlgorithmesFactory;
import be.ac.umons.olbregts.graphgame.exception.IllegalGraphException;
import be.ac.umons.olbregts.graphgame.model.Game;
import be.ac.umons.olbregts.graphgame.model.Graph;
import be.ac.umons.olbregts.graphgame.view.wining_condition.WinningPanel;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Permet d'afficher un graphe
 *
 * @author Simon
 */
public class AlgorithmScreen extends JPanel {

    private static final long serialVersionUID = -8123406571694511514L;
    private AlgorithmInfo[] availableAlgo;
    private Algorithm algorithm;
    private Graph graph;
    private Game game;

    private JPanel algorithmSelection;
    private JComboBox<AlgorithmInfo> algoSelector;
    private JButton applyAlgo;

    private JPanel commandPanel;
    private JSlider secondSlider;
    private Timer autoStepTimer;
    private boolean autoStepStarted;
    private JButton startAutoStep;
    private JButton step;
    private JButton compute;
    private JButton reload;
    private JButton changeAlgo;

    private JPanel winning;
    private JButton validWinning;

    private GraphPanel graphPanel;

    public AlgorithmScreen(Graph graph) throws ParseException {
        this.graph = graph;
        autoStepStarted = false;
        availableAlgo = AlgorithmesFactory.getAvailableAlgorithm().toArray(new AlgorithmInfo[0]);
        initUI();
        initButtonAction();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));

        algorithmSelection = new JPanel();
        algorithmSelection.setLayout(new BoxLayout(algorithmSelection, BoxLayout.X_AXIS));
        algorithmSelection.setBorder(BorderFactory.createTitledBorder("Select the algorithm to use"));
        algoSelector = new JComboBox<>(availableAlgo);
        algorithmSelection.add(algoSelector);
        applyAlgo = new JButton("Apply");
        algorithmSelection.add(applyAlgo);
        northPanel.add(algorithmSelection);

        winning = new JPanel(new BorderLayout());
        validWinning = new JButton("Ok");
        winning.add(validWinning, BorderLayout.EAST);
        winning.setVisible(false);
        northPanel.add(winning);
        add(northPanel, BorderLayout.NORTH);

        commandPanel = new JPanel();
        commandPanel.setLayout(new BoxLayout(commandPanel, BoxLayout.Y_AXIS));
        commandPanel.add(Box.createVerticalStrut(18));
        commandPanel.add(new JLabel("Execution control"));
        commandPanel.add(Box.createVerticalStrut(12));
        commandPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(BorderLayout.WEST, commandPanel);

        JPanel autoStep = new JPanel();
        autoStep.setLayout(new BoxLayout(autoStep, BoxLayout.Y_AXIS));
        autoStep.setBorder(BorderFactory.createTitledBorder("Tenth of a seconds by step"));
        secondSlider = new JSlider(JSlider.HORIZONTAL, 0, 50, 1);
        secondSlider.setMajorTickSpacing(10);
        secondSlider.setPaintTicks(true);
        secondSlider.setPaintLabels(true);
        autoStep.add(secondSlider);
        startAutoStep = new JButton("Start");

        autoStep.add(startAutoStep);
        commandPanel.add(autoStep);
        commandPanel.add(Box.createVerticalStrut(6));

        step = new JButton("Do a step");
        commandPanel.add(step);
        commandPanel.add(Box.createVerticalStrut(6));

        compute = new JButton("Go to end");
        commandPanel.add(Box.createRigidArea(new Dimension(3, 0)));
        commandPanel.add(compute);
        commandPanel.add(Box.createVerticalStrut(18));

        reload = new JButton("Reset");
        commandPanel.add(reload);
        commandPanel.add(Box.createVerticalStrut(6));

        changeAlgo = new JButton("Change algorithm");
        commandPanel.add(changeAlgo);

        egalizeButtonSize();

        enableComponents(commandPanel, false);

        graphPanel = new GraphPanel(graph);
        graphPanel.setEditable(false);
        add(BorderLayout.CENTER, graphPanel);
        setVisible(true);
    }

    private void initButtonAction() {
        applyAlgo.addActionListener(e -> {
            AlgorithmInfo selected = (AlgorithmInfo) algoSelector.getSelectedItem();
            enableComponents(algorithmSelection, false);
            WinningPanel winningPanel = selected.getWinningPanel();
            winningPanel.setGraph(graph);
            winning.add(winningPanel, BorderLayout.CENTER);
            winning.setVisible(true);
        });

        validWinning.addActionListener(e -> {
            AlgorithmInfo selected = (AlgorithmInfo) algoSelector.getSelectedItem();
            if (selected.getWinningPanel().canExtractGame()) {
                game = selected.getWinningPanel().getGame();
                algorithm = selected.getAlgorithm();
                try {
                    algorithm.reset(game);
                    graphPanel.setAlgorithm(algorithm);
                    winning.setVisible(false);
                    enableComponents(commandPanel, true);
                } catch (IllegalGraphException e1) {
                    JOptionPane.showMessageDialog(AlgorithmScreen.this, e1.getMessage(), "Error during initialization", JOptionPane.ERROR_MESSAGE);
                    enableComponents(algorithmSelection, true);
                    winning.setVisible(false);
                }
            }
        });

        startAutoStep.addActionListener(e -> {
            if (autoStepStarted) {
                stopAutoStart();
            } else {
                startAutoStart();
            }
        });

        step.addActionListener(e -> {
            algorithm.computeAStep();
            updateGraph();
            if (algorithm.isEnded()) {
                enableComponents(commandPanel, false);
                reload.setEnabled(true);
                changeAlgo.setEnabled(true);
            }
        });

        compute.addActionListener(e -> {
            algorithm.compute();
            updateGraph();
            enableComponents(commandPanel, false);
            reload.setEnabled(true);
            changeAlgo.setEnabled(true);
        });

        reload.addActionListener(e -> {
            try {
                algorithm.reset(game);
                resetGraph();
                enableComponents(commandPanel, true);
            } catch (IllegalGraphException e1) {
                //Can't happen cause the graph was validated before
            }
        });

        changeAlgo.addActionListener(e -> {
            try {
                algorithm.reset(game);
                updateGraph();
                enableComponents(commandPanel, true);
                resetGraph();
                graphPanel.resetView();
                enableComponents(commandPanel, false);
                enableComponents(algorithmSelection, true);
            } catch (IllegalGraphException e1) {
                //Can't happen cause the graph was validated before
            }

        });

    }

    private void enableComponents(Container container, boolean enable) {
        Component[] components = container.getComponents();
        for (Component component : components) {
            component.setEnabled(enable);
            if (component instanceof Container) {
                enableComponents((Container) component, enable);
            }
        }
    }

    private void egalizeButtonSize() {
        Dimension larger = changeAlgo.getPreferredSize();
        reload.setPreferredSize(larger);
        step.setPreferredSize(larger);
        compute.setPreferredSize(larger);
        startAutoStep.setPreferredSize(larger);
        reload.setMinimumSize(larger);
        startAutoStep.setMinimumSize(larger);
        compute.setMinimumSize(larger);
        step.setMinimumSize(larger);
        reload.setMaximumSize(larger);
        step.setMaximumSize(larger);
        compute.setMaximumSize(larger);
        startAutoStep.setMaximumSize(larger);
    }

    public void updateGraph() {
        graphPanel.setAlgorithm(algorithm);
    }

    public void resetGraph() {
        graphPanel.resetView();
    }

    private void startAutoStart() {
        autoStepTimer = new Timer();
        autoStepStarted = true;
        int interval = secondSlider.getValue();
        if (interval == 0) {
            interval = 1;
        }
        enableComponents(commandPanel, false);
        startAutoStep.setEnabled(true);
        startAutoStep.setText("Stop");
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (!algorithm.isEnded()) {
                    algorithm.computeAStep();
                    updateGraph();
                } else {
                    stopAutoStart();
                }
            }
        };
        autoStepTimer.scheduleAtFixedRate(task, 0, interval * 100);

    }

    private void stopAutoStart() {
        autoStepStarted = false;
        startAutoStep.setText("Start");
        autoStepTimer.cancel();
        autoStepTimer = null;
        if (algorithm.isEnded()) {
            startAutoStep.setEnabled(false);
            reload.setEnabled(true);
            changeAlgo.setEnabled(true);
        } else {
            enableComponents(commandPanel, true);
        }
    }
}