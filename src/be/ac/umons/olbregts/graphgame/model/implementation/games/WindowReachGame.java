package be.ac.umons.olbregts.graphgame.model.implementation.games;

import be.ac.umons.olbregts.graphgame.model.Graph;

import java.util.Arrays;
import java.util.List;

/**
 * Created by simon on 23/05/16.
 */
public class WindowReachGame extends WindowingQuantitativeGame {

    private List<String> targetVertexs;

    public WindowReachGame(Graph graph, int target, int windowsSize, String[] targets) {
        this(graph, target, windowsSize, Arrays.asList(targets));
    }

    public WindowReachGame(Graph graph, int target, int windowsSize, List<String> targets) {
        super(graph, target, windowsSize);
        targetVertexs = targets;
    }

    public List<String> getTargetVertexs() {
        return targetVertexs;
    }
}
