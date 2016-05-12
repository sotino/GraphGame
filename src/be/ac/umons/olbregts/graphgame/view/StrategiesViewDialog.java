package be.ac.umons.olbregts.graphgame.view;

import be.ac.umons.olbregts.graphgame.algorithm.Algorithm;
import be.ac.umons.olbregts.graphgame.algorithm.Strategy;
import be.ac.umons.olbregts.graphgame.model.Graph;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by simon on 11/05/16.
 */
public class StrategiesViewDialog extends JDialog {


    private Graph graph;
    private Algorithm algorithm;
    private boolean firstPack;

    public StrategiesViewDialog(Frame owner) {
        super(owner,"Strategies view",false);
        firstPack=false;
    }


    public void init(Graph graph, Algorithm algorithm) {
        this.graph = graph;
        this.algorithm = algorithm;
        firstPack=false;
    }

    public void updateStrategies() {
        if(graph!= null && algorithm != null) {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
            if (graph.getVertexCount() >= 1) {
                ArrayList<String[]> strats = new ArrayList<>();
                String[] title = new String[graph.getVertexCount() + 1];
                title[0] = "I";
                int i = 1;
                for (String vertexId : graph.getVertexsId()) {
                    title[i] = vertexId;
                    Strategy s = algorithm.getStrategy(vertexId);
                    for (int j = 0; j < s.getSelectedEdge().length; j++) {
                        if( strats.size() <= j){
                            strats.add(j,new String[graph.getVertexCount()+1]);
                            strats.get(j)[0]= ""+j;
                        }
                        strats.get(j)[i] = s.getSelectedEdge()[j];
                    }
                    i++;
                }
                String[][] dataRows = strats.toArray(new String[0][0]);
                setContentPane(new JScrollPane(new JTable(dataRows, title)));
            }
            if (!firstPack) {
                pack();
                firstPack = true;
            }
        }
    }
}
