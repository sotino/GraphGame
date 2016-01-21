/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.umons.olbregts.graphgame.algorithm.implementation;

import be.ac.umons.olbregts.graphgame.algorithm.MemoryLessStrategy;
import be.ac.umons.olbregts.graphgame.algorithm.Algorithm;
import be.ac.umons.olbregts.graphgame.algorithm.Strategy;
import be.ac.umons.olbregts.graphgame.exception.IllegalGraphException;
import be.ac.umons.olbregts.graphgame.model.Game;
import be.ac.umons.olbregts.graphgame.model.Graph;
import be.ac.umons.olbregts.graphgame.model.Heap;
import be.ac.umons.olbregts.graphgame.model.HeapElement;
import be.ac.umons.olbregts.graphgame.model.implementation.games.ReachibilityGame;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author Simon
 */
public class DijkstraTP implements Algorithm {

    private ReachibilityGame game;
    private Integer[] targets;
    private int lastSelected;
    // The heap Q that contains the fixed element not yet proccessed
    private Heap<QVertex> q;
    // Allow direct acces to a element of Q
    private ArrayList<QVertex> qElements;
    // The heap of each vertex. Allow to know the actual distance to targets.
    private ArrayList<Heap<VertexHeapElement>> vertexsHeap;
    // Save the number of edge available for each vertex
    private ArrayList<Integer> availableEdges;
    // Save the bocked edges
    private ArrayList<ArrayList<Integer>> blockedEdge;

    @Override
    public void reset(Game game) throws IllegalGraphException {
        if (!(game instanceof ReachibilityGame)) {
            throw new IllegalGraphException("The objectif does not match. A ReachibilityGame is needed");
        }
        if (!isValid(game)) {
            throw new IllegalGraphException("The cost on edge can't be negative");
        }
        this.game = (ReachibilityGame) game;
        this.targets = this.game.getWiningCondition().toArray(new Integer[0]);
        lastSelected = -1;
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
                    int[] preds = game.getGraph().getPredecessor(u.vertexId);
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
    public Strategy getStrategy(int index) {
        Heap<VertexHeapElement> heap = vertexsHeap.get(index);
        int choose = -1;
        if (heap.isEmpty()) {
            ArrayList<Integer> blocked = blockedEdge.get(index);
            if (isEnded() || !blocked.isEmpty()) {
                for (int succ : game.getGraph().getSuccessors(index)) {
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
    public boolean isInWinningRegion(int vertexId){
        Heap<VertexHeapElement> heap = vertexsHeap.get(vertexId);
        return !heap.isEmpty() && ! (heap.peek().distance == Integer.MAX_VALUE);
    }

    @Override
    public String getLabel(int vertexId) {
        Heap<VertexHeapElement> heap = vertexsHeap.get(vertexId);
        if (heap.isEmpty()) {
            return "+ inf";
        }
        int value = heap.peek().distance;
        if(value == Integer.MAX_VALUE){
          return "+ inf";
        }
        return "" + value;
    }

    @Override
    public Color getVertexColor(int vertexId) {
        if(vertexId == lastSelected && ! isEnded())
            return Color.CYAN;
        for(int target : game.getWiningCondition()){
            if(vertexId == target){
                return Color.YELLOW;
            }
        }
        return null;
    }

    @Override
    public Color getEdgeColor(int originId, int destinationId) {
        for(int d : blockedEdge.get(originId)){
            if(d == destinationId){
                return Color.RED;
            }
        }
        Strategy strategy = getStrategy(originId);
        for(int d : strategy.getSelectedEdge()){
            if(destinationId == d){
                return Color.GREEN;
            }
        }
        return null;
    }

    private void initializeTP() {
        int vertexCount = game.getGraph().getVertexCount();
        vertexsHeap = new ArrayList<>(vertexCount);
        availableEdges = new ArrayList<>(vertexCount);
        blockedEdge = new ArrayList<>(vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            vertexsHeap.add(new Heap<VertexHeapElement>());
            availableEdges.add(game.getGraph().getSuccessorCount(i));
            blockedEdge.add(new ArrayList<Integer>());
        }
        for (int target : targets) {
            vertexsHeap.get(target).insert(new VertexHeapElement(0, -1));
        }
    }

    private boolean isTarget(int vertexId) {
        for (int i : targets) {
            if (i == vertexId)
                return true;
        }
        return false;
    }

    private void initHeapTP() {
        int vertexCount = game.getGraph().getVertexCount();
        qElements = new ArrayList<>(vertexCount);
        q = new Heap<>();
        QVertex[] v = new QVertex[vertexCount];
        int targetPointer = 0;
        int otherPointer = targets.length;
        for (int i = 0; i < vertexCount; i++) {
            if (isTarget(i)) {
                v[targetPointer] = new QVertex(i);
                qElements.add(i, v[targetPointer]);
                targetPointer++;
            } else {
                v[otherPointer] = new QVertex(i);
                qElements.add(i, v[otherPointer]);
                otherPointer++;
            }
        }
        q.initialize(v);
    }

    private void relaxTP(int pred, int edgeCost, QVertex u) {
        int cost = edgeCost + u.lireMin();
        vertexsHeap.get(pred).insert(new VertexHeapElement(cost, u.vertexId));
        QVertex predInQ = qElements.get(pred);
        if (predInQ.heapIndex != -1) {
            q.decreaseKey(predInQ.heapIndex, predInQ);
        }
    }

    private void block(int uIndex) {
        availableEdges.set(uIndex, availableEdges.get(uIndex) - 1);
        int succRemoved = vertexsHeap.get(uIndex).extractMin().succ;
        blockedEdge.get(uIndex).add(succRemoved);
        //TODO test utility of cost
        int cost = Integer.MAX_VALUE;
        if (!vertexsHeap.get(uIndex).isEmpty()) {
            cost = vertexsHeap.get(uIndex).peek().distance;
        }
        q.heapify(0);
    }

    private boolean isValid(Game game) {
        for (int i = 0; i < game.getGraph().getVertexCount(); i++) {
            for (int w : game.getGraph().getSuccessorsWeight(i)) {
                if (w < 0)
                    return false;
            }
        }
        return true;
    }

    private class QVertex implements HeapElement<QVertex> {

        private int vertexId;
        private int heapIndex;

        public QVertex(int vertexId) {
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
        private int succ;

        public VertexHeapElement(int distance, int succ) {
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
