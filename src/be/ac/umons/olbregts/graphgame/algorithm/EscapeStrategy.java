/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.umons.olbregts.graphgame.algorithm;

/**
 * @author Simon
 */
public class EscapeStrategy implements Strategy {

    private int mainChoose;
    private int escapeChoose;
    private int limitStep;

    public EscapeStrategy(int mainChoose) {
        this.mainChoose = mainChoose;
        escapeChoose = -1;
    }

    public EscapeStrategy(int mainChoose, int escapeChoose, int limitStep) {
        this.mainChoose = mainChoose;
        this.escapeChoose = escapeChoose;
        this.limitStep = limitStep;
    }

    @Override
    public String printChoose() {
        String strat = "/";
        if (mainChoose != -1) {
            strat = "-> " + "[V" + (mainChoose + 1) + ']';
        }
        if (escapeChoose != -1) {
            strat += " at cost " + limitStep + " -> " + "[V" + (escapeChoose + 1) + ']';
        }
        return strat;
    }

    @Override
    public int[] getSelectedEdge() {
        int nbEdge = 0;
        if (mainChoose != -1) {
            nbEdge++;
            if (escapeChoose != -1 && escapeChoose != mainChoose) {
                nbEdge++;
            }
        }
        int[] choose = new int[nbEdge];
        if (mainChoose != -1) {
            choose[0] = mainChoose;
            if (escapeChoose != -1 && escapeChoose != mainChoose) {
                choose[1] = escapeChoose;
            }
        }
        return choose;
    }

    public int getMainChoose() {
        return mainChoose;
    }

    public int getEscapeChoose() {
        return escapeChoose;
    }

    public int getLimitStep() {
        return limitStep;
    }
}
