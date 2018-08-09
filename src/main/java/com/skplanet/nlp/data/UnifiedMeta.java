package com.skplanet.nlp.data;

import com.skplanet.nlp.common.Utilities;
import org.apache.log4j.Logger;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Unified Meta
 * <br>
 * <br>
 * Created by Donghun Shin
 * <br>
 * Contact: donghun.shin@sk.com, sindongboy@gmail.com
 * <br>
 * Date: 3/19/14
 * <br>
 */
@SuppressWarnings("unused")
public class UnifiedMeta {
    private static final Logger LOGGER = Logger.getLogger(UnifiedMeta.class.getName());
    private static final String FIELD_DELIM = "\t";
    private static final String ITEM_DELIM = "\\^";

    // unified id
    private String uid = null;

    // content id ==> hoppin id
    private List<String> hoppinIdList = null;

    // tstore id
    private List<String> tstoreIdList = null;

    // title
    private String title = null;

    // original title
    private String titleOrg = null;

    // synopsis
    private String synopsis = null;

    // list of dates
    private List<Calendar> dates = null;

    // rate
    private String rate = null;

    // genre
    private String genre = null;

    // genre bigram
    private List<String> genreBigram = null;

    // list of director
    private List<String> directors = null;

    // list of actors
    private List<String> actors = null;

    // national code
    private List<String> nationalCode = null;

    // KMDB keywords
    private List<String> keywords = null;

    // - weighted score 적용을 위해, 매핑 되는 모든 스코어를 저장하기위하여, 리스트로 저장을 한다.
    // vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
    // hoppin score
    private List<Double> hoppinScore = null;
    // hoppin score count
    private List<Integer> hoppinScoreCount = null;
    // naver score
    private List<Double> nScore = null;
    // naver score count
    private List<Integer> nScoreCount = null;
    // hoppin backup score
    private double hoppinScoreBackup = 0.0;
    // naver backup score
    private double naverScoreBackup = 0.0;
    // hoppin backup score count
    private int hoppinScoreCountBackup = 0;
    // naver backup score count
    private int naverScoreCountBackup = 0;
    // ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    // - weighted score 적용을 위해, 매핑 되는 모든 스코어를 저장하기위하여, 리스트로 저장을 한다.


    private int purchaseCount = -1;

    // =============== TSTORE ================== //
    private String tstoreScore = "0.00";
    private String tstoreScoreCount = "0";
    private String tstorePurchaseCount = "0";


    // =============== TSTORE ================== //

    // 사용자 키워드 추가 ( hoppin )
    private List<String> addTermList = null;
    private List<String> removeTermList = null;

    /**
     * default contructor
     */
    public UnifiedMeta() {
        this.hoppinIdList = new ArrayList<String>();
        this.tstoreIdList = new ArrayList<String>();
        this.dates = new ArrayList<Calendar>();
        this.directors = new ArrayList<String>();
        this.actors = new ArrayList<String>();
        this.keywords = new ArrayList<String>();
        this.genreBigram = new ArrayList<String>();
        this.nationalCode = new ArrayList<String>();
        this.hoppinScore = new ArrayList<Double>();
        this.hoppinScoreCount = new ArrayList<Integer>();
        this.nScore = new ArrayList<Double>();
        this.nScoreCount = new ArrayList<Integer>();
        this.addTermList = new ArrayList<String>();
        this.removeTermList = new ArrayList<String>();
    }

    /**
     * Get Unified ID
     * @return unified id
     */
    public String getUid() {
        return uid;
    }

    /**
     * Set Unified ID
     * @param uid unified id to be set
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * Get Hoppin Id List
     * @return hoppin id list
     */
    public List<String> getHoppinIdList() {
        return this.hoppinIdList;
    }

    /**
     * Set Hoppin Id List
     * @param hidList hoppin id list to be set
     */
    public void setHoppinIdList(List<String> hidList) {
        this.hoppinIdList = hidList;
    }
    public void setHoppinIdList(String[] hidList) {
        Collections.addAll(this.hoppinIdList, hidList);
    }
    public void setHoppinIdList(String hidList) {
        this.hoppinIdList.clear();
        String[] hids = hidList.split(ITEM_DELIM);
        if (hids.length == 1 && "null".equals(hids[0])) {
            return;
        }
        Collections.addAll(this.hoppinIdList, hids);
    }
    public void addHoppinIdList(String hidList) {
        String[] hids = hidList.split(ITEM_DELIM);
        if (hids.length == 1 && "null".equals(hids[0])) {
            return;
        }
        Collections.addAll(this.hoppinIdList, hids);
    }

    /**
     * Get Tstore Id List
     * @return tstore id list
     */
    public List<String> getTstoreIdList() {
        return this.hoppinIdList;
    }

    /**
     * Set Tstore Id List
     * @param tidList tstore id list to be set
     */
    public void setTstoreIdList(List<String> tidList) {
        this.tstoreIdList = tidList;
    }
    public void setTstoreIdList(String[] tidList) {
        Collections.addAll(this.tstoreIdList, tidList);
    }
    public void setTstoreIdList(String tidList) {
        if (tidList.trim().length() == 0) {
            return;
        }
        this.tstoreIdList.clear();
        String[] tids = tidList.split(ITEM_DELIM);
        Collections.addAll(this.tstoreIdList, tids);
    }
    public void addTstoreIdList(String tidList) {
        String[] tids = tidList.split(ITEM_DELIM);
        Collections.addAll(this.tstoreIdList, tids);
    }

    /**
     * Get Title
     * @return title
     */
    public String getTitle() {
        if (title == null) {
            return "null";
        }
        return title;
    }

    /**
     * Set title
     * @param title title to be set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get Original Title
     * @return title
     */
    public String getTitleOrg() {
        if (titleOrg == null) {
            return "null";
        }
        return titleOrg;
    }

    /**
     * Set Original Title
     * @param titleOrg original title
     */
    public void setTitleOrg(String titleOrg) {
        this.titleOrg = titleOrg;
    }

    /**
     * Get Synopsis
     * @return synopsis
     */
    public String getSynopsis() {
        return synopsis;
    }

    /**
     * Set Synopsis
     * @param synopsis synopsis to be set
     */
    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    /**
     * Get Released Date (list)
     * @return list of released dates
     */
    public List<Calendar> getDates() {
        return dates;
    }

    /**
     * Get sorted released dates set
     * @return set of sorted released dates
     */
    public Set<Calendar> getSortedDate() {
        Set<Calendar> sortedList = new TreeSet<Calendar>();
        for (Calendar c : this.dates) {
            sortedList.add(c);
        }
        return sortedList;
    }

    /**
     * Get most recently released date
     * @return most recently released date
     */
    public Calendar getMostRecentDate() {
        Iterator<Calendar> iter = this.getSortedDate().iterator();
        Calendar result = null;
        while (iter.hasNext()) {
            result = iter.next();
        }
        return result;
    }

    /**
     * Set released dates
     * @param dates released dates to be set
     */
    public void setDates(List<Calendar> dates) {
        this.dates = dates;
    }
    public void setDates(String[] dates) {
        for (String d : dates) {
            Calendar newDates = null;
            if ("null".equals(d)) {
                newDates = new GregorianCalendar();
                newDates.set(Calendar.YEAR, 9999);
                newDates.set(Calendar.MONTH, 0);
                newDates.set(Calendar.DAY_OF_MONTH, 1);
            } else if (d.length() == 4) {
                newDates = new GregorianCalendar();
                newDates.set(Calendar.YEAR, Integer.parseInt(d));
                newDates.set(Calendar.MONTH, 0);
                newDates.set(Calendar.DAY_OF_MONTH, 1);
            } else if (d.length() == 6) {
                int year = Integer.parseInt(d.substring(0, 4));
                int month = Integer.parseInt(d.substring(4)) - 1;
                newDates = new GregorianCalendar();
                newDates.set(Calendar.YEAR, year);
                newDates.set(Calendar.MONTH, month);
                newDates.set(Calendar.DAY_OF_MONTH, 1);
            } else if (d.length() == 8) {
                int year = Integer.parseInt(d.substring(0, 4));
                int month = Integer.parseInt(d.substring(4, 6)) - 1;
                int day = Integer.parseInt(d.substring(6));
                newDates = new GregorianCalendar();
                newDates.set(Calendar.YEAR, year);
                newDates.set(Calendar.MONTH, month);
                newDates.set(Calendar.DAY_OF_MONTH, day);
            }

            if (newDates != null) {
                this.dates.add(newDates);
            } else {
                LOGGER.error("Naver Meta has wrong Released Date format : " + d);
            }
        }
    }
    public void setDates(String dates) {
        String[] date = dates.split(ITEM_DELIM);
        for (String d : date) {
            Calendar newDates = null;
            if ("null".equals(d)) {
                newDates = new GregorianCalendar();
                newDates.set(Calendar.YEAR, 9999);
                newDates.set(Calendar.MONTH, 0);
                newDates.set(Calendar.DAY_OF_MONTH, 1);
            } else if (d.length() == 4) {
                newDates = new GregorianCalendar();
                newDates.set(Calendar.YEAR, Integer.parseInt(d));
                newDates.set(Calendar.MONTH, 0);
                newDates.set(Calendar.DAY_OF_MONTH, 1);
            } else if (d.length() == 6) {
                int year = Integer.parseInt(d.substring(0, 4));
                int month = Integer.parseInt(d.substring(4)) - 1;
                newDates = new GregorianCalendar();
                newDates.set(Calendar.YEAR, year);
                newDates.set(Calendar.MONTH, month);
                newDates.set(Calendar.DAY_OF_MONTH, 1);
            } else if (d.length() == 8) {
                int year = Integer.parseInt(d.substring(0, 4));
                int month = Integer.parseInt(d.substring(4, 6)) - 1;
                int day = Integer.parseInt(d.substring(6));
                newDates = new GregorianCalendar();
                newDates.set(Calendar.YEAR, year);
                newDates.set(Calendar.MONTH, month);
                newDates.set(Calendar.DAY_OF_MONTH, day);
            }

            if (newDates != null) {
                this.dates.add(newDates);
            } else {
                LOGGER.debug("Unified Meta has wrong Released Date format : " + d);
            }
        }
    }

    /**
     * Get Movie Rate
     * @return movie rate
     */
    public String getRate() {
        return rate;
    }

    /**
     * Set Movie Rate
     * @param rate movie rate to be set
     */
    public void setRate(String rate) {
        this.rate = rate;
    }

    /**
     * Get Genre
     * @return genre
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Set Movie Genre
     * @param genre genre to be set
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * Get Genre Bigram
     * @return genre bigram list
     */
    public List<String> getGenreBigram() {
        return this.genreBigram;
    }

    /**
     * Set Genre Bigram
     * @param genreBigram genre bigram to be set
     */
    public void setGenreBigram(List<String> genreBigram) {
        this.genreBigram = genreBigram;
    }

    /**
     * Get Directors
     * @return directors
     */
    public List<String> getDirectors() {
        return directors;
    }

    /**
     * Set Directors
     * @param directors directors
     */
    public void setDirectors(List<String> directors) {
        this.directors = directors;
    }
    public void setDirectors(String[] directors) {
        Collections.addAll(this.directors, directors);
    }
    public void setDirectors(String directors) {
        String[] director = directors.split(ITEM_DELIM);
        Collections.addAll(this.directors, director);
    }

    /**
     * Get Actors
     * @return actors
     */
    public List<String> getActors() {
        return actors;
    }

    /**
     * Set Actors
     * @param actors actors
     */
    public void setActors(List<String> actors) {
        this.actors = actors;
    }
    public void setActors(String[] actors) {
        Collections.addAll(this.actors, actors);
    }
    public void setActors(String actors) {
        String[] actor = actors.split(ITEM_DELIM);
        Collections.addAll(this.actors, actor);
    }

    /**
     * Get National code
     * @return national code
     */
    public List<String> getNationalCode() {
        return nationalCode;
    }

    /**
     * Set national code
     * @param nationalCode national code
     */
    public void setNationalCode(List<String> nationalCode) {
        this.nationalCode = nationalCode;
    }
    public void setNationalCode(String[] nationalCodes) {
        Collections.addAll(this.nationalCode, nationalCodes);
    }
    public void setNationalCode(String nationalCodes) {
        String[] nationalCodeFields = nationalCodes.split(ITEM_DELIM);
        Collections.addAll(this.nationalCode, nationalCodeFields);
    }

    /**
     * Get KMDB Keywords
     * @return KMDB Keywords
     */
    public List<String> getKeywords() {
        return keywords;
    }

    /**
     * Set KMDB Keywords
     * @param keywords KMDB keywords to be set
     */
    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
    public void setKeywords(String[] keywords) {
        Collections.addAll(this.keywords, keywords);
    }
    public void setKeywords(String keywords) {
        String[] keyword = keywords.split(ITEM_DELIM);
        Collections.addAll(this.keywords, keyword);
    }

    /**
     * Get Hoppin Score
     * @return Hoppin Score
     */
    public double getHoppinScore() {
        return Utilities.weightedScore(this.hoppinScore, this.hoppinScoreCount);
    }

    /**
     * Set Hoppin Score
     * @param score hoppin score
     */
    public void setHoppinScore(double score) {
        this.hoppinScore.add(score);
    }
    public int getHoppinScoreCount() {
        int scoreCountNum = 0;
        for (int s : this.hoppinScoreCount) {
            scoreCountNum += s;
        }
        return scoreCountNum;
    }
    public void setHoppinScoreCount(int scoreCount) {
        this.hoppinScoreCount.add(scoreCount);
    }

    /**
     * Get Naver Score
     * @return naver score
     */
    public double getNaverScore() {
        return Utilities.weightedScore(this.nScore, this.nScoreCount);
    }

    /**
     * Set Naver Score
     * @param score naver score
     */
    public void setNaverScore(double score) {
        this.nScore.add(score);
    }

    public String getTstoreScore() {
        return this.tstoreScore;
    }
    public void setTstoreScore(String score) {
        this.tstoreScore = score;
    }

    public String getTstoreScoreCount() {
        return this.tstoreScoreCount;
    }
    public void setTstoreScoreCount(String scoreCount) {
        this.tstoreScoreCount = scoreCount;
    }

    /**
     * Get Naver Score Count
     * @return naver score count
     */
    public int getNaverScoreCount() {
        int scoreCountNum = 0;
        for (int s : this.nScoreCount) {
            scoreCountNum += s;
        }
        return scoreCountNum;
    }

    /**
     * Set Naver Score Count
     * @param count naver score count
     */
    public void setNaverScoreCount(int count) {
        this.nScoreCount.add(count);
    }

    /**
     * Backup Hoppin Score
     * @param score hoppin score to be backed up
     */
    public void hoppinBackupScore(String score) {
        this.hoppinScoreBackup = Double.parseDouble(score);
    }

    /**
     * Backup Hoppin Score count
     * @param scoreCount hoppin score count to be backed up
     */
    public void hoppinBackupScoreCount(String scoreCount) {
        this.hoppinScoreCountBackup = Integer.parseInt(scoreCount);
    }

    /**
     * Backup Naver Score
     * @param score naver score to be backed up
     */
    public void naverBackupScore(String score) {
        this.naverScoreBackup = Double.parseDouble(score);
    }

    /**
     * Backup Naver Score Count
     * @param scoreCount naver score count to be backed up
     */
    public void naverBackupScoreCount(String scoreCount) {
        this.naverScoreCountBackup = Integer.parseInt(scoreCount);
    }

    /**
     * Get Hoppin Purchase Count
     * @return hoppin purchase count
     */
    public int getHoppinPurchaseCount() {
        return purchaseCount;
    }

    /**
     * Get Tstore Purchase Count
     * @return hoppin purchase count
     */
    public String getTstorePurchaseCount() {
        return this.tstorePurchaseCount;
    }


    /**
     * Set Hoppin Purchase count
     * @param purchaseCount hoppin purchase count to be set
     */
    public void setHoppinPurchaseCount(String purchaseCount) {
        this.purchaseCount = Integer.parseInt(purchaseCount);
    }

    /**
     * Set Tstore Purchase count
     * @param purchaseCount tstore purchase count
     */
    public void setTstorePurchaseCount(String purchaseCount) {
        this.tstorePurchaseCount = purchaseCount;
    }

    /**
     * Get Hoppin User Keywords to be added
     * @return hoppin user keyword to be added
     */
    public List<String> getAddTermList() {
        return addTermList;
    }

    /**
     * Add Term to the User Keyword list
     * @param term term to be added
     */
    public void addTermList(String term) {
        if (this.addTermList == null) {
            this.addTermList = new ArrayList<String>();
        }
        if (!this.addTermList.contains(term)) {
            this.addTermList.add(term);
        }
    }

    /**
     * Set User Keyword to be added
     * @param addTermList term list for User Keyword
     */
    public void setAddTermList(List<String> addTermList) {
        for (String term : addTermList) {
            addTermList(term);
        }
    }

    /**
     * Get User Keyword to be removed
     * @return user keyword to be removed
     */
    public List<String> getRemoveTermList() {
        return removeTermList;
    }

    /**
     * Add Remove Term to the User Keyword List
     * @param term term to be added
     */
    public void removeTermList(String term) {
        if (this.removeTermList == null) {
            this.removeTermList = new ArrayList<String>();
        }
        if (!this.removeTermList.contains(term)) {
            this.removeTermList.add(term);
        }
    }

    /**
     * Set Remove Term List
     * @param removeTermList remove term list
     */
    public void setRemoveTermList(List<String> removeTermList) {
        for (String term : removeTermList) {
            removeTermList(term);
        }
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        DecimalFormat df = new DecimalFormat("0.00");

        String scoreLocal;
        String scoreCountLocal;
        String nScoreLocal;
        String nScoreCountLocal;

        if (this.hoppinScore.isEmpty()) {
            scoreLocal = df.format(hoppinScoreBackup);
            scoreCountLocal = hoppinScoreCountBackup + "";
        } else {
            scoreLocal = df.format(this.getHoppinScore());
            scoreCountLocal = this.getHoppinScoreCount() + "";
        }

        if (this.nScore.isEmpty()) {
            nScoreLocal = df.format(naverScoreBackup);
            nScoreCountLocal = naverScoreCountBackup + "";
        } else {
            nScoreLocal = df.format(this.getNaverScore());
            nScoreCountLocal = this.getNaverScoreCount() + "";
        }

        sb.append(this.uid).append(FIELD_DELIM);
        if (this.hoppinIdList != null && this.hoppinIdList.size() > 0) {
            if (Utilities.arrayToString(this.hoppinIdList, "^").length() == 0) {
                sb.append("null").append(FIELD_DELIM);
            } else {
                sb.append(Utilities.arrayToString(this.hoppinIdList, "^")).append(FIELD_DELIM);
            }
        } else {
            sb.append("null").append(FIELD_DELIM);
        }

        if (this.tstoreIdList != null && this.tstoreIdList.size() > 0) {
            sb.append(Utilities.arrayToString(this.tstoreIdList, "^")).append(FIELD_DELIM);
        } else {
            sb.append("null").append(FIELD_DELIM);
        }
        sb.append(this.title).append(FIELD_DELIM);
        sb.append(this.titleOrg).append(FIELD_DELIM);
        sb.append(this.synopsis).append(FIELD_DELIM);
        for (int i = 0; i < this.dates.size(); i++) {
            String dateStr = dateFormat.format(this.dates.get(i).getTime());
            if (i == this.dates.size() - 1) {
                sb.append(dateStr).append(FIELD_DELIM);
            } else {
                sb.append(dateStr).append("^");
            }
        }
        sb.append(this.rate).append(FIELD_DELIM);
        sb.append(this.genre).append(FIELD_DELIM);
        if (this.genreBigram != null && this.genreBigram.size() > 0) {
            for (int i = 0; i < this.genreBigram.size(); i++) {
                if (i == this.genreBigram.size() - 1) {
                    sb.append(this.genreBigram.get(i)).append(FIELD_DELIM);
                } else {
                    sb.append(this.genreBigram.get(i)).append("^");
                }
            }
        } else {
            sb.append("null").append(FIELD_DELIM);
        }


        sb.append(Utilities.arrayToString(this.directors, "^")).append(FIELD_DELIM);
        sb.append(Utilities.arrayToString(this.actors, "^")).append(FIELD_DELIM);
        if (this.nationalCode.size() > 0) {
            sb.append(Utilities.arrayToString(this.nationalCode, "^")).append(FIELD_DELIM);
        } else {
            sb.append("null").append(FIELD_DELIM);
        }
        if (this.keywords.size() > 0) {
            sb.append(Utilities.arrayToString(this.keywords, "^")).append(FIELD_DELIM);
        } else {
            sb.append("null").append(FIELD_DELIM);
        }

        // hoppin^naver^tstore score/score participant.
        sb.append(scoreLocal).append("^") // hoppin
                .append(nScoreLocal).append("^") // naver
                .append(this.tstoreScore).append(FIELD_DELIM); // tstore

        sb.append(scoreCountLocal).append("^") // hoppin
                .append(nScoreCountLocal).append("^") // naver
                .append(tstoreScoreCount).append(FIELD_DELIM); // tstore

        // hoppin^tstore purchase count
        sb.append(this.purchaseCount).append("^") // hoppin
                .append(this.tstorePurchaseCount).append(FIELD_DELIM); // tstore

        // add term
        if (this.addTermList != null && this.addTermList.size() > 0) {
            for (int i = 0; i < this.addTermList.size(); i++) {
                if (i == this.addTermList.size() - 1) {
                    sb.append(this.addTermList.get(i)).append(FIELD_DELIM);
                } else {
                    sb.append(this.addTermList.get(i)).append("^");
                }
            }
        } else {
            sb.append("null").append(FIELD_DELIM);
        }


        // remove term
        if (this.removeTermList != null && this.removeTermList.size() > 0) {
            for (int i = 0; i < this.removeTermList.size(); i++) {
                if (i == this.removeTermList.size() - 1) {
                    sb.append(this.removeTermList.get(i));
                } else {
                    sb.append(this.removeTermList.get(i)).append("^");
                }
            }
        } else {
            sb.append("null");
        }

        return sb.toString();
    }
}
