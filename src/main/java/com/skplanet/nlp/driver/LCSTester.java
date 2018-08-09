package com.skplanet.nlp.driver;

import com.skplanet.nlp.common.LCS;

/**
 * @author Donghun Shin, donghun.shin@sk.com
 * @date 12/1/14.
 */
public class LCSTester {
    public static void main(String[] args) {
        String a = "급수별 한자";
        String b = "급수한자";
        System.out.println(LCS.getLCSRatio(a, b));
        System.out.println(LCS.getLCSRatio(b, a));
    }
}
