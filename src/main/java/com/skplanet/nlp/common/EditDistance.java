package com.skplanet.nlp.common;

import java.util.List;

/**
 * Simple Edit distance implementation
 * Created by Donghun Shin
 * Contact: donghun.shin@sk.com, sindongboy@gmail.com
 * Date: 11/2/13
 */
@SuppressWarnings("unused")
public final class EditDistance {

    private EditDistance() {

    }

    /**
     * Simple Similarity Computation between two string
     * it decompose a string into Jaso level and compares them
     * @param s1 first string
     * @param s2 second string
     * @return similarity score
     */
    public static double similarity(String s1, String s2) {
        // length of first item must be longer than seconds one.
        if (s1.length() < s2.length()) {
            // swap them
            String swap = s1;
            s1 = s2;
            s2 = swap;
        }

        // Jaso Decomposition
        List<String> inputA = JasoDecomposition.han2jaso(s1.toLowerCase());
        List<String> inputB = JasoDecomposition.han2jaso(s2.toLowerCase());


        int totalLength = inputA.size();
        // handle empty string
        if (inputA.isEmpty() && inputB.isEmpty()) {
            return 1.0;
        } else if (inputA.isEmpty() || inputB.isEmpty()) {
            return 0.0;
        } else {
            return (totalLength - computeEditDistance(inputA, inputB)) / (double) totalLength;
        }
    }

    /**
     * Simple Edit distance algorithm
     * @param inputA first Jaso-level decomposed string list
     * @param inputB second Jaso-level decomposed string list
     * @return edit distance cost value
     */
    private static int computeEditDistance(List<String> inputA, List<String> inputB) {

        // init. the costs
        int[] costs = new int[inputB.size() + 1];

        for (int i = 0; i <= inputA.size(); i++) {

            int lastValue = i;

            for (int j = 0; j <= inputB.size(); j++) {

                if (i == 0) {
                    costs[j] = j;
                } else {

                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (!inputA.get(i - 1).equals(inputB.get(j - 1))) {
                            newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                        }
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0) {
                costs[inputB.size()] = lastValue;
            }
        }

        return costs[inputB.size()];
    }
}
