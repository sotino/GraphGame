package be.ac.umons.olbregts.graphgame.algorithm.implementation;

import be.ac.umons.olbregts.graphgame.algorithm.Algorithm;
import be.ac.umons.olbregts.graphgame.algorithm.strategy.*;
import be.ac.umons.olbregts.graphgame.exception.IllegalGraphException;
import be.ac.umons.olbregts.graphgame.io.GraphLoader;
import be.ac.umons.olbregts.graphgame.model.Game;
import be.ac.umons.olbregts.graphgame.model.Graph;
import be.ac.umons.olbregts.graphgame.model.implementation.games.WindowReachGame;
import be.ac.umons.olbregts.graphgame.model.implementation.games.WindowingQuantitativeGame;
import be.ac.umons.olbregts.graphgame.model.implementation.objectoriented.GraphObjectOriented;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.List;

/**
 * Created by simon on 27/05/16.
 */
public class WmpReach implements Algorithm {

    private WindowReachGame game;
    private Graph g;
    private Map<String, UnionStrategy> strats;
    private Map<String, String> labels;
    private List<String> winningRegion;
    private List<String> xPrime;
    private int step;

    @Override
    public void reset(Game game) throws IllegalGraphException {
        if (!(game instanceof WindowReachGame)) {
            throw new IllegalGraphException("The objectif does not match. A WindowingQuantitativeGame is needed");
        }
        this.game = (WindowReachGame) game;
        int n = game.getGraph().getVertexCount();
        g = new GraphObjectOriented();
        strats = new HashMap<>(n);
        labels = new HashMap<>(n);
        winningRegion = new ArrayList<>(n);
        xPrime = new ArrayList<>(n);
        step = 0;
        for (String vertexId : game.getGraph().getVertexsId()) {
            g.addVertex(vertexId + "-0", game.getGraph().getPlayer(vertexId));
            g.addVertex(vertexId + "-1", game.getGraph().getPlayer(vertexId));
            strats.put(vertexId, new UnionStrategy());
        }
        for (String vertedId : game.getGraph().getVertexsId()) {
            int[] succCost = game.getGraph().getSuccessorsWeight(vertedId);
            String[] succs = game.getGraph().getSuccessors(vertedId);
            for (int i = 0; i < succs.length; i++) {
                if (this.game.getTargetVertexs().contains(succs[i])) {
                    g.addEdge(vertedId + "-0", succs[i] + "-1", succCost[i]);
                } else {
                    g.addEdge(vertedId + "-0", succs[i] + "-0", succCost[i]);
                }
                g.addEdge(vertedId + "-1", succs[i] + "-1", succCost[i]);
            }
        }
    }

    @Override
    public boolean isEnded() {
        return step > 1;
    }

    @Override
    public void compute() {
        while (!isEnded()) {
            computeAStep();
        }
    }

    @Override
    public void computeAStep() {
        switch (step) {
            case 0:
                DirectFWMP dfwmp = new DirectFWMP();
                try {
                    dfwmp.reset(new WindowingQuantitativeGame(g, 0, game.getWindowsSize()));
                    dfwmp.compute();
                    Arrays.stream(dfwmp.getWinningRegion()).forEach(v -> {
                        if(v.charAt(v.length()-1) == '1') {
                            xPrime.add(v);
                            labels.put(v.substring(0, v.length() - 2), "X'");
                        }
                    });
                    for (String vertexId : game.getGraph().getVertexsId()) {
                        strats.get(vertexId).addStrategy(convertStrat(dfwmp.getStrategy(vertexId + "-1")));
                    }
                } catch (IllegalGraphException e) {
                    e.printStackTrace();
                }
                step = 1;
                break;
            case 1:
                GDEnd gdEnd = new GDEnd();
                try {
                    gdEnd.reset(new WindowReachGame(g, 0, game.getWindowsSize(), xPrime));
                    gdEnd.compute();
                    for (String vertexId : gdEnd.getWinningRegion()) {
                        labels.put(vertexId.substring(0,vertexId.length() - 2),"Y'");
                        if (vertexId.charAt(vertexId.length() - 1) == '0' || game.getTargetVertexs().contains(vertexId.substring(0,vertexId.length()-2))) {
                            String v = vertexId.substring(0,vertexId.length() - 2);
                            if(!winningRegion.contains(v))
                                winningRegion.add(v);
                        }
                    }
                    for (String vertexId : game.getGraph().getVertexsId()) {
                        strats.get(vertexId).addStrategy(convertStrat(gdEnd.getStrategy(vertexId + "-0")));
                    }
                } catch (IllegalGraphException e) {
                    e.printStackTrace();
                }
                step = 2;
                break;
        }
    }

    private Strategy convertStrat(Strategy strategy){
        if(strategy instanceof UnionStrategy){
            UnionStrategy unionStrategy = (UnionStrategy) strategy;
            List<Strategy> newStrats = new LinkedList<>();
            unionStrategy.getStrategies().stream().forEach(s -> newStrats.add(convertStrat(s)));
            return new UnionStrategy(newStrats);
        }else if(strategy instanceof MemoryLessStrategy){
            String[] choose = strategy.getSelectedEdge();
            if(choose.length == 1) {
                return new MemoryLessStrategy(removeAddedBit(choose)[0]);
            }else {
                return new MemoryLessStrategy();
            }
        }
        return new WindowStrategy(removeAddedBit(strategy.getSelectedEdge()));
    }

    private String[] removeAddedBit(String[] vertex){
        String[] newVertex = new String[vertex.length];
        for(int i=0;i<vertex.length;i++){
            newVertex[i]=vertex[i].substring(0,vertex[i].length()-2);
        }
        return newVertex;
    }


    @Override
    public boolean isInWinningRegion(String vertexId) {
        return winningRegion.contains(vertexId);
    }

    @Override
    public String[] getWinningRegion() {
        return winningRegion.toArray(new String[winningRegion.size()]);
    }

    @Override
    public Strategy getStrategy(String vertexId) {
        return strats.get(vertexId);
    }

    @Override
    public String getLabel(String vertexId) {
        return labels.get(vertexId);
    }

    @Override
    public Color getVertexColor(String vertexId) {
        if (game.getTargetVertexs().contains(vertexId)) {
            return Color.YELLOW;
        }
        return null;
    }

    @Override
    public Color getEdgeColor(String srcId, String targetId) {
        if (strats.get(srcId) != null && Arrays.stream(strats.get(srcId).getSelectedEdge()).anyMatch(targetId::equals)) {
            return Color.GREEN;
        }
        return null;
    }
}
