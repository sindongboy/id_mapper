package com.skplanet.nlp.map;

import com.google.common.collect.ArrayListMultimap;
import com.skplanet.nlp.common.Properties;
import com.skplanet.nlp.common.Utilities;
import com.skplanet.nlp.config.Configuration;
import com.skplanet.nlp.controller.UnifiedMetaController;
import com.skplanet.nlp.data.NaverMeta;
import com.skplanet.nlp.data.UnifiedMeta;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Naver Mapper
 * <br>
 * <br>
 * Created by Donghun Shin
 * <br>
 * Contact: donghun.shin@sk.com, sindongboy@gmail.com
 * <br>
 * Date: 11/1/13
 * <br>
 */
public class NaverMapper extends AbstractMapper<NaverMeta> {
    private static final Logger LOGGER = Logger.getLogger(NaverMapper.class.getName());

    //----------------------------------//
    // members
    //----------------------------------//
    private ArrayListMultimap<String, String> IDPair = null;
    // unified id to naver id map manually corrected
    private Map<String, String> correctMap = null;

    //----------------------------------//
    // methods
    //----------------------------------//

    // constructors
    public NaverMapper() {
        super();
        this.IDPair = ArrayListMultimap.create();
    }

    /**
     * {@inheritDoc}
     * @see AbstractMapper#init()
     */
    public void init() {
        // ---------------------------------------- //
        // load default resources ( stopwords etc. )
        // ---------------------------------------- //
        super.init();

        // ---------------------------------------- //
        // load unified meta list
        // ---------------------------------------- //
        uController = new UnifiedMetaController();
        uController.init();
        this.unifiedMetaList = uController.load();
        this.unifiedMetaMapByUID = uController.getUnifiedMetaMap();
        this.currentUID = uController.getMaxUnifiedID() + 1;

        // ---------------------------------------- //
        // load Naver Meta
        // ---------------------------------------- //
        Configuration config = Configuration.getInstance();
        File naverMetaFile = null;
        try {
            config.loadProperties(Properties.NAVER_CONFIG);
            URL naverMetaUrl = config.getResource(config.readProperty(Properties.NAVER_CONFIG, Properties.META_FILE));
            naverMetaFile = new File(naverMetaUrl.getFile());
        } catch (IOException e) {
            LOGGER.error("Failed read Naver Properties : " + Properties.NAVER_CONFIG, e);
        }

        BufferedReader reader;
        String line;
        try {
            reader = new BufferedReader(new FileReader(naverMetaFile));
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() == 0 || line.startsWith("#")) {
                    continue;
                }

                String[] fields = line.split("\t");
                if (fields.length != 12) {
                    LOGGER.debug("wrong format : " + line);
                    continue;
                }

                /*
                0: naver id
                1: naver title kr
                2: naver title en
                3: hoppin title kr
                4: hoppin title en
                5: released date
                6: directors
                7: actors
                8: scoring participant
                9: score
                10 : in theater
                11 : crawl based org. title
                 */

                NaverMeta m = new NaverMeta();
                m.setNid(fields[0]);
                m.setNaverTitle(fields[1]);
                m.setNaverTitleOrg(fields[2]);
                m.setServiceTitle(fields[3]);
                m.setServiceTitleOrg(fields[4]);
                m.setReleasedDate(fields[5], "\\^");
                m.setDirectors(fields[6].split("\\^"));
                m.setActors(fields[7].split("\\^"));
                m.setScoreCount(Integer.parseInt(fields[8].replace(",","")));
                m.setScore(Float.parseFloat(fields[9]));
                if ("O".equals(fields[11])) {
                    m.setOrgTitleCrawl(true);
                }

                this.productMetaList.add(m);
            }
            reader.close();

        } catch (IOException e) {
            LOGGER.error("failed to load configuration file: " + Properties.NAVER_CONFIG, e);
        }
        LOGGER.info("Naver Meta is loaded successfully: " + this.productMetaList.size());
    }

    /**
     * map and unify the given meta
     *
     * @return list of {@link com.skplanet.nlp.data.UnifiedMeta}
     */
    @Override
    public List<UnifiedMeta> map() {
        // load mapping correction reference
        loadMapCorrection();
        for (String nid : this.correctMap.keySet()) {
            // 수집되지 않은 네이버 영화 목록임
            if (this.getNaverMetaFromNaverID(nid) == null) {
                LOGGER.info("NOT CRAWLED NAVER MOVIE ID: " + nid + " - " + this.correctMap.get(nid));
                continue;
            }

            if (this.unifiedMetaMapByPID.containsKey(this.correctMap.get(nid))) {
                String uid = this.unifiedMetaMapByPID.get(this.correctMap.get(nid)).getUid();
                if (this.IDPair.containsKey(uid)) {
                    if (!this.IDPair.get(uid).contains(nid)) {
                        this.IDPair.put(uid, nid);
                    }
                } else {
                    this.IDPair.put(uid, nid);
                }

                this.unifiedMetaMapByUID.get(uid).setNaverScore(this.getNaverMetaFromNaverID(nid).getScore());
                this.unifiedMetaMapByUID.get(uid).setNaverScoreCount(this.getNaverMetaFromNaverID(nid).getScoreCount());

                uidTarget.add(uid);
            }
        }

        int totalCount = this.productMetaList.size();
        int count = 1;
        int mapCount = uidTarget.size();
        for (NaverMeta naverMeta : this.productMetaList) {
            if (this.correctMap.containsKey(naverMeta.getNid())) {
                continue;
            }
            String normTitle;
            String uID = null;

            /*
            if (naverMeta.isOrgTitleCrawl()) {
                for (String naverTitleOrg : naverMeta.getNaverTitleOrg()) {
                    normTitle = Utilities.getNormalizedTitle(naverTitleOrg);
                    uID = this.findExistMeta(normTitle, naverMeta.getReleasedDate(), naverMeta.getDirectors(), naverMeta.getActors(), true);
                    if (uID != null) {
                        break;
                    }
                }
            } else {
                normTitle = Utilities.getNormalizedTitleWithStopword(naverMeta.getNaverTitle(), this.stopwordList);
                uID = this.findExistMeta(normTitle, naverMeta.getReleasedDate(), naverMeta.getDirectors(), naverMeta.getActors(), false);
            }
            */
            normTitle = Utilities.getNormalizedTitleWithStopword(naverMeta.getNaverTitle(), this.stopwordList);
            uID = this.findExistMeta(normTitle, naverMeta.getReleasedDate(), naverMeta.getDirectors(), naverMeta.getActors(), false);

            if (uID != null) { // exists
                if (this.IDPair.containsKey(uID)) {
                    if (!this.IDPair.get(uID).contains(naverMeta.getNid())) {
                        this.IDPair.put(uID, naverMeta.getNid());
                    }
                } else {
                    this.IDPair.put(uID, naverMeta.getNid());
                }

                this.unifiedMetaMapByUID.get(uID).setNaverScore(naverMeta.getScore());
                this.unifiedMetaMapByUID.get(uID).setNaverScoreCount(naverMeta.getScoreCount());
                mapCount++;
            }

            if (count % 100 == 0) {
                LOGGER.info("processing : "+ count + "/" + totalCount + " (" + mapCount + " matched )");
            }
            count++;
        }

        LOGGER.info("Unified meta updated: " + this.unifiedMetaList.size());
        LOGGER.debug("Total number of naver id mapped : " + this.IDPair.size());

        return this.unifiedMetaList;
    }

    /**
     * Get Naver ID and Unified ID pair
     *
     * @return naver id and unified id map : {@link ArrayListMultimap}
     */
    public ArrayListMultimap<String, String> getIDPair() {
        if (this.IDPair.isEmpty()) {
            LOGGER.warn("Must call map() first to get id pair!");
            return null;
        }
        return this.IDPair;
    }

    /**
     * load manually corrected naver id to product id map
     */
    private void loadMapCorrection() {
        this.correctMap = new HashMap<String, String>();

        // load corrected naver id to product id map
        Configuration config = Configuration.getInstance();
        File file = new File(config.getResource(Properties.NAVER_MAP_CORRECT).getFile());
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() == 0 || line.trim().startsWith("#")) {
                    continue;
                }
                String[] fields = line.split("\t");
                if (fields.length != 2) {
                    continue;
                }
                // fields
                // 0 : naver id
                // 1 : product id (naver id)
                this.correctMap.put(fields[0], fields[1]);
            }
            reader.close();
            LOGGER.info("naver map correction loaded : " + this.correctMap.size());
        } catch (FileNotFoundException e) {
            LOGGER.error("file not found : " + file.getName(), e);
        } catch (IOException e) {
            LOGGER.error("failed to read : " + file.getName(), e);
        }
    }


    /**
     * Get {@link NaverMeta} from Naver ID
     * @param id naver id
     * @return {@link NaverMeta}
     */
    private NaverMeta getNaverMetaFromNaverID(String id) {
        for (NaverMeta nm : this.productMetaList) {
            if (nm.getNid().equals(id)) {
                return nm;
            }
        }
        return null;
    }
}
