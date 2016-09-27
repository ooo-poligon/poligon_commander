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
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.*;
import javafx.util.Duration;
import modalwindows.AlertWindow;
import org.apache.commons.io.FileUtils;
import utils.CurrencyCourse;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class PoligonCommander extends Application {
    public static final String versionNumber = " версия 2.2.4";
    private static String d = (String.valueOf(Math.random())).substring(2);
    public static final String SPLASH_IMAGE = "/images/splash2.png";
    public static final File tmpDir = new File("\\\\Server03\\бд_сайта\\poligon_images\\temp_" + d);

    private Pane splashLayout;
    private ProgressBar loadProgress;
    private Label progressText;
    private Label versionNumberLabel;
    private Stage mainStage = new Stage();
    private static final int SPLASH_WIDTH = 600;
    private static final int SPLASH_HEIGHT = 420;


    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void init() {
        StackPane stackPane = new StackPane();
        ImageView splash = new ImageView(new Image(SPLASH_IMAGE));

        loadProgress = new ProgressBar();
        loadProgress.setPrefWidth(SPLASH_WIDTH);
        progressText = new Label("Загрузка данных . . .");
        versionNumberLabel = new Label(versionNumber);
        splashLayout = new VBox();
        stackPane.getChildren().addAll(splash, versionNumberLabel);
        splashLayout.getChildren().addAll(stackPane, loadProgress, progressText);
        versionNumberLabel.setStyle("-fx-font-size: 8px; -fx-font-weight: bold");
        versionNumberLabel.setTextFill(Color.WHITE);
        StackPane.setAlignment(versionNumberLabel, Pos.TOP_LEFT);
        progressText.setAlignment(Pos.CENTER);
        splashLayout.setEffect(new DropShadow());
    }

    @Override
    public void start(final Stage initStage) throws Exception {
        final Task<Void> friendTask = new Task<Void>() {
            @Override
            protected Void call() throws IOException, InterruptedException, SQLException {

                updateMessage("Инициализация базы данных . . .");
                try {
                    FileUtils.forceMkdir(tmpDir);
                } catch (IOException e) {
                    AlertWindow.showErrorMessage("Не удалось создать временную директорию на локальном сервере, проверьте доступность сетевого соединения,");
                }
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
        //scene.getStylesheets().add(this.getClass().getResource("styles/Styles.css").toExternalForm());
        mainStage.setScene(scene);
        mainStage.setTitle("\"Poligon Commander\" (" + versionNumber.subSequence(1, versionNumber.length()) + ")");
        mainStage.show();
        mainStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                try {
                    FileUtils.deleteDirectory(tmpDir);
                } catch (IOException e) {
                    AlertWindow.showErrorMessage(e.getMessage());
                }
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
