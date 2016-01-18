/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.umons.olbregts.graphgame.view;

import be.ac.umons.olbregts.graphgame.io.FileLoader;
import be.ac.umons.olbregts.graphgame.model.implementation.objectoriented.GraphObjectOriented;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        init(FileLoader.loadGraph(fileName));
    }

    private void init(GraphObjectOriented graph) {
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
        vertex1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EditorPane.this.graphPanel.addVertex(1);
            }
        });
        JButton vertex2 = new JButton("Player 2 vertex");
        vertex2.setMaximumSize(new Dimension(Integer.MAX_VALUE, vertex2.getMaximumSize().height));
        vertexAddPanel.add(vertex2);
        vertex2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EditorPane.this.graphPanel.addVertex(2);
            }
        });
        menu.add(vertexAddPanel);
        menu.add(Box.createVerticalStrut(6));


        JButton delete = new JButton("Delete selected");
        delete.setMaximumSize(new Dimension(Integer.MAX_VALUE, delete.getMaximumSize().height));
        menu.add(delete);
        delete.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                EditorPane.this.graphPanel.deleteSelected();
            }
        });
        menu.add(Box.createVerticalStrut(6));

        JButton layout = new JButton("Apply layout");
        layout.setMaximumSize(new Dimension(Integer.MAX_VALUE, layout.getMaximumSize().height));
        menu.add(layout);
        layout.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                EditorPane.this.graphPanel.applyLayout();
            }
        });
        menu.add(Box.createVerticalStrut(12));

        JButton save = new JButton("Save");
        save.setMaximumSize(new Dimension(Integer.MAX_VALUE, save.getMaximumSize().height));
        menu.add(save);
        save.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (fileName == null) {
                    JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
                    int returnVal = fc.showSaveDialog(EditorPane.this);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        fileName = fc.getSelectedFile().getAbsolutePath();
                    }
                }
                if (fileName != null) {
                    try {
                        FileLoader.saveGraph(graphPanel.getGraphModel(), fileName);
                    } catch (UnsupportedEncodingException ex) {
                        Logger.getLogger(EditorPane.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(EditorPane.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        menu.add(Box.createVerticalStrut(6));

        JButton load = new JButton("Load for algorithm");
        load.setMaximumSize(new Dimension(Integer.MAX_VALUE, load.getMaximumSize().height));
        menu.add(load);
        load.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.loadGraph(graphPanel.getGraphModel());
            }
        });

        if (graph == null) {
            graph = new GraphObjectOriented();
        }
        graphPanel = new GraphPanel(graph, false);
        graphPanel.setAutoLayout(false);
        graphPanel.setEditable(true);
        add(BorderLayout.CENTER, graphPanel);
        setVisible(true);
    }
}
