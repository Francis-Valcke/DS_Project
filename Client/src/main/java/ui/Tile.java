package ui;

import classes.Coordinate;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import logic.Client;


public class Tile extends StackPane {

    private static boolean yourTurn = false;

    private Rectangle frontRectangle;
    private Rectangle backRectangle;
    private ScaleTransition open;
    private ScaleTransition close;
    private PauseTransition pause;
    boolean flipped;
    private Coordinate coordinate;
    private int value;


    public Tile(Coordinate coordinate, int size){
        this.coordinate = coordinate;
        pause = new PauseTransition(Duration.seconds(2));

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


        //front.getChildren().add(frontText);
        //back.getChildren().add(backText);

        open = makeFlipAnimation(backRectangle, frontRectangle, 200);
        close = makeFlipAnimation(frontRectangle, backRectangle, 200);

        pause.setOnFinished(e -> close.play());
        //close.setOnFinished(e -> backText.setText(""));

        flipped = false;

        makeFlipAnimation(frontRectangle, backRectangle, 1).play();

        setOnMouseClicked(this::handleMouseClick);

    }

    public void handleMouseClick(MouseEvent event) {
        if (flipped) return;
        if(yourTurn){
            Client.getInstance().setNextMove(coordinate);
        }
    }

    public synchronized void open(int value) {
        this.value = value;
        //System.out.println("open");
        flipped = true;
        System.out.println(System.getProperty("user.dir"));
        Image img = new Image("themes/Colors/" + value + ".jpg");
        backRectangle.setFill(new ImagePattern(img));
        open.play();
    }

    public synchronized void close(int ms) {
        flipped = false;

        pause.play();
    }

    public static boolean isYourTurn() {
        return yourTurn;
    }

    public static void setYourTurn(boolean yourTurn) {
        Tile.yourTurn = yourTurn;
    }

    private ScaleTransition makeFlipAnimation(Rectangle show, Rectangle hide, int duration) {
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
