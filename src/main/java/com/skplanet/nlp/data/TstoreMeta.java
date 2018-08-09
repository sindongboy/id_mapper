package com.skplanet.nlp.data;

import com.skplanet.nlp.common.Properties;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * TSTORE Meta
 * @author Donghun Shin, donghun.shin@sk.com
 * @date 8/12/14.
 */
public class TstoreMeta {
    private static final Logger LOGGER = Logger.getLogger(TstoreMeta.class.getName());
    private static final String ITEM_DELIM = "\\^";


    private String pid;
    private String title;
    private String synopsis;
    private String rate;
    private List<Calendar> date;
    private List<String> actors;
    private List<String> genre;
    private List<String> director;
    private String purchaseCount;
    private String avgScore;
    private String scoreCount;

    public TstoreMeta() {

    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public List<Calendar> getDate() {
        return date;
    }

    public void setDate(String date) {
        List<Calendar> allDates = new ArrayList<Calendar>();
        String[] fields = date.split(Properties.FIELD_DELIM);

        for (String f : fields) {
            Calendar newDates = null;
            if ("null".equals(f)) {
                newDates = new GregorianCalendar();
                newDates.set(Calendar.YEAR, 9999);
                newDates.set(Calendar.MONTH, 0);
                newDates.set(Calendar.DAY_OF_MONTH, 1);
            } else if (f.length() == 4) {
                newDates = new GregorianCalendar();
                newDates.set(Calendar.YEAR, Integer.parseInt(f));
                newDates.set(Calendar.MONTH, 0);
                newDates.set(Calendar.DAY_OF_MONTH, 1);
            } else if (f.length() == 6) {
                int year = Integer.parseInt(f.substring(0, 4));
                int month = Integer.parseInt(f.substring(4)) - 1;
                newDates = new GregorianCalendar();
                newDates.set(Calendar.YEAR, year);
                newDates.set(Calendar.MONTH, month);
                newDates.set(Calendar.DAY_OF_MONTH, 1);
            } else if (f.length() == 8) {
                int year = Integer.parseInt(f.substring(0, 4));
                int month = Integer.parseInt(f.substring(4, 6)) - 1;
                int day = Integer.parseInt(f.substring(6));
                newDates = new GregorianCalendar();
                newDates.set(Calendar.YEAR, year);
                newDates.set(Calendar.MONTH, month);
                newDates.set(Calendar.DAY_OF_MONTH, day);
            } else {
                newDates = new GregorianCalendar();
                newDates.set(Calendar.YEAR, 9999);
                newDates.set(Calendar.MONTH, 0);
                newDates.set(Calendar.DAY_OF_MONTH, 1);
            }

            if (newDates != null) {
                allDates.add(newDates);
            } else {
                LOGGER.error("Tstore Meta has wrong Released Date format : " + date);
            }
        }

        this.date = allDates;
    }

    public List<String> getActors() {
        return actors;
    }

    public void setActors(String actors) {
        List<String> actorList = new ArrayList<String>();
        String[] fields = actors.split(Properties.FIELD_DELIM);
        for (String f : fields) {
            actorList.add(f);
        }
        this.actors = actorList;
    }

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

    public List<String> getDirector() {
        return director;
    }

    public void setDirector(String director) {
        List<String> directorList = new ArrayList<String>();
        for (String f : director.split(Properties.FIELD_DELIM)) {
            directorList.add(f);
        }
        this.director = directorList;
    }

    public String getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(String avgScore) {
	    double score = Double.parseDouble(avgScore);
	    this.avgScore = (score * 2.00) + "";
    }

    public String getPurchaseCount() {
        return purchaseCount;
    }

    public void setPurchaseCount(String purchaseCount) {
        this.purchaseCount = purchaseCount;
    }

    public String getScoreCount() {
        return scoreCount;
    }

    public void setScoreCount(String scoreCount) {
        this.scoreCount = scoreCount;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }
}
