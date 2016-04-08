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

    String[] getVertexsId();

    boolean contains(String vertexId);

    int getPlayer(String vertexId);

    boolean hasSuccessors(String vertexId);

    int getSuccessorCount(String vertexId);

    String[] getSuccessors(String vertexId);

    int[] getSuccessorsWeight(String vertexId);

    boolean hasPredecessors(String vertexId);

    int getPredecessorCount(String vertexId);

    String[] getPredecessor(String vertexId);

    int[] getPredecessorWeight(String vertexId);

    void addVertex(String vertexId, int player);

    void deleteVertex(String vertexId);

    boolean addEdge(String srcId, String targetId, int cost);

    void deleteEdge(String srcId, String targetId);

    Graph getSubgraph(String[] vertexs);

    Graph clone();
}
