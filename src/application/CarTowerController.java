package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

public class CarTowerController implements Initializable{
	@FXML
	private Button btnServerStart;
	@FXML
	private Button btnServerStop;
	@FXML
	private ListView<Car> lvCar;
	@FXML
	private ListView<User> lvUser;
	@FXML
	private TextArea textarea;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		btnServerStart.setOnAction(e->{
			
		});
		btnServerStop.setOnAction(e->{
			
		});
	}
	
}
