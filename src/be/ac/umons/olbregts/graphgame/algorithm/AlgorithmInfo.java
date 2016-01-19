package be.ac.umons.olbregts.graphgame.algorithm;

import be.ac.umons.olbregts.graphgame.view.wining_condition.WinningPanel;

/**
 * Created by Simon on 19-01-16.
 */
public class AlgorithmInfo {

    private String name;
    private PathAlgorithm algorithm;
    private WinningPanel winningPanel;

    public AlgorithmInfo(String name, PathAlgorithm algorithm, WinningPanel winningPanel) {
        this.name = name;
        this.algorithm = algorithm;
        this.winningPanel = winningPanel;
    }

    public String getName() {
        return name;
    }

    public PathAlgorithm getAlgorithm() {
        return algorithm;
    }

    public WinningPanel getWinningPanel() {
        return winningPanel;
    }

    @Override
    public String toString() {
        return name;
    }
}
