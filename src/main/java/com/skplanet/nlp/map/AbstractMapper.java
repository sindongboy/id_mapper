package com.skplanet.nlp.map;

import com.skplanet.nlp.common.EditDistance;
import com.skplanet.nlp.common.Properties;
import com.skplanet.nlp.common.Utilities;
import com.skplanet.nlp.config.Configuration;
import com.skplanet.nlp.controller.UnifiedMetaController;
import com.skplanet.nlp.data.UnifiedMeta;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;

/**
 * Abstract Generic Mapper
 *
 * @author Donghun Shin, donghun.shin@sk.com
 * @date 8/21/14.
 */
public abstract class AbstractMapper<T> implements Mapper {


    private static final Logger LOGGER = Logger.getLogger(AbstractMapper.class.getName());

    //----------------------------------//
    // statics
    //----------------------------------//
    // unified id format
    protected static final String IDFORMAT = "U%08d";
    // title matching weight
    protected static final double TITLE_WEIGHT = 0.5;
    // released date matching weight
    protected static final double DATE_WEIGHT = 0.1;
    // directors matching weight
    protected static final double DIRECTOR_WEIGHT = 0.2;
    // actors matching weight
    protected static final double ACTOR_WEIGHT = 0.2;

    // title matching threshold
    protected static final double TITLE_SIM_VAL = 0.9;
    // director matching threshold
    protected static final double DIRECTOR_SIM_VAL = 0.5;
    // actor matching threshold
    protected static final double ACTOR_SIM_VAL = 0.5;

    //----------------------------------//
    // members
    //----------------------------------//
    protected int currentUID = -1;
    protected Map<String, UnifiedMeta> unifiedMetaMapByUID = null;
    protected Map<String, UnifiedMeta> unifiedMetaMapByPID = null;
    protected UnifiedMetaController uController = null;

    // only uidTarget UID need to be mapped
    protected Set<String> uidTarget = null;
    protected List<UnifiedMeta> unifiedMetaList = null;
    protected List<T> productMetaList = null;
    protected List<String> stopwordList = null;
    // overall matching threshold (set by configuration)
    protected double matchingThreshold = 0.0;


    /**
     * Sole constructor (called by sub-classes implicitly)
     */
    protected AbstractMapper() {
        this.unifiedMetaMapByUID = new HashMap<String, UnifiedMeta>();
        this.unifiedMetaMapByPID = new HashMap<String, UnifiedMeta>();
        this.unifiedMetaList = new ArrayList<UnifiedMeta>();
        this.productMetaList = new ArrayList<T>();
        this.uidTarget = new HashSet<String>();
    }

    /**
     * Initialize the mapper
     */
    @Override
    public void init() {
        Configuration config = Configuration.getInstance();
        String line;
        BufferedReader reader;

        try {
            // load main configuration
            config.loadProperties(Properties.MAIN_CONFIG);

            // matching threshold value
            matchingThreshold = Double.parseDouble(config.readProperty(Properties.MAIN_CONFIG, Properties.MATCH_THRESHOLD));
            URL stopwordPath = config.getResource(Properties.TITLE_STOPWORD);
            File stopwordFile = new File(stopwordPath.getFile());
            this.stopwordList = new ArrayList<String>();

            reader = new BufferedReader(new FileReader(stopwordFile));
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() == 0 || line.startsWith("#")) {
                    continue;
                }
                this.stopwordList.add(line.trim().toLowerCase());
            }
            reader.close();

        } catch (IOException e) {
            LOGGER.error("failed to set properties : " + Properties.MAIN_CONFIG, e);
        }
    }

    /**
     * find given movie meta from previously loaded unified meta list
     *
     * @param title     title
     * @param dates     date
     * @param directors directors
     * @param actors    actors
     * @return UID for the matched, or returns null
     */
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

            double dateScore;
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

    /**
     * Check given titles are the same
     *
     * @param aTitle title a
     * @param bTitle title b
     * @return matching score either {@code TITLE_WEIGHT} or 0.0
     */
    protected double checkTitle(String aTitle, String bTitle) {
        double similarity = EditDistance.similarity(aTitle, bTitle);
        if (similarity < 0.5D) {
            return BigDecimal.ZERO.doubleValue();
        }
        if (similarity >= TITLE_SIM_VAL) {
            return similarity * TITLE_WEIGHT;
        }

        if (aTitle.startsWith(bTitle) || bTitle.startsWith(aTitle) || aTitle.endsWith(bTitle) || bTitle.endsWith(aTitle)) {
            return similarity * TITLE_WEIGHT;
        }
        return BigDecimal.ZERO.doubleValue();
    }

    /**
     * Check given dates are the same
     *
     * @param aDate date a
     * @param bDate date b
     * @return matching score
     */
    protected double checkDate(Calendar aDate, Calendar bDate) {
        Calendar noDate = new GregorianCalendar();
        noDate.set(Calendar.YEAR, 9999);
        if (aDate.get(Calendar.YEAR) == noDate.get(Calendar.YEAR) ||
                bDate.get(Calendar.YEAR) == noDate.get(Calendar.YEAR)) {
            return DATE_WEIGHT / 2.0D;
        }
        // check year
        if (aDate.get(Calendar.YEAR) == bDate.get(Calendar.YEAR)) {
            return DATE_WEIGHT;
        }
        return 0.0D;

        /*
        // if 9999 year exists, then assume partially matched for year field
        if (aDate.get(Calendar.YEAR) == noDate.get(Calendar.YEAR) ||
                bDate.get(Calendar.YEAR) == noDate.get(Calendar.YEAR)) {
            matched++;
            matched++;
        }
        */
    }

    /**
     * Check given directors are the same;
     *
     * @param aDirectors source director list
     * @param bDirectors target director list
     * @return matching score
     */
    protected double checkDirector(List<String> aDirectors, List<String> bDirectors) {
        double result;
        int matched = 0;
        int length = bDirectors.size();

        for (String directorB : bDirectors) {
            directorB = Utilities.getNormalizedTitle(directorB);
            // if no meta is handed, then return half of the weight, because often
            // no meta is provided
            // must be tuned with the actual result
            if ("null".equals(directorB)) {
                return DIRECTOR_WEIGHT / 2.0;
            }
            for (String directorA : aDirectors) {
                if ("null".equals(directorA)) {
                    return DIRECTOR_WEIGHT / 2.0;
                }
                directorA = Utilities.getNormalizedTitle(directorA);

                if (EditDistance.similarity(directorA, directorB) > DIRECTOR_SIM_VAL) {
                    matched++;
                    break;
                }
            }
        }
        result = ((double) matched / (double) length) * DIRECTOR_WEIGHT;

        return result;
    }

    /**
     * Check given actors are the same
     *
     * @param aActors source actor list
     * @param bActors target actor list
     * @return matching score
     */
    protected double checkActors(List<String> aActors, List<String> bActors) {
        double result;
        int matched = 0;

        if (aActors.size() > 5) {
            aActors = aActors.subList(0, 5);
        }

        if (bActors.size() > 5) {
            bActors = bActors.subList(0, 5);
        }

        int length = bActors.size();
        for (String aActor : bActors) {
            // if no meta is handed, then return half of the weight, because often
            // no meta is provided
            // must be tuned with the actual result
            if ("null".equals(aActor)) {
                return ACTOR_WEIGHT / 2.0;
            }

            for (String bActor : aActors) {
                if (EditDistance.similarity(aActor, bActor) > ACTOR_SIM_VAL) {
                    matched++;
                    break;
                }
            }

        }
        result = ((double) matched / (double) length) * ACTOR_WEIGHT;

        return result;
    }

}
