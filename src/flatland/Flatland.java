package flatland;/**
 * Created by Kyrre on 06.04.2016.
 */

import flatland.sprites.Cell;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Flatland extends Application {

    Cell[] cells;
    double playbackInterval = 3000;
    final double MAX_PLAYBACK_INTERVAL = 3000;
    final double MIN_PLAYBACK_INTERVAL = 100;
    long sinceLast = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        cells = new Cell[100];
        for (int i = 0; i < cells.length; i++) {
            cells[i] = new Cell(Cell.Type.BLANK);
        }

        GridPane board = new GridPane();
        board.setPadding(new Insets(5, 5, 5, 5));

        for (int i = 0,num = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                board.add(cells[num++], i, j);
            }
        }
        //GUI setup
        Label labelSpeedSlider = new Label("Animation speed:");
        Slider speedSlider = new Slider(100, MAX_PLAYBACK_INTERVAL, playbackInterval);
        speedSlider.setOrientation(Orientation.HORIZONTAL);
        speedSlider.setMaxWidth(300);
        speedSlider.setPrefWidth(300);
        speedSlider.setBlockIncrement(1000);
        speedSlider.setValue(MAX_PLAYBACK_INTERVAL- playbackInterval +MIN_PLAYBACK_INTERVAL);

        HBox hbox = new HBox();
        hbox.setPadding(new Insets(5, 5, 5, 5));
        hbox.setSpacing(5);
        hbox.getChildren().addAll(labelSpeedSlider,speedSlider);
        board.add(hbox,0,10,10,1);

        Scene scene = new Scene(board);
        primaryStage.setScene(scene);
        primaryStage.setTitle("TicTacToe By Legato");
        primaryStage.getIcons().add(new Image("flatland/img/icons.png", 50, 50, false, false));
        primaryStage.show();

        //GUI updates
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - sinceLast > playbackInterval *1000000){
                    sinceLast = now;
                    setButton((int) (Math.random()*100));
                }
            }
        }.start();

        speedSlider.valueProperty().addListener(cl -> {
            playbackInterval = MAX_PLAYBACK_INTERVAL-speedSlider.getValue()+MIN_PLAYBACK_INTERVAL;
            System.out.println("speed slider: "+ playbackInterval);
        });
    }

    boolean turn = true;

    private void setButton(int index){
        Cell.Type type = turn ? Cell.Type.FOOD : Cell.Type.POISON;
        turn = !turn;
        cells[index].changeType(type);
    }

}