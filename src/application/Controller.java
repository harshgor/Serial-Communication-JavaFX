package application;

import java.net.URL;
import java.util.ResourceBundle;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Controller implements Initializable {

	private Stage stage;
	private Scene scene;
	private Parent root;
	
	@FXML
	private ComboBox comPort;
	@FXML
	private ComboBox baudRate;
	@FXML
	private ComboBox dataBits;
	@FXML
	private ComboBox stopBits;
	@FXML
	private ComboBox parityBits;
	@FXML
	private ComboBox endLine;
	@FXML
	private Button openButton;
	@FXML
	private Button closeButton;
	@FXML
	private Button sendButton;
	@FXML
	private ProgressBar comStatus;
	@FXML
	private TextField TextField_dataToSend;
	@FXML
	private TextArea incomingData;

	SerialPort serialPort1;
	OutputStream outputStream1;
	String dataBuffer = "";
	
	ObservableList<String> portList;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		ObservableList<String> baudRateList = FXCollections.observableArrayList("4800", "9600", "38400", "57600",
				"115200");
		baudRate.setItems(baudRateList);
		baudRate.getSelectionModel().select(1);

		ObservableList<String> dataBitsList = FXCollections.observableArrayList("6", "7", "8");
		dataBits.setItems(dataBitsList);
		dataBits.getSelectionModel().selectFirst();

		ObservableList<String> stopBitsList = FXCollections.observableArrayList("1", "1.5", "2.0");
		stopBits.setItems(stopBitsList);
		stopBits.getSelectionModel().selectFirst();

		ObservableList<String> parityBitsList = FXCollections.observableArrayList("No_Parity", "Odd_Parity",
				"Even_Parity", "Space_Parity");
		parityBits.setItems(parityBitsList);
		parityBits.getSelectionModel().selectFirst();

		ObservableList<String> endLineList = FXCollections.observableArrayList("None", "New Line(\n)",
				"Change return(\r)", "Both(\n\r)");
		endLine.setItems(endLineList);
		endLine.getSelectionModel().selectFirst();

		// Button controls

		comPort.setDisable(false);
		//comStatus.setValue(0);

		openButton.setDisable(false);
		closeButton.setDisable(true);
		sendButton.setDisable(true);
		
		

		// Adding Items to ComboBox

		// ObservableList<String> comPortList =
		// FXCollections.observableArrayList("COM1", "COM2", "COM3");
		// comPort.setItems(comPortList);

//		comPort.getItems().clear();
//		SerialPort[] portLists = SerialPort.getCommPorts();
//		for (SerialPort port : portLists) {
//			ObservableList<String> comPortList = FXCollections.observableArrayList(port.getSystemPortName());
//			comPort.setItems(comPortList);
//		}
//		
		detectPort();
		

	}

	public void detectPort(){
		System.out.println("\n1/3 Now detecting port...");

        portList = FXCollections.observableArrayList();
        
        SerialPort[] serialPortNames = SerialPort.getCommPorts();
        for(SerialPort port: serialPortNames){
        	System.out.println(port.getSystemPortName());
        	portList.add(port.getSystemPortName());
        }
        
        comPort.setItems(portList);
	}
	
	public void OpenActionPerformed(ActionEvent e) {

		//Dialog<String> dialog = new Dialog<>();
		
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		
		try {
			SerialPort[] portLists = SerialPort.getCommPorts();
			serialPort1 = portLists[comPort.getSelectionModel().getSelectedIndex()];
			serialPort1.setBaudRate(Integer.parseInt(baudRate.getSelectionModel().getSelectedItem().toString()));
			serialPort1.setNumDataBits(Integer.parseInt(dataBits.getSelectionModel().getSelectedItem().toString()));
			serialPort1.setNumStopBits(Integer.parseInt(stopBits.getSelectionModel().getSelectedItem().toString()));
			serialPort1.setParity(parityBits.getSelectionModel().getSelectedIndex());
			serialPort1.openPort();

			if (serialPort1.isOpen()) {
				//dialog.setContentText(serialPort1.getDescriptivePortName() + "--Success to open");
				alert.setHeaderText("Port opened successfully");
				alert.setContentText(serialPort1.getDescriptivePortName() + "--Success to open" );
				alert.showAndWait();
				
				comPort.setDisable(true);
				// comStatus.setDisable(true);
				openButton.setDisable(true);
				closeButton.setDisable(false);
				sendButton.setDisable(false);

				Serial_EventBasedReading(serialPort1);

			} else {
				//dialog.setContentText(serialPort1.getDescriptivePortName() + "--Failed to open");
				alert.setContentText(serialPort1.getDescriptivePortName() + "--Failed to open" );
				alert.showAndWait();
			}

		} catch (ArrayIndexOutOfBoundsException a) {
			//dialog.setContentText("Please choose COM port");
			alert.setContentText("Please choose COM port");
			alert.showAndWait();
		} catch (Exception a) {
			// dialog.setContentText(AlertType.ERROR);
			alert.setContentText("Error occurred");
		}

	}

	public void SendActionPerformed(ActionEvent e) {

		//Dialog<String> dialog = new Dialog<>();

		outputStream1 = serialPort1.getOutputStream();
		String dataToSend = "";

		switch (endLine.getSelectionModel().getSelectedIndex()) {
		case 0:
			dataToSend = TextField_dataToSend.getText();
			break;
		case 1:
			dataToSend = TextField_dataToSend.getText() + "\n";
			break;
		case 2:
			dataToSend = TextField_dataToSend.getText() + "\r";
			break;
		case 3:
			dataToSend = TextField_dataToSend.getText() + "\r\n";
			break;
		}

		try {
			outputStream1.write(dataToSend.getBytes());
		} catch (IOException ex) {
			System.out.println(ex);
			//dialog.setContentText("Could not send data");
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("Could not send data");
		}
	}

	public void Serial_EventBasedReading(SerialPort activePort) {
		activePort.addDataListener(new SerialPortDataListener() {

			@Override
			public int getListeningEvents() {
				return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
			}

			@Override
			public void serialEvent(SerialPortEvent event) {
				byte[] newData = event.getReceivedData();
				for (int i = 0; i < newData.length; i++) {
					dataBuffer += (char) newData[i];
					incomingData.setText(dataBuffer);
				}
			}
		});
	}

	public void comPortPopupMenuWillBecomeVisible(ActionEvent e) {
//		comPort.getItems().clear();
//		SerialPort[] portLists = SerialPort.getCommPorts();
//		for (SerialPort port : portLists) {
//			ObservableList<String> comPortList = FXCollections.observableArrayList(port.getSystemPortName());
//			comPort.setItems(comPortList);
//		}
	}

	public void CloseActionPerformed(ActionEvent e) {

		if (serialPort1.isOpen()) {

			serialPort1.closePort();
			comPort.setDisable(false);
			// comStatus.setValue(0);
			openButton.setDisable(false);
			closeButton.setDisable(true);
			sendButton.setDisable(true);
		}
	}
	
	
	public void SwitchSceneToSerial(ActionEvent event) throws IOException{
		root = FXMLLoader.load(getClass().getResource("Main.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	
	public void SwitchSceneToSocket(ActionEvent event) throws IOException{
		Parent root = FXMLLoader.load(getClass().getResource("Socket.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}

}
