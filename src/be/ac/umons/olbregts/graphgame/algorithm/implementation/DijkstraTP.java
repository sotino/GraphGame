/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.umons.olbregts.graphgame.algorithm.implementation;

import be.ac.umons.olbregts.graphgame.algorithm.Algorithm;
import be.ac.umons.olbregts.graphgame.algorithm.MemoryLessStrategy;
import be.ac.umons.olbregts.graphgame.algorithm.Strategy;
import be.ac.umons.olbregts.graphgame.exception.IllegalGraphException;
import be.ac.umons.olbregts.graphgame.model.Game;
import be.ac.umons.olbregts.graphgame.model.Graph;
import be.ac.umons.olbregts.graphgame.model.Heap;
import be.ac.umons.olbregts.graphgame.model.HeapElement;
import be.ac.umons.olbregts.graphgame.model.implementation.games.ReachibilityGame;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Simon
 */
public class DijkstraTP implements Algorithm {

    private ReachibilityGame game;
    private String[] targets;
    private String lastSelected;
    // The heap Q that contains the fixed element not yet proccessed
    private Heap<QVertex> q;
    // Allow direct acces to a element of Q
    private Map<String,QVertex> qElements;
    // The heap of each vertex. Allow to know the actual distance to targets.
    private Map<String,Heap<VertexHeapElement>> vertexsHeap;
    // Save the number of edge available for each vertex
    private Map<String,Integer> availableEdges;
    // Save the bocked edges
    private Map<String,ArrayList<String>> blockedEdge;

    @Override
    public void reset(Game game) throws IllegalGraphException {
        if (!(game instanceof ReachibilityGame)) {
            throw new IllegalGraphException("The objectif does not match. A ReachibilityGame is needed");
        }
        if (!isValid(game)) {
            throw new IllegalGraphException("The cost on edge can't be negative");
        }
        this.game = (ReachibilityGame) game;
        this.targets = this.game.getWiningCondition().toArray(new String[0]);
        lastSelected = null;
        initializeTP();
        initHeapTP();
    }

    @Override
    public boolean isEnded() {
        return q.isEmpty();
    }

    @Override
    public void compute() {
        while (!isEnded()) {
            computeAStep();
        }
    }

    @Override
    public void computeAStep() {
        if (!q.isEmpty()) {
            QVertex u = q.peek();
            lastSelected = u.vertexId;
            if (u.lireMin() == Integer.MAX_VALUE) {
                q.clear();
            } else {
                if (game.getGraph().getPlayer(u.vertexId) == Graph.PLAYER1 || availableEdges.get(u.vertexId) == 1) {
                    q.extractMin();
                    String[] preds = game.getGraph().getPredecessor(u.vertexId);
                    int[] predsWeight = game.getGraph().getPredecessorWeight(u.vertexId);
                    for (int i = 0; i < preds.length; i++) {
                        relaxTP(preds[i], predsWeight[i], u);
                    }
                } else {
                    block(u.vertexId);
                }
            }
        }
    }

    @Override
    public Strategy getStrategy(String vertexId) {
        Heap<VertexHeapElement> heap = vertexsHeap.get(vertexId);
        String choose = null;
        if (heap.isEmpty()) {
            ArrayList<String> blocked = blockedEdge.get(vertexId);
            if (isEnded() || !blocked.isEmpty()) {
                for (String succ : game.getGraph().getSuccessors(vertexId)) {
                    if (!blocked.contains(succ)) {
                        choose = succ;
                    }
                }
            }
        } else {
            choose = heap.peek().succ;
        }
        return new MemoryLessStrategy(choose);
    }

    @Override
    public boolean isInWinningRegion(String vertexId) {
        Heap<VertexHeapElement> heap = vertexsHeap.get(vertexId);
        return !heap.isEmpty() && !(heap.peek().distance == Integer.MAX_VALUE);
    }

    @Override
    public String[] getWinningRegion(){
        java.util.List<String> winningRegion = new ArrayList<>();
        for(String vertexId:game.getGraph().getVertexsId()){
            if(isInWinningRegion(vertexId)){
                winningRegion.add(vertexId);
            }
        }
        return winningRegion.toArray(new String[winningRegion.size()]);
    }

    @Override
    public String getLabel(String vertexId) {
        Heap<VertexHeapElement> heap = vertexsHeap.get(vertexId);
        if (heap.isEmpty()) {
            return "+ inf";
        }
        int value = heap.peek().distance;
        if (value == Integer.MAX_VALUE) {
            return "+ inf";
        }
        return "" + value;
    }

    @Override
    public Color getVertexColor(String vertexId) {
        if (vertexId.equals(lastSelected) && !isEnded())
            return Color.CYAN;
        for (String target : game.getWiningCondition()) {
            if (vertexId.equals(target)) {
                return Color.YELLOW;
            }
        }
        return null;
    }

    @Override
    public Color getEdgeColor(String srcId, String targetId) {
        for (String d : blockedEdge.get(srcId)) {
            if (d.equals(targetId)) {
                return Color.RED;
            }
        }
        Strategy strategy = getStrategy(srcId);
        for (String d : strategy.getSelectedEdge()) {
            if (d.equals(targetId)) {
                return Color.GREEN;
            }
        }
        return null;
    }

    private void initializeTP() {
        int vertexCount = game.getGraph().getVertexCount();
        vertexsHeap = new HashMap<>(vertexCount);
        availableEdges = new HashMap<>(vertexCount);
        blockedEdge = new HashMap<>(vertexCount);
        for(String vertexId:game.getGraph().getVertexsId()){
            vertexsHeap.put(vertexId,new Heap<>());
            availableEdges.put(vertexId,game.getGraph().getSuccessorCount(vertexId));
            blockedEdge.put(vertexId,new ArrayList<>());
        }
        for (String target : targets) {
            vertexsHeap.get(target).insert(new VertexHeapElement(0, null));
        }
    }

    private boolean isTarget(String vertexId) {
        for (String i : targets) {
            if (i.equals(vertexId))
                return true;
        }
        return false;
    }

    private void initHeapTP() {
        int vertexCount = game.getGraph().getVertexCount();
        qElements = new HashMap<>(vertexCount);
        q = new Heap<>();
        QVertex[] v = new QVertex[vertexCount];
        int targetPointer = 0;
        int otherPointer = targets.length;
        for(String vertexId:game.getGraph().getVertexsId()){
        //for (int i = 0; i < vertexCount; i++) {
            if (isTarget(vertexId)) {
                v[targetPointer] = new QVertex(vertexId);
                qElements.put(vertexId, v[targetPointer]);
                targetPointer++;
            } else {
                v[otherPointer] = new QVertex(vertexId);
                qElements.put(vertexId, v[otherPointer]);
                otherPointer++;
            }
        }
        q.initialize(v);
    }

    private void relaxTP(String pred, int edgeCost, QVertex u) {
        int cost = edgeCost + u.lireMin();
        vertexsHeap.get(pred).insert(new VertexHeapElement(cost, u.vertexId));
        QVertex predInQ = qElements.get(pred);
        if (predInQ.heapIndex != -1) {
            q.decreaseKey(predInQ.heapIndex, predInQ);
        }
    }

    private void block(String uIndex) {
        availableEdges.put(uIndex, availableEdges.get(uIndex) - 1);
        String succRemoved = vertexsHeap.get(uIndex).extractMin().succ;
        blockedEdge.get(uIndex).add(succRemoved);
        q.heapify(0);
    }

    private boolean isValid(Game game) {
        for(String vertexId: game.getGraph().getVertexsId()){
            for (int w : game.getGraph().getSuccessorsWeight(vertexId)) {
                if (w < 0)
                    return false;
            }
        }
        return true;
    }

    private class QVertex implements HeapElement<QVertex> {

        private String vertexId;
        private int heapIndex;

        public QVertex(String vertexId) {
            this.vertexId = vertexId;
        }

        @Override
        public int getHeapIndex() {
            return heapIndex;
        }

        @Override
        public void setHeapIndex(int index) {
            heapIndex = index;
        }

        public int lireMin() {
            int min = Integer.MAX_VALUE;
            if (!vertexsHeap.get(vertexId).isEmpty()) {
                min = vertexsHeap.get(vertexId).peek().distance;
            }
            return min;
        }

        @Override
        public int compareTo(QVertex o) {
            return Integer.compare(lireMin(), o.lireMin());
        }

        @Override
        public String toString() {
            return "[V" + (vertexId + 1) + "|C:" + lireMin() + ']';
        }
    }

    private class VertexHeapElement implements HeapElement<VertexHeapElement> {

        private int heapIndex;
        private int distance;
        private String succ;

        public VertexHeapElement(int distance, String succ) {
            this.distance = distance;
            this.succ = succ;
        }

        @Override
        public int getHeapIndex() {
            return heapIndex;
        }

        @Override
        public void setHeapIndex(int index) {
            heapIndex = index;
        }

        @Override
        public int compareTo(VertexHeapElement o) {
            return Integer.compare(distance, o.distance);
        }
    }
}
