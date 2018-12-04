package ui;

import classes.Coordinate;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import logic.Client;


public class Tile extends StackPane {

    private static boolean yourTurn = false;

    private StackPane front;
    private StackPane back;
    private Rectangle frontRectangle;
    private Rectangle backRectangle;
    private ScaleTransition open;
    private ScaleTransition close;
    private PauseTransition pause;
    boolean flipped;
    private Text backText;
    private Text frontText;
    private Coordinate coordinate;
    private int value;


    public Tile(Coordinate coordinate, int size){
        this.coordinate = coordinate;
        pause = new PauseTransition(Duration.seconds(2));
        front = new StackPane();
        back = new StackPane();

        frontRectangle = new Rectangle();
        frontRectangle.widthProperty().bind(this.widthProperty().subtract(20));
        frontRectangle.heightProperty().bind(this.heightProperty().subtract(20));
        frontRectangle.maxWidth(Double.MAX_VALUE);
        frontRectangle.maxHeight(Double.MAX_VALUE);
        frontRectangle.setFill(Color.ORANGE);
        frontRectangle.setStroke(Color.BLACK);

        backRectangle = new Rectangle();
        backRectangle.widthProperty().bind(this.widthProperty().subtract(20));
        backRectangle.heightProperty().bind(this.heightProperty().subtract(20));
        backRectangle.maxWidth(Double.MAX_VALUE);
        backRectangle.maxHeight(Double.MAX_VALUE);
        backRectangle.setFill(Color.ORANGE);
        backRectangle.setStroke(Color.BLACK);


        front.getChildren().add(frontRectangle);
        back.getChildren().add(backRectangle);

        frontText = new Text();
        frontText.setText("front");
        frontText.setFont(Font.font(30));
        frontText.maxHeight(Double.MAX_VALUE);
        backText = new Text();
        backText.setText("back");
        backText.setFont(Font.font(30));

        front.getChildren().add(frontText);
        back.getChildren().add(backText);

        open = makeFlipAnimation(back, front, 200);
        close = makeFlipAnimation(front, back, 200);

        pause.setOnFinished(e -> close.play());
        //close.setOnFinished(e -> backText.setText(""));

        flipped = false;

        makeFlipAnimation(front,back,1).play();

        setOnMouseClicked(this::handleMouseClick);

    }

    public void handleMouseClick(MouseEvent event) {
        if (flipped) return;
        if(yourTurn){
            Client.getInstance().setNextMove(coordinate);
        }
    }
    public synchronized void open(String value) {
        //System.out.println("open");
        flipped = true;
        backText.setText(value);
        open.play();
    }

    public synchronized void close(int ms) {
        //backText.setText("");
        flipped = false;

        pause.play();
    }

    public boolean hasSameValue(Tile other) {
        return backText.getText().equals(other.backText.getText());
    }

    public static boolean isYourTurn() {
        return yourTurn;
    }

    public static void setYourTurn(boolean yourTurn) {
        Tile.yourTurn = yourTurn;
    }

    public Text getBackText() {
        return backText;
    }

    public void setBackText(Text backText) {
        this.backText = backText;
    }

    private ScaleTransition makeFlipAnimation(StackPane show, StackPane hide, int duration){
        ScaleTransition st = new ScaleTransition(Duration.millis(duration), hide);
        st.setFromX(1);
        st.setToX(0);

        ScaleTransition stShow = new ScaleTransition(Duration.millis(duration), show);
        stShow.setFromX(0);
        stShow.setToX(1);

        st.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                getChildren().remove(hide);
                getChildren().add(show);
                stShow.play();
            }
        });

        return st;
    }
}
