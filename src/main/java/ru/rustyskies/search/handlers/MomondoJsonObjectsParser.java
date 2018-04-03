package ru.rustyskies.search.handlers;

import lombok.Data;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import ru.rustyskies.model.Trip;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Parses Momondo's JSON objects
 * 
 * @author Egor Markin
 * @since 27.03.2015
 */
public class MomondoJsonObjectsParser {

	@Data
	private static class Airport {
        public String continentCode;
		public String countryName;
		public String countryCode;
		public String displayName;
		public String iata;
		public String mainCityName;
	}

    @Data
	private static class Airline {
		public String iata;
		public String name;
		public String shortName;
	}

    @Data
	private static class Fee {
		public int airlineIndex;
		public String description;
		public int maxAmountEUR;
		public int minAmountEUR;
		public String type;
		public int paymentId;
	}

    @Data
	private static class Flight {
		public String key;
		public int[] segmentIndexes;
		public int ticketClassIndex;
	}

    @Data
	private static class Leg {
		public int airlineIndex;
		public Date arrival;
		public Date departure;
		public int destinationIndex;
		public int duration;
		public String flightNumber;
		public String key;
		public int originIndex;
		public int stopOvers;
		public int[] stopOverCodeIndexes;
	}

    @Data
	private static class Offer {
		public int totalPrice;
		public int totalPriceEUR;
		public int flightIndex;
		public int[] feeIndexes;
		public String deepLink;
		public String mobileDeepLink;
		public String currency;
	}

    @Data
	private static class Segment {
		public int duration;
		public String key;
		public int[] legIndexes;
	}
	
	private Map<String, Airport> airports = new HashMap<>();
	private List<Airline> airlines = new LinkedList<>();
	private List<Fee> fees = new LinkedList<>();
	private List<Flight> flights = new LinkedList<>();
	private List<Leg> legs = new LinkedList<>();
	private List<Offer> offers = new LinkedList<>();
	private List<Segment> segments = new LinkedList<>();
    
    private static final SimpleDateFormat defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	public void applyData(JSONObject baseObject) throws JSONException {
		Iterator<?> keys = baseObject.keys();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			Object value = baseObject.get(key);
			
			//System.out.println(key + " - " + value != null ? value.getClass() : "null");

			if (value instanceof JSONArray) {
				JSONArray array = (JSONArray) value;
				for (int i = 0; i < array.length(); i++) {
					JSONObject o = array.getJSONObject(i);
                    switch (key) {
                        case "Airports": {
                            Airport b = new Airport();
                            copyData(o, b);
                            airports.put(b.iata, b);
                            break;
                        }
                        case "Airlines": {
                            Airline b = new Airline();
                            copyData(o, b);
                            airlines.add(b);
                            break;
                        }
                        case "Fees": {
                            Fee b = new Fee();
                            copyData(o, b);
                            fees.add(b);
                            break;
                        }
                        case "Flights": {
                            Flight b = new Flight();
                            copyData(o, b);
                            flights.add(b);
                            break;
                        }
                        case "Legs": {
                            Leg b = new Leg();
                            copyData(o, b);
                            legs.add(b);
                            break;
                        }
                        case "Offers": {
                            Offer b = new Offer();
                            copyData(o, b);
                            offers.add(b);
                            break;
                        }
                        case "Segments": {
                            Segment b = new Segment();
                            copyData(o, b);
                            segments.add(b);
                            break;
                        }
                    }
				}
			}
		}
	}
	
	private void copyData(JSONObject src, Object dest) {
		Field[] fields = dest.getClass().getFields();
        for (Field field : fields) {
            String javaFieldName = field.getName();
            String jsonFieldName = Character.toUpperCase(javaFieldName.charAt(0)) + javaFieldName.substring(1);

            if (!src.has(jsonFieldName)) {
                continue;
            }

            try {
                Class<?> fieldType = field.getType();

                if (fieldType.isArray()) {
                    Object value = src.get(jsonFieldName);
                    if (value instanceof JSONArray) {
                        JSONArray a = (JSONArray) value;

                        if (a.length() > 0) {
                            Object firstItem = a.get(0);
                            if (firstItem instanceof Integer) {
                                int[] b = new int[a.length()];
                                for (int j = 0; j < a.length(); j++) {
                                    b[j] = a.getInt(j);
                                }
                                field.set(dest, b);
                            } else if (firstItem instanceof Long) {
                                long[] b = new long[a.length()];
                                for (int j = 0; j < a.length(); j++) {
                                    b[j] = a.getLong(j);
                                }
                                field.set(dest, b);
                            } else if (firstItem instanceof String) {
                                String[] b = new String[a.length()];
                                for (int j = 0; j < a.length(); j++) {
                                    b[j] = a.getString(j);
                                }
                                field.set(dest, b);
                            } else if (firstItem instanceof Double) {
                                double[] b = new double[a.length()];
                                for (int j = 0; j < a.length(); j++) {
                                    b[j] = a.getDouble(j);
                                }
                                field.set(dest, b);
                            } else if (firstItem instanceof Boolean) {
                                boolean[] b = new boolean[a.length()];
                                for (int j = 0; j < a.length(); j++) {
                                    b[j] = a.getBoolean(j);
                                }
                                field.set(dest, b);
                            } else {
                                throw new RuntimeException("Unsupported type: " + firstItem.getClass());
                            }
                        }
                    }
                } else if (fieldType.equals(String.class)) {
                    if (src.isNull(jsonFieldName)) {
                        field.set(dest, null);
                    } else {
                        field.set(dest, src.getString(jsonFieldName));
                    }
                } else if (fieldType.equals(int.class)) {
                    if (src.isNull(jsonFieldName)) {
                        field.setInt(dest, 0);
                    } else {
                        field.setInt(dest, src.getInt(jsonFieldName));
                    }
                } else if (fieldType.equals(boolean.class)) {
                    if (src.isNull(jsonFieldName)) {
                        field.setBoolean(dest, false);
                    } else {
                        field.setBoolean(dest, src.getBoolean(jsonFieldName));
                    }
                } else if (fieldType.equals(double.class)) {
                    if (src.isNull(jsonFieldName)) {
                        field.setDouble(dest, 0);
                    } else {
                        field.setDouble(dest, src.getDouble(jsonFieldName));
                    }
                } else if (fieldType.equals(long.class)) {
                    if (src.isNull(jsonFieldName)) {
                        field.setLong(dest, 0);
                    } else {
                        field.setLong(dest, src.getLong(jsonFieldName));
                    }
                } else if (fieldType.equals(Date.class)) {
                    String strValue = src.isNull(jsonFieldName) ? null : src.getString(jsonFieldName);
                    if (strValue != null && strValue.trim().equals("")) {
                        Date date;
                        try {
                            java.util.Date d = defaultDateFormat.parse(strValue.trim());
                            date = new Date(d.getTime());
                        } catch (ParseException e) {
                            throw new RuntimeException("Incorrect date/time format: " + strValue);
                        }
                        field.set(dest, date);
                    } else {
                        field.set(dest, null);
                    }
//				} else if (fieldType.equals(.class)) {
//					value = src.get(jsonFieldName);
//					fields[i].set(dest, value);
                } else {
                    throw new RuntimeException("Unsupported type: " + fieldType.getName() + " for field " + jsonFieldName + " for object " + src.toString(2));
                }
            } catch (JSONException | IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
	}

	public List<Trip> getTrips() {
		List<Trip> result = new LinkedList<>();
		
		// Converting offers into Trips list
		for (Offer offer : offers) {
			Trip t = new Trip();
			result.add(t);
			
			t.price = offer.totalPrice;
			t.priceCurrency = offer.currency;
			t.priceEur = offer.totalPriceEUR;
			t.ticketUrl = offer.deepLink;
			
			Flight flight = flights.get(offer.flightIndex);
			if (flight != null) {
				Segment[] sgmnts = getSegments(flight.segmentIndexes);
				// Ida
				if (sgmnts.length > 0) {
					if (sgmnts[0].legIndexes != null) {
						t.stops1 = sgmnts[0].legIndexes.length - 1;
						t.airlines1 = getAirlines(getLegs(sgmnts[0].legIndexes));
					} else {
						t.stops1 = 0;
					}
					t.travelTime1 = sgmnts[0].duration;
				}
				
				// Vuelta
				if (sgmnts.length > 1) {
					if (sgmnts[1].legIndexes != null) {
						t.stops2 = sgmnts[1].legIndexes.length - 1;
						t.airlines2 = getAirlines(getLegs(sgmnts[1].legIndexes));
					} else {
						t.stops2 = 0;
					}
					t.travelTime2 = sgmnts[1].duration;
				}
			}
		}
		
		// Sorting trips
		Collections.sort(result);
		
		return result;
	}
	
	private String getAirlines(Leg[] lgs) {
		StringBuilder names = new StringBuilder();
        for (Leg lg : lgs) {
            if (names.length() > 0) {
                names.append(", ");
            }
            Airline a = airlines.get(lg.airlineIndex);
            if (a != null) {
                names.append(a.name);
            }
        }
		return names.toString();
	}
	
	private Segment[] getSegments(int[] s) {
		if (s == null || s.length == 0) {
			return new Segment[0];
		}
		Segment[] result = new Segment[s.length];
		for (int i = 0; i < s.length; i++) {
			result[i] = segments.get(s[i]);
		}
		return result;
	}
	
	private Leg[] getLegs(int[] legsIndexes) {
		Leg[] result = new Leg[legsIndexes.length];
		for (int i = 0; i < legsIndexes.length; i++) {
			result[i] = legs.get(legsIndexes[i]);
		}
		return result;
	}
	
	public void printStatistics() {
		System.out.println("airports.size = " + airports.size());
		System.out.println("airlines.size = " + airlines.size());
		System.out.println("fees.size = " + fees.size());
		System.out.println("flights.size = " + flights.size());
		System.out.println("legs.size = " + legs.size());
		System.out.println("offers.size = " + offers.size());
		System.out.println("segments.size = " + segments.size());
	}
	
	public static void main(String[] args) {
//		JSONObject src = new JSONObject();
//		try {
//			src.put("AirlineIndex", 123);
//			src.put("Description", "Some description");
//			src.put("MaxAmountEUR", 345);
//			src.put("MinAmountEUR", 223);
//			src.put("Type", "Payment");
//			src.put("PaymentId", 3);
//		} catch (JSONException e) {
//			throw new RuntimeException(e);
//		}
		
		String[] files = new String[] {
			"SimpleData/20150330104502-0.json",
//			"SimpleData/20150330104502-1.json",
//			"SimpleData/20150330104502-2.json",
//			"SimpleData/20150330104502-3.json",
//			"SimpleData/20150330104502-4.json"
		};
		
		MomondoJsonObjectsParser parser = new MomondoJsonObjectsParser();
		for (String fileName : files) {
			JSONObject src;
			try {
				src = new JSONObject(new JSONTokener(new FileReader(fileName)));
			} catch (FileNotFoundException | JSONException e1) {
				throw new RuntimeException(e1);
			}

            List<Trip> trips;
			try {
				parser.applyData(src);
				trips = parser.getTrips();
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
			
			for (Trip t : trips) {
				System.out.println(t);
			}
			
//			System.out.println("Airports: " + parser.airports.keySet());
//			System.out.println("Airlines: " + parser.airlines.size());
//			System.out.println("Fees: " + parser.fees.size());
//			System.out.println("Flights: " + parser.flights.size());
//			System.out.println("Legs: " + parser.legs.size());
//			System.out.println("Offers: " + parser.offers.size());
//			System.out.println("Segments: " + parser.segments.size());
			System.out.println();
		}
	}

}
