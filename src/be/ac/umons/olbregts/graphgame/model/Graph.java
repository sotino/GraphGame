/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.umons.olbregts.graphgame.model;

/**
 * @author Simon
 */
public interface Graph {

    public static final int PLAYER1 = 1;
    public static final int PLAYER2 = 2;

    public int getVertexCount();

    public int getPlayer(int vertexId);

    public boolean hasSuccessors(int vertexId);

    public int getSuccessorCount(int vertexId);

    public int[] getSuccessors(int vertexId);

    public int[] getSuccessorsWeight(int vertexId);

    public boolean hasPredecessors(int vertexId);

    public int getPredecessorCount(int vertexId);

    public int[] getPredecessor(int vertexId);

    public int[] getPredecessorWeight(int vertexId);

    public int addVertex(int player);

    public void deleteVertex(int vertexId);

    public boolean addEdge(int srcIndex, int targetIndex, int cost);

    public void deleteEdge(int sourceId, int destId);

    public Graph getSubgraph(int[] vertexs);

    public Graph clone();
}
