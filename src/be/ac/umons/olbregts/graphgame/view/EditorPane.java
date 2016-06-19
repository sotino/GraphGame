/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.umons.olbregts.graphgame.view;

import be.ac.umons.olbregts.graphgame.io.GraphLoader;
import be.ac.umons.olbregts.graphgame.model.Graph;
import be.ac.umons.olbregts.graphgame.model.implementation.objectoriented.GraphObjectOriented;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

/**
 * @author Simon
 */
public class EditorPane extends JPanel {

    private GraphPanel graphPanel;
    private String fileName;
    private MainFrame mainFrame;

    public EditorPane(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        init(null);
    }

    public EditorPane(MainFrame mainFrame, String fileName) throws FileNotFoundException {
        this.mainFrame = mainFrame;
        this.fileName = fileName;
        init(GraphLoader.loadGraph(fileName));
    }

    private void init(Graph graph) {
        setLayout(new BorderLayout());
        JPanel menu = new JPanel();
        add(menu, BorderLayout.WEST);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.add(new JLabel("Menu"));
        menu.add(Box.createVerticalStrut(6));

        JPanel vertexAddPanel = new JPanel();
        vertexAddPanel.setLayout(new BoxLayout(vertexAddPanel, BoxLayout.Y_AXIS));
        vertexAddPanel.setBorder(BorderFactory.createTitledBorder("Add vertex to graph"));
        JButton vertex1 = new JButton("Player 1 vertex");
        vertex1.setMaximumSize(new Dimension(Integer.MAX_VALUE, vertex1.getMaximumSize().height));
        vertexAddPanel.add(vertex1);
        vertex1.addActionListener(e -> EditorPane.this.graphPanel.addVertex(1));
        JButton vertex2 = new JButton("Player 2 vertex");
        vertex2.setMaximumSize(new Dimension(Integer.MAX_VALUE, vertex2.getMaximumSize().height));
        vertexAddPanel.add(vertex2);
        vertex2.addActionListener(e -> EditorPane.this.graphPanel.addVertex(2));
        menu.add(vertexAddPanel);
        menu.add(Box.createVerticalStrut(6));


        JButton delete = new JButton("Delete selected");
        delete.setMaximumSize(new Dimension(Integer.MAX_VALUE, delete.getMaximumSize().height));
        menu.add(delete);
        delete.addActionListener(e -> EditorPane.this.graphPanel.deleteSelected());
        JButton loop = new JButton("Add loop to selected");
        loop.setMaximumSize(new Dimension(Integer.MAX_VALUE, loop.getMaximumSize().height));
        menu.add(loop);
        loop.addActionListener(e -> EditorPane.this.graphPanel.addLoopToSelected());
        menu.add(Box.createVerticalStrut(12));

        JButton save = new JButton("Save");
        save.setMaximumSize(new Dimension(Integer.MAX_VALUE, save.getMaximumSize().height));
        menu.add(save);
        save.addActionListener(e -> {
            if (fileName == null) {
                JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
                int returnVal = fc.showSaveDialog(EditorPane.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    fileName = fc.getSelectedFile().getAbsolutePath();
                }
            }
            if (fileName != null) {
                try {
                    GraphLoader.saveGraph(graphPanel.getGraphModel(), fileName);
                } catch (UnsupportedEncodingException ex) {
                    JOptionPane.showMessageDialog(EditorPane.this, "UTF-8 encoding was not supported.\nThe graph was not saved.", "Error during the save", JOptionPane.ERROR_MESSAGE);
                } catch (FileNotFoundException ex) {
                    JOptionPane.showMessageDialog(EditorPane.this, "The file can't be created.\nThe graph was not saved.", "Error during the save", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        menu.add(Box.createVerticalStrut(6));

        JButton load = new JButton("Load for algorithm");
        load.setMaximumSize(new Dimension(Integer.MAX_VALUE, load.getMaximumSize().height));
        menu.add(load);
        load.addActionListener(e -> mainFrame.loadGraph((GraphObjectOriented) graphPanel.getGraphModel()));

        if (graph == null) {
            graph = new GraphObjectOriented();
        }
        graphPanel = new GraphPanel(graph);
        graphPanel.setEditable(true);
        add(BorderLayout.CENTER, graphPanel);
        setVisible(true);
    }
}
