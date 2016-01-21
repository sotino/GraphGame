/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.umons.olbregts.graphgame.view;

import be.ac.umons.olbregts.graphgame.algorithm.Algorithm;
import be.ac.umons.olbregts.graphgame.model.Graph;
import be.ac.umons.olbregts.graphgame.model.implementation.objectoriented.GraphObjectOriented;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * @author Simon
 */
public class GraphPanel extends mxGraphComponent {

    private static final String P1_STYLE = "shape=ellipse";
    private static final String P2_STYLE = "defaultVertex";
    private ArrayList<Object> vertexsView;
    private Graph model;
    private Algorithm algorithm;

    public GraphPanel() {
        this(new GraphObjectOriented());
    }

    public GraphPanel(Graph model) {
        this(model, null);
    }

    public GraphPanel(Graph model, Algorithm algorithm) {
        super(new mxGraph());
        this.model = model;
        this.algorithm = algorithm;
        getViewport().setOpaque(true);
        getViewport().setBackground(Color.WHITE);
        vertexsView = new ArrayList<>();
        graph.setAllowDanglingEdges(false);
        setEditable(false);
        displayGraph();

        getConnectionHandler().addListener(mxEvent.CONNECT, new mxIEventListener() {
            public void invoke(Object sender, mxEventObject evt) {
                boolean edgeAdded = false;
                mxCell edge = (mxCell) evt.getProperty("cell");
                if (edge.getTarget().isVertex()) {
                    int srcIndex = Integer.parseInt(edge.getSource().getId());
                    int targetIndex = Integer.parseInt(edge.getTarget().getId());
                    Integer cost = null;
                    String msg = "Type edge cost";
                    while (cost == null) {
                        String s = JOptionPane.showInputDialog(msg);
                        if (s == null) {
                            break;
                        }
                        try {
                            cost = Integer.parseInt(s);
                        } catch (NumberFormatException nfe) {
                            cost = null;
                            msg = "The cost must be an integer\n Type edge cost";
                        }
                    }
                    if (cost != null) {
                        edgeAdded = GraphPanel.this.model.addEdge(srcIndex, targetIndex, cost);
                        edge.setValue("" + cost);
                    }
                }
                if (!edgeAdded) {
                    Object[] cells = {edge};
                    graph.removeCells(cells);
                }
            }
        });
    }

    public void setEditable(boolean editable) {
        graph.setCellsLocked(!editable);
        graph.setCellsSelectable(editable);
        graph.setEnabled(!editable);
        graph.setConnectableEdges(editable);
        setConnectable(editable);
    }

    public void resetView() {
        displayGraph();
    }

    public void updateGraph(Algorithm pathAlgorithm) {
        displayGraph();
       /**
         graph.setCellStyles(mxConstants.STYLE_STROKEWIDTH, "1", lastSelected);
        if (algorithm.getLastSelected() != -1) {
            lastSelected[0] = getVertexView(algorithm.getLastSelected());
            graph.setCellStyles(mxConstants.STYLE_STROKEWIDTH, "5", lastSelected);
        }
        Object[] parent = {graph.getDefaultParent()};
        Object[] edges = graph.getAllEdges(parent);
        graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, "black", edges);
        graph.setCellStyles(mxConstants.STYLE_STROKEWIDTH, "1", edges);

        graph.getModel().beginUpdate();
        for (Vertex v : graphModel.getVertexs()) {
            int distance = algorithm.getDistance(v.getIndex());
            String distanceStr = distance == Integer.MAX_VALUE ? "inf" : "" + distance;
            String label = "" + (v.getIndex() + 1);
            if (displayDistance) {
                label += '(' + distanceStr + ')';
            }
            changeLabel(v.getIndex(), label);
            if (v != graphModel.getDestination()) {
                int[] choose = algorithm.getStrategy(v.getIndex()).getSelectedEdge();
                boolean first = true;
                for (int e : choose) {
                    if (first) {
                        changeEdgeColor(v.getIndex(), e, "lightgreen");
                        first = false;
                    } else {
                        changeEdgeColor(v.getIndex(), e, "darkgreen");
                    }
                }
                for (int e : algorithm.getBlockedEdge(v.getIndex())) {
                    changeEdgeColor(v.getIndex(), e, "red");
                }
            }
        }
        graph.getModel().endUpdate();
        graph.repaint();*/
    }

    private void changeEdgeColor(int source, int destination, String color) {
        Object[] edges = graph.getEdgesBetween(getVertexView(source), getVertexView(destination), true);
        graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, color, edges);
        String size = color.equals("black") ? "1" : "5";
        graph.setCellStyles(mxConstants.STYLE_STROKEWIDTH, size, edges);
    }

    private void changeLabel(int indice, String label) {
        mxCellState state = graph.getView().getState(vertexsView.get(indice));
        state.setLabel(label);
    }

    private void displayGraph() {
        Object parent = graph.getDefaultParent();
        graph.getModel().beginUpdate();
        try {
            graph.removeCells(graph.getChildVertices(graph.getDefaultParent()));
            graph.refresh();
            int nbNodes = model.getVertexCount();
            vertexsView.clear();
            for (int i = 0; i < nbNodes; i++) {
                addVertexView(i);
            }
            for(int u = 0; u < nbNodes ; u++){
                int[] succ = model.getSuccessors(u);
                int[] succW = model.getSuccessorsWeight(u);
                for(int vIndex = 0; vIndex < succ.length; vIndex++){
                    int v = succ[vIndex];
                    String label = "" + succW[vIndex];
                    graph.insertEdge(parent,null,label,getVertexView(u),getVertexView(v),"labelBackgroundColor=white");
                    if(algorithm != null) {
                        Color color = algorithm.getEdgeColor(u, v);
                        if (color != null) {
                            changeEdgeColor(u, v, colorToHex(color));
                        }
                    }
                }
            }
            applyLayout();
        } finally {
            graph.getModel().endUpdate();
        }
    }

    public final void addVertex(int player) {
        int vertexId = model.addVertex(player);
        addVertexView(vertexId);
    }

    private Object getVertexView(int vertexId) {
        return vertexsView.get(vertexId);
    }

    private void addVertexView(int vertexId) {
        Object parent = graph.getDefaultParent();
        String label = "[" + (vertexId + 1) + "]";
        if(algorithm != null) {
            String algLabel = algorithm.getLabel(vertexId);
            if (algLabel != null) {
                label += " " + algLabel;
            }
        }
        if (model.getPlayer(vertexId) == Graph.PLAYER1) {
            vertexsView.add(vertexId, graph.insertVertex(parent, "" + vertexId, label, 100, 100, 80, 30, P1_STYLE));
        } else {
            vertexsView.add(vertexId, graph.insertVertex(parent, "" + vertexId, label, 100, 100, 80, 30, P2_STYLE));
        }
        ((mxCell) vertexsView.get(vertexId)).setId("" +vertexId);
        if(algorithm != null) {
            Color color = algorithm.getVertexColor(vertexId);
            Object[] o = {vertexsView.get(vertexId)};
            if (color != null) {
                graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, colorToHex(color), o);
            }
            if(algorithm.isInWinningRegion(vertexId)){
                graph.setCellStyles(mxConstants.STYLE_STROKEWIDTH, "3", o);
                graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, colorToHex(Color.GREEN.darker()),o);
            }else if(algorithm.isEnded()){
                graph.setCellStyles(mxConstants.STYLE_STROKEWIDTH, "3", o);
                graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, colorToHex(Color.RED.darker()),o);
            }
        }
    }

    private static String colorToHex(Color color){
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    public void applyLayout() {
        graph.getModel().beginUpdate();
        mxIGraphLayout layout = new mxHierarchicalLayout(graph);
        layout.execute(graph.getDefaultParent());
        shift();
        graph.getModel().endUpdate();
    }

    private void shift() {
        Object[] cells = graph.getChildCells(graph.getDefaultParent());
        for (Object cell : cells) {
            Object[] c = {cell};
            graph.moveCells(c, 25, 25);
        }
    }

    public void deleteSelected() {
        mxCell c = ((mxCell) graph.getSelectionCell());
        if (c != null) {
            if (c.isVertex()) {
                int vertexId = Integer.parseInt(c.getId());
                    model.deleteVertex(vertexId);
                    Object[] cells = {c};
                    graph.removeCells(cells);
            } else if (c.isEdge()) {
                int sourceId = Integer.parseInt(c.getSource().getId());
                int destId = Integer.parseInt(c.getTarget().getId());
                model.deleteEdge(sourceId, destId);
                Object[] cells = {c};
                graph.removeCells(cells);
            }
        }
    }

    public Graph getGraphModel() {
        return model;
    }

    public void setAlgorithm(Algorithm algorithm){
        this.algorithm = algorithm;
        updateGraph(algorithm);
    }
}
