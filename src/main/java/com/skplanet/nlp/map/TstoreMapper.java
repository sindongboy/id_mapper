package com.skplanet.nlp.map;

import com.skplanet.nlp.common.Properties;
import com.skplanet.nlp.common.Utilities;
import com.skplanet.nlp.config.Configuration;
import com.skplanet.nlp.controller.UnifiedMetaController;
import com.skplanet.nlp.data.TstoreMeta;
import com.skplanet.nlp.data.UnifiedMeta;
import org.apache.log4j.Logger;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * Tstore ID Mapper
 *
 * @author Donghun Shin, donghun.shin@sk.com
 * @date 8/21/14.
 */
public class TstoreMapper extends AbstractMapper<TstoreMeta> {
    private static final Logger LOGGER = Logger.getLogger(TstoreMapper.class.getName());

    private UnifiedMetaController uController = null;
    private Map<String, String> tid2uidMap = null;

    /**
     * Constructor
     */
    public TstoreMapper() {
        this.tid2uidMap = new HashMap<String, String>();
    }

    public void init() {
        // load default resources
        super.init();

        // ------------------
        // load unified meta
        // ------------------
        uController = new UnifiedMetaController();
        uController.init();
        this.unifiedMetaList = uController.load();
        this.unifiedMetaMapByUID = uController.getUnifiedMetaMap();
        this.currentUID = uController.getMaxUnifiedID() + 1;

        Configuration config = Configuration.getInstance();
        try {
            config.loadProperties(Properties.TSTORE_CONFIG);
        } catch (IOException e) {
            LOGGER.error("Failed to load properties : " + Properties.TSTORE_CONFIG, e);
        }
        File tstoreMetaFile = new File(config.getResource(config.readProperty(Properties.TSTORE_CONFIG, Properties.META_FILE)).getFile());

        // load tstore meta
        String line;
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(tstoreMetaFile));

            while ((line = reader.readLine()) != null) {
                if (line.trim().length() == 0) {
                    continue;
                }

                /*
                0: PROD_ID
                1: PROD_NM
                2: PROD_DTL_DESC
                3: PROD_GRD_NM
                4: GENRE_NM
                5: ISSUE_DAY
                6: ARTIST1_NM
                7: ARTIST2_NM
                8: AVG_EVLU_SCORE
                9: PATICPERS_CNT
                10: PRCHS_CNT
                */
                
                String[] fields = line.split("\\t");
                TstoreMeta meta = new TstoreMeta();
                // PROD_ID
                meta.setPid(fields[0]);
                // PROD_NM
                meta.setTitle(fields[1]);
                // PROD_DTL_DESC
                meta.setSynopsis(fields[2]);
                // PROD_GRD_NM
                meta.setRate(fields[3]);
                // GENRE_NM
                meta.setGenre(fields[4]);
                // ISSUE_DAY
                meta.setDate(fields[5].replace(".", ""));
                // ARTIST1_NM (actor)
                meta.setActors(fields[6]);
                // ARTIST2_NM (directory)
                meta.setDirector(fields[7]);
                // AVG_EVLU_SCORE
                meta.setAvgScore(fields[8]);
                // PATICPERS_CNT
                meta.setScoreCount(fields[9]);
                // PRCHS_CNT
                meta.setPurchaseCount(fields[10]);

                this.productMetaList.add(meta);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            LOGGER.error("Tstore Properties File not found : " + Properties.TSTORE_CONFIG, e);
        } catch (IOException e) {
            LOGGER.error("Failed to read Tstore Properties : " + Properties.TSTORE_CONFIG, e);
        }
    }

    /**
     * map and unify the given meta
     *
     * @return list of {@link UnifiedMeta}
     */
    public List<UnifiedMeta> map() {

        int count = 0;
        for (TstoreMeta tmeta : this.productMetaList) {
            if (count % 100 == 0) {
                LOGGER.info("tstore mapping : " + count);
            }
            count++;
            if (uController.existTstoreID(tmeta.getPid())) {
                LOGGER.info("tstore id (" + tmeta.getPid() + ") already exist");
                continue;
            }

            String normalizedTitle = Utilities.getNormalizedTitleWithStopword(tmeta.getTitle(), this.stopwordList);
            String uid = findExistMeta(normalizedTitle, tmeta.getDate(), tmeta.getDirector(), tmeta.getActors(), false);
            if (uid != null) {
                LOGGER.debug("tstore meta matched : " + unifiedMetaMapByUID.get(uid).getTitle() + "(" + uid + ") - " + tmeta.getTitle() + "(" + tmeta.getPid() + ")");
                UnifiedMeta um = this.unifiedMetaMapByUID.get(uid);
                this.unifiedMetaMapByUID.get(uid).addTstoreIdList(tmeta.getPid());
                this.unifiedMetaMapByUID.get(uid).setGenre(tmeta.getGenre());
                this.unifiedMetaMapByUID.get(uid).setGenreBigram(tmeta.getGenreBigram());
                this.unifiedMetaMapByUID.get(uid).setTstoreScore(tmeta.getAvgScore());
                this.unifiedMetaMapByUID.get(uid).setTstoreScoreCount(tmeta.getScoreCount());
                this.unifiedMetaMapByUID.get(uid).setTstorePurchaseCount(tmeta.getPurchaseCount());
                this.tid2uidMap.put(tmeta.getPid(), uid);
            } else {
                LOGGER.debug("tstore meta not found : " + tmeta.getTitle() + "(" + tmeta.getPid() + ") ==> added (" + currentUID + ")");
                // add new unified meta
                UnifiedMeta m = new UnifiedMeta();
                m.setUid(String.format(IDFORMAT, this.currentUID++));
                m.setTstoreIdList(tmeta.getPid());
                m.setTitle(tmeta.getTitle());
                m.setSynopsis(tmeta.getSynopsis());
                m.setRate(tmeta.getRate());
                m.setDirectors(tmeta.getDirector());
                m.setActors(tmeta.getActors());
                m.setDates(tmeta.getDate());
                m.setGenre(tmeta.getGenre());
                m.setGenreBigram(tmeta.getGenreBigram());
                m.setTstorePurchaseCount(tmeta.getPurchaseCount());
                m.setTstoreScore(tmeta.getAvgScore());
                m.setTstoreScoreCount(tmeta.getScoreCount());
                m.setTstorePurchaseCount(tmeta.getPurchaseCount());
                this.unifiedMetaList.add(m);
                this.unifiedMetaMapByUID.put(m.getUid(), m);
            }
        }
        LOGGER.info("Tstore Mapping finished : " + unifiedMetaList.size());
        /*
        List<UnifiedMeta> result = new ArrayList<UnifiedMeta>();
        for (String key : this.unifiedMetaMapByUID.keySet()) {
            result.add(this.unifiedMetaMapByUID.get(key));
        }
        return result;
        */
        return this.unifiedMetaList;
    }

    /**
     * Get Tstore Meta List
     * @return list of {@link com.skplanet.nlp.data.TstoreMeta}
     */
    public List<TstoreMeta> getTstoreMetaList() {
        return this.productMetaList;
    }

    /**
     * find given movie meta from previously loaded unified meta list
     *
     * @param title     title
     * @param dates     date
     * @param directors directors
     * @param actors    actors
     * @param org
     * @return UID for the matched, or returns null
     */
    @Override
    protected String findExistMeta(String title, List<Calendar> dates, List<String> directors, List<String> actors, boolean org) {
        double result;
        double max = 0.0;
        String uid = null;

        Set<String> keys = this.unifiedMetaMapByUID.keySet();
        for (String k : keys) {
            if (uidTarget.contains(k)) {
                continue;
            }
            UnifiedMeta um = this.unifiedMetaMapByUID.get(k);
            String normalizedTitle;
            if (org) {
                normalizedTitle = Utilities.getNormalizedTitle(um.getTitleOrg());
            } else {
                normalizedTitle = Utilities.getNormalizedTitleWithStopword(um.getTitle(), this.stopwordList);
            }

            // check title
            result = checkTitle(normalizedTitle, title);
            if (result == BigDecimal.ZERO.doubleValue()) {
                continue;
            }

            double dateScore = BigDecimal.ZERO.doubleValue();
            double directorScore;
            double actorScore;

            // check date
            /*
            for (Calendar date : dates) {
                for (Calendar umDate : um.getDates()) {
                    double tmpScore = checkDate(umDate, date);
                    if (dateScore < tmpScore) {
                        dateScore = tmpScore;
                    }
                }
            }
            */
            dateScore = DATE_WEIGHT;

            // check director
            directorScore = checkDirector(um.getDirectors(), directors);

            // check actors
            actorScore = checkActors(um.getActors(), actors);

            result += dateScore + directorScore + actorScore;

            if (result < matchingThreshold) {
                // TODO: add some heuristics for exceptional cases?
                // 1. date + director
                // 2. director + actors
                continue;
            }

            if (max < result) {
                max = result;
                uid = um.getUid();
            }
        }
        return uid;
    }
	
	public Map<String, String> getIdPair() {
		return this.tid2uidMap;
	}
}
