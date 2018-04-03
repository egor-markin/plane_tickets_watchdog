package ru.rustyskies.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.rustyskies.constants.Airport;
import ru.rustyskies.utils.DateUtils;

import java.sql.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Egor Markin
 * @since 22.10.2014
 */
public class TripsReportData {
	
	public final static String DATES_SPLITTER = ",";
	
	private static final Log logger = LogFactory.getLog(TripsReportData.class);
	
	private final Airport srcAirport;
	private final Airport destAirport;
	private final Date toDateStart;
	private final Date toDateEnd;
	private final Date backDateStart;
	private final Date backDateEnd;
	
	private final Date reportDate;
	private final List<Trip> trips; // Trips sorted by price
	
	// Calculable
	private final String reportTitle;
	private final int tripsNumber;
	private final String totalSearchTime;
	private final String avgSearchTime;
	private final String minSearchTime;
	private final String maxSearchTime;
	private final List<String> toDates;
	private final List<String> backDates;
	private final Map<String, Trip> tripsData;
	private final int minimalPrice;
	private final int maximalPrice;
	
	private Map<String, String> cellColors;
	
	public TripsReportData(SearchRangeConf conf, long searchTime, Date reportDate, List<Trip> trips) {
		this(conf.srcAirport, conf.destAirport, conf.toDateStart, conf.toDateEnd, conf.backDateStart, conf.backDateEnd,
				searchTime, reportDate, trips);
	}
	
	public TripsReportData(Airport srcAirport, Airport destAirport, Date toDateStart, Date toDateEnd,
			Date backDateStart, Date backDateEnd, long searchTime, Date reportDate, List<Trip> trips) {
		
		StringBuilder reportTitleB = new StringBuilder();
		reportTitleB.append("Airplane prices: ");
		reportTitleB.append(srcAirport.country).append(".").append(srcAirport.city).append(" (").append(srcAirport.code).append(")");
		if (backDateStart != null && backDateEnd != null) {
			reportTitleB.append(" <-> ");
		} else {
			reportTitleB.append(" -> ");
		}
		reportTitleB.append(destAirport.country).append(".").append(destAirport.city).append(" (").append(destAirport.code).append(")");
		reportTitleB.append(" on ");
		reportTitleB.append("(").append(DateUtils.getDateAsString(toDateStart)).append(" -> ").append(DateUtils.getDateAsString(toDateEnd)).append(")");
		if (backDateStart != null && backDateEnd != null) {
			reportTitleB.append(" <-> ");
			reportTitleB.append("(").append(DateUtils.getDateAsString(backDateStart)).append(" -> ").append(DateUtils.getDateAsString(backDateEnd)).append(")");
		}
		
		this.reportTitle = reportTitleB.toString();
		this.srcAirport = srcAirport;
		this.destAirport = destAirport;
		this.toDateStart = toDateStart;
		this.toDateEnd = toDateEnd;
		this.backDateStart = backDateStart;
		this.backDateEnd = backDateEnd;
		this.reportDate = reportDate;
		this.trips = trips;
		
		// Search statistics
		long minST = Long.MAX_VALUE;
		long maxST = Long.MIN_VALUE;
		for (Trip t : trips) {
			if (t.searchTime < minST) {
				minST = t.searchTime;
			}
			if (t.searchTime > maxST) {
				maxST = t.searchTime;
			}
		}
		this.tripsNumber = trips.size();
		this.totalSearchTime = DateUtils.getSecondsAsFormatedTime(searchTime);
		this.avgSearchTime = DateUtils.getSecondsAsFormatedTime(searchTime / tripsNumber);
		this.minSearchTime = DateUtils.getSecondsAsFormatedTime(minST);
		this.maxSearchTime = DateUtils.getSecondsAsFormatedTime(maxST);
		
		long d;
		
		// toDates list
		toDates = new LinkedList<String>();
		d = toDateStart.getTime();
		while (d <= toDateEnd.getTime()) {
			toDates.add(DateUtils.getDateAsString(new Date(d)));
			d += 86400000; // +1 day
		}
		
		// backDates list
		backDates = new LinkedList<String>();
		if (backDateStart != null && backDateEnd != null) {
			d = backDateStart.getTime();
			while (d <= backDateEnd.getTime()) {
				backDates.add(DateUtils.getDateAsString(new Date(d)));
				d += 86400000; // +1 day
			}
		} else {
			backDates.add("");
		}
		
		// Generating tripsData based on trips list
		this.tripsData = new HashMap<>();
		for (Trip t : trips) {
			putTrip(t);
		}
		
		// ==== Calculating cells colors ====
		
		// At first looking for min and max prices within tripsData
		int minPrice = Integer.MAX_VALUE;
		int maxPrice = Integer.MIN_VALUE;
		for (Trip t : trips) {
			if (t.price < minPrice) {
				minPrice = t.price;
			}
			if (t.price > maxPrice) {
				maxPrice = t.price;
			}
		}
		int pricesRange = maxPrice - minPrice;
		
		this.minimalPrice = minPrice;
		this.maximalPrice = maxPrice;
		
		// Generating a map with colors
		cellColors = new HashMap<>();
		for (Trip t : trips) {
			double brightness = 1.0 - (double)(t.price - minPrice) / (double) pricesRange;
			
			//System.out.println("price " + t.price + " brightness: " + brightness);
			
			//Color c = Color.getHSBColor((float) 0.6375, (float) 0.95, (float) brightness);
			//String hex = String.format("%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue()).toUpperCase();
			//String hex = Integer.toHexString(c.getRGB()).toUpperCase();
			//String hex = Integer.toHexString(c.getRGB() & 0xffffff).toUpperCase();
			
			int color = 110 + (int) (145 * brightness);
			
			String hex = String.format("%02X%02X%02X", color, color, color);
			
			cellColors.put(t.getKey(), hex);
		}
	}
	
	public Trip getTrip(Date toDate, Date backDate) {
		return getTrip(DateUtils.getDateAsString(toDate), backDate != null ? DateUtils.getDateAsString(backDate) : null);
	}
	public Trip getTrip(String toDate, String backDate) {
		Trip t;
		if (backDate != null && !backDate.trim().equals("")) {
			t = tripsData.get(toDate + DATES_SPLITTER + backDate);
		} else {
			t = tripsData.get(toDate);
		}
		return t;
	}
	
	public void putTrip(Trip trip) {
		tripsData.put(trip.getKey(), trip);
	}
	
	public String getColor(Date toDate, Date backDate) {
		return getColor(DateUtils.getDateAsString(toDate), backDate != null ? DateUtils.getDateAsString(backDate) : null);
	}
	public String getColor(String toDate, String backDate) {
		String color;
		if (backDate != null && !backDate.trim().equals("")) {
			color = cellColors.get(toDate + DATES_SPLITTER + backDate);
		} else {
			color = cellColors.get(toDate);
		}
		
		if (color == null || color.trim().equals("")) {
			//throw new RuntimeException("Unable to obtain color for " + toDate + "-" + backDate + ". cellColors: " + cellColors);
			color = "FFFFFF"; // White
			//logger.warn("Unable to get a color for " + toDate + "-" + backDate);
		}
		return color;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	
	public Map<String, Trip> getTripsData() {
		return tripsData;
	}
	
	public List<Trip> getTrips() {
		return trips;
	}

	public String getReportTitle() {
		return reportTitle;
	}

	public Airport getSrcAirport() {
		return srcAirport;
	}

	public Airport getDestAirport() {
		return destAirport;
	}

	public String getToDateStart() {
		return DateUtils.getDateAsString(toDateStart);
	}

	public String getToDateEnd() {
		return DateUtils.getDateAsString(toDateEnd);
	}

	public String getBackDateStart() {
		return DateUtils.getDateAsString(backDateStart);
	}

	public String getBackDateEnd() {
		return DateUtils.getDateAsString(backDateEnd);
	}

	public String getReportDate() {
		return DateUtils.getDateTimeAsString(reportDate);
	}

	public int getTripsNumber() {
		return tripsNumber;
	}

	public String getTotalSearchTime() {
		return totalSearchTime;
	}

	public String getAvgSearchTime() {
		return avgSearchTime;
	}

	public List<String> getToDates() {
		return toDates;
	}

	public List<String> getBackDates() {
		return backDates;
	}

	public int getMinimalPrice() {
		return minimalPrice;
	}

	public String getMinSearchTime() {
		return minSearchTime;
	}

	public String getMaxSearchTime() {
		return maxSearchTime;
	}

	public int getMaximalPrice() {
		return maximalPrice;
	}
}
