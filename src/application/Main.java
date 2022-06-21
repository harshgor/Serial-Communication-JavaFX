package application;
	
import com.dustinredmond.fxtrayicon.FXTrayIcon;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			primaryStage.setTitle("Vasy Tray");
			
			FXTrayIcon trayIcon = new FXTrayIcon(primaryStage, getClass().getResource("icon.jpg"));
			trayIcon.show();
			
			trayIcon.setTrayIconTooltip("vasy tray");
			
			MenuItem exitAndRemove = new MenuItem("Exit and Remove");
			MenuItem about = new MenuItem("About");
			MenuItem reload = new MenuItem("Reload");
		
			exitAndRemove.setOnAction(e -> trayIcon.hide());
			
			trayIcon.addMenuItem(about);
			trayIcon.addMenuItem(reload);
			trayIcon.addMenuItem(exitAndRemove);
			
			Menu menuOptions = new Menu("Advanced");
			MenuItem siteManager = new MenuItem("Site Manager");
			MenuItem desktopShortcut = new MenuItem("Create Desktop Shortcut");
			MenuItem anonymousRequests = new MenuItem("Block Anonymous Requests");
			
			menuOptions.getItems().addAll(siteManager, desktopShortcut, anonymousRequests);
			trayIcon.addMenuItem(menuOptions);
			
			Menu menuOptions1 = new Menu("Diagnostic");
			MenuItem browseAppFolder = new MenuItem("Browse App Folder");
			MenuItem browseUserFolder = new MenuItem("Browse User Folder");
			MenuItem browseSharedFolder = new MenuItem("Browse Shared Folder");
			MenuItem notifications = new MenuItem("Show All notifications");
			MenuItem viewLogs = new MenuItem("View Logs(Live Feed)");
			MenuItem zipLogs = new MenuItem("Zip logs(To desktop)");
			
			menuOptions1.getItems().addAll(browseAppFolder, browseUserFolder, browseSharedFolder, notifications, viewLogs, zipLogs);
			trayIcon.addMenuItem(menuOptions1);
			
			primaryStage.setScene(scene);
			primaryStage.show();
			
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
