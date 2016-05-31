package be.ac.umons.olbregts.graphgame.algorithm.strategy;

/**
 * Created by simon on 10/05/16.
 */
public class WindowStrategy implements Strategy {

    /*
     * strategies[i] is the vertex to choose when there are i step before the maximal window size
      * i.e. i = lMax - l
     */
    private int lMax;
    private String[] strategies;

    public WindowStrategy(int lMax) {
        this.lMax = lMax;
        strategies = new String[lMax];
    }

    public WindowStrategy(String[] strategies) {
        this.strategies = strategies;
        lMax = strategies.length;
    }

    public void setStrategies(int l, String strategy) {
        strategies[lToIndex(l)] = strategy;
    }

    public String getStrategy(int l) {
        return strategies[lToIndex(l)];
    }

    private int lToIndex(int l) {
        return lMax - l - 1;
    }

    @Override
    public String[] getSelectedEdge() {
        return strategies;
    }

    @Override
    public String printChoose() {
        return null;
    }
}
