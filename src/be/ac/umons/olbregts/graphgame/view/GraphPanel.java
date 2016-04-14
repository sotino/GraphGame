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
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
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
    //private ArrayList<Object> vertexsView;
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
        //vertexsView = new ArrayList<>();
        graph.setAllowDanglingEdges(false);
        setEditable(false);
        displayGraph();
        getConnectionHandler().addListener(mxEvent.CONNECT, (sender, evt) -> {
            boolean edgeAdded = false;
            mxCell edge = (mxCell) evt.getProperty("cell");
            if (edge.getTarget().isVertex()) {
                String srcId = edge.getSource().getId();
                String targetId = edge.getTarget().getId();
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
                    edgeAdded = GraphPanel.this.model.addEdge(srcId, targetId, cost);
                    edge.setValue("" + cost);
                }
            }
            if (!edgeAdded) {
                Object[] cells = {edge};
                graph.removeCells(cells);
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

    public void updateGraph() {
        displayGraph();
        /*
        graph.getModel().beginUpdate();
        for(String vertexId: model.getVertexsId()){
            updateVertexColor(vertexId);
            if(algorithm != null) {
                changeLabel(vertexId, algorithm.getLabel(vertexId));
                for (String succId : model.getSuccessors(vertexId)) {
                    Color color = algorithm.getEdgeColor(vertexId, succId);
                    if (color != null) {
                        changeEdgeColor(vertexId, succId, colorToHex(color));
                    }
                }
            }
        }
        graph.getModel().endUpdate();
        graph.repaint();*/
    }

    private void changeEdgeColor(String srcId, String targetId, String color) {
        Object[] edges = graph.getEdgesBetween(getVertexView(srcId), getVertexView(targetId), true);
        graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, color, edges);
        String size = color.equals("black") ? "1" : "5";
        graph.setCellStyles(mxConstants.STYLE_STROKEWIDTH, size, edges);
    }

    private void changeLabel(String vertexId, String label) {
        mxCellState state = graph.getView().getState(getVertexView(vertexId));
        state.setLabel(vertexId+" "+label);
    }

    private void displayGraph() {
        Object parent = graph.getDefaultParent();
        graph.getModel().beginUpdate();
        try {
            graph.removeCells(graph.getChildVertices(graph.getDefaultParent()));
            graph.refresh();
            int nbNodes = model.getVertexCount();
            for (String vertexId : model.getVertexsId()) {
                addVertexView(vertexId);
            }
            for (String vertexId : model.getVertexsId()) {
                //for (int u = 0; u < nbNodes; u++) {
                String[] succ = model.getSuccessors(vertexId);
                int[] succW = model.getSuccessorsWeight(vertexId);
                for (int vIndex = 0; vIndex < succ.length; vIndex++) {
                    String succId = succ[vIndex];
                    String label = "" + succW[vIndex];
                    graph.insertEdge(parent, null, label, getVertexView(vertexId), getVertexView(succId), "labelBackgroundColor=white");
                    if (algorithm != null) {
                        Color color = algorithm.getEdgeColor(vertexId, succId);
                        if (color != null) {
                            changeEdgeColor(vertexId, succId, colorToHex(color));
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
        String id = "V" + (model.getVertexCount() + 1);
        model.addVertex(id, player);
        addVertexView(id);
    }

    private mxCell getVertexView(String vertexId) {
        return (mxCell) ((mxGraphModel) graph.getModel()).getCell(vertexId);
    }

    private void addVertexView(String vertexId) {
        Object parent = graph.getDefaultParent();
        String label = "[" + vertexId + "]";
        if (algorithm != null) {
            String algLabel = algorithm.getLabel(vertexId);
            if (algLabel != null) {
                label += " " + algLabel;
            }
        }
        if (model.getPlayer(vertexId) == Graph.PLAYER1) {
            graph.insertVertex(parent, "" + vertexId, label, 100, 100, 80, 30, P1_STYLE);
            //vertexsView.add(vertexId, graph.insertVertex(parent, "" + vertexId, label, 100, 100, 80, 30, P1_STYLE));
        } else {
            graph.insertVertex(parent, "" + vertexId, label, 100, 100, 80, 30, P2_STYLE);
            //vertexsView.add(vertexId, graph.insertVertex(parent, "" + vertexId, label, 100, 100, 80, 30, P2_STYLE));
        }
        mxCell c = getVertexView(vertexId);
        // ((mxCell) vertexsView.get(vertexId)).setId("" + vertexId);
        updateVertexColor(vertexId);
    }

    private void updateVertexColor(String vertexId) {
        if (algorithm != null) {
            Color color = algorithm.getVertexColor(vertexId);
            Object[] o = {getVertexView(vertexId)};
            if (color != null) {
                graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, colorToHex(color), o);
            }
            if (algorithm.isInWinningRegion(vertexId)) {
                graph.setCellStyles(mxConstants.STYLE_STROKEWIDTH, "3", o);
                graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, colorToHex(Color.GREEN.darker()), o);
            } else if (algorithm.isEnded()) {
                graph.setCellStyles(mxConstants.STYLE_STROKEWIDTH, "3", o);
                graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, colorToHex(Color.RED.darker()), o);
            }else{
                graph.setCellStyles(mxConstants.STYLE_STROKEWIDTH, "1", o);
                graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, colorToHex(Color.BLACK), o);
            }
        }
    }


    private static String colorToHex(Color color) {
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
                model.deleteVertex(c.getId());
                Object[] cells = {c};
                graph.removeCells(cells);
            } else if (c.isEdge()) {
                model.deleteEdge(c.getSource().getId(), c.getTarget().getId());
                Object[] cells = {c};
                graph.removeCells(cells);
            }
        }
    }

    public Graph getGraphModel() {
        return model;
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
        updateGraph();
    }
}
