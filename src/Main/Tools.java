package Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import weigthed.graph.Country;
import weigthed.graph.WeightedEdge;
import weigthed.graph.WeigthedSparseGraph;

public class Tools {

	/** This constant is used to get distance in KM */
	private final double KILO_METERS = 6371;

	private ArrayList<Country> listOfCountries;

	private ArrayList<WeightedEdge> listOfEdges;

	/////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 * @param verticesFile File represent list of vertices (Countries) , each line
	 *                     is considered to be country
	 * 
	 * @return list of vertices that contains all of countries from a file
	 * 
	 */
	public ArrayList<Country> getVerticesList(File verticesFile) {

		ArrayList<Country> listOfCountries = new ArrayList<Country>(50);

		try {

			Scanner scan = new Scanner(verticesFile);
			while (scan.hasNextLine()) {

				String line = scan.nextLine();
				String[] parts = line.split(" ");

				String nameOfCountry = parts[0];

				double longitude = Double.parseDouble(parts[1]);
				double latitude = Double.parseDouble(parts[2]);

				Country newCountry = new Country(nameOfCountry, latitude, longitude);
				listOfCountries.add(newCountry);
			}

			scan.close();

		} catch (FileNotFoundException e) {

			Alert alert = new Alert(AlertType.WARNING);
			alert.setContentText("Error of reading File");
			alert.show();
		}

		return listOfCountries;

	}

	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	/**
	 * @param edgesFile File represent list of directed edges (Flights) , each line
	 *                  is considered to be Flight
	 * 
	 * @return list of vertices that contains all of edges from a file
	 * 
	 */
	public ArrayList<WeightedEdge> getEdgesList(File edgesFile, ArrayList<Country> listOfCountries) {

		ArrayList<WeightedEdge> listOfFlighs = new ArrayList<WeightedEdge>(70);
		try {

			Scanner scan = new Scanner(edgesFile);
			while (scan.hasNextLine()) {

				String line = scan.nextLine();
				String[] parts = line.split(" ");

				Country countryFrom = new Country(parts[0].trim());
				int vertixFrom = listOfCountries.indexOf(countryFrom);

				Country countryTo = new Country(parts[1].trim());
				int vertixTo = listOfCountries.indexOf(countryTo);

				double[] c1 = getLatLong(listOfCountries.get(vertixFrom).getLatitude(),
						listOfCountries.get(vertixFrom).getLongitude());

				double[] c2 = getLatLong(listOfCountries.get(vertixTo).getLatitude(),
						listOfCountries.get(vertixTo).getLongitude());

				double weigth = (int) getDistance(c1[0], c1[1], c2[0], c2[1]);

				WeightedEdge newFligth = new WeightedEdge(vertixFrom, vertixTo, weigth);
				listOfFlighs.add(newFligth);

			}

			scan.close();

		} catch (FileNotFoundException e) {

			Alert alert = new Alert(AlertType.WARNING);
			alert.setContentText("Error of reading File");
			alert.show();
		}

		return listOfFlighs;

	}

	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 * @param countries erticesFile File represent list of vertices (Countries) ,
	 *                  each line is considered to be country
	 * 
	 * @param flights   File represent list of directed edges (Flights) , each line
	 *                  is considered to be Flight
	 * 
	 * @return graph that is build from list of vertices (Countries) and list of
	 *         edges (Fligths)
	 * 
	 */
	public WeigthedSparseGraph<Country> buildGragh(File countries, File flights) {

		listOfCountries = getVerticesList(countries);
		listOfEdges = getEdgesList(flights, listOfCountries);
		WeigthedSparseGraph<Country> worldMapFlights = new WeigthedSparseGraph<Country>(listOfCountries, listOfEdges);
		return worldMapFlights;

	}

	///////////////////////////////////////////////////////////////////////////////////////

	public double getDistance(double fromLatitude, double fromLongitude, double toLatitude, double toLongitude) {

		// Convert latitudes nad longitude to radians
		fromLatitude = Math.toRadians(fromLatitude);
		fromLongitude = Math.toRadians(fromLongitude);
		toLatitude = Math.toRadians(toLatitude);
		toLongitude = Math.toRadians(toLongitude);

		// Haversine formula
		double term1Input = (toLatitude - fromLatitude) / 2;
		double term1 = Math.pow(Math.sin(term1Input), 2);

		double term2 = Math.cos(fromLatitude) * Math.cos(toLatitude);

		double term3Input = (toLongitude - fromLongitude) / 2;
		double term3 = Math.pow(Math.sin(term3Input), 2);

		double combineTerms = term1 + term2 * term3;

		double distance = KILO_METERS * 2 * Math.asin(Math.sqrt(combineTerms));

		return distance;

	}

	///////////////////////////////////////////////////////////////////////////////////////

	public double[] getLatLong(double xCoordinates, double yCoordinates) {

		int geo_left = -180;
		int geo_right = 180;
		int geo_top = 80;
		int geo_bottom = -80;

		// dimensions of the canvas
		int canvas_width = 1097;
		int canvas_height = 626;

		// get relative coordinate of pixel, should be on a scale of 0 to 1
		double rel_x = yCoordinates / canvas_width;
		double rel_y = xCoordinates / canvas_height;

		// linear interpolate to find latitude and longitude
		double latitude = geo_left + (geo_right - geo_left) * rel_x;
		double longitude = geo_top + (geo_bottom - geo_top) * rel_y;

		return new double[] { latitude, longitude };
	}

	///////////////////////////////////////////////////////////////////////////////////////

	public ArrayList<Country> getListOfCountries() {
		return listOfCountries;
	}

	public void setListOfCountries(ArrayList<Country> listOfCountries) {
		this.listOfCountries = listOfCountries;
	}

	public ArrayList<WeightedEdge> getListOfEdges() {
		return listOfEdges;
	}

	public void setListOfEdges(ArrayList<WeightedEdge> listOfEdges) {
		this.listOfEdges = listOfEdges;
	}

	public double getKILO_METERS() {
		return KILO_METERS;
	}

}
