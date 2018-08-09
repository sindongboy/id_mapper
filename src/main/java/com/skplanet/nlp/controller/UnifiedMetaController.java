package com.skplanet.nlp.controller;

import com.skplanet.nlp.common.Properties;
import com.skplanet.nlp.config.Configuration;
import com.skplanet.nlp.data.UnifiedMeta;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * Unified Meta Controller
 * <br>
 * <br>
 * Created by Donghun Shin
 * <br>
 * Contact: donghun.shin@sk.com, sindongboy@gmail.com
 * <br>
 * Date: 3/20/14
 * <br>
 */
@SuppressWarnings("unused")
public class UnifiedMetaController implements Controller<UnifiedMeta> {
    private static final Logger LOGGER = Logger.getLogger(UnifiedMetaController.class.getName());

    private static final String FIELD_DELIM = "\\t";
    private static final String ITEM_DELIM = "\\^";

    // Unified Meta File
    private File unifiedMetaFile = null;

    // Unified Meta List
    private List<UnifiedMeta> unifiedMetaList = null;

    // Map to the Unified Meta
    private Map<String, UnifiedMeta> unifiedMetaMapByHoppinID = null;
    private Map<String, UnifiedMeta> unifiedMetaMapByTstoreID = null;
    private Map<String, UnifiedMeta> unifiedMetaMapByUID = null;

    // Hoppin, Tstore ID Set
    private Set<String> hoppinIDset = null;
    private Set<String> tstoreIDset = null;
    private Set<Integer> unifiedIDset = null;

    /**
     * Constructor
     */
    public UnifiedMetaController() {
        this.unifiedMetaList = new ArrayList<UnifiedMeta>();

        this.unifiedMetaMapByHoppinID = new HashMap<String, UnifiedMeta>();
        this.unifiedMetaMapByTstoreID = new HashMap<String, UnifiedMeta>();
        this.unifiedMetaMapByUID = new HashMap<String, UnifiedMeta>();

        this.hoppinIDset = new HashSet<String>();
        this.tstoreIDset = new HashSet<String>();
        this.unifiedIDset = new HashSet<Integer>();
    }

    /**
     * Initialize the controller
     */
    @Override
    public void init() {
        Configuration config = Configuration.getInstance();
        try {
            config.loadProperties(Properties.MAIN_CONFIG);
        } catch (IOException e) {
            LOGGER.error("failed to load main configuration file: " + Properties.MAIN_CONFIG, e);
        }

        // get unified meta file to be loaded if exists
        URL metaURL = config.getResource(config.readProperty(Properties.MAIN_CONFIG, Properties.META_FILE));
        if (metaURL != null) {
            this.unifiedMetaFile = new File(metaURL.getFile());
        } else {
            LOGGER.debug("no unified meta file exists: " + config.readProperty(Properties.MAIN_CONFIG, Properties.META_FILE));
        }
    }

    /**
     * load meta data (if exists)
     *
     * @return list of Meta data
     */
    @Override
    public List<UnifiedMeta> load() {

        if (!this.unifiedMetaFile.exists()) {
            return null;
        }
        BufferedReader reader;
        String line;


        try {
            reader = new BufferedReader(new FileReader(this.unifiedMetaFile));
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() == 0) {
                    continue;
                }

                String[] fields = line.split(FIELD_DELIM);
                if (fields.length != 19) {
                    LOGGER.warn("wrong format : " + line);
                    continue;
                }

                /*
                0: UID
                1: HOPPIN IDS
                2: TSTORE IDS
                3: TITLE
                4: TITLE ORG
                5: SYNOPSIS
                6: RELEASE DATE
                7: RATED
                8: GENRE
                9: GENRE BIGRAM
                10: DIRECTORS
                11: ACTORS
                12: NATIONAL CODE
                13: KEYWORDS
                14: SCORE
                15: SCORE COUNT
                16: PURCHASE COUNT
                17: ADD TERM
                18: REMOVE TERM
                 */

                UnifiedMeta m = new UnifiedMeta();
                m.setUid(fields[0]);
                unifiedIDset.add(Integer.parseInt(fields[0].substring(1)));
                m.setHoppinIdList(fields[1]);
                m.setTstoreIdList(fields[2]);
                m.setTitle(fields[3]);
                m.setTitleOrg(fields[4]);
                m.setSynopsis(fields[5]);
                m.setDates(fields[6]);
                m.setRate(fields[7]);
                m.setGenre(fields[8]);
                m.setGenreBigram(Arrays.asList(fields[9].split(ITEM_DELIM)));
                m.setDirectors(fields[10]);
                m.setActors(fields[11]);
                m.setNationalCode(fields[12]);
                m.setKeywords(fields[13]);
                // score
                String[] scoreField = fields[14].split(ITEM_DELIM);
                if (scoreField.length != 3) {
                    LOGGER.error("Score fields must be length of 3");
                }
                m.setHoppinScore(Double.parseDouble(scoreField[0]));
                m.setNaverScore(Double.parseDouble(scoreField[1]));
                m.setTstoreScore(scoreField[2]);
                m.hoppinBackupScore(scoreField[0]);
                m.naverBackupScore(scoreField[1]);

                // score count
                String[] scoreCountField = fields[15].split(ITEM_DELIM);
                if (scoreCountField.length != 3) {
                    LOGGER.error("Score count fields must be length of 3");
                }
                m.setHoppinScoreCount(Integer.parseInt(scoreCountField[0]));
                m.setNaverScoreCount(Integer.parseInt(scoreCountField[1]));
                m.setTstoreScoreCount(scoreCountField[2]);
                m.hoppinBackupScoreCount(scoreCountField[0]);
                m.naverBackupScoreCount(scoreCountField[1]);

                // purchase count
                String[] purchaseField = fields[16].split(ITEM_DELIM);
                m.setHoppinPurchaseCount(purchaseField[0]);
                m.setTstorePurchaseCount(purchaseField[1]);

                // add / remove term
                m.setAddTermList(Arrays.asList(fields[17].split(ITEM_DELIM)));
                m.setRemoveTermList(Arrays.asList(fields[18].split(ITEM_DELIM)));

                // set hoppin id set
                for (String hoppinID : m.getHoppinIdList()) {
                    this.hoppinIDset.add(hoppinID);
                }

                // set hoppin id to unified meta map
                for (String hoppinID : m.getHoppinIdList()) {
                    this.unifiedMetaMapByHoppinID.put(hoppinID, m);
                }

                this.unifiedMetaList.add(m);
                this.unifiedMetaMapByUID.put(m.getUid(), m);

            }
            reader.close();
            LOGGER.info("unified meta loaded successfully!: " + this.unifiedMetaList.size());

        } catch (FileNotFoundException e) {
            LOGGER.error("file doesn't exist: " + this.unifiedMetaFile.getName(), e);
        } catch (IOException e) {
            LOGGER.error("failed to read unified meta file : " + this.unifiedMetaFile.getName(), e);
        }

        return this.unifiedMetaList;
    }

    /**
     * check if given hoppin id is already exists
     * @param hid given hoppin id
     * @return true if exists otherwise false
     */
    public boolean existHoppinID(String hid) {
        return this.hoppinIDset.contains(hid);
    }

    /**
     * check if given tstore id is already exists
     * @param tid given tstore id
     * @return true if exists otherwise false
     */
    public boolean existTstoreID(String tid) {
        return this.tstoreIDset.contains(tid);
    }

    /**
     * Get Unified Meta Map from Hoppin ID
     * @param hid hoppin id
     * @return {@link UnifiedMeta}
     */
    public UnifiedMeta getUnifiedMetaFromHoppinID(String hid) {
        return this.unifiedMetaMapByHoppinID.get(hid);
    }

    /**
     * Get Unified Meta Map from Tstore ID
     * @param tid tstore id
     * @return {@link UnifiedMeta}
     */
    public UnifiedMeta getUnifiedMetaFromTstoreID(String tid) {
        return this.unifiedMetaMapByTstoreID.get(tid);
    }


    /**
     * Get Unified Meta Map from Unified ID
     * @return UID to Unified Meta map
     */
    public Map<String, UnifiedMeta> getUnifiedMetaMap() {
        return this.unifiedMetaMapByUID;
    }

    /**
     * Get Max unified id for loaded unified meta list
     * @return max unified id
     */
    public int getMaxUnifiedID() {
        if (this.unifiedIDset.isEmpty()) {
            return 0;
        }
        return Collections.max(this.unifiedIDset);
    }

}
