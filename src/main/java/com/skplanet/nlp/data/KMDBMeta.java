package com.skplanet.nlp.data;

import org.apache.log4j.Logger;

import java.util.*;

/**
 * KMDB Meta
 * <br>
 * <br>
 * Created by Donghun Shin
 * <br>
 * Contact: donghun.shin@sk.com, sindongboy@gmail.com
 * <br>
 * Date: 3/18/14
 * <br>
 */
public class KMDBMeta {

    private static final Logger LOGGER = Logger.getLogger(KMDBMeta.class.getName());
    private static final String ITEM_DELIM = "\\^";

    private String kmdbId = null;
    private String title = null;
    private List<Calendar> date = null;
    private List<String> nationalCode = null;
    private List<String> directors = null;
    private List<String> actors = null;
    private String genre = null;
    private List<String> keywords = null;
    private double score = -1.0;
    private String image = null;
    private String synopsis = null;

    public KMDBMeta() {
        this.date = new ArrayList<Calendar>();
        this.nationalCode = new ArrayList<String>();
        this.directors = new ArrayList<String>();
        this.actors = new ArrayList<String>();
        this.keywords = new ArrayList<String>();
    }

    public String getKmdbId() {
        return kmdbId;
    }

    public void setKmdbId(String kmdbId) {
        this.kmdbId = kmdbId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Calendar> getDate() { return date; }

    public Set<Calendar> getSortedDate() {
        Set<Calendar> sortedList = new TreeSet<Calendar>();
        for (Calendar c : this.date) {
            sortedList.add(c);
        }
        return sortedList;
    }
    public Calendar getMostRecentDate() {
        Iterator<Calendar> iter = this.getSortedDate().iterator();
        Calendar result = null;
        while (iter.hasNext()) {
            result = iter.next();
        }
        return result;
    }
    public void setDate(List<Calendar> date) {
        this.date = date;
    }

    public void setDate(String[] date1) {
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
                LOGGER.error("KMDB Meta has wrong Released Date format : " + date);
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
                LOGGER.error("KMDB Meta has wrong Released Date format : " + dates);
            }
        }
    }

    public List<String> getNationalCode() {
        return nationalCode;
    }

    public void setNationalCode(List<String> nationalCode) {
        this.nationalCode = nationalCode;
    }

    public void setNationalCode(String[] nationalCode) {
        Collections.addAll(this.nationalCode, nationalCode);
    }

    public void setNationalCode(String nationalCodes) {
        String[] nationalCodeFields = nationalCodes.split(ITEM_DELIM);
        Collections.addAll(this.nationalCode, nationalCodeFields);
    }

    public List<String> getDirectors() {
        return directors;
    }

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

    public List<String> getActors() {
        return actors;
    }

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

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public List<String> getKeywords() {
        return keywords;
    }

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

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }
}
