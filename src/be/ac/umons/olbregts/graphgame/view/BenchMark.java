package be.ac.umons.olbregts.graphgame.view;

import be.ac.umons.olbregts.graphgame.algorithm.Algorithm;
import be.ac.umons.olbregts.graphgame.algorithm.AlgorithmInfo;
import be.ac.umons.olbregts.graphgame.algorithm.AlgorithmesFactory;
import be.ac.umons.olbregts.graphgame.exception.IllegalGraphException;
import be.ac.umons.olbregts.graphgame.io.GraphLoader;
import be.ac.umons.olbregts.graphgame.model.Game;
import be.ac.umons.olbregts.graphgame.model.Graph;
import be.ac.umons.olbregts.graphgame.model.implementation.objectoriented.GraphObjectOriented;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.List;
import java.util.Random;

/**
 * Created by simon on 31/03/16.
 */
public class BenchMark extends JPanel {

    private JSpinner nodeStartCount;
    private JSpinner nodeMaxCount;
    private JSpinner nodeStep;
    private JSpinner minNodeOutDegre;
    private JSpinner maxNodeOutDegre;
    private JSpinner minEdgeCost;
    private JSpinner maxEdgeCost;
    private JList<AlgorithmInfo> algoSelector;
    private final JProgressBar executionProgression;

    public BenchMark() {
        super();
        setLayout(new BorderLayout());
        JPanel paramPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHEAST;
        c.insets = new Insets(0, 0, 5, 2);


        c.gridy = 0;
        c.gridx = 0;
        paramPanel.add(new JLabel("Start node count"), c);
        SpinnerNumberModel model = new SpinnerNumberModel();
        model.setValue(10);
        nodeStartCount = new JSpinner(model);
        c.gridx = 1;
        paramPanel.add(nodeStartCount, c);

        c.gridy = 1;
        c.gridx = 0;
        paramPanel.add(new JLabel("Max node count"), c);
        model = new SpinnerNumberModel();
        model.setValue(100);
        nodeMaxCount = new JSpinner(model);
        c.gridx = 1;
        paramPanel.add(nodeMaxCount, c);

        c.gridy = 2;
        c.gridx = 0;
        paramPanel.add(new JLabel("Node count increment"), c);
        model = new SpinnerNumberModel();
        model.setValue(10);
        nodeStep = new JSpinner(model);
        c.gridx = 1;
        paramPanel.add(nodeStep, c);

        c.gridy = 3;
        c.gridx = 0;
        paramPanel.add(new JLabel("Node min out degree"), c);
        model = new SpinnerNumberModel();
        model.setValue(1);
        minNodeOutDegre = new JSpinner(model);
        c.gridx = 1;
        paramPanel.add(minNodeOutDegre, c);

        c.gridy = 4;
        c.gridx = 0;
        paramPanel.add(new JLabel("Node max out degree"), c);
        model = new SpinnerNumberModel();
        model.setValue(4);
        maxNodeOutDegre = new JSpinner(model);
        c.gridx = 1;
        paramPanel.add(maxNodeOutDegre, c);

        c.gridy = 5;
        c.gridx = 0;
        paramPanel.add(new JLabel("Edge min cost"), c);
        model = new SpinnerNumberModel();
        model.setValue(-25);
        minEdgeCost = new JSpinner(model);
        c.gridx = 1;
        paramPanel.add(minEdgeCost, c);

        c.gridy = 6;
        c.gridx = 0;
        paramPanel.add(new JLabel("Edge max cost"), c);
        model = new SpinnerNumberModel();
        model.setValue(25);
        maxEdgeCost = new JSpinner(model);
        c.gridx = 1;
        paramPanel.add(maxEdgeCost, c);

        c.gridy = 7;
        c.gridx = 0;
        paramPanel.add(new JLabel("Algorithms to test"), c);
        AlgorithmInfo[] availableAlgo = new AlgorithmInfo[0];
        try {
            availableAlgo = AlgorithmesFactory.getAvailableAlgorithm().toArray(availableAlgo);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        algoSelector = new JList<>(availableAlgo);
        //algoSelector.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        algoSelector.setVisibleRowCount(5);
        JScrollPane listScroller = new JScrollPane(algoSelector);
        c.gridx = 1;
        paramPanel.add(listScroller, c);

        JButton launch = new JButton("Launch");
        launch.addActionListener(e -> {
            launchAction();
        });
        //paramPanel.add(Box.createHorizontalBox());
        JPanel launchPanel = new JPanel();
        launchPanel.setLayout(new BoxLayout(launchPanel, BoxLayout.X_AXIS));
        launchPanel.add(Box.createHorizontalGlue());
        launchPanel.add(launch);
        c.gridy = 8;
        c.gridx = 1;
        paramPanel.add(launchPanel, c);

        add(paramPanel, BorderLayout.NORTH);
        add(Box.createVerticalGlue(), BorderLayout.CENTER);
        executionProgression = new JProgressBar(0, 100);
        executionProgression.setVisible(false);
        add(executionProgression, BorderLayout.SOUTH);
    }

    private void launchAction() {
        int start = (int) nodeStartCount.getValue();
        int end = (int) nodeMaxCount.getValue();
        int step = (int) nodeStep.getValue();
        int executionCount = (end - start) / step;
        List<AlgorithmInfo> selectedAlgo = algoSelector.getSelectedValuesList();
        executionCount *= selectedAlgo.size();
        executionProgression.setMaximum(executionCount);
        executionProgression.setValue(0);
        executionProgression.setVisible(true);
        int minOutDegree = (int) minNodeOutDegre.getValue();
        int maxOutDegree = (int) maxNodeOutDegre.getValue();
        int minCost = (int) minEdgeCost.getValue();
        int maxCost = (int) maxEdgeCost.getValue();
        new algorithmTestTask(start, end, step, minOutDegree, maxOutDegree, minCost, maxCost, selectedAlgo).execute();

    }

    class algorithmTestTask extends SwingWorker<Void, Integer> {

        int start;
        int end;
        int step;
        int minOutDegree;
        int maxOutDegree;
        int minCost;
        int maxCost;
        int deltaCost;
        List<AlgorithmInfo> selectedAlgo;

        public algorithmTestTask(int start, int end, int step, int minOutDegree, int maxOutDegree, int minCost, int maxCost, List<AlgorithmInfo> selectedAlgo) {
            this.start = start;
            this.end = end;
            this.step = step;
            this.minOutDegree = minOutDegree;
            this.maxOutDegree = maxOutDegree;
            this.minCost = minCost;
            this.maxCost = maxCost;
            deltaCost = maxCost - minCost;
            this.selectedAlgo = selectedAlgo;
        }

        @Override
        protected void process(List<Integer> stepDone) {
            int lastStep = stepDone.get(stepDone.size() - 1);
            executionProgression.setValue(lastStep);
        }

        @Override
        protected Void doInBackground() {
            int testCount = 0;
            for (int size = start; size <= end; size += step) {
                System.out.println("Graph size: " + size);
                Graph graph = generateGraph(size);
                for (AlgorithmInfo algoInfo : selectedAlgo) {
                    try {
                        Algorithm alg = algoInfo.getAlgorithm();
                        Game game = algoInfo.getWinningPanel().getDefaultGame(graph);
                        final long startTime = System.nanoTime();
                        alg.reset(game);
                        alg.compute();
                        final long endTime = System.nanoTime();
                        final long timeElapsed = (endTime - startTime) / 1000000;
                        System.out.println("  Algo " + algoInfo.getName() + ": " + timeElapsed);
                    } catch (IllegalGraphException e) {
                        System.out.println("  Algo " + algoInfo.getName() + " failled:" + e.getMessage());
                        System.exit(-1);
                    }
                    testCount++;
                    publish(testCount);
                }
            }
            return null;
        }

        private Graph generateGraph(int size) {
            GraphObjectOriented graph = new GraphObjectOriented();
            Random rnd = new Random();
            /** Generation of the vertex */
            for (int i = 0; i < size; i++) {
                int player = (rnd.nextInt(2) == 0 ? Graph.PLAYER1 : Graph.PLAYER2);
                graph.addVertex(""+i,player);
            }

            /** Connect each edge to one of the previous
             *  to have a spanning tree
             *  to ensure to have a connected graph */
            for (int i = 1; i < size; i++) {
                int succ = (i == 1 ? 0 : rnd.nextInt(i - 1));
                int cost = minCost + rnd.nextInt(deltaCost + 1);
                graph.addEdge(""+i, ""+succ, cost);
            }

            /** Add random edge for density */
            int minSucc = Math.max(1,minOutDegree);
            int maxSucc = Math.max(1,maxOutDegree);
            int delta = maxSucc - minSucc;
            for (int i = 0; i < size; i++) {
                int succCount = minSucc + rnd.nextInt(delta + 1);
                for (int j = 0; j < succCount; j++) {
                    int succ = rnd.nextInt(size);
                    int cost = minCost + rnd.nextInt(deltaCost + 1);
                    graph.addEdge(""+i,""+ succ, cost);
                }
            }

            try {
                File folder = new File("generated");
                if(!folder.exists()){
                    folder.mkdir();
                }
                GraphLoader.saveGraph(graph, "generated/generated_" + size + ".graph");
            } catch (UnsupportedEncodingException | FileNotFoundException e) {
                e.printStackTrace();
            }
            return graph;
        }

        @Override
        protected void done() {

        }
    }
}
