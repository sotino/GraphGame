/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.umons.olbregts.graphgame.view;

import be.ac.umons.olbregts.graphgame.algorithm.PathAlgorithm;
import be.ac.umons.olbregts.graphgame.model.implementation.objectoriented.GraphObjectOriented;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Simon
 */
class ValueIterationRenderer implements ResultRenderer {

    private JFrame resultViewer;
    private GraphView parent;
    private GraphObjectOriented graph;
    private ValueIterationTableModel tableModel;
    private PathAlgorithm pa;
    private JTable table;

    public ValueIterationRenderer(GraphView parent, GraphObjectOriented graph, PathAlgorithm pa) {
        this.parent = parent;
        this.graph = graph;
        this.pa = pa;
    }

    @Override
    public void render() {
        if (resultViewer == null) {
            resultViewer = new JFrame("Value iteration results");
            resultViewer.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    resultViewer = null;
                    super.windowClosing(e);
                }
            });
            tableModel = new ValueIterationTableModel(graph.getVertexNumber());
            table = new JTable(tableModel);
            for (int i = 0; i < graph.getVertexNumber(); i++) {
                table.getColumnModel().getColumn(i * 2).setMaxWidth(35);
                table.getColumnModel().getColumn(i * 2).setMinWidth(35);
                table.getColumnModel().getColumn(i * 2 + 1).setMinWidth(150);
            }
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            JScrollPane sp = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            resultViewer.add(sp);
            resultViewer.pack();
            resultViewer.setSize(575, 462);
            resultViewer.setVisible(true);
        }
        tableModel.addResult(pa);
        Rectangle lastCell = table.getCellRect(table.getRowCount() - 1, 0, true);
        table.scrollRectToVisible(lastCell);
        if (pa.isEnded()) {
            parent.updateGraph();
        }
    }

    @Override
    public void reset() {
        if (resultViewer != null) {
            tableModel.reset();
            resultViewer.dispose();
            resultViewer = null;
            parent.resetGraph();
        }
    }
}
