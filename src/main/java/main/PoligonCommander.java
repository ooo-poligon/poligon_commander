package main;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.*;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.util.Duration;
import utils.CurrencyCourse;

import java.io.IOException;
import java.sql.SQLException;

public class PoligonCommander extends Application {
    public static final String SPLASH_IMAGE = "/images/splash_2_0.png";

    private Pane splashLayout;
    private ProgressBar loadProgress;
    private Label progressText;
    private Stage mainStage = new Stage();
    private static final int SPLASH_WIDTH = 600;
    private static final int SPLASH_HEIGHT = 420;

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void init() {
        ImageView splash = new ImageView(new Image(SPLASH_IMAGE));
        loadProgress = new ProgressBar();
        loadProgress.setPrefWidth(SPLASH_WIDTH);
        progressText = new Label("Загрузка данных . . .");
        splashLayout = new VBox();
        //splashLayout.getChildren().addAll(splash);
        splashLayout.getChildren().addAll(splash, loadProgress, progressText);
        progressText.setAlignment(Pos.CENTER);
        splashLayout.setEffect(new DropShadow());
    }

    @Override
    public void start(final Stage initStage) throws Exception {
        final Task<Void> friendTask = new Task<Void>() {
            @Override
            protected Void call() throws IOException, InterruptedException, SQLException {

                updateMessage("Инициализация базы данных . . .");
                PCGUIController.getAllProductsList();
                PCGUIController.getAllFilesOfProgramList();
                PCGUIController.getAllQuantitiesList();
                PCGUIController.getAllCategoriesList();

                int count = 0;
                for (int i = 0; i < PCGUIController.allProductsTitles.size(); i++) {
                    Thread.sleep(0,001);
                    updateProgress(i + 1, PCGUIController.allProductsTitles.size());
                    String nextFriend = PCGUIController.allProductsTitles.get(i);
                    updateMessage("Загружаются товары . . .  " + nextFriend);
                    count = i;
                }

                updateMessage("Загружено " + count + " товаров. Идёт подготовка к запуску . . .");
                Thread.sleep(500);

                return null;
            }
        };

        showSplash(
                initStage,
                friendTask,
                () -> {showMainStage();}
        );
        new Thread(friendTask).start();
    }

    private void showMainStage () throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/PCGUI.fxml"));
        Scene scene = new Scene(root);
        mainStage.setScene(scene);
        mainStage.setTitle("\"Poligon Commander\" (version 2.1.0)");
        mainStage.show();
        mainStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
    }

    private void showSplash(
            final Stage initStage,
            Task<?> task,
            InitCompletionHandler initCompletionHandler
    ) {
        progressText.textProperty().bind(task.messageProperty());
        loadProgress.progressProperty().bind(task.progressProperty());
        task.stateProperty().addListener((observableValue, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                loadProgress.progressProperty().unbind();
                loadProgress.setProgress(1);
                initStage.toFront();
                FadeTransition fadeSplash = new FadeTransition(Duration.seconds(1.2), splashLayout);
                fadeSplash.setFromValue(1.0);
                fadeSplash.setToValue(0.0);
                fadeSplash.setOnFinished(actionEvent -> initStage.hide());
                fadeSplash.play();

                try {
                    initCompletionHandler.complete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } // todo add code to gracefully handle other task states.
        });

        Scene splashScene = new Scene(splashLayout);
        initStage.initStyle(StageStyle.UNDECORATED);
        final Rectangle2D bounds = Screen.getPrimary().getBounds();
        initStage.setScene(splashScene);
        initStage.setX(bounds.getMinX() + bounds.getWidth() / 2 - SPLASH_WIDTH / 2);
        initStage.setY(bounds.getMinY() + bounds.getHeight() / 2 - SPLASH_HEIGHT / 2);
        initStage.show();
    }

    public interface InitCompletionHandler {
        public void complete() throws IOException;
    }
}
