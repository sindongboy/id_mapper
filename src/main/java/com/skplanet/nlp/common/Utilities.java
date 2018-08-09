package com.skplanet.nlp.common;

import java.util.Calendar;
import java.util.List;

/**
 * User Modeling Utility Class
 * <br>
 * <br>
 * Created by Donghun Shin
 * <br>
 * Contact: donghun.shin@sk.com, sindongboy@gmail.com
 * <br>
 * Date: 10/29/13
 * <br>
 */
@SuppressWarnings("unused")
public final class Utilities {

    private static final String BRACKET_PART = "[\\(\\[\\{].*?[\\)\\]\\}]";
    private static final String TITLE_SYMBOL = "[\"'|\\s!-.,\\(\\)\\[\\]\\{\\}:;<>~]+";

    private Utilities() {

    }

    /**
     * Pattern-based Title Normalization
     * @param orgTitle title text to be normalized
     * @return normalized title text
     */
    public static String getNormalizedTitle(String orgTitle) {
        return orgTitle.toLowerCase().replaceAll(TITLE_SYMBOL, "");
    }

    /**
     * Pattern-based + Dictionary-based Normalization
     * @param orgTitle title text to be normalized
     * @param stopwordList stopwords list
     * @return normalized title text
     */
    public static String getNormalizedTitleWithStopword(String orgTitle, List<String> stopwordList) {
        orgTitle = orgTitle.toLowerCase();
        for (String sw : stopwordList) {
            orgTitle = orgTitle.replace(sw, "");
        }
        String newTitle = orgTitle.toLowerCase().replaceAll(BRACKET_PART, "");
        newTitle = newTitle.toLowerCase().replaceAll(TITLE_SYMBOL, "");
        return newTitle;
    }

    /**
     * Check if there exists overlap between two arrays
     * @param arrayA array 1
     * @param arrayB array 2
     * @return true if there exists overlap between two arrays
     */
    public static boolean arrayOverlapCheck(String[] arrayA, String[] arrayB) {
        for (String a : arrayA) {
            for (String b : arrayB) {
                if (a.contains(b) || b.contains(a)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * string array to single line string
     * @param array string array to be converted
     * @param delim delimiter
     * @return string converted array
     */
    public static String arrayToString(String[] array, String delim) {
        String result = "";
        for (int i = 0; i < array.length; i++) {
            if (i == array.length - 1) {
                result += array[i];
                break;
            }
            result += array[i] + delim;
        }
        return result;
    }

    public static String arrayToString(List<String> array, String delim) {
        String result = "";
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).trim().length() == 0) {
                continue;
            }
            if (array.get(i).equals("null")) {
                continue;
            }
            if (i == array.size() - 1) {
                result += array.get(i);
                break;
            }
            result += array.get(i) + delim;
        }
        return result;
    }

    /**
     * string array to single line string
     * @param array string array to be converted
     * @param delim delimiter
     * @return string converted array
     */
    public static String listToString(List<String> array, String delim) {
        String result = "";
        for (int i = 0; i < array.size(); i++) {
            if (i == array.size() - 1) {
                result += array.get(i);
                break;
            }
            result += array.get(i) + delim;
        }
        return result;
    }

    public static boolean isEqualDates(Calendar[] a, Calendar[] b) {
        return false;
    }

    /**
     * Check if both Dates array has common.
     * @param a first date array
     * @param b second date array
     * @param field Calendar field
     * @return true if matched
     */
    public static boolean isEqualDates(Calendar[] a, Calendar[] b, int field) {
        for (Calendar ac : a) {
            for (Calendar bc : b) {
                if (ac.get(field) == bc.get(field)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get weighted score from multiple score based on the score participant
     * @param score socres
     * @param count number of participant for each score
     * @return weighted score
     */
    public static double weightedScore(double[] score, int[] count) {
        double result = -1.0;
        int countTotal = 0;
        double[] weights = new double[count.length];

        for (int c : count) {
            countTotal += c;
        }
        int wIndex = 0;
        for (int c : count) {
            weights[wIndex++] = (double) c / countTotal;
        }

        wIndex = 0;
        for (double s : score) { result += s * weights[wIndex++]; }
        return result;
    }
    public static double weightedScore(List<Double> score, List<Integer> count) {
        double result = 0.0;
        int countTotal = 0;
        double[] weights = new double[count.size()];

        for (int c : count) {
            countTotal += c;
        }

        if (countTotal == 0) {
            return 0.0;
        }

        int wIndex = 0;
        for (int c : count) {
            weights[wIndex++] = (double) c / countTotal;
        }

        wIndex = 0;
        for (double s : score) {
            result += s * weights[wIndex++];
        }
        return result;
    }
}
