package be.ac.umons.olbregts.graphgame.view.wining_condition;

import be.ac.umons.olbregts.graphgame.model.Game;
import be.ac.umons.olbregts.graphgame.model.Graph;
import be.ac.umons.olbregts.graphgame.model.implementation.games.WindowReachGame;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by simon on 23/05/16.
 */
public class WindowReachPanel extends WinningPanel {

    private List<JCheckBox> vertexs;
    private JSpinner windowSpinner;

    @Override
    public void initUI() {
        removeAll();
        vertexs = new ArrayList<>();
        setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Choose the targets:");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);
        JPanel vertexSelection = new JPanel();
        vertexSelection.setLayout(new BoxLayout(vertexSelection, BoxLayout.X_AXIS));
        vertexSelection.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (String vertexId : getGraph().getVertexsId()) {
            JCheckBox box = new JCheckBox(vertexId);
            box.setAlignmentX(Component.LEFT_ALIGNMENT);
            vertexs.add(box);
            vertexSelection.add(box);
        }
        JScrollPane scrollPane = new JScrollPane(vertexSelection, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        content.add(scrollPane);
        add(content, BorderLayout.CENTER);
        JPanel windowSizePanel = new JPanel();
        windowSizePanel.setLayout(new BoxLayout(windowSizePanel, BoxLayout.X_AXIS));
        windowSizePanel.add(new JLabel("Windows size:"));
        SpinnerNumberModel model = new SpinnerNumberModel();
        model.setValue(0);
        windowSpinner = new JSpinner(model);
        windowSizePanel.add(windowSpinner);
        add(windowSizePanel, BorderLayout.SOUTH);
    }

    @Override
    public Game getGame() {
        List<String> targets = new ArrayList<>();
        for (JCheckBox checkBox : vertexs) {
            if (checkBox.isSelected()) {
                targets.add(checkBox.getText());
            }
        }
        return new WindowReachGame(getGraph(), 0, (Integer) windowSpinner.getValue(), targets);
    }

    @Override
    public Game getDefaultGame(Graph graph) {
        List<String> targets = new ArrayList<>(1);
        int targetCount = (int) (graph.getVertexCount() * 0.1);
        for (int i = 0; i < targetCount; i++) {
            targets.add(graph.getVertexsId()[0]);
        }
        return new WindowReachGame(graph, 0, Math.max(3, targetCount), targets);
    }

    @Override
    public boolean canExtractGame() {
        return true;
    }
}
