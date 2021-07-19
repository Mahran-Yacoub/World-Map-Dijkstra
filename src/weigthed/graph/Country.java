package weigthed.graph;

/**
 * This class will be used to create objects to represent countries as vertices
 * in a graph
 *
 */
public class Country {

	/** Name of a country */
	private String name;

	/** Y AXIS */
	private double latitude;

	/** X - Axis */
	private double longitude;
	
	
	
	public Country(String name) {
		
		this.name = name;
	}

	
	
	public Country(String name, double latitude, double longitude) {

		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	@Override
	public boolean equals(Object o) {

		if (o == null) {
			return false;
		}

		if (o == this) {
			return true;
		}

		if (o.getClass() != this.getClass()) {

			return false;
		}

		Country country = (Country) o;

		if (this.name.equals(country.name)) {
			return true;
		}

		return false;
	}
	
	@Override
	public String toString() {
		
		return this.name ;
	}

}
