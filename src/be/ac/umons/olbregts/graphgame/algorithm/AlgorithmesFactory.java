package be.ac.umons.olbregts.graphgame.algorithm;

import be.ac.umons.olbregts.graphgame.view.wining_condition.WinningPanel;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Simon on 19-01-16.
 */
public class AlgorithmesFactory {

    private final static String SEPARATOR = ";";
    private static List<AlgorithmInfo> algorithmList;


    public static List<AlgorithmInfo> getAvailableAlgorithm() throws ParseException {
        if (algorithmList == null) {
            algorithmList = loadAlgorithm();
        }
        return algorithmList;
    }

    private static List<AlgorithmInfo> loadAlgorithm() throws ParseException {
        try {
            List<AlgorithmInfo> algorithms = new ArrayList<>();
            Scanner fileScanner = new Scanner(new File("algorithm.csv"));
            if (!fileScanner.hasNextLine()) {
                throw new ParseException("The file is empty", 0);
            }
            String currentLine = fileScanner.nextLine();
            String[] title = currentLine.split(SEPARATOR);
            if (title.length != 3 || !title[0].equals("Name") || !title[1].equals("Algorithm") || !title[2].equals("WinningPanel")) {
                throw new ParseException("The first line must contains: \"Name;Algorithm;WinningPanel\"", 0);
            }
            while (fileScanner.hasNextLine()) {
                currentLine = fileScanner.nextLine();
                String[] values = currentLine.split(SEPARATOR);
                if (values.length != 3) {
                    throw new ParseException("Each line must have 3 value separed by ';'", 0);
                }
                String name = values[0];
                Object alg = null;
                try {
                    alg = Class.forName(values[1]).newInstance();
                } catch (InstantiationException e) {
                    throw new ParseException("The class " + values[1] + " can't be instantiated. Remeber that the constructeur must have no parameters", 0);
                } catch (IllegalAccessException e) {
                    throw new ParseException("The constructor of the class " + values[1] + " can't be accessed. Check constructor visibility", 0);
                } catch (ClassNotFoundException e) {
                    throw new ParseException("The class " + values[1] + " can't be found", 0);
                }
                if (!(alg instanceof Algorithm)) {
                    throw new ParseException("The class " + values[1] + " doesn't implement Algorithm", 0);
                }
                Algorithm algorithm = (Algorithm) alg;


                Object wp = null;
                try {
                    wp = Class.forName(values[2]).newInstance();
                } catch (InstantiationException e) {
                    throw new ParseException("The class " + values[2] + " can't be instantiated. Remeber that the constructeur must have no parameters", 0);
                } catch (IllegalAccessException e) {
                    throw new ParseException("The constructor of the class " + values[2] + " can't be accessed. Check constructor visibility", 0);
                } catch (ClassNotFoundException e) {
                    throw new ParseException("The class " + values[2] + " can't be found", 0);
                }
                if (!(wp instanceof WinningPanel)) {
                    throw new ParseException("The third value:" + values[1] + " doesn't implement WinningPanel", 0);
                }
                WinningPanel winningPanel = (WinningPanel) wp;
                algorithms.add(new AlgorithmInfo(name, algorithm, winningPanel));
            }
            return algorithms;
        } catch (FileNotFoundException e) {
            throw new ParseException("The file ./algorithm.csv can't be found", 0);
        }
    }
}
