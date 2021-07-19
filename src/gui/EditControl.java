package gui;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Scanner;
import org.controlsfx.control.textfield.TextFields;
import Main.Driver;
import Main.Tools;
import gui.MainControl.Node;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import weigthed.graph.Country;
import weigthed.graph.WeightedEdge;

public class EditControl implements Initializable {

	@FXML
	private TextField countryName;

	@FXML
	private TextField longitude;

	@FXML
	private TextField latitude;

	@FXML
	private ComboBox<String> addedSourceCountry;

	@FXML
	private ComboBox<String> addedDestinationCountry;


	@FXML
	private ComboBox<String> removedCountry;

	@FXML
	private ComboBox<String> removedSourceFlight;

	@FXML
	private ComboBox<String> removedDestinationFlight;

	//////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////

	@FXML
	void addCountry(ActionEvent event) {

		String name = this.countryName.getText().trim();
		String longitudeX = this.longitude.getText().trim();
		String latitudeY = this.latitude.getText().trim();

		if (!name.equals("") && !latitudeY.equals("") && !longitudeX.equals("")) {

			try {

				double x = new Double(longitudeX);
				double y = new Double(latitudeY);

				Country newCountry = new Country(name, x, y);
				boolean isadded = Driver.control.getWorldMapGraph().addVertex(newCountry);

				if (isadded) {

					addToCountriesFile(Driver.control.getCountriesFile(), name, x, y);
					editAllComboBoxes(name, 1);
					MainControl.Node node = new Node(x, y, 5, newCountry);
					node.setOnMouseClicked(Driver.control::setCircleAction);
					Driver.control.getCountries().add(newCountry);
					Driver.control.getPane().getChildren().add(node);
					reFillAutoComplete(Driver.control.getSourceCountry(), Driver.control.getCountriesName());
					reFillAutoComplete(Driver.control.getDestinationCountry(), Driver.control.getCountriesName());

					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setContentText("Successful");
					alert.show();

				} else {

					Alert alert = new Alert(AlertType.WARNING);
					alert.setContentText("Failed , it is already exist");
					alert.show();

				}

			} catch (Exception e) {

				Alert alert = new Alert(AlertType.WARNING);
				alert.setContentText("Enter Correct X and Y");
				alert.show();
			}

		} else {

			Alert alert = new Alert(AlertType.WARNING);
			alert.setContentText("Enter name , X and Y");
			alert.show();
		}

	}

	//////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////

	private void addToCountriesFile(File countriesFile, String name, double x, double y) {

		// Format of each line --> name latitude longitude -- >name Y X

		String newLine = name + " " + y + " " + x;

		try {

			FileWriter fr = new FileWriter(countriesFile, true);
			PrintWriter pr = new PrintWriter(fr);
			pr.print("\n" + newLine);

			fr.close();
			pr.close();

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	//////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////

	@FXML
	void addFlight(ActionEvent event) {

		String source = this.addedSourceCountry.getSelectionModel().getSelectedItem();
		String destination = this.addedDestinationCountry.getSelectionModel().getSelectedItem();

		if (source != null && destination != null) {

			try {

				Country countrySource = new Country(source);
				Country countryDestination = new Country(destination);
				
				ArrayList<Country> list = (ArrayList<Country>) Driver.control.getWorldMapGraph().getVertices();
				int indexSource = list.indexOf(countrySource);
				int indexDestination = list.indexOf(countryDestination);

				Tools t = new Tools();
				double[] c1 = t.getLatLong(list.get(indexSource).getLatitude(), list.get(indexSource).getLongitude());
				double[] c2 = t.getLatLong(list.get(indexDestination).getLatitude(),
						list.get(indexDestination).getLongitude());
				double weigth = (int) t.getDistance(c1[0], c1[1], c2[0], c2[1]);

				WeightedEdge newEdge = new WeightedEdge(indexSource, indexDestination, weigth);
				Driver.control.getEdgesList().add(newEdge);
				boolean isAdded = Driver.control.getWorldMapGraph().addEdge(newEdge);
				

				if (isAdded) {

					Driver.control.getEdgesList().add(newEdge);
					addFlightToFile(Driver.control.getFlighsFile(), source, destination, weigth+"");
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setContentText("Successful");
					alert.show();

				} else {

					Alert alert = new Alert(AlertType.WARNING);
					alert.setContentText("Failed , its Already Exist");
					alert.show();
				}

			} catch (Exception e) {

				Alert alert = new Alert(AlertType.WARNING);
				alert.setContentText("Enter correct destance");
				alert.show();

			}

		} else {

			Alert alert = new Alert(AlertType.WARNING);
			alert.setContentText("Select source and \ndestination countries\n and destance");
			alert.show();
		}

	}

	//////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////

	private void addFlightToFile(File flighsFile, String source, String destination, String destance) {

		// Format of each line --> From To Destance

		String newLine = source + " " + destination + " " + destance;

		try {

			FileWriter fr = new FileWriter(flighsFile, true);
			PrintWriter pr = new PrintWriter(fr);
			pr.print("\n" + newLine);

			fr.close();
			pr.close();

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	//////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////

	@FXML
	void removeCountry(ActionEvent event) {

		String name = this.removedCountry.getSelectionModel().getSelectedItem();
		if (name != null) {

			Country removedCountry = new Country(name);
			Driver.control.getWorldMapGraph().remove(removedCountry);
			removeCountryFromFile(name, Driver.control.getCountriesFile());
			removeFlightsFromFile(name, Driver.control.getFlighsFile());
			editAllComboBoxes(name, 2);
			removeFromPane(removedCountry);
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setContentText("Successful");
			alert.show();

		} else {

			Alert alert = new Alert(AlertType.WARNING);
			alert.setContentText("Select Country please");
			alert.show();
		}

	}

	private void removeFromPane(Country removedCountry) {

		ObservableList<javafx.scene.Node> componentsOfPane = Driver.control.getPane().getChildren();
		int length = componentsOfPane.size();
		for (int i = 0; i < length; i++) {

			javafx.scene.Node node = componentsOfPane.get(i);
			if (node instanceof Node) {

				Node point = (Node) node;
				Country country = point.getCountry();

				if (country.equals(removedCountry)) {

					componentsOfPane.remove(node);
					reFillAutoComplete(Driver.control.getSourceCountry(), Driver.control.getCountriesName());
					reFillAutoComplete(Driver.control.getDestinationCountry(), Driver.control.getCountriesName());
					return;
				}

			}
		}

	}

	//////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////

	private void reFillAutoComplete(TextField field, ArrayList<String> countriesName) {

		TextFields.bindAutoCompletion(field, countriesName);
	}

	//////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	public void removeFlightsFromFile(String name, File flighsFile) {

		try {

			Scanner scan = new Scanner(flighsFile);
			File tempFile = new File(flighsFile.getParent() + "\\ temp.txt");
			if (!tempFile.exists()) {

				tempFile.createNewFile();
			}

			FileWriter fr = new FileWriter(tempFile);
			PrintWriter pr = new PrintWriter(fr);
			boolean isFirstLine = true;
			while (scan.hasNextLine()) {

				String line = scan.nextLine();

				if (!line.contains(name)) {

					if (isFirstLine) {

						pr.print(line);
						isFirstLine = false;

					} else {

						pr.print("\n" + line);

					}
				}

			}

			scan.close();
			pr.close();
			fr.close();

			flighsFile.delete();
			tempFile.renameTo(new File(tempFile.getParent() + "\\Flights.txt"));

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	//////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////

	private void removeCountryFromFile(String name, File countriesFile) {

		try {

			Scanner scan = new Scanner(countriesFile);
			File tempFile = new File(countriesFile.getParent() + "\\ temp.txt");
			if (!tempFile.exists()) {

				tempFile.createNewFile();
			}

			FileWriter fr = new FileWriter(tempFile);
			PrintWriter pr = new PrintWriter(fr);
			boolean isFirstLine = true;
			while (scan.hasNextLine()) {

				String line = scan.nextLine();
				String namePart = line.substring(0, line.indexOf(" "));
				if (!name.equals(namePart)) {

					if (isFirstLine) {

						pr.print(line);
						isFirstLine = false;

					} else {

						pr.print("\n" + line);

					}
				}

			}

			scan.close();
			pr.close();
			fr.close();

			countriesFile.delete();
			tempFile.renameTo(new File(tempFile.getParent() + "\\Countries.txt"));

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	//////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////

	@FXML
	void removeFlight(ActionEvent event) {

		String source = this.removedSourceFlight.getSelectionModel().getSelectedItem();
		String destination = this.removedDestinationFlight.getSelectionModel().getSelectedItem();

		if (source != null && destination != null) {

			Country countrySource = new Country(source);
			Country countryDestination = new Country(destination);
			int indexSource = Driver.control.getWorldMapGraph().getVertices().indexOf(countrySource);
			int indexDestination = Driver.control.getWorldMapGraph().getVertices().indexOf(countryDestination);
			boolean isRemoved = Driver.control.getWorldMapGraph().remove(indexSource, indexDestination);

			if (isRemoved) {

				removeAspecificFlightFromAFile(source, destination, Driver.control.getFlighsFile());
				Alert alert = new Alert(AlertType.WARNING);
				alert.setContentText("Successfuk removed");
				alert.show();

			} else {

				Alert alert = new Alert(AlertType.WARNING);
				alert.setContentText("Failed , it is already not exist");
				alert.show();
			}

		} else {

			Alert alert = new Alert(AlertType.WARNING);
			alert.setContentText("Enter Source and destination");
			alert.show();
		}

	}

	//////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////

	private void removeAspecificFlightFromAFile(String source, String destination, File flighsFile) {

		try {

			Scanner scan = new Scanner(flighsFile);
			File tempFile = new File(flighsFile.getParent() + "\\ temp.txt");
			if (!tempFile.exists()) {

				tempFile.createNewFile();
			}

			FileWriter fr = new FileWriter(tempFile);
			PrintWriter pr = new PrintWriter(fr);
			boolean isFirstLine = true;
			while (scan.hasNextLine()) {

				String line = scan.nextLine();
				String[] parts = line.split(" ");

				if (!source.equals(parts[0]) && !destination.equals(parts[1])) {

					if (isFirstLine) {

						pr.print(line);
						isFirstLine = false;

					} else {

						pr.print("\n" + line);

					}
				}

			}

			scan.close();
			pr.close();
			fr.close();

			flighsFile.delete();
			tempFile.renameTo(new File(tempFile.getParent() + "\\Flights.txt"));

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	//////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		fillComboBoxes(Driver.control.getCountriesName());

	}

	//////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * This method is used to fill all comboboxes with countries names that are
	 * saves in
	 * 
	 * @param countriesName
	 */
	private void fillComboBoxes(ArrayList<String> countriesName) {

		this.removedCountry.getItems().addAll(countriesName);
		this.addedSourceCountry.getItems().addAll(countriesName);
		this.addedDestinationCountry.getItems().addAll(countriesName);
		this.removedSourceFlight.getItems().addAll(countriesName);
		this.removedDestinationFlight.getItems().addAll(countriesName);

	}

	//////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 * This method add or remove a specific country name to all combo boxes in UI
	 * 
	 * @param name        name of country
	 * @param addOrRemove 1:add 2:remove
	 * 
	 */
	private void editAllComboBoxes(String name, int addOrRemove) {

		if (addOrRemove == 1) {

			Driver.control.getCountriesName().add(name);
			this.removedCountry.getItems().add(name);
			this.addedSourceCountry.getItems().add(name);
			this.addedDestinationCountry.getItems().add(name);
			this.removedSourceFlight.getItems().add(name);
			this.removedDestinationFlight.getItems().add(name);

		} else if (addOrRemove == 2) {

			System.out.println(Driver.control.getCountriesName().remove(name));
			this.removedCountry.getItems().remove(name);
			this.addedSourceCountry.getItems().remove(name);
			this.addedDestinationCountry.getItems().remove(name);
			this.removedSourceFlight.getItems().remove(name);
			this.removedDestinationFlight.getItems().remove(name);
		}

	}

}
