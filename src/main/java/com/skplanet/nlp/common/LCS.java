package com.skplanet.nlp.common;

import org.apache.log4j.Logger;

/**
 * Created by Donghun Shin
 * Contact: donghun.shin@sk.com, sindongboy@gmail.com
 * Date: 11/2/13
 */
@SuppressWarnings("unused")
public final class LCS {

    private static final Logger LOGGER = Logger.getLogger(LCS.class.getName());

    private LCS() {

    }

    // ------------------
    // members
    // ------------------

    /**
     * Compute length of longest common sequence between both strings
     * @param inputA first string input
     * @param inputB second string input
     * @return length of longest common sequence
     */
    public static int getLCSLength(String inputA, String inputB) {

        String x = inputA.trim().toLowerCase();
        String y = inputB.trim().toLowerCase();

        int m = x.length();
        int n = y.length();

        // opt[i][j] = length of LCS of x[i..M] and y[j..N]
        int[][] opt = new int[m + 1][n + 1];

        // compute length of LCS
        for (int i = m - 1; i >= 0; i--) {
            for (int j = n - 1; j >= 0; j--) {
                if (x.charAt(i) == y.charAt(j)) {
                    opt[i][j] = opt[i + 1][j + 1] + 1;
                } else {
                    opt[i][j] = Math.max(opt[i + 1][j], opt[i][j + 1]);
                }
            }
        }

        // recover LCS itself
        StringBuilder commonSequence = new StringBuilder();
        int length = 0;
        int i = 0, j = 0;
        while (i < m && j < n) {
            if (x.charAt(i) == y.charAt(j)) {
                commonSequence.append(x.charAt(i));
                length++;
                i++;
                j++;
            } else if (opt[i + 1][j] >= opt[i][j + 1]) {
                i++;
            } else {

                j++;
            }
        }
        LOGGER.debug("longest common sequence : " + commonSequence.toString());
        return length;
    }

    public static double getLCSRatio(String a, String b) {
        int commonLen = getLCSLength(a, b);
        double maxLen = (double) Math.max(a.length(), b.length());
        return commonLen / maxLen;
    }

}
