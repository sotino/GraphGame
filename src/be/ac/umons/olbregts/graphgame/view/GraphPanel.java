/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.umons.olbregts.graphgame.view;

import be.ac.umons.olbregts.graphgame.algorithm.PathAlgorithm;
import be.ac.umons.olbregts.graphgame.model.implementation.objectoriented.Edge;
import be.ac.umons.olbregts.graphgame.model.implementation.objectoriented.GraphObjectOriented;
import be.ac.umons.olbregts.graphgame.model.implementation.objectoriented.Vertex;
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

    private static final String DESTINATION_STYLE = "shape=ellipse;fillColor=yellow";
    private static final String P1_STYLE = "shape=ellipse";
    private static final String P2_STYLE = "defaultVertex";
    private GraphObjectOriented graphModel;
    private ArrayList<Object> vertexsView;
    private Object[] lastSelected;
    private boolean displayDistance;
    private boolean autoLayout;

    public GraphPanel(GraphObjectOriented graphModel, boolean displayDistance) {
        super(new mxGraph());
        getViewport().setOpaque(true);
        getViewport().setBackground(Color.WHITE);
        this.graphModel = graphModel;
        lastSelected = new Object[1];
        vertexsView = new ArrayList<>();
        this.displayDistance = displayDistance;
        autoLayout = true;
        graph.setAllowDanglingEdges(false);
        setEditable(false);
        if (graphModel.getDestination() == null) {
            if (!graphModel.getVertexs().isEmpty()) {
                for (Vertex v : graphModel.getVertexs()) {
                    if (v.getPlayer() == 1) {
                        graphModel.setDestination(v);
                        break;
                    }
                }
            }
            if (graphModel.getDestination() == null) {
                addVertex(1);
                graphModel.setDestination(graphModel.getLastVertex());
            }
        }
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
                        edgeAdded = GraphPanel.this.graphModel.addEdge(srcIndex, targetIndex, cost);
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

    public GraphPanel(GraphObjectOriented graph) {
        this(graph, true);
    }

    public GraphPanel() {
        this(new GraphObjectOriented(), true);
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

    public void updateGraph(PathAlgorithm pathAlgorithm) {
        graph.setCellStyles(mxConstants.STYLE_STROKEWIDTH, "1", lastSelected);
        if (pathAlgorithm.getLastSelected() != -1) {
            lastSelected[0] = getVertexView(pathAlgorithm.getLastSelected());
            graph.setCellStyles(mxConstants.STYLE_STROKEWIDTH, "5", lastSelected);
        }
        Object[] parent = {graph.getDefaultParent()};
        Object[] edges = graph.getAllEdges(parent);
        graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, "black", edges);
        graph.setCellStyles(mxConstants.STYLE_STROKEWIDTH, "1", edges);

        graph.getModel().beginUpdate();
        for (Vertex v : graphModel.getVertexs()) {
            int distance = pathAlgorithm.getDistance(v.getIndex());
            String distanceStr = distance == Integer.MAX_VALUE ? "inf" : "" + distance;
            String label = "" + (v.getIndex() + 1);
            if (displayDistance) {
                label += '(' + distanceStr + ')';
            }
            changeLabel(v.getIndex(), label);
            if (v != graphModel.getDestination()) {
                int[] choose = pathAlgorithm.getStrategy(v.getIndex()).getSelectedEdge();
                boolean first = true;
                for (int e : choose) {
                    if (first) {
                        changeEdgeColor(v.getIndex(), e, "lightgreen");
                        first = false;
                    } else {
                        changeEdgeColor(v.getIndex(), e, "darkgreen");
                    }
                }
                for (int e : pathAlgorithm.getBlockedEdge(v.getIndex())) {
                    changeEdgeColor(v.getIndex(), e, "red");
                }
            }
        }
        graph.getModel().endUpdate();
        graph.repaint();
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
            int nbNodes = graphModel.getVertexs().size();
            vertexsView.clear();
            for (int i = 0; i < nbNodes; i++) {
                Vertex v = graphModel.getVertex(i);
                addVertexView(v);
            }
            for (int i = 0; i < nbNodes; i++) {
                Vertex v = graphModel.getVertex(i);
                for (Edge e : v.getPred()) {
                    String value = "" + e.getCost();
                    graph.insertEdge(parent, null, value, getVertexView(e.getSource()), getVertexView(e.getTarget()), "labelBackgroundColor=white");

                }
            }
            if (autoLayout) {
                applyLayout();
            }
        } finally {
            graph.getModel().endUpdate();
        }
    }

    public void setAutoLayout(boolean autoLayout) {
        this.autoLayout = autoLayout;
        if (autoLayout) {
            applyLayout();
        }
    }

    public final void addVertex(int player) {
        graphModel.addVertex(player);
        Vertex v = graphModel.getLastVertex();
        addVertexView(v);
    }

    private Object getVertexView(int vertexId) {
        return vertexsView.get(vertexId);
    }

    private void addVertexView(Vertex v) {
        Object parent = graph.getDefaultParent();
        int i = v.getIndex();
        String label = "" + (i + 1);
        if (displayDistance) {
            label += "(inf)";
        }
        if (v == graphModel.getDestination()) {
            vertexsView.add(i, graph.insertVertex(parent, "" + i, label, 100, 100, 80, 30, DESTINATION_STYLE));
        } else if (v.getPlayer() == 1) {
            vertexsView.add(i, graph.insertVertex(parent, "" + i, label, 100, 100, 80, 30, P1_STYLE));
        } else {
            vertexsView.add(i, graph.insertVertex(parent, "" + i, label, 100, 100, 80, 30, P2_STYLE));
        }
        ((mxCell) vertexsView.get(i)).setId("" + i);
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
                int index = Integer.parseInt(c.getId());
                if (graphModel.getDestination() == graphModel.getVertex(index)) {
                    JOptionPane.showMessageDialog(this, "You can't delete the destination vertex");
                } else {
                    graphModel.deleteVertex(index);
                    Object[] cells = {c};
                    graph.removeCells(cells);

                }
            } else if (c.isEdge()) {
                int srcIndex = Integer.parseInt(c.getSource().getId());
                int targetIndex = Integer.parseInt(c.getTarget().getId());
                graphModel.deleteEdge(srcIndex, targetIndex);
                Object[] cells = {c};
                graph.removeCells(cells);
            }
        }
    }

    public GraphObjectOriented getGraphModel() {
        return graphModel;
    }
}
