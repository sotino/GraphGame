package be.ac.umons.olbregts.graphgame.view.wining_condition;

import be.ac.umons.olbregts.graphgame.model.Game;
import be.ac.umons.olbregts.graphgame.model.Graph;
import be.ac.umons.olbregts.graphgame.model.implementation.games.ReachibilityGame;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Simon on 19-01-16.
 */
public class ReachabilityPanel extends WinningPanel {

    private List<JCheckBox> vertexs;

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
    }

    @Override
    public Game getGame() {
        List<String> targets = new ArrayList<>();
        for (JCheckBox checkBox : vertexs) {
            if (checkBox.isSelected()) {
                targets.add(checkBox.getText());
            }
        }
        return new ReachibilityGame(getGraph(), targets);
    }

    @Override
    public Game getDefaultGame(Graph graph) {
        List<String> targets = new ArrayList<>(1);
        if (graph.getVertexsId().length != 0) {
            targets.add(graph.getVertexsId()[0]);
        }
        return new ReachibilityGame(graph, targets);
    }

    @Override
    public boolean canExtractGame() {
        return true;
    }
}
