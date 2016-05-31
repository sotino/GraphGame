package be.ac.umons.olbregts.graphgame.algorithm.strategy;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by simon on 27/05/16.
 */
public class UnionStrategy implements Strategy {

    private List<Strategy> strategies;

    public UnionStrategy() {
        this(new LinkedList<>());
    }

    public UnionStrategy(List<Strategy> strategies) {
        this.strategies = strategies;
    }

    public void addStrategy(Strategy strategy) {
        if(strategy instanceof UnionStrategy){
            UnionStrategy us = (UnionStrategy) strategy;
            us.getStrategies().stream().forEach(strategies::add);
        }else {
            strategies.add(strategy);
        }
    }

    public List<Strategy> getStrategies() {
        return strategies;
    }

    @Override
    public String[] getSelectedEdge() {
        List<String> allStrat = new LinkedList<>();
        for (Strategy s : strategies) {
            Arrays.stream(s.getSelectedEdge()).forEach(allStrat::add);
        }
        return allStrat.toArray(new String[allStrat.size()]);
    }

    @Override
    public String printChoose() {
        return null;
    }
}
