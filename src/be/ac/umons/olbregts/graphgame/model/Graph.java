/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.umons.olbregts.graphgame.model;

/**
 * @author Simon
 */
public interface Graph {

    int PLAYER1 = 1;
    int PLAYER2 = 2;

    int getVertexCount();

    int getPlayer(int vertexId);

    boolean hasSuccessors(int vertexId);

    int getSuccessorCount(int vertexId);

    int[] getSuccessors(int vertexId);

    int[] getSuccessorsWeight(int vertexId);

    boolean hasPredecessors(int vertexId);

    int getPredecessorCount(int vertexId);

    int[] getPredecessor(int vertexId);

    int[] getPredecessorWeight(int vertexId);

    int addVertex(int player);

    void deleteVertex(int vertexId);

    boolean addEdge(int srcIndex, int targetIndex, int cost);

    void deleteEdge(int sourceId, int destId);

    Graph getSubgraph(int[] vertexs);

    Graph clone();
}
