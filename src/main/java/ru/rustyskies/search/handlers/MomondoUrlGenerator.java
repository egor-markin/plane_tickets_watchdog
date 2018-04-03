package ru.rustyskies.search.handlers;

import ru.rustyskies.constants.Airport;
import ru.rustyskies.model.Flight;
import ru.rustyskies.model.SearchConf;
import ru.rustyskies.utils.DateUtils;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Egor Markin
 * @since 07.04.2015
 */
public class MomondoUrlGenerator {
	
	public static final String BASE_URL = "https://www.momondo.ru/";
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");


	public static String getUrl(SearchConf searchConf) {
        return getUrl(searchConf.flights, searchConf.onlyDirectFlights);
    }

//	public static String getUrl(Airport srcAirport, Airport destAirport, Date toDate, boolean onlyDirectFlights) {
//		return getUrl(srcAirport, destAirport, toDate, null, onlyDirectFlights);
//	}
	
//	public static String getUrl(Airport srcAirport, Airport destAirport, Date toDate, Date backDate, boolean onlyDirectFlights) {
//		List<Flight> flights = new LinkedList<Flight>();
//		flights.add(new Flight(srcAirport, destAirport, toDate));
//		if (backDate != null) {
//			flights.add(new Flight(destAirport, srcAirport, backDate));
//		}
//		return getUrl(flights, onlyDirectFlights);
//	}
	
	public static String getUrl(List<Flight> flights, boolean onlyDirectFlights) {
		StringBuilder url = new StringBuilder();
		url.append(BASE_URL);
		url.append("flightsearch/?Search=true&");
		if (flights.size() == 1) {
			url.append("TripType=1&"); // 1 - one way, 2 - back and forth
			url.append("SegNo=1&"); // Number of segments, 1 for one way, 2 for back and forth
		} else {
			url.append("TripType=2&"); // 1 - one way, 2 - back and forth
			url.append("SegNo=2&"); // Number of segments, 1 for one way, 2 for back and forth
		}

		for (int i = 0; i < flights.size(); i++) {
			Flight f = flights.get(i);
			url.append("SO").append(i).append("=").append(f.srcAirport.code).append("&");
			url.append("SD").append(i).append("=").append(f.destAirport.code).append("&");
			url.append("SDP").append(i).append("=").append(dateFormat.format(f.date)).append("&");
		}
		url.append("AD=1&"); // Number of adult passengers
		url.append("TK=ECO&"); // ECO - Economy, FLX - Premium, BIZ - Business, FST - First class
		url.append("DO=").append(onlyDirectFlights).append("&"); // Only direct flights
		url.append("NA=true"); // Search for airports nearby

		return url.toString();
	}
}
