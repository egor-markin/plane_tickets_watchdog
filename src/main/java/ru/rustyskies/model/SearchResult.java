package ru.rustyskies.model;

import java.util.List;

/**
 * @author Egor Markin
 * @since 08.04.2015
 */
public class SearchResult {
	
	public final String searchGroupId;
	
	public final List<Trip> trips;
	
	public SearchResult(List<Trip> trips) {
		this(null, trips);
	}
	
	public SearchResult(String searchGroupId, List<Trip> trips) {
		this.searchGroupId = searchGroupId;
		this.trips = trips;
	}
	
}
