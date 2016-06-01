/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.umons.olbregts.graphgame.view;

import be.ac.umons.olbregts.graphgame.algorithm.Algorithm;
import be.ac.umons.olbregts.graphgame.model.Graph;
import be.ac.umons.olbregts.graphgame.model.implementation.objectoriented.GraphObjectOriented;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.*;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Constructor;

/**
 * @author Simon
 */
public class GraphPanel extends JPanel {

    private static final String P1_STYLE = "shape=ellipse";
    private static final String P2_STYLE = "defaultVertex";
    private Graph model;
    private Algorithm algorithm;
    private mxGraphComponent graphComponent;
    private StrategiesViewDialog stratDialog;

    public GraphPanel() {
        this(new GraphObjectOriented());
    }

    public GraphPanel(Graph model) {
        this(model, null);
    }

    public GraphPanel(Graph model, Algorithm algorithm) {
        setLayout(new BorderLayout());
        JPanel controlPanel = new JPanel(new BorderLayout());

        JPanel layoutPanel = new JPanel(new BorderLayout());
        JButton applyLayout = new JButton("Apply");
        layoutPanel.add(applyLayout, BorderLayout.EAST);
        JComboBox<Layouts> layoutCB = new JComboBox<>(Layouts.values());
        layoutPanel.add(layoutCB, BorderLayout.CENTER);
        applyLayout.addActionListener(e -> {
            Layouts selected = (Layouts) layoutCB.getSelectedItem();
            applyLayout(selected);
        });
        layoutCB.setSelectedItem(Layouts.FAST_ORGANIC);
        controlPanel.add(layoutPanel, BorderLayout.WEST);

        JPanel zoomPanel = new JPanel(new FlowLayout());
        zoomPanel.add(new JLabel("Zoom:"));
        JButton zoomIn = new JButton("IN");
        zoomIn.addActionListener(e -> {
            graphComponent.zoomIn();
        });
        zoomPanel.add(zoomIn);
        JButton zoomOut = new JButton("OUT");
        zoomOut.addActionListener(e -> {
            graphComponent.zoomOut();
        });
        zoomPanel.add(zoomOut);
        controlPanel.add(zoomPanel, BorderLayout.EAST);
        add(controlPanel, BorderLayout.SOUTH);

        graphComponent = new mxGraphComponent(new mxGraph());
        add(graphComponent, BorderLayout.CENTER);
        this.model = model;
        this.algorithm = algorithm;
        graphComponent.getViewport().setOpaque(true);
        graphComponent.getViewport().setBackground(Color.WHITE);
        graphComponent.getGraph().setAllowDanglingEdges(false);
        setEditable(false);
        displayGraph();
        applyLayout(Layouts.FAST_ORGANIC);
        graphComponent.getConnectionHandler().addListener(mxEvent.CONNECT, (sender, evt) -> {
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
                graphComponent.getGraph().removeCells(cells);
            }
        });
        Window parentWindow = SwingUtilities.windowForComponent(this);
        Frame parentFrame = null;
        if (parentWindow instanceof Frame) {
            parentFrame = (Frame) parentWindow;
        }
        stratDialog = new StrategiesViewDialog(parentFrame);
    }

    public void setEditable(boolean editable) {
        graphComponent.getGraph().setCellsSelectable(editable);
        graphComponent.getGraph().setEnabled(!editable);
        graphComponent.getGraph().setConnectableEdges(editable);
        graphComponent.setConnectable(editable);
    }

    public void updateGraph() {
        graphComponent.getGraph().getModel().beginUpdate();
        for (String vertexId : model.getVertexsId()) {
            updateVertexColor(vertexId);
        }
        graphComponent.getGraph().getModel().endUpdate();

        graphComponent.getGraph().getModel().beginUpdate();
        boolean needStratDialog = false;
        if (algorithm != null) {
            for (String vertexId : model.getVertexsId()) {
                changeLabel(vertexId, algorithm.getLabel(vertexId));
                if (algorithm.getStrategy(vertexId) != null && algorithm.getStrategy(vertexId).getSelectedEdge() != null)
                    needStratDialog |= algorithm.getStrategy(vertexId).getSelectedEdge().length > 1;
                for (String succId : model.getSuccessors(vertexId)) {
                    Color color = algorithm.getEdgeColor(vertexId, succId);
                    changeEdgeColor(vertexId, succId, color);
                }
            }
        }


        if (needStratDialog) {
            stratDialog.updateStrategies();
            stratDialog.setVisible(true);
        } else {
            stratDialog.setVisible(false);
        }
        graphComponent.getGraph().getModel().endUpdate();
        graphComponent.getGraph().repaint();
    }

    private void changeEdgeColor(String srcId, String targetId, Color color) {
        String hexColor;
        if (color == null) {
            hexColor = "#6482B9";
        } else {
            hexColor = colorToHex(color);
        }
        Object[] edges = graphComponent.getGraph().getEdgesBetween(getVertexView(srcId), getVertexView(targetId), true);
        graphComponent.getGraph().setCellStyles(mxConstants.STYLE_STROKECOLOR, hexColor, edges);
        String size = color == null ? "1" : "5";
        graphComponent.getGraph().setCellStyles(mxConstants.STYLE_STROKEWIDTH, size, edges);
    }

    private void changeLabel(String vertexId, String label) {
        mxCellState state = graphComponent.getGraph().getView().getState(getVertexView(vertexId));
        String str = '[' + vertexId + "]";
        if(label != null){
            str += " " + label;
        }
        state.setLabel(str);
    }

    private void displayGraph() {
        Object parent = graphComponent.getGraph().getDefaultParent();
        graphComponent.getGraph().getModel().beginUpdate();
        try {
            graphComponent.getGraph().removeCells(graphComponent.getGraph().getChildVertices(graphComponent.getGraph().getDefaultParent()));
            graphComponent.getGraph().refresh();
            for (String vertexId : model.getVertexsId()) {
                addVertexView(vertexId);
            }
            for (String vertexId : model.getVertexsId()) {
                String[] succ = model.getSuccessors(vertexId);
                int[] succW = model.getSuccessorsWeight(vertexId);
                for (int vIndex = 0; vIndex < succ.length; vIndex++) {
                    String succId = succ[vIndex];
                    String label = "" + succW[vIndex];
                    graphComponent.getGraph().insertEdge(parent, null, label, getVertexView(vertexId), getVertexView(succId), "labelBackgroundColor=white");
                    if (algorithm != null) {
                        Color color = algorithm.getEdgeColor(vertexId, succId);
                        changeEdgeColor(vertexId, succId, color);
                    }
                }
            }
        } finally {
            graphComponent.getGraph().getModel().endUpdate();
        }
        graphComponent.getGraph().repaint();
    }

    public final void addVertex(int player) {
        String id = "V" + (model.getVertexCount() + 1);
        model.addVertex(id, player);
        addVertexView(id);
    }

    private mxCell getVertexView(String vertexId) {
        return (mxCell) ((mxGraphModel) graphComponent.getGraph().getModel()).getCell(vertexId);
    }

    private void addVertexView(String vertexId) {
        Object parent = graphComponent.getGraph().getDefaultParent();
        String label = "[" + vertexId + "]";
        if (algorithm != null) {
            String algLabel = algorithm.getLabel(vertexId);
            if (algLabel != null) {
                label += " " + algLabel;
            }
        }
        if (model.getPlayer(vertexId) == Graph.PLAYER1) {
            graphComponent.getGraph().insertVertex(parent, "" + vertexId, label, 100, 100, 80, 30, P1_STYLE);
        } else {
            graphComponent.getGraph().insertVertex(parent, "" + vertexId, label, 100, 100, 80, 30, P2_STYLE);
        }
        updateVertexColor(vertexId);
    }

    private void updateVertexColor(String vertexId) {
        if (algorithm != null) {
            Color color = algorithm.getVertexColor(vertexId);
            Object[] o = {getVertexView(vertexId)};
            if (color != null) {
                graphComponent.getGraph().setCellStyles(mxConstants.STYLE_FILLCOLOR, colorToHex(color), o);
            } else {
                graphComponent.getGraph().setCellStyles(mxConstants.STYLE_FILLCOLOR, "#C3D9FF", o);
            }
            if (algorithm.isInWinningRegion(vertexId)) {
                graphComponent.getGraph().setCellStyles(mxConstants.STYLE_STROKEWIDTH, "3", o);
                graphComponent.getGraph().setCellStyles(mxConstants.STYLE_STROKECOLOR, colorToHex(Color.GREEN.darker()), o);
            } else if (algorithm.isEnded()) {
                graphComponent.getGraph().setCellStyles(mxConstants.STYLE_STROKEWIDTH, "3", o);
                graphComponent.getGraph().setCellStyles(mxConstants.STYLE_STROKECOLOR, colorToHex(Color.RED.darker()), o);
            } else {
                graphComponent.getGraph().setCellStyles(mxConstants.STYLE_STROKEWIDTH, "1", o);
                graphComponent.getGraph().setCellStyles(mxConstants.STYLE_STROKECOLOR, "#6482B9", o);
            }
        }
    }


    private static String colorToHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    public void applyLayout(Layouts layouts) {
        displayGraph();
        mxGraphLayout graphLayout = layouts.getLayout(graphComponent.getGraph());
        graphLayout.execute(graphComponent.getGraph().getDefaultParent());
    }

    public void deleteSelected() {
        mxCell c = ((mxCell) graphComponent.getGraph().getSelectionCell());
        if (c != null) {
            if (c.isVertex()) {
                model.deleteVertex(c.getId());
                Object[] cells = {c};
                graphComponent.getGraph().removeCells(cells);
            } else if (c.isEdge()) {
                model.deleteEdge(c.getSource().getId(), c.getTarget().getId());
                Object[] cells = {c};
                graphComponent.getGraph().removeCells(cells);
            }
        }
    }

    public Graph getGraphModel() {
        return model;
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
        stratDialog.init(model, algorithm);
        stratDialog.setVisible(false);
        updateGraph();
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        stratDialog.setVisible(false);
    }


    public enum Layouts {
        FAST_ORGANIC("Fast Organic", mxFastOrganicLayout.class),
        ORGANIC("Organic", mxOrganicLayout.class),
        HIERARCHICAL("Hierarchical", mxHierarchicalLayout.class),
        CIRCLE("Circle", mxCircleLayout.class),
        COMPACT_TREE("Compact Tree", mxCompactTreeLayout.class);

        private String name;
        private Class<? extends mxGraphLayout> layoutClass;

        Layouts(String name, Class<? extends mxGraphLayout> layoutClass) {
            this.name = name;
            this.layoutClass = layoutClass;
        }

        public String getName() {
            return name;
        }

        public mxGraphLayout getLayout(mxGraph graphView) {
            Class[] types = {mxGraph.class};
            try {
                Constructor<? extends mxGraphLayout> constructor = layoutClass.getConstructor(types);
                return constructor.newInstance(graphView);
            } catch (Exception e) {
                //can't happen
                e.printStackTrace();
            }
            return new mxFastOrganicLayout(graphView);
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
