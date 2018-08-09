package com.skplanet.nlp.data;

import org.apache.log4j.Logger;

import java.util.*;

/**
 * Hoppin Meta
 * <br>
 * <br>
 * Created by Donghun Shin
 * <br>
 * Contact: donghun.shin@sk.com, sindongboy@gmail.com
 * <br>
 * Date: 3/18/14
 * <br>
 */
public class HoppinMeta {

    private static final Logger LOGGER = Logger.getLogger(HoppinMeta.class.getName());
    private static final String ITEM_DELIM = "\\^";

    // members
    private String contentId = null;
    private String title = null;
    private String titleOrg = null;
    private String synopsis = null;
    private String rated = null;
    private List<Calendar> date = null;
    private List<String> nationalCode = null;
    private List<String> genre = null;
    private List<String> directors = null;
    private List<String> actors = null;
    private double score = 0.00;
    private int scoreCount = 0;
    private int purchaseCount = 0;
    private List<String> addTermList = null;
    private List<String> removeTermList = null;


    public List<String> getAddTermList() {
        return addTermList;
    }

    public void setAddTermList(List<String> addTermList) {
        this.addTermList = addTermList;
    }

    public void setAddTermList(String text, String delim) {
        for (String term : Arrays.asList(text.split(delim))) {
            addTermList(term);
        }
    }

    public void addTermList(String aTerm) {
        if (this.addTermList == null) {
            this.addTermList = new ArrayList<String>();
        }
        if (!this.addTermList.contains(aTerm)) {
            this.addTermList.add(aTerm);
        }
    }

    public List<String> getRemoveTermList() {
        return removeTermList;
    }

    public void removeTermList(String aTerm) {
        if (this.removeTermList == null) {
            this.removeTermList = new ArrayList<String>();
        }
        if (!this.removeTermList.contains(aTerm)) {
            this.removeTermList.add(aTerm);
        }
    }

    public void setRemoveTermList(List<String> removeTermList) {
        this.removeTermList = removeTermList;
    }

    public void setRemoveTermList(String text, String delim) {
        for (String term : Arrays.asList(text.split(delim))) {
            removeTermList(term);
        }
    }

    /**
     * Default Constructor
     */
    public HoppinMeta() {
        this.date = new ArrayList<Calendar>();
        this.directors = new ArrayList<String>();
        this.actors = new ArrayList<String>();
        this.nationalCode = new ArrayList<String>();
        this.addTermList = new ArrayList<String>();
        this.removeTermList = new ArrayList<String>();
    }

    /**
     * Get Content ID
     * @return content id
     */
    public String getContentId() {
        return contentId;
    }

    /**
     * Set Content ID
     * @param contentId content id
     */
    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    /**
     * Get Title
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set Title
     * @param title title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleOrg() {
        return titleOrg;
    }

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
     * @param synopsis synopsis
     */
    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    /**
     * Get Rated
     * @return movie rate
     */
    public String getRated() {
        return rated;
    }

    /**
     * Set Rate
     * @param rated movie rate
     */
    public void setRated(String rated) {
        this.rated = rated;
    }

    /**
     * Get Date
     * @return date
     */
    public List<Calendar> getDate() {
        return date;
    }

    /**
     * Set Date
     * @param date date
     */
    public void setDate(List<Calendar> date) {
        this.date = date;
    }
    public void setDate(String [] date1) {
        if (this.date == null) {
            this.date = new ArrayList<Calendar>();
        }
        for (String d : date1) {
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
                this.date.add(newDates);
            } else {
                LOGGER.error("Hoppin Meta has wrong Released Date format : " + date);
            }
        }
    }

    public void setDate(String dates) {
        if (this.date == null) {
            this.date = new ArrayList<Calendar>();
        }
        String[] dateFields = dates.split(ITEM_DELIM);
        for (String d : dateFields) {
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
                this.date.add(newDates);
            } else {
                LOGGER.error("Naver Meta has wrong Released Date format : " + dates);
            }
        }
    }

    /**
     * Get Sorted Date
     * @return sorted released date
     */
    public Set<Calendar> getSortedDate() {
        Set<Calendar> sortedList = new TreeSet<Calendar>();
        for (Calendar d : this.date) {
            sortedList.add(d);
        }
        return sortedList;
    }

    /**
     * Get Most Recent Released date
     * @return most recent released date
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
     * Get National Code
     * @return national code
     */
    public List<String> getNationalCode() {
        return nationalCode;
    }

    /**
     * Set National Code
     * @param nationalCode national code
     */
    public void setNationalCode(List<String> nationalCode) { this.nationalCode = nationalCode; }

    public void setNationalCode(String nationalCodes) {
        String[] nationalCodeFields = nationalCodes.split(ITEM_DELIM);
        Collections.addAll(this.nationalCode, nationalCodeFields);
    }

    public void setNationalCode(String[] nationalCodes) {
        Collections.addAll(this.nationalCode, nationalCodes);
    }

    /**
     * Get Genre
     * @return genre
     */
    public String getGenre() {
        if (this.genre == null) {
            this.genre = new ArrayList<String>();
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < this.genre.size(); i++) {
            if (i == this.genre.size() - 1) {
                result.append(this.genre.get(i));
            } else {
                result.append(this.genre.get(i)).append("^");
            }
        }
        return result.toString();
    }

    public List<String> getGenreBigram() {
        List<String> result = new ArrayList<String>();
        if (this.genre == null || this.genre.isEmpty()) {
            return result;
        }

        for (int i = 0; i < this.genre.size() - 1; i++) {
            for (int j = i + 1; j < this.genre.size(); j++) {
                result.add(this.genre.get(i) + "_" + this.genre.get(j));
            }
        }
        if (result.isEmpty()) {
            result.add("null");
        }
        return result;
    }

    /**
     * Set Genre
     * @param genre genre
     */
    public void setGenre(String genre) {
        String[] fields = genre.split(ITEM_DELIM);
        if (this.genre == null) {
            this.genre = new ArrayList<String>();
        }
        for (String f : fields) {
            if (f.contains("/")) {
                String[] subFields = f.split("/");
                for (String sf : subFields) {
                    this.genre.add(sf);
                }
                continue;
            }
            this.genre.add(f);
        }
        Collections.sort(this.genre);
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
     * Get Score
     * @return score
     */
    public double getScore() {
        return score;
    }

    /**
     * Set Score
     * @param score score
     */
    public void setScore(double score) {
        this.score = score;
    }

    /**
     * Get Score Count
     * @return score count
     */
    public int getScoreCount() {
        return scoreCount;
    }

    /**
     * Set Score Count
     * @param scoreCount score count
     */
    public void setScoreCount(int scoreCount) {
        this.scoreCount = scoreCount;
    }

    /**
     * Get purchase count
     * @return purchase count
     */
    public int getPurchaseCount() {
        return purchaseCount;
    }

    /**
     * Set purchase count
     * @param purchaseCount purchase count
     */
    public void setPurchaseCount(int purchaseCount) {
        this.purchaseCount = purchaseCount;
    }
}
