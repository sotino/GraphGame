/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.umons.olbregts.graphgame.view;

import be.ac.umons.olbregts.graphgame.algorithm.PathAlgorithm;
import be.ac.umons.olbregts.graphgame.algorithm.implementation.Attractor;
import be.ac.umons.olbregts.graphgame.algorithm.implementation.DijkstraTP;
import be.ac.umons.olbregts.graphgame.algorithm.implementation.ValueIteration;
import be.ac.umons.olbregts.graphgame.exception.IllegalGraphException;
import be.ac.umons.olbregts.graphgame.model.implementation.objectoriented.GraphObjectOriented;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Permet d'afficher un graphe
 *
 * @author Simon
 */
public class GraphView extends JPanel {

    private static final long serialVersionUID = -8123406571694511514L;
    private AlgoSelectionItem[] availableAlgo;
    private JButton applyAlgo;
    private JButton startAutoStep;
    private JButton step;
    private JButton compute;
    private JButton reload;
    private JButton changeAlgo;
    private JComboBox<AlgoSelectionItem> algoSelector;
    private JSlider secondSlider;
    private JPanel commandPanel;
    private JPanel algorithmSelection;
    private GraphObjectOriented graph;
    private GraphPanel graphPanel;
    private PathAlgorithm pathAlgorithm;
    private ResultRenderer renderer;
    private boolean autoStepStarted;
    private Timer autoStepTimer;

    public GraphView(GraphObjectOriented graph) {
        this.graph = graph;
        autoStepStarted = false;
        initAlgorithmList();
        initUI();
        initButtonAction();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        algorithmSelection = new JPanel();
        algorithmSelection.setLayout(new BoxLayout(algorithmSelection, BoxLayout.X_AXIS));
        algorithmSelection.setBorder(BorderFactory.createTitledBorder("Select the algorithm to use"));
        algoSelector = new JComboBox<>(availableAlgo);
        algorithmSelection.add(algoSelector);
        applyAlgo = new JButton("Apply");
        algorithmSelection.add(applyAlgo);
        add(BorderLayout.NORTH, algorithmSelection);


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
        applyAlgo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
             //   try {
                    AlgoSelectionItem selected = (AlgoSelectionItem) algoSelector.getSelectedItem();
                    pathAlgorithm = selected.algo;
                    renderer = selected.renderer;
                   // pathAlgorithm.reset(graph);
                    enableComponents(commandPanel, true);
                    enableComponents(algorithmSelection, false);
              /*  } catch (IllegalGraphException ex) {
                    JOptionPane.showMessageDialog(GraphView.this, ex.getMessage(), "Impossible to load the graph", JOptionPane.ERROR_MESSAGE);
                }*/
            }
        });

        startAutoStep.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (autoStepStarted) {
                    stopAutoStart();
                } else {
                    startAutoStart();
                }
            }
        });

        step.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pathAlgorithm.computeAStep();
                renderer.render();
                if (pathAlgorithm.isEnded()) {
                    enableComponents(commandPanel, false);
                    reload.setEnabled(true);
                    changeAlgo.setEnabled(true);
                }
            }
        });

        compute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pathAlgorithm.compute();
                renderer.render();
                enableComponents(commandPanel, false);
                reload.setEnabled(true);
                changeAlgo.setEnabled(true);
            }
        });

        reload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
             //   try {
                   // pathAlgorithm.reset(graph);
                    renderer.reset();
                    enableComponents(commandPanel, true);
           //     } catch (IllegalGraphException ex) {
             //       JOptionPane.showMessageDialog(GraphView.this, ex.getMessage(), "Impossible to load the graph", JOptionPane.ERROR_MESSAGE);
               // }
            }
        });

        changeAlgo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
             //   try {
               //     pathAlgorithm.reset(graph);
                    renderer.reset();
                    enableComponents(commandPanel, true);
               // } catch (IllegalGraphException ex) {
                 //   JOptionPane.showMessageDialog(GraphView.this, ex.getMessage(), "Impossible to load the graph", JOptionPane.ERROR_MESSAGE);
               // }
                GraphView.this.graphPanel.resetView();

                enableComponents(commandPanel, false);
                enableComponents(algorithmSelection, true);
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
        graphPanel.updateGraph(pathAlgorithm);
    }

    public void resetGraph() {
        GraphView.this.graphPanel.resetView();
    }

    private void initAlgorithmList() {
        availableAlgo = new AlgoSelectionItem[3];
        availableAlgo[0] = new AlgoSelectionItem("DijkstraTp", new DijkstraTP(), new MemoryLessRenderer(this));
        ValueIteration valueIteration = new ValueIteration();
        availableAlgo[1] = new AlgoSelectionItem("Value iteration", valueIteration, new ValueIterationRenderer(this, graph, valueIteration));
        availableAlgo[2] = new AlgoSelectionItem("Attractor", new Attractor(), new MemoryLessRenderer(this));

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
                if (!pathAlgorithm.isEnded()) {
                    pathAlgorithm.computeAStep();
                    renderer.render();
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
        if (pathAlgorithm.isEnded()) {
            startAutoStep.setEnabled(false);
            reload.setEnabled(true);
            changeAlgo.setEnabled(true);
        } else {
            enableComponents(commandPanel, true);
        }
    }

    private class AlgoSelectionItem {

        private String label;
        private PathAlgorithm algo;
        private ResultRenderer renderer;

        public AlgoSelectionItem(String label, PathAlgorithm algo, ResultRenderer renderer) {
            this.label = label;
            this.algo = algo;
            this.renderer = renderer;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}