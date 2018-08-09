package com.skplanet.nlp.data;

import com.skplanet.nlp.common.Utilities;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Naver Meta
 * <br>
 * <br>
 * Created by Donghun Shin
 * <br>
 * Contact: donghun.shin@sk.com, sindongboy@gmail.com
 * <br>
 * Date: 10/25/13
 * <br>
 */
@SuppressWarnings("unused")
public class NaverMeta {
    private static final Logger LOGGER = Logger.getLogger(NaverMeta.class.getName());

    //----------------------------------//
    // members
    //----------------------------------//

    private String nid = null;
    private String serviceTitle = null;
    private String serviceTitleOrg = null;
    private String naverTitle = null;
    private List<String> naverTitleOrg = null;
    private List<Calendar> releasedDate = null;
    private List<String> directors = null;
    private List<String> actors = null;
    private float score = 0.0f;
    private int scoreCount = 0;


    private boolean orgTitleCrawl = false;

    public NaverMeta() {
        this.releasedDate = new ArrayList<Calendar>();
        this.directors = new ArrayList<String>();
        this.actors = new ArrayList<String>();
        this.naverTitleOrg = new ArrayList<String>();
    }

    //----------------------------------//
    // getter & setter
    //----------------------------------//

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getServiceTitle() {
        return serviceTitle;
    }

    public void setServiceTitle(String serviceTitle) {
        this.serviceTitle = serviceTitle;
    }

    public String getNaverTitle() {
        return naverTitle;
    }

    public void setNaverTitle(String naverTitle) {
        this.naverTitle = naverTitle;
    }

    public List<Calendar> getReleasedDate() {
        return releasedDate;
    }

    public Set<Calendar> getSortedDate() {
        Set<Calendar> sortedList = new TreeSet<Calendar>();
        for (Calendar c : this.releasedDate) {
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

    public int getScoreCount() {
        return scoreCount;
    }

    public void setScoreCount(int nVoted) {
        this.scoreCount = nVoted;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    /**
     * Set Naver's Released Dates, it now allows multiple released dates delim. by "%"
     * @param date date to be processed
     */
    public void setReleasedDate(String date) {
        List<Calendar> allDates = new ArrayList<Calendar>();
        String[] fields = date.split("%");

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
            }

            if (newDates != null) {
                allDates.add(newDates);
            } else {
                LOGGER.error("Naver Meta has wrong Released Date format : " + date);
            }
        }

        this.releasedDate = allDates;

    }

    public void setReleasedDate(String date, String delim) {
        List<Calendar> allDates = new ArrayList<Calendar>();
        String[] fields = date.split(delim);

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
            }

            if (newDates != null) {
                allDates.add(newDates);
            } else {
                LOGGER.error("Naver Meta has wrong Released Date format : " + date);
            }
        }

        this.releasedDate = allDates;
    }

    public List<String> getDirectors() {
        return directors;
    }

    public void setDirectors(String[] directors) {
        Collections.addAll(this.directors, directors);
    }

    public List<String> getActors() {
        return actors;
    }

    public void setActors(String[] actors) {
        Collections.addAll(this.actors, actors);
    }

    public boolean isOrgTitleCrawl() {
        return orgTitleCrawl;
    }

    public void setOrgTitleCrawl(boolean orgTitleCrawl) {
        this.orgTitleCrawl = orgTitleCrawl;
    }

    public String getServiceTitleOrg() {
        return serviceTitleOrg;
    }

    public void setServiceTitleOrg(String serviceTitleOrg) {
        this.serviceTitleOrg = serviceTitleOrg;
    }

    public List<String> getNaverTitleOrg() {
        return naverTitleOrg;
    }

    public void setNaverTitleOrg(String naverTitleOrg) {
        this.naverTitleOrg.add(naverTitleOrg);
        String[] fields = naverTitleOrg.split("[,:]");
        for (String f : fields) {
            this.naverTitleOrg.add(f);
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(nid).append("\t");
        sb.append(serviceTitle).append("\t");
        sb.append(naverTitle).append("\t");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        for (int i = 0; i < this.releasedDate.size(); i++) {
            String dateStr = dateFormat.format(this.releasedDate.get(i).getTime());
            if (i == this.releasedDate.size() - 1) {
                sb.append(dateStr).append("\t");
            } else {
                sb.append(dateStr).append("%");
            }
        }
        sb.append(Utilities.arrayToString(directors, "^")).append("\t");
        sb.append(Utilities.arrayToString(actors, "^"));
        return sb.toString();
    }
}
