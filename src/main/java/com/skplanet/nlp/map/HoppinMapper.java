package com.skplanet.nlp.map;

import com.skplanet.nlp.common.Properties;
import com.skplanet.nlp.common.Utilities;
import com.skplanet.nlp.config.Configuration;
import com.skplanet.nlp.controller.UnifiedMetaController;
import com.skplanet.nlp.data.HoppinMeta;
import com.skplanet.nlp.data.UnifiedMeta;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

/**
 * ID Mapper for Hoppin
 * <br>
 * <br>
 * Created by Donghun Shin
 * <br>
 * Contact: donghun.shin@sk.com, sindongboy@gmail.com
 * <br>
 * Date: 4/2/14
 * <br>
 */
public class HoppinMapper extends AbstractMapper<HoppinMeta> {
    private static final Logger LOGGER = Logger.getLogger(HoppinMapper.class.getName());

    private Map<String, String> termAddMap = null;
    private Map<String, String> termRemoveMap = null;

    public HoppinMapper() {
        super();
        this.termAddMap = new HashMap<String, String>();
        this.termRemoveMap = new HashMap<String, String>();
    }

    /**
     * Initialize ID Mapper
     */
    @Override
    public void init() {
        // ----------------------- //
        // load default resources
        // ------------------------ //
        super.init();

        // ----------------------- //
        // load unified meta list
        // ----------------------- //
        uController = new UnifiedMetaController();
        uController.init();
        this.unifiedMetaList = uController.load();
        this.unifiedMetaMapByUID = uController.getUnifiedMetaMap();
        this.currentUID = uController.getMaxUnifiedID() + 1;

        // ----------------------- //
        // load hoppin meta list
        // ----------------------- //
        Configuration config = Configuration.getInstance();
        try {
            config.loadProperties(Properties.HOPPIN_CONFIG);
        } catch (IOException e) {
            LOGGER.error("Failed to load properties : " + Properties.HOPPIN_CONFIG, e);
        }
        File hoppinMetaFile = new File(config.getResource(config.readProperty(Properties.HOPPIN_CONFIG, Properties.META_FILE)).getFile());
        File termAddFile = new File(config.getResource(Properties.TERM_ADD_MAP).getFile());
        File termRemoveFile = new File(config.getResource(Properties.TERM_REMOVE_MAP).getFile());
        BufferedReader reader;

        try {
            String sbuf;

            // read term add
            reader = new BufferedReader(new FileReader(termAddFile));
            while ((sbuf = reader.readLine()) != null) {
                if (sbuf.trim().length() == 0) {
                    continue;
                }

                String[] fields = sbuf.split("\\t");
                if (fields.length != 2) {
                    continue;
                }
                this.termAddMap.put(fields[0], fields[1]);
            }
            reader.close();
            LOGGER.info("add-term loaded : " + this.termAddMap.size());

            // read term remove
            reader = new BufferedReader(new FileReader(termRemoveFile));
            while ((sbuf = reader.readLine()) != null) {
                if (sbuf.trim().length() == 0) {
                    continue;
                }

                String[] fields = sbuf.split("\\t");
                if (fields.length != 2) {
                    continue;
                }
                this.termRemoveMap.put(fields[0], fields[1]);
            }
            reader.close();
            LOGGER.info("remove-term loaded : " + this.termRemoveMap.size());

            // hoppin meta loading
            char[] cbuf = new char[(int) hoppinMetaFile.length()];
            reader = new BufferedReader(new FileReader(hoppinMetaFile));
            while (!reader.ready()) {
            }
            reader.read(cbuf);
            reader.close();

            sbuf = String.valueOf(cbuf);
            sbuf = sbuf.trim();

            String[] lines = sbuf.split("\n");

            /*
            0: content id
            1: title
            2: synopsis
            3: rate
            4: dates
            5: national code
            6: genre
            7: directors
            8: actors
            9: score
            10: score count
            11: purchase count
             */
            for (String line : lines) {
                if (line.length() == 0 || line.startsWith("#")) {
                    LOGGER.warn("wrong line : " + line);
                    continue;
                }

                String[] fields = line.split("\t");

                HoppinMeta meta = new HoppinMeta();
                meta.setContentId(fields[0]);
                meta.setTitle(fields[1]);
                meta.setTitleOrg(fields[2].toLowerCase());
                meta.setSynopsis(fields[3]);
                meta.setRated(fields[4]);
                meta.setDate(fields[5].replaceAll("\\.", ""));
                meta.setNationalCode(fields[6]);
                meta.setGenre(fields[7]);
                meta.setDirectors(fields[8]);
                meta.setActors(fields[9]);
                meta.setScore(Double.parseDouble(fields[10]));
                meta.setScoreCount(Integer.parseInt(fields[11]));
                meta.setPurchaseCount(Integer.parseInt(fields[12]));

                if (this.termAddMap.containsKey(fields[0])) {
                    meta.setAddTermList(this.termAddMap.get(fields[0]), "\\^");
                } else {
                    meta.addTermList("null");
                }

                if (this.termRemoveMap.containsKey(fields[0])) {
                    meta.setRemoveTermList(this.termRemoveMap.get(fields[0]), "\\^");
                } else {
                    meta.removeTermList("null");
                }

                this.productMetaList.add(meta);
            }
            LOGGER.info("hoppin meta loaded : " + this.productMetaList.size());
        } catch (FileNotFoundException e) {
            LOGGER.error("can't find hoppin meta file : " + hoppinMetaFile.getName(), e);
        } catch (IOException e) {
            LOGGER.error("failed to read hoppin meta file : " + hoppinMetaFile.getName(), e);
        }

    }

    /**
     * map and unify the given meta
     *
     * @return list of {@link UnifiedMeta}
     */
    @Override
    public List<UnifiedMeta> map() {
        int newID = uController.getMaxUnifiedID() + 1;

        int count = 0;
        for (HoppinMeta m : this.productMetaList) {
            if (count % 100 == 0) {
                LOGGER.info("processing : " + count);
            }
            count++;

            if (uController.existHoppinID(m.getContentId())) {
                LOGGER.info("the hoppin id (" + m.getContentId() + ") already exist!");
                continue;
            }

            // hoppin meta
            String normHoppinTitle;
            List<String> hoppinDirectors = m.getDirectors();
            List<Calendar> hoppinDates = m.getDate();
            List<String> hoppinActors = m.getActors();
            String foundUid;
            /*
            if ("null".equals(m.getTitleOrg())) {
                normHoppinTitle = Utilities.getNormalizedTitleWithStopword(m.getTitle(), this.stopwordList);
                foundUid = findExistMeta(normHoppinTitle, hoppinDates, hoppinDirectors, hoppinActors, false);
            } else {
                normHoppinTitle = Utilities.getNormalizedTitle(m.getTitleOrg());
                foundUid = findExistMeta(normHoppinTitle, hoppinDates, hoppinDirectors, hoppinActors, true);
            }
            */
            normHoppinTitle = Utilities.getNormalizedTitleWithStopword(m.getTitle(), this.stopwordList);
            foundUid = findExistMeta(normHoppinTitle, hoppinDates, hoppinDirectors, hoppinActors, false);

            if (foundUid == null) {
                UnifiedMeta um = new UnifiedMeta();
                um.setUid(String.format(IDFORMAT, newID++));
                um.setHoppinIdList(m.getContentId());
                um.setTitle(m.getTitle().replaceAll("\\(.*?\\)", "").trim());
                um.setTitleOrg(m.getTitleOrg().replaceAll("\\(.*?\\)", "").trim());
                um.setSynopsis(m.getSynopsis());
                um.setDates(m.getDate());
                um.setRate(m.getRated());
                um.setGenre(m.getGenre());
                um.setGenreBigram(m.getGenreBigram());
                um.setDirectors(m.getDirectors());
                um.setActors(m.getActors());
                um.setNationalCode(m.getNationalCode());
                um.setHoppinScore(m.getScore());
                um.setHoppinScoreCount(m.getScoreCount());
                um.setHoppinPurchaseCount(m.getPurchaseCount() + "");
                um.setAddTermList(m.getAddTermList());
                um.setRemoveTermList(m.getRemoveTermList());

                this.unifiedMetaList.add(um);
                this.unifiedMetaMapByPID.put(m.getContentId(), um);
                this.unifiedMetaMapByUID.put(um.getUid(), um);

                LOGGER.debug("newly added to the unified meta list: (" + um.getUid() + ") " + um.getTitle());
            } else {
                // update cid list if necessary
                boolean exists = false;
                for (String cid : this.unifiedMetaMapByUID.get(foundUid).getHoppinIdList()) {
                    if (cid.equals(m.getContentId())) {
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    this.unifiedMetaMapByUID.get(foundUid).getHoppinIdList().add(m.getContentId());
                }

                this.unifiedMetaMapByUID.get(foundUid).setHoppinScore(m.getScore());
                this.unifiedMetaMapByUID.get(foundUid).setHoppinScoreCount(m.getScoreCount());
                int prevPurchaseCount = this.unifiedMetaMapByUID.get(foundUid).getHoppinPurchaseCount();
                this.unifiedMetaMapByUID.get(foundUid).setHoppinPurchaseCount((prevPurchaseCount + m.getPurchaseCount()) + "");
            }
        }

        // reorder pid by score count
        List<UnifiedMeta> result = new ArrayList<UnifiedMeta>();
        for (String key : this.unifiedMetaMapByUID.keySet()) {
            result.add(this.unifiedMetaMapByUID.get(key));
        }

        for (int i = 0; i < result.size(); i++) {
            List<String> cids = result.get(i).getHoppinIdList();
            int max = -1;
            String newMax = null;
            StringBuilder newPIDs = new StringBuilder();
            for (String id : cids) {
                if (max < this.getHoppinMetaFromContentID(id).getScoreCount()) {
                    newMax = id;
                    max = this.getHoppinMetaFromContentID(id).getScoreCount();
                }
            }
            for (String id : cids) {
                if (id.equals(newMax)) {
                    newPIDs.insert(0, id);
                } else {
                    newPIDs.append("^").append(id);
                }
            }
            result.get(i).setHoppinIdList(newPIDs.toString());
        }
        return result;
    }

    /**
     * Get {@link HoppinMeta} from Hoppin Content ID
     * @param id content id
     * @return {@link HoppinMeta}
     */
    private HoppinMeta getHoppinMetaFromContentID(String id) {
        for (HoppinMeta hm : this.productMetaList) {
            if (hm.getContentId().equals(id)) {
                return hm;
            }
        }
        return null;
    }

    public List<HoppinMeta> getHoppinMetaList() {
        return this.productMetaList;
    }
}
