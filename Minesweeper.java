import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.util.Stack;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import java.util.Collections;
import java.util.ArrayList;


public class Minesweeper extends Application {
    private GridPane root;
    private int gridWidth = 8;
    private int gridLength = 8;
    private int numMines = 8;
    private ArrayList<Mine> mines = new ArrayList<>();
    private ArrayList<Boolean> mineLocations = new ArrayList<>();
    private Stack<Square> stack = new Stack<>();
    private Scene menuScene;
    private Scene scene;
    private Spinner<Integer> width;
    private Spinner<Integer> height;
    private Spinner<Integer> mineSpinner;
    private ArrayList<Square> flagged = new ArrayList<>();
    public Stage stage;

    public void start(Stage stageIn) {
        stage = stageIn;
        showMenu();
    }

    private void setupGame(){
        mineLocations.clear();
        mines.clear();
        flagged.clear();
        for(int i=0; i<numMines; i++){
            mineLocations.add(true);
        }
        for(int i=0; i<gridWidth*gridLength-numMines; i++){
            mineLocations.add(false);
        }
        Collections.shuffle(mineLocations);
        root = new GridPane();
        int index = 0;
        for(int i=0; i<gridWidth; i++) {
            for(int j=0; j<gridLength; j++) {
                if(mineLocations.get(index)){
                    Mine h = new Mine(i, j);
                    h.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            if(event.getButton() == MouseButton.PRIMARY){
                                for(Mine m : mines){
                                    m.show();
                                }
                                Alert alert = new Alert(Alert.AlertType.NONE);
                                alert.setTitle("Game Over");
                                alert.setHeaderText(null);
                                alert.setContentText("You touched a mine!");
                                ButtonType button = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
                                alert.getButtonTypes().setAll(button);
                                alert.showAndWait();
                                setupGame();
                                stage.setScene(menuScene);

                            } else if (event.getButton() == MouseButton.SECONDARY) {
                                Mine j = (Mine) event.getSource();
                                j.mark();
                                if(j.getMarked()) {
                                    flagged.add(j);
                                }else{
                                    flagged.remove(j);
                                }
                                checkFinished();
                            }
                        }
                    });
                    root.add(h, i, j);
                    mines.add(h);


                }else {
                    Square h = new Square(i, j);
                    h.setOnMouseClicked(new handleSquareClick());
                    root.add(h, i, j);
                }
                index++;
            }
        }
        scene = new Scene(root, 30*gridWidth, 30*gridLength);
    }

    private class handleSquareClick implements EventHandler<MouseEvent>{
        public void handle(MouseEvent event){
            if(event.getButton() == MouseButton.PRIMARY){
                Square s = (Square)event.getSource();
                stack.push(s);
                while(!stack.empty()){
                    s = stack.pop();
                    if(!s.getShown()) {
                        calcBorder(s);
                        if (!(s instanceof Mine)) {
                            s.show();
                        }
                        if (s.getBorderNum() == 0) {
                            for (int i = -1; i <= 1; i++) {
                                for (int j = -1; j <= 1; j++) {
                                    Square temp = get(s.getX() + i,s.getY() + j);
                                    if(temp.getX() != -1 && temp.getY() != -1) {
                                        stack.push(temp);
                                    }
                                }
                            }
                        }
                    }

                }
            } else if (event.getButton() == MouseButton.SECONDARY) {
                Square j = (Square) event.getSource();
                j.mark();
                if(j.getMarked()) {
                    flagged.add(j);
                }else{
                    flagged.remove(j);
                }
                checkFinished();
            }
        }
    }

    private Square get(int x, int y){
        if(x>=0 && x<gridWidth && y>=0 && y<gridLength) {
            for (Node o : root.getChildren()) {
                if (GridPane.getColumnIndex(o) == x && GridPane.getRowIndex(o) == y) {

                    return (Square) o;
                }
            }
        }
        return new Square(-1,-1);
    }

    private void calcBorder(Square s) {
        if(!s.getShown()) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (get(s.getX() + i, s.getY() + j) instanceof Mine) {
                        s.increment();
                    }
                }
            }
        }
    }

    private void checkFinished(){
        if(mines.size() == flagged.size()) {
            if(flagged.containsAll(mines)){
                Alert alert = new Alert(Alert.AlertType.NONE);
                alert.setTitle("Game Over");
                alert.setHeaderText(null);
                alert.setContentText("You cleared the minefield!");
                ButtonType button = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
                alert.getButtonTypes().setAll(button);
                alert.showAndWait();
                setupGame();
                stage.setScene(menuScene);
            }
        }
    }

    private void showMenu(){
        GridPane menu = new GridPane();
        Label widthLabel = new Label(" Width: ");
        Label heightLabel = new Label(" Height: ");
        Label mineLabel = new Label(" Number of Mines: ");
        width = new Spinner<>();
        height = new Spinner<>();
        mineSpinner = new Spinner<>();
        IntegerSpinnerValueFactory factory = new IntegerSpinnerValueFactory(4, 40);
        IntegerSpinnerValueFactory factory1 = new IntegerSpinnerValueFactory(4, 40);
        IntegerSpinnerValueFactory factory2 = new IntegerSpinnerValueFactory(1, 16);
        width.setValueFactory(factory);
        height.setValueFactory(factory1);
        mineSpinner.setValueFactory(factory2);
        width.valueProperty().addListener((obs, oldValue, newValue) ->
                {factory2.setMax(newValue * height.getValue());
                mineSpinner.setValueFactory(factory2);});
        height.valueProperty().addListener((obs, oldValue, newValue) ->
                {factory2.setMax(newValue * width.getValue());
                mineSpinner.setValueFactory(factory2);});
        Button start = new Button("Start");
        stage.setTitle("Minesweeper");
        start.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                gridWidth = width.getValue();
                gridLength = height.getValue();
                numMines = mineSpinner.getValue();
                setupGame();
                stage.setScene(scene);
                stage.show();
            }
        });

        menu.add(widthLabel, 0,0);
        menu.add(width, 1,0);
        menu.add(heightLabel, 0,1);
        menu.add(height, 1,1);
        menu.add(mineSpinner, 1, 2);
        menu.add(mineLabel, 0, 2);
        menu.add(start, 0,3);

        menuScene = new Scene(menu, 300,300);
        stage.setTitle("Minesweeper");
        stage.setScene(menuScene);
        stage.show();
    }



    public static void main(String[] args){
        launch(args);
    }


}
