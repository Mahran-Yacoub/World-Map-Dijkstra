package Main;

import gui.MainControl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Driver extends Application {

	public static MainControl control;

	@Override
	public void start(Stage primaryStage) throws Exception {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("./../Main.fxml"));
		Parent root = loader.load();
		// Control class object
		control = loader.getController();
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Shortest Path Between Countries");
		primaryStage.show();

	}

	public static void main(String[] args) {

		Application.launch(args);
	}

}
