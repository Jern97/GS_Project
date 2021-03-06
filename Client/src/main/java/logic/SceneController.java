package logic;

import interfaces.GameControllerInterface;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.rmi.RemoteException;

public class SceneController {

    private static final SceneController instance = new SceneController();
    private Stage prevStage = null;

    private SceneController() {
    }

    public static SceneController getInstance() {
        return instance;
    }

    public void showRegisterScene() {
        try {
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/register.fxml"));
            Scene scene = new Scene(root, 288, 400);
            stage.setTitle("Register");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();

            prevStage.close();
            prevStage = stage;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void showLoginScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Stage stage = new Stage();
            Parent root = loader.load();
            LoginController loginController = loader.getController();
            Scene scene = new Scene(root, 288, 400);
            stage.setTitle("Sign in");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();

            if (prevStage != null) prevStage.close();
            prevStage = stage;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void createGameScene(GameControllerInterface gc) {
        try {
            Client.getInstance().setGameController(gc);

            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gameView.fxml"));
            loader.setController(gc);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setTitle("Memory");
            stage.setResizable(true);
            stage.setScene(scene);
            stage.show();
            //Zorgen dat de controller de game verlaat als het scherm gesloten wordt
            stage.setOnCloseRequest(e -> {
                ((GameController) gc).leaveGame();
                SceneController.getInstance().showLobbyScene();
            });
            prevStage.close();
            prevStage = stage;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    public void showLobbyScene() {
        try {
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/lobby.fxml"));
            Scene scene = new Scene(root);
            stage.setTitle("Lobby");
            stage.setResizable(true);
            stage.setScene(scene);
            stage.setOnCloseRequest(event -> {
                try {
                    Client.getInstance().disconnect(true);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                SceneController.getInstance().showLoginScene();
            });
            stage.show();

            prevStage.close();
            prevStage = stage;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
