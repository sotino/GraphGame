/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.umons.olbregts.graphgame.algorithm;

/**
 * @author Simon
 */
public class EscapeStrategy implements Strategy {

    private String mainChoose;
    private String escapeChoose;
    private int limitStep;

    public EscapeStrategy(String mainChoose) {
        this.mainChoose = mainChoose;
        escapeChoose = null;
    }

    public EscapeStrategy(String mainChoose, String escapeChoose, int limitStep) {
        this.mainChoose = mainChoose;
        this.escapeChoose = escapeChoose;
        this.limitStep = limitStep;
    }

    @Override
    public String printChoose() {
        String strat = "/";
        if (mainChoose != null) {
            strat = "-> " + "[V" + mainChoose + ']';
        }
        if (escapeChoose != null) {
            strat += " at cost " + limitStep + " -> " + "[V" + escapeChoose  + ']';
        }
        return strat;
    }

    @Override
    public String[] getSelectedEdge() {
        int nbEdge = 0;
        if (mainChoose != null) {
            nbEdge++;
            if (escapeChoose != null && !escapeChoose.equals(mainChoose)) {
                nbEdge++;
            }
        }
        String[] choose = new String[nbEdge];
        if (mainChoose != null) {
            choose[0] = mainChoose;
            if (escapeChoose != null && !escapeChoose.equals(mainChoose)) {
                choose[1] = escapeChoose;
            }
        }
        return choose;
    }

    public String getMainChoose() {
        return mainChoose;
    }

    public String getEscapeChoose() {
        return escapeChoose;
    }

    public int getLimitStep() {
        return limitStep;
    }
}
