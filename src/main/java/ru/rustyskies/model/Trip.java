package ru.rustyskies.model;

import lombok.Data;
import ru.rustyskies.search.Search;
import ru.rustyskies.utils.DateUtils;

/**
 * @author Egor Markin
 * @since 21.10.2014
 */
@Data
public class Trip implements Comparable<Trip> {
	
	public Flight flight1;
	public String airlines1;
	public int travelTime1;
	public int stops1;
	
	public Flight flight2;
	public String airlines2;
	public int travelTime2;
	public int stops2;
	
	public int price;
	public String priceCurrency;
	
	public int priceEur;
	
	public String ticketUrl;
	public String momondoSearchUrl;
	public long searchTime;

	public Trip() {
	}
	
	public Trip(int price) {
		this();
		this.price = price;
	}
	
	public String getKey() {
		StringBuilder key = new StringBuilder();
		key.append(DateUtils.getDateAsString(flight1.date));
		if (flight2 != null && flight2.date != null) {
			key.append(TripsReportData.DATES_SPLITTER).append(DateUtils.getDateAsString(flight2.date));
		}
		return key.toString();
	}

	public String getTravelTime1Formatted() {
		return DateUtils.getSecondsAsFormatedTime(travelTime1);
	}

    public String getTravelTime2Formatted() {
        return DateUtils.getSecondsAsFormatedTime(travelTime2);
    }

    public long getStayDaysCount() {
	    if (flight1 != null && flight1.date != null && flight2 != null && flight2.date != null) {
            return (flight2.date.getTime() - flight1.date.getTime()) / Search.MSEC_A_DAY;
        } else {
	        return 0;
        }
    }

    @Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Trip [");
		builder.append("flight1=").append(flight1).append(" ");
		builder.append("airlines1=").append(airlines1).append(" ");
		builder.append("travelTime1=").append(getTravelTime1Formatted()).append(" ");
		builder.append("stops1=").append(stops1).append(" ");
		
		if (flight2 != null && airlines2 != null) {
			builder.append("flight2=").append(flight2).append(" ");
			builder.append("airlines2=").append(airlines2).append(" ");
			builder.append("travelTime2=").append(getTravelTime2Formatted()).append(" ");
			builder.append("stops2=").append(stops2).append(" ");
		}
		builder.append("price=").append(price).append(" ");
        builder.append("stayDaysCount=").append(getStayDaysCount()).append(" ");
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int compareTo(Trip o) {
		if (o != null) {
			if (this.price != o.price) {
				return Integer.compare(this.price, o.price);
			} else {
				return Integer.compare(this.travelTime1 + this.travelTime2, o.travelTime1 + o.travelTime2);
			}
		} else {
			return 1;
		}
	}
}
