package ru.rustyskies.model;

import com.google.common.collect.ImmutableList;
import lombok.Value;
import ru.rustyskies.constants.Airport;

import java.sql.Date;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Egor Markin
 * @since 08.04.2015
 */
@Value
public class SearchConf {

	public String searchGroupId;
	public Airport srcAirport;
	public Airport destAirport;
	public Date toDate;
	public Date backDate;
	public boolean onlyTopResult;
	public boolean onlyDirectFlights;

    public List<Flight> flights;

	public SearchConf(String searchGroupId, Airport srcAirport, Airport destAirport, Date toDate, Date backDate,
                       boolean onlyTopResult, boolean onlyDirectFlights) {
		this.searchGroupId = searchGroupId;
		this.srcAirport = srcAirport;
		this.destAirport = destAirport;
		this.toDate = toDate;
		this.backDate = backDate;
		this.onlyTopResult = onlyTopResult;
		this.onlyDirectFlights = onlyDirectFlights;

        if (backDate == null) {
            flights = ImmutableList.of(new Flight(srcAirport, destAirport, toDate));
        } else {
            flights = ImmutableList.of(new Flight(srcAirport, destAirport, toDate), new Flight(destAirport, srcAirport, backDate));
        }
    }
	
	public SearchConf(Airport srcAirport, Airport destAirport, Date toDate, Date backDate,
                      boolean onlyTopResult, boolean onlyDirectFlights) {
		this(null, srcAirport, destAirport, toDate, backDate, onlyTopResult, onlyDirectFlights);
	}
}
