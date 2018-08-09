package com.skplanet.nlp.common;

/**
 * <br>
 * <br>
 * Created by Donghun Shin
 * <br>
 * Contact: donghun.shin@sk.com, sindongboy@gmail.com
 * <br>
 * Date: 10/29/13
 * <br>
 */
public final class Properties {


    // -------------
    // files
    // -------------
    // properties
    public static final String MAIN_CONFIG = "main.properties";
    public static final String NAVER_CONFIG = "naver.properties";
    public static final String HOPPIN_CONFIG = "hoppin.properties";
    public static final String KMDB_CONFIG = "kmdb.properties";
    public static final String TSTORE_CONFIG = "tstore.properties";

    // resources
    public static final String TITLE_STOPWORD = "title-stopword.dict";
    public static final String NAVER_MAP_CORRECT = "correct.map";
    public static final String NAVER_MAP = "naver.map";
    public static final String KMDB_MAP = "kmdb.map";
    public static final String TERM_ADD_MAP = "m2k_add.tsv";
    public static final String TERM_REMOVE_MAP = "m2k_rem.tsv";

    // -------------
    // fields
    // -------------

    // commons
    public static final String META_FILE = "META";

    // Main Configuration
    public static final String MATCH_THRESHOLD = "MATCH_THRESHOLD";

    // analysis type
    public static final int HOPPIN = 0;
    public static final int NAVER = 1;
    public static final int KMDB = 2;
    public static final int TSTORE = 3;

    // --------------------
    // common static field
    // --------------------
    public static final String FIELD_DELIM = "%";

    /**
     * constructor prevent creating instance
     */
    private Properties() {
    }


}
