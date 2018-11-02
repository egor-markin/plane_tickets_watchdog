package ru.rustyskies.model;

import lombok.Getter;
import ru.rustyskies.constants.Airport;

import java.sql.Date;

/**
 * @author Egor Markin
 * @since 21.10.2014
 */
@Getter
public class Flight {

	public final Airport srcAirport;
	public final Airport destAirport;
	public final Date date;
	
	public Flight(Airport srcAirport, Airport destAirport, Date date) {
		super();
		this.srcAirport = srcAirport;
		this.destAirport = destAirport;
		this.date = date;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Flight [srcAirport=");
		builder.append(srcAirport);
		builder.append(", destAirport=");
		builder.append(destAirport);
		builder.append(", date=");
		builder.append(date);
		builder.append("]");
		return builder.toString();
	}
	
}
