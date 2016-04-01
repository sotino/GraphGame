/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.umons.olbregts.graphgame.view;

import be.ac.umons.olbregts.graphgame.io.GraphLoader;
import be.ac.umons.olbregts.graphgame.model.implementation.objectoriented.GraphObjectOriented;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Simon
 */
public class MainFrame extends javax.swing.JFrame {

    private javax.swing.JLabel homeImage;

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {

        homeImage = new javax.swing.JLabel();
        JMenuBar jMenuBar1 = new JMenuBar();
        JMenu jMenu1 = new JMenu();
        JMenuItem jMenuItem3 = new JMenuItem();
        JMenuItem jMenuItem1 = new JMenuItem();
        JMenuItem jMenuItem2 = new JMenuItem();
        JMenuItem jMenuItem4 = new JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Two player graph");
        setMinimumSize(new java.awt.Dimension(800, 600));
        setPreferredSize(new java.awt.Dimension(800, 600));

        homeImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/home.jpg")));
        homeImage.setText("homeImage");
        getContentPane().add(homeImage, java.awt.BorderLayout.CENTER);

        jMenu1.setText("Graph");

        jMenuItem3.setText("Create");
        jMenuItem3.addActionListener(evt -> jMenuItem3ActionPerformed(evt));
        jMenu1.add(jMenuItem3);

        jMenuItem1.setText("Load");
        jMenuItem1.addActionListener(evt -> jMenuItem1ActionPerformed(evt));
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText("Edit");
        jMenuItem2.addActionListener(evt -> jMenuItem2ActionPerformed(evt));
        jMenu1.add(jMenuItem2);

        jMenuItem4.setText("Benchmark");
        jMenuItem4.addActionListener(evt -> jMenuItem4ActionPerformed(evt));
        jMenu1.add(jMenuItem4);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        pack();
    }

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {
        getContentPane().removeAll();
        getContentPane().add(new EditorPane(this), BorderLayout.CENTER);
        pack();
    }

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String fileName = fc.getSelectedFile().getAbsolutePath();
                GraphObjectOriented graph = GraphLoader.loadGraph(fileName);
                loadGraph(graph);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String fileName = fc.getSelectedFile().getAbsolutePath();
                getContentPane().removeAll();
                getContentPane().add(new EditorPane(this, fileName), BorderLayout.CENTER);
                pack();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {
        getContentPane().removeAll();
        getContentPane().add(new BenchMark(), BorderLayout.CENTER);
        pack();
    }

    protected void loadGraph(GraphObjectOriented graphModel) {
        getContentPane().removeAll();
        try {
            getContentPane().add(new AlgorithmScreen(graphModel), BorderLayout.CENTER);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Error during the parsing of the configuration file.\n" + e.getMessage(), "Error while loading available algorithms", JOptionPane.ERROR_MESSAGE);
            getContentPane().add(homeImage, java.awt.BorderLayout.CENTER);
            repaint();
        }
        pack();
    }
}
