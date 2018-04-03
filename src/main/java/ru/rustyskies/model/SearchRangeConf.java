package ru.rustyskies.model;

import ru.rustyskies.constants.Airport;

import java.sql.Date;

/**
 * @author Egor Markin
 * @since 27.10.2014
 */
public class SearchRangeConf {
	
	public Airport srcAirport;
	public Airport destAirport;
	public Date toDateStart;
	public Date toDateEnd;
	public Date backDateStart;
	public Date backDateEnd;
	public int elementsCount;
	
	public SearchRangeConf() {
	}
	
	public SearchRangeConf(Airport srcAirport, Airport destAirport, Date toDateStart, Date toDateEnd, Date backDateStart,
			Date backDateEnd, int elementsCount) {
		this.srcAirport = srcAirport;
		this.destAirport = destAirport;
		this.toDateStart = toDateStart;
		this.toDateEnd = toDateEnd;
		this.backDateStart = backDateStart;
		this.backDateEnd = backDateEnd;
		this.elementsCount = elementsCount;
	}

	@Override
	public String toString() {
		return "SearchRangeConf{" +
				"srcAirport=" + srcAirport +
				", destAirport=" + destAirport +
				", toDateStart=" + toDateStart +
				", toDateEnd=" + toDateEnd +
				", backDateStart=" + backDateStart +
				", backDateEnd=" + backDateEnd +
				", elementsCount=" + elementsCount +
				'}';
	}
}
