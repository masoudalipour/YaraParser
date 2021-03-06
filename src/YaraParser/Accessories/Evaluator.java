package YaraParser.Accessories;

import YaraParser.TransitionBasedSystem.Configuration.CompactTree;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Evaluator {
    public static void evaluate(String testPath, String predictedPath, HashSet<String> puncTags) throws Exception {
        CoNLLReader goldReader = new CoNLLReader(testPath);
        CoNLLReader predictedReader = new CoNLLReader(predictedPath);
        ArrayList<CompactTree> goldConfiguration = goldReader.readStringData();
        ArrayList<CompactTree> predConfiguration = predictedReader.readStringData();
        goldReader.close();
        predictedReader.close();
        double unlabMatch = 0;
        double labMatch = 0;
        int all = 0;
        double fullULabMatch = 0;
        double fullLabMatch = 0;
        int numTree = 0;
        for (int i = 0; i < predConfiguration.size(); i++) {
            HashMap<Integer, Pair<Integer, String>> goldDeps = goldConfiguration.get(i).goldDependencies;
            HashMap<Integer, Pair<Integer, String>> predDeps = predConfiguration.get(i).goldDependencies;
            ArrayList<String> goldTags = goldConfiguration.get(i).posTags;
            numTree++;
            boolean fullMatch = true;
            boolean fullUnlabMatch = true;
            for (int dep : goldDeps.keySet()) {
                if (!puncTags.contains(goldTags.get(dep - 1).trim())) {
                    all++;
                    int gh = goldDeps.get(dep).first;
                    int ph = predDeps.get(dep).first;
                    String gl = goldDeps.get(dep).second.trim();
                    String pl = predDeps.get(dep).second.trim();
                    if (ph == gh) {
                        unlabMatch++;
                        if (pl.equals(gl))
                            labMatch++;
                        else {
                            fullMatch = false;
                        }
                    } else {
                        fullMatch = false;
                        fullUnlabMatch = false;
                    }
                }
            }
            if (fullMatch)
                fullLabMatch++;
            if (fullUnlabMatch)
                fullULabMatch++;
        }
        DecimalFormat decimalFormat = new DecimalFormat("0.00%");
        double labeledAccuracy = labMatch / all;
        double unlabaledAccuracy = unlabMatch / all;
        System.out.println("Labeled accuracy: " + decimalFormat.format(labeledAccuracy));
        System.out.println("Unlabeled accuracy:  " + decimalFormat.format(unlabaledAccuracy));
        double labExact = fullLabMatch / numTree;
        double ulabExact = fullULabMatch / numTree;
        System.out.println("Labeled exact match:  " + decimalFormat.format(labExact));
        System.out.println("Unlabeled exact match:  " + decimalFormat.format(ulabExact));
        System.out.println();
    }
}
