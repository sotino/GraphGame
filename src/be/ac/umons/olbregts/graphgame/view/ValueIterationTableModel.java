/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.umons.olbregts.graphgame.view;

import be.ac.umons.olbregts.graphgame.algorithm.PathAlgorithm;
import be.ac.umons.olbregts.graphgame.algorithm.implementation.ValueIteration;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * @author Simon
 */
public class ValueIterationTableModel extends AbstractTableModel {

    private String[] columnName;
    private int vertexNumber;
    private ArrayList<String[]> data;

    public ValueIterationTableModel(int vertexNumber) {
        this.vertexNumber = vertexNumber;
        columnName = new String[vertexNumber * 2];
        for (int i = 0; i < vertexNumber; i++) {
            columnName[i * 2] = "V[" + (i + 1) + "]";
            columnName[i * 2 + 1] = "V[" + (i + 1) + "] strategy";
        }
        data = new ArrayList<>();
    }

    @Override
    public String getColumnName(int column) {
        return columnName[column];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnName.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data.get(rowIndex)[columnIndex];
    }

    public void addResult(PathAlgorithm pa) {
        if (pa instanceof ValueIteration) {
            ValueIteration vi = (ValueIteration) pa;
            String[] rowData = new String[vertexNumber * 3];
            for (int i = 0; i < vertexNumber; i++) {
                rowData[i * 2] = pa.getLabel(i);//pa.getDistance(i) == Integer.MAX_VALUE ? "inf" : "" + pa.getDistance(i));
                rowData[i * 2 + 1] = pa.getStrategy(i).printChoose();
            }
            data.add(rowData);
            fireTableDataChanged();
        }
    }

    public void reset() {
        data.clear();
        fireTableDataChanged();
    }
}
