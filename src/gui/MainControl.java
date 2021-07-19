package gui;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Stack;
import org.controlsfx.control.textfield.TextFields;
import Main.Tools;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import weigthed.graph.Country;
import weigthed.graph.WeightedEdge;
import weigthed.graph.WeigthedSparseGraph;

public class MainControl implements Initializable {

	public ArrayList<String> countriesName;

	private WeigthedSparseGraph<Country> worldMapGraph;

	private ObservableList<Row> flighs;

	private boolean isSource = true;

	private boolean isSourceAndDestinationDetermined = false;

	private Node sourcePoint;

	private Node destinationPoint;

	private ArrayList<Country> countries;

	private ArrayList<WeightedEdge> edgesList;

	private boolean isThereLines = false;

	private double totalDestance = 0;

	private File countriesFile;

	private File flighsFile;

	@FXML
	private Pane pane;

	@FXML
	private TextField sourceCountry;

	@FXML
	private TextField destinationCountry;

	@FXML
	private TextArea outputDestance;

	@FXML
	private TableView<Row> tableDetails;

	@FXML
	private TableColumn<Row, String> from;

	@FXML
	private TableColumn<Row, String> to;

	@FXML
	private TableColumn<Row, String> distance;

	/////////////////////////////////////////////////////////////////////////////////////////

	/** Clear All Data */
	@FXML
	void clear(ActionEvent event) {
		clearAll();
	}

	private void clearAll() {

		this.sourceCountry.clear();
		this.destinationCountry.clear();
		this.outputDestance.clear();
		this.totalDestance = 0;

		if (flighs != null) {
			this.flighs.clear();
		}

		isSource = true;
		isSourceAndDestinationDetermined = false;

		if (sourcePoint != null) {

			sourcePoint.setFill(Color.BLACK);
			sourcePoint = null;
		}

		if (destinationPoint != null) {

			destinationPoint.setFill(Color.BLACK);
			destinationPoint = null;
		}

		if (isThereLines) {

			ObservableList<javafx.scene.Node> componentsOfPane = pane.getChildren();
			int length = componentsOfPane.size();
			ArrayList<javafx.scene.Node> removedNodes = new ArrayList<>();
			for (int i = 0; i < length; i++) {

				javafx.scene.Node node = componentsOfPane.get(i);
				if (node instanceof Line) {

					removedNodes.add(node);
				}

			}

			for (javafx.scene.Node node : removedNodes) {

				componentsOfPane.remove(node);
			}

			this.isThereLines = false;
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////

	/** Start Edit Stage */
	@FXML
	void edit(ActionEvent event) {

		try {

			Stage stage = new Stage();
			Parent root = FXMLLoader.load(getClass().getResource("./../EditGUI.fxml"));
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle("Edit Stage");
			clearAll();
			stage.show();

		} catch (Exception e) {

			Alert alert = new Alert(AlertType.WARNING);
			alert.setContentText("Edit Start Error");
			alert.show();
		}

	}

	/////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * We Use this method to determine and draw the shortest path between source and
	 * destination countries
	 */
	@FXML
	void run(ActionEvent event) {

		boolean flagMouse = false, flagKeyboard = false;

		String source = this.sourceCountry.getText().trim();
		String destination = this.destinationCountry.getText().trim();

		if (!source.equals("") && !destination.equals("")) {

			flagKeyboard = true;
		}

		if (this.sourcePoint != null && this.destinationPoint != null) {

			flagMouse = true;

		}

		if (flagKeyboard && flagMouse) {

			Alert alert = new Alert(AlertType.WARNING);
			alert.setContentText("Choose either by keyboard\nOr Mouse Not Both");
			alert.show();
			clearAll();

		} else if (flagMouse) {

			if (!isThereLines) {

				Country start = this.sourcePoint.getCountry();
				Country end = this.destinationPoint.getCountry();
				runByMouse(start, end);

			} else {

				Alert alert = new Alert(AlertType.WARNING);
				alert.setContentText("Clear Exist Lines First");
				alert.show();
			}

		} else if (flagKeyboard) {

			if (!isThereLines) {

				if (source.equals(destination)) {

					Alert alert = new Alert(AlertType.WARNING);
					alert.setContentText("Enter Two \nDifferent Countries");
					alert.show();

				} else {

					runByKeyboard(source, destination);

				}

			} else {

				Alert alert = new Alert(AlertType.WARNING);
				alert.setContentText("Clear Exist Lines First");
				alert.show();
			}

		} else {

			Alert alert = new Alert(AlertType.WARNING);
			alert.setContentText("Enter source and destination");
			alert.show();
		}

	}

	/////////////////////////////////////////////////////////////////////////////////////////

	private void runByKeyboard(String startCountry, String endCountry) {

		Country source = new Country(startCountry);
		Country destination = new Country(endCountry);
		runByMouse(source, destination);

	}

	private void runByMouse(Country source, Country destination) {

		int sourceIndex = this.countries.indexOf(source);
		int destinationIndex = this.countries.indexOf(destination);

		if (sourceIndex == -1) {

			Alert alert = new Alert(AlertType.WARNING);
			alert.setContentText(source.getName() + "\nDoes Not Exist");
			alert.show();
			return;
		}

		if (destinationIndex == -1) {

			Alert alert = new Alert(AlertType.WARNING);
			alert.setContentText(destination.getName() + "\nDoes Not Exist");
			alert.show();
			return;
		}

		Stack<Country> shortestPath = worldMapGraph.getShortestPath(sourceIndex, destinationIndex);
		if (shortestPath != null) {

			this.isThereLines = true;
			ArrayList<Row> rows = new ArrayList<MainControl.Row>();

			while (shortestPath.size() > 1) {

				Country country1 = shortestPath.pop();
				Country country2 = shortestPath.pop();
				shortestPath.push(country2);

				Line flight = new Line(country1.getLongitude(), country1.getLatitude(), country2.getLongitude(),
						country2.getLatitude());
				pane.getChildren().add(flight);

				Row row = fillTable(country1, country2, edgesList, countries);
				rows.add(row);

			}

			flighs = FXCollections.observableArrayList(rows);
			tableDetails.setItems(flighs);
			this.outputDestance.setText(totalDestance + " In KM");

		} else {

			Alert alert = new Alert(AlertType.WARNING);
			alert.setContentText("There is no path exist");
			alert.show();
		}

	}

	/////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * This method will use to get a details of each part of path to fill a table
	 */
	private Row fillTable(Country country1, Country country2, ArrayList<WeightedEdge> edgesList,
			ArrayList<Country> countries) {

		String countryName1 = country1.getName();
		String countryName2 = country2.getName();

		int indexOfCountry1 = countries.indexOf(country1);
		int indexOfCountry2 = countries.indexOf(country2);

		WeightedEdge edge = new WeightedEdge(indexOfCountry1, indexOfCountry2);
		int indexOfEdge = edgesList.indexOf(edge);
		double distance = edgesList.get(indexOfEdge).getWeight();
		String distanceAsString = distance + " KM";

		Row row = new Row(countryName1, countryName2, distanceAsString);
		totalDestance += distance;

		return row;

	}

	/////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// 1- Build Graph
		Tools tools = new Tools();
		countriesFile = new File("D:\\Java\\Java_2021\\Project3_Version1\\src\\Countries.txt");
		flighsFile = new File("D:\\Java\\Java_2021\\Project3_Version1\\src\\Flights.txt");
		worldMapGraph = tools.buildGragh(countriesFile, flighsFile);
		

		this.countries = tools.getListOfCountries();
		this.edgesList = tools.getListOfEdges();

		// 2- Set Countries
		setPoints(tools.getListOfCountries());

		// 3- Setup table of flights
		from.setCellValueFactory(new PropertyValueFactory<Row, String>("source"));
		to.setCellValueFactory(new PropertyValueFactory<Row, String>("destination"));
		distance.setCellValueFactory(new PropertyValueFactory<Row, String>("destance"));

		// Set Auto Complete For Text Fields
		countriesName = fillAutoComplete(countries);
		TextFields.bindAutoCompletion(this.sourceCountry, countriesName);
		TextFields.bindAutoCompletion(this.destinationCountry, countriesName);

	}

	/////////////////////////////////////////////////////////////////////////////////////////

	private ArrayList<String> fillAutoComplete(ArrayList<Country> countries) {

		ArrayList<String> names = new ArrayList<String>();
		for (Country c : countries) {

			String name = c.getName();
			names.add(name);
		}

		return names;
	}

	/////////////////////////////////////////////////////////////////////////////////////////

	/** Set Pints on Map To represent Countries */
	private void setPoints(ArrayList<Country> listOfCountries) {

		for (Country c : listOfCountries) {

			double latitudeY = c.getLatitude();
			double longitudeX = c.getLongitude();
			Node point = new Node(longitudeX, latitudeY, 5, c);
			point.setOnMouseClicked(this::setCircleAction);
			pane.getChildren().add(point);

		}
	}

	/**
	 * We use this as call back method to determine source and destination country
	 */
	public void setCircleAction(MouseEvent e) {

		Object object = e.getSource();

		if ((object instanceof Node) && !isSourceAndDestinationDetermined) {

			Node point = (Node) object;

			if (isSource) {

				point.setFill(Color.GREEN);
				isSource = false;
				sourcePoint = point;

			} else {

				point.setFill(Color.RED);
				isSourceAndDestinationDetermined = true;
				destinationPoint = point;

			}
		}

	}

	/////////////////////////////////////////////////////////////////////////////////////////

	public ArrayList<String> getCountriesName() {
		return countriesName;
	}

	public WeigthedSparseGraph<Country> getWorldMapGraph() {
		return worldMapGraph;
	}

	public File getCountriesFile() {
		return countriesFile;
	}

	public File getFlighsFile() {
		return flighsFile;
	}

	public Pane getPane() {
		return pane;
	}

	public TextField getSourceCountry() {
		return sourceCountry;
	}

	public TextField getDestinationCountry() {
		return destinationCountry;
	}

	
	public ArrayList<WeightedEdge> getEdgesList() {
		return edgesList;
	}

	
	public ArrayList<Country> getCountries() {
		return countries;
	}

	/////////////////////////////////////////////////////////////////////////////////////////


	/** A class to represent row in tableview */
	public static class Row {

		private String source;
		private String destination;
		private String destance;

		public Row() {

		}

		public Row(String source, String destination, String destance) {

			this.source = source;
			this.destination = destination;
			this.destance = destance;
		}

		public String getSource() {
			return source;
		}

		public void setSource(String source) {
			this.source = source;
		}

		public String getDestination() {
			return destination;
		}

		public void setDestination(String destination) {
			this.destination = destination;
		}

		public String getDestance() {
			return destance;
		}

		public void setDestance(String destance) {
			this.destance = destance;
		}

	}

	static class Node extends Circle {

		private Country country;

		public Node(double x, double y, double radius, Country country) {

			super(x, y, radius);
			this.country = country;
		}

		public Country getCountry() {
			return country;
		}

		public void setCountry(Country country) {
			this.country = country;
		}

	}

}
