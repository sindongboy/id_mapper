package com.skplanet.nlp.data;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.log4j.Logger;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Hoppin Drama/Animation meta
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 2/27/15
 */
public class HoppinDramaMeta {
	private static final Logger LOGGER = Logger.getLogger(HoppinDramaMeta.class.getName());
	
	private Config myConfig = null;

	// json field definition for value overriding
	private static final String ID = "id";
	private static final String TITLE = "title";
	private static final String B_DATE = "begin";
	private static final String E_DATE = "end";
	private static final String SYNOPSIS = "synopsis";
	private static final String DIRECTORS = "directors";
	private static final String ACTORS_1 = "actors1";
	private static final String ACTORS_2 = "actors2";
	private static final String SCORE = "score";
	private static final String SCORE_COUNT = "score-count";

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	private static DecimalFormat scoreFormat = new DecimalFormat("0.00");

	// members
	private String id = null;
	private String title = null;
	private Calendar bDate = null;
	private boolean ends = false;
	private String synopsis = null;
	private List<String> directors = null;
	private List<String> actors1 = null;
	private List<String> actors2 = null;
	private double score = 0.00;
	private int scoreCount = 0;

	public HoppinDramaMeta() {

	}

	/**
	 * Constructor for value overriding
	 * @param config {@link com.typesafe.config.Config}
	 */
	public HoppinDramaMeta(final Config config) {
		this.myConfig = config;
		this.id = config.getString(ID);
		this.title = config.getString(TITLE);
		String bDateStr = config.getString(B_DATE);
		this.bDate = new GregorianCalendar();
		int year, month, day;
		if ("null".equals(bDateStr)) {
			year = 9999;
			month = 01;
			day = 01;
		} else {
			year = Integer.parseInt(bDateStr.substring(0, 4));
			month = Integer.parseInt(bDateStr.substring(4, 6)) - 1;
			day = Integer.parseInt(bDateStr.substring(6));
		}
		this.bDate.set(Calendar.YEAR, year);
		this.bDate.set(Calendar.MONTH, month);
		this.bDate.set(Calendar.DAY_OF_MONTH, day);
		this.ends = config.getBoolean(E_DATE);
		this.synopsis = config.getString(SYNOPSIS);
		this.directors = config.getStringList(DIRECTORS);
		this.actors1 = config.getStringList(ACTORS_1);
		this.actors2 = config.getStringList(ACTORS_2);
		this.score = config.getDouble(SCORE);
		this.scoreCount = config.getInt(SCORE_COUNT);
	}

	/**
	 * Get ID
	 * @return id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set ID
	 * @param id id to be set
	 */
	public void setId(String id) {
		this.id = id;
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
	 * @param title title to be set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Get Start Date*
	 * @return start date
	 */
	public Calendar getStartDate() {
		return bDate;
	}

	/**
	 * Set Start date
	 * @param date start date
	 */
	public void setStartDate(Calendar date) {
		this.bDate = date;
	}

	/**
	 * Set End Status
	 * @param end end?
	 */
	public void setEnds(boolean end) {
		this.ends = end;
	}

	/**
	 * check if the contents is currently playing on tv or not
	 * @return true for currently playing, false otherwise
	 */
	public boolean isEnds() {
		return this.ends;
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
	 * Get Director list
	 * @return director list
	 */
	public List<String> getDirectors() {
		return directors;
	}

	/**
	 * Set Director list
	 * @param directors director list
	 */
	public void setDirectors(List<String> directors) {
		this.directors = directors;
	}

	/**
	 * Get Main Actor list
	 * @return main actor list
	 */
	public List<String> getActors1() {
		return actors1;
	}

	/**
	 * Set Main Actor List
	 * @param actors actor list
	 */
	public void setActors1(List<String> actors) {
		this.actors1 = actors;
	}

	/**
	 * Get General Actor list
	 * @return actor list
	 */
	public List<String> getActors2() {
		return actors2;
	}

	/**
	 * Set General Actor list
	 * @param actors actor list
	 */
	public void setActors2(List<String> actors) {
		this.actors2 = actors;
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
	public double getScoreCount() {
		return scoreCount;
	}

	/**
	 * Set Score Count
	 * @param scoreCount score count
	 */
	public void setScoreCount(int scoreCount) {
		this.scoreCount = scoreCount;
	}

	@Override
	public String toString() {
		return "HoppinDramaMeta{" +
				"id='" + id + '\'' +
				", title='" + title + '\'' +
				", bDate=" + bDate +
				", ends=" + ends +
				", synopsis='" + synopsis + '\'' +
				", directors=" + directors +
				", actors1=" + actors1 +
				", actors2=" + actors2 +
				", score=" + score +
				", scoreCount=" + scoreCount +
				'}';
	}
	
	/*
	public static void main(String[] args) {
		Config mainConfig = ConfigFactory.load("daum-meta");
		long btime, etime;
		Collection<HoppinDramaMeta> metaCollection = new ArrayList<HoppinDramaMeta>();
		btime = System.currentTimeMillis();
		for (Config singleItem : mainConfig.getConfigList("daum.meta")) {
			HoppinDramaMeta singleMeta = new HoppinDramaMeta(singleItem);
			metaCollection.add(singleMeta);
		}
		etime = System.currentTimeMillis();
		System.out.println("meta loaded in " + (etime - btime) + " msec.");
	}
	*/
}
