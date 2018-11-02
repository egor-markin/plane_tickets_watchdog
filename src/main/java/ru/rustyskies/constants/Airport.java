package ru.rustyskies.constants;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Egor Markin
 * @since 21.10.2014
 */
@Getter
public enum Airport {
	
	// Europe
	Moscow("MOW", "Moscow", "Russia", Region.Europe),
	London("LON", "London", "UK", Region.Europe),
	Paris("PAR", "Paris", "France", Region.Europe),
	Frankfurt("FRA", "Frankfurt am Main", "Germany", Region.Europe),
	Amsterdam("AMS", "Amsterdam", "Netherlands", Region.Europe),
	Madrid("MAD", "Madrid", "Spain", Region.Europe),
	Munich("MUC", "Munich", "Germany", Region.Europe),
	Rome("ROM", "Rome", "Italy", Region.Europe),
	Barcelona("BCN", "Barcelona", "Spain", Region.Europe),
	Zurich("ZRH", "Zürich", "Switzerland", Region.Europe),
	Venice("VCE", "Venice", "Italy", Region.Europe),
    Milan("MIL", "Milan", "Italy", Region.Europe),
    Berlin("BER", "Berlin", "Germany", Region.Europe),
    Tunis("TUN", "Tunis", "Tunisia", Region.Europe),
    Stockholm("STO", "Stockholm", "Sweden", Region.Europe),
	
	// Middle East
	Dubai("DXB", "Dubai", "UAE", Region.MiddleEast),
	Antalya("AYT", "Antalya", "Turkey", Region.MiddleEast),
	Istanbul("IST", "Istanbul", "Turkey", Region.MiddleEast),
	
	// North America
	NewYork("NYC", "New York", "USA", Region.NorthAmerica),
	Atlanta("ATL", "Atlanta", "USA", Region.NorthAmerica),
	Chicago("CHI", "Chicago", "USA", Region.NorthAmerica),
	Dallas("DFW", "Dallas", "USA", Region.NorthAmerica),
	Denver("DEN", "Denver", "USA", Region.NorthAmerica),
	Charlotte("CLT", "Charlotte", "USA", Region.NorthAmerica),
	Miami("MIA", "Miami", "USA", Region.NorthAmerica),
	Houston("HOU", "Houston", "USA", Region.NorthAmerica),
	
	// Central America
	MexicoCity("MEX", "Mexico City", "Mexico", Region.CentralAmerica, Subregion.North), 
	Panama("PTY", "Panamá", "Panamá", Region.CentralAmerica, Subregion.North),
	SanSalvador("SAL", "San Salvador", "Salvador", Region.CentralAmerica, Subregion.North),
	Managua("MGA", "Managua", "Nicaragua", Region.CentralAmerica, Subregion.North),
	GuatemalaCity("GUA", "Guatemala City", "Guatemala", Region.CentralAmerica, Subregion.North),
	SanPedroSula("SAP", "San Pedro Sula", "Honduras", Region.CentralAmerica, Subregion.North),
	SanJose("SJO", "San José", "Costa Rica", Region.CentralAmerica, Subregion.North),

	// Caribbean
	Havana("HAV", "Havana", "Cuba", Region.Caribbean, Subregion.North),
	SantoDomingo("SDQ", "Santo Domingo", "Dominican Republic", Region.Caribbean, Subregion.North),
	
	// South America
	Belize("BZE", "Belize City", "Belize", Region.SouthAmerica, Subregion.North),
	Bogota("BOG", "Bogotá", "Columbia", Region.SouthAmerica, Subregion.North), 
	Lima("LIM", "Lima", "Peru", Region.SouthAmerica), 
	Quito("UIO", "Quito", "Ecuador", Region.SouthAmerica), 
	Guayaquil("GYE", "Guayaquil", "Equador", Region.SouthAmerica), 
	Santiago("SCL", "Santiago", "Chile", Region.SouthAmerica, Subregion.South), 
	SaoPaulo("SAO", "São Paulo", "Barzil", Region.SouthAmerica, Subregion.South),  
	Asuncion("ASU", "Asunción", "Paraguay", Region.SouthAmerica), 
	SantaCruz("SRZ", "Santa Cruz", "Bolivia", Region.SouthAmerica), 
	Caracas("CCS", "Caracas", "Venezuela", Region.SouthAmerica, Subregion.North), 
	BuenosAires("BUE", "Buenos Aires", "Argentina", Region.SouthAmerica, Subregion.South), 
	RioDeJaneiro("RIO", "Rio de Janeiro", "Brazil", Region.SouthAmerica, Subregion.South), 
	Montevideo("MVD", "Montevideo", "Uruguay", Region.SouthAmerica),
	Recife("REC", "Recife", "Brazil", Region.SouthAmerica, Subregion.South),

    // Asia
    Jakarta("JKT", "Jakarta", "Indonesia", Region.Asia),
    Manila("MNL", "Manila", "Philippines", Region.Asia),
    Bangkok("BKK", "Bangkok", "Thailand", Region.Asia),
    Phuket("HKT", "Phuket", "Thailand", Region.Asia),
    HoChiMinh("SGN", "Ho Chi Minh", "Vietnam", Region.Asia),
    Colombo("CMB", "Colombo", "Sri Lanka", Region.Asia),
	Tokyo("TYO", "Tokyo", "Japan", Region.Asia),

    // Africa
    Marrakesh("RAK", "Marrakesh", "Morocco", Region.Africa);
	
	public final String code;
	public final String city;
	public final String country;
	public final Region region;
	public final Subregion subregion;
	
	Airport(String code, String city, String country, Region region) {
		this.code = code;
		this.city = city;
		this.country = country;
		this.region = region;
		this.subregion = null;
	}
	
	Airport(String code, String city, String country, Region region, Subregion subregion) {
		this.code = code;
		this.city = city;
		this.country = country;
		this.region = region;
		this.subregion = subregion;
	}
	
	public static List<Airport> getAirportsForRegion(Region r) {
		List<Airport> list = new LinkedList<>();
		Airport[] airports = Airport.values();
		for (Airport a : airports) {
			if (r.equals(a.region)) {
				list.add(a);
			}
		}
		return list;
	}
	
	public static List<Airport> getAirportsForSubregion(Subregion sr) {
		List<Airport> list = new LinkedList<>();
		Airport[] airports = Airport.values();
		for (Airport a : airports) {
			if (sr.equals(a.subregion)) {
				list.add(a);
			}
		}
		return list;
	}
}
