package com.skplanet.nlp.map;

import com.skplanet.nlp.common.Properties;
import com.skplanet.nlp.common.Utilities;
import com.skplanet.nlp.config.Configuration;
import com.skplanet.nlp.controller.UnifiedMetaController;
import com.skplanet.nlp.data.KMDBMeta;
import com.skplanet.nlp.data.UnifiedMeta;
import org.apache.log4j.Logger;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;

/**
 * KMDB Meta mapper
 * <br>
 * <br>
 * Created by Donghun Shin
 * <br>
 * Contact: donghun.shin@sk.com, sindongboy@gmail.com
 * <br>
 * Date: 11/1/13
 * <br>
 */
public class KMDBMapper extends AbstractMapper<KMDBMeta> {
    private static final Logger LOGGER = Logger.getLogger(KMDBMapper.class.getName());

    //----------------------------------//
    // members
    //----------------------------------//
    private Map<String, Double> scoreMap = null;
    private Map<String, String> preMap = null;
    private List<UnifiedMeta> reducedUnifiedMetaList = null;

    //----------------------------------//
    // methods
    //----------------------------------//

    // constructors
    public KMDBMapper() {
        super();
        this.scoreMap = new HashMap<String, Double>();
        this.productMetaList = new ArrayList<KMDBMeta>();
        this.preMap = new HashMap<String, String>();
        this.reducedUnifiedMetaList = new ArrayList<UnifiedMeta>();
    }

    /**
     * {@inheritDoc}
     * @see AbstractMapper#init()
     */
    public void init() {
        // -------------------------- //
        // load default resource
        // -------------------------- //
        super.init();

        // -------------------------- //
        // load unified meta list
        // -------------------------- //
        uController = new UnifiedMetaController();
        uController.init();
        this.unifiedMetaList = uController.load();
        this.unifiedMetaMapByUID = uController.getUnifiedMetaMap();
        this.currentUID = uController.getMaxUnifiedID() + 1;

        // -------------------------- //
        // load kmdb meta list
        // -------------------------- //
        File kmdbMetaFile = null;
        File preMapFile = null;
        try {
            Configuration config = Configuration.getInstance();
            config.loadProperties(Properties.KMDB_CONFIG);
            URL metaURL = config.getResource(config.readProperty(Properties.KMDB_CONFIG, Properties.META_FILE));
            kmdbMetaFile = new File(metaURL.getFile());

            URL preMapUrl = config.getResource(Properties.KMDB_MAP);
            if (preMapUrl == null) {
                LOGGER.error("no kmdb mapping file");
            } else {
                preMapFile = new File(preMapUrl.getFile());
            }
        } catch (IOException e) {
            LOGGER.error("KMDB configuration loading failed", e);
        }
        BufferedReader reader;
        try {
            // load pre-mapped
            String sbuf;
            if (preMapFile != null) {
                reader = new BufferedReader(new FileReader(preMapFile));
                while ((sbuf = reader.readLine()) != null) {
                    if (sbuf.trim().length() == 0) {
                        continue;
                    }
                    String[] fields = sbuf.split("\\t");
                    preMap.put(fields[0], fields[1]);
                }
                reader.close();
            }

            for (UnifiedMeta um : this.unifiedMetaList) {
                if (!preMap.containsValue(um.getUid())) {
                    this.reducedUnifiedMetaList.add(um);
                }
            }

            // load meta
            char[] cbuf = new char[(int) kmdbMetaFile.length()];
            reader = new BufferedReader(new FileReader(kmdbMetaFile));
            while (!reader.ready()) {
            }
            reader.read(cbuf);
            reader.close();

            sbuf = String.valueOf(cbuf);
            sbuf = sbuf.trim();

            String[] lines = sbuf.split("\n");

            /*
            0: KMDB id
            1: query title ==> exception
            2: KMDB title
            3: dates
            4: national code
            5: directors
            6: actors
            7: genre
            8: keywords
            9: score
            10: image
            11: synopsis
             */

            for (String line : lines) {
                if (line.trim().length() == 0 || line.startsWith("#")) {
                    LOGGER.warn("wrong line : " + line);
                    continue;
                }

                String[] fields = line.split("\t");

                if (fields.length != 11) {
                    LOGGER.warn("wrong line : " + line + "(" + fields.length + ")");
                    continue;
                }

                if (!(fields[2].length() == 8 || fields[2].length() == 6 || fields[2].length() == 4)) {
                    continue;
                }

                KMDBMeta meta = new KMDBMeta();
                meta.setKmdbId(fields[0]);
                meta.setTitle(fields[1].replaceAll("\\(.*?\\)", "").trim());
                meta.setDate(fields[2]);
                meta.setNationalCode(fields[3]);
                meta.setDirectors(fields[4]);
                meta.setActors(fields[5]);
                meta.setGenre(fields[6]);
                meta.setKeywords(fields[7]);
                meta.setScore(Double.parseDouble(fields[8]));
                meta.setImage(fields[9]);
                meta.setSynopsis(fields[10]);

                this.productMetaList.add(meta);

            }

            LOGGER.info("KMDB meta loaded: " + this.productMetaList.size());
        } catch (FileNotFoundException e) {
            LOGGER.error("KMDB meta file doesn't exist: " + kmdbMetaFile.getName(), e);
        } catch (IOException e) {
            LOGGER.error("failed to read KMDB meta file: " + kmdbMetaFile.getName(), e);
        }
    }

    /**
     * map and unify the given meta
     *
     * @return list of {@link com.skplanet.nlp.data.UnifiedMeta}
     */
    @Override
    public List<UnifiedMeta> map() {
        int mapCount = 0;
        int totalCount = this.productMetaList.size();


        int count = 1;
        for (KMDBMeta kmdbMeta : this.productMetaList) {
            String normTitle = Utilities.getNormalizedTitleWithStopword(kmdbMeta.getTitle(), this.stopwordList);

            String uID;
            if (preMap.containsKey(kmdbMeta.getKmdbId())) {
                uID = preMap.get(kmdbMeta.getKmdbId());
            } else {
                uID = this.findExistMeta(normTitle, kmdbMeta.getDate(), kmdbMeta.getDirectors(), kmdbMeta.getActors(), false);
            }
            if (uID != null) {
                this.unifiedMetaMapByUID.get(uID).setKeywords(kmdbMeta.getKeywords());
                this.unifiedMetaMapByPID.put(kmdbMeta.getKmdbId(), unifiedMetaMapByUID.get(uID));
                mapCount++;
            }
            if (count % 100 == 0) {
                LOGGER.info("processing : "+ count + "/" + totalCount + " (" + mapCount + " matched )");
            }
            count++;
        }

        LOGGER.debug("Total number of kmdb id mapped : " + mapCount);
        return this.unifiedMetaList;
    }

    /**
     * Get Kmdb id to unified id mapping
     * @return kmdb id to unified id mapping
     */
    public Map<String, String> getIdMappingTable() {
        Map<String, String> result = new HashMap<String, String>();
        Iterator iter = unifiedMetaMapByPID.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry map = (Map.Entry) iter.next();
            UnifiedMeta um = (UnifiedMeta) map.getValue();
            result.put((String) map.getKey(), um.getUid());
        }
        return result;
    }


    /**
     * find given movie meta from previously loaded unified meta list
     * KMDB 의 경우 날짜 정보 누락 현상이 심하여 일단 날짜가 없으면 맞다고 가정한다.
     *
     * @param title     title
     * @param dates     date
     * @param directors directors
     * @param actors    actors
     * @return UID for the matched, or returns null
     */
    @Override
    protected String findExistMeta(String title, List<Calendar> dates, List<String> directors, List<String> actors, boolean org) {
        double result;
        double max = 0.0;
        String uid = null;

        for (UnifiedMeta um  : reducedUnifiedMetaList) {
            String normalizedTitle = Utilities.getNormalizedTitleWithStopword(um.getTitle(), this.stopwordList);

            // check title
            result = checkTitle(normalizedTitle, title);
            if (result == BigDecimal.ZERO.doubleValue()) {
                continue;
            }

            double dateScore = BigDecimal.ZERO.doubleValue();
            double directorScore;
            double actorScore;

            // check date
            for (Calendar date : dates) {
                // KMDB 의 경우 날짜정보 누락 현상이 심하여, 일단 날짜정보가 없을때는 맞다고 가정한다.
                if (dates.size() == 1 && date.get(Calendar.YEAR) == 9999) {
                    dateScore = ((double) 3.0 / 3.0) * DATE_WEIGHT;
                    break;
                }
                for (Calendar umDate : um.getDates()) {
                    double tmpScore = checkDate(umDate, date);
                    if (dateScore < tmpScore) {
                        dateScore = tmpScore;
                    }
                }
            }

            // check director
            directorScore = checkDirector(um.getDirectors(), directors);

            // check actors
            actorScore = checkActors(um.getActors(), actors);

            result += dateScore + directorScore + actorScore;

            if (result < matchingThreshold) {
                continue;
            }

            if (max < result) {
                max = result;
                uid = um.getUid();
            }
        }
        if (uid != null) {
            if (scoreMap.containsKey(uid)) {
                if (scoreMap.get(uid) < max) {
                    scoreMap.put(uid, max);
                } else {
                    return null;
                }
            } else {
                scoreMap.put(uid, max);
            }
        }
        return uid;
    }

    /**
     * Check given dates are the same
     *
     * @param aDate date a
     * @param bDate date b
     * @return matching score
     */
    @Override
    protected double checkDate(Calendar aDate, Calendar bDate) {
        double result;
        int matched = 0;
        // check year
        if (aDate.get(Calendar.YEAR) == bDate.get(Calendar.YEAR)) {
            matched = 3;
        }

        result = ((double) matched / 3.0) * DATE_WEIGHT;
        return result;
    }


}
