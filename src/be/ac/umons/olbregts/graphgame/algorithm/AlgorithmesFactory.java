package be.ac.umons.olbregts.graphgame.algorithm;

import be.ac.umons.olbregts.graphgame.view.wining_condition.WinningPanel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Simon on 19-01-16.
 */
public class AlgorithmesFactory {

    private final static String SEPARATOR = ";";
    private static List<AlgorithmInfo> algorithmList;


    public static List<AlgorithmInfo> getAvailableAlgorithm() {
        if(algorithmList == null){
            loadAlgorithm();
        }
        return algorithmList;
    }

    private static void loadAlgorithm(){
        try {
            Scanner fileScanner = new Scanner(new File("algorithm.csv"));
            if (!fileScanner.hasNextLine()) {
                System.err.println("error");
            }
            String currentLine = fileScanner.nextLine();
            String[] title = currentLine.split(SEPARATOR);
            if (title.length != 3 || !title[0].equals("Name") || !title[1].equals("Algorithm") || !title[2].equals("WinningPanel")) {
                System.err.println("error");
            }
            algorithmList = new ArrayList<>();
            while (fileScanner.hasNextLine()){
                currentLine = fileScanner.nextLine();
                String[] values = currentLine.split(SEPARATOR);
                if(values.length != 3){
                    System.err.println("error");
                }
                String name = values[0];
                Object alg = Class.forName(values[1]).newInstance();
                if(! (alg instanceof PathAlgorithm)){
                    System.err.println("error");
                }
                PathAlgorithm algorithm = (PathAlgorithm) alg;
                Object wp = Class.forName(values[2]).newInstance();
                if(! (wp instanceof WinningPanel)){
                    System.err.println("error");
                }
                WinningPanel winningPanel = (WinningPanel) wp;
                algorithmList.add(new AlgorithmInfo(name,algorithm,winningPanel));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
