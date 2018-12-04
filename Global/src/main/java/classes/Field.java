package classes;

import java.io.Serializable;

public class Field implements Serializable {
    private int x;
    private int y;
    private int value;
    private boolean flipped;

    public Field(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;
        flipped = false;
    }

    //Copy constructor om info over speelveld door te geven aan de client
    //value wordt enkel meegegeven als het veld al omgedraaid is
    public Field(Field toCopy){
        this.x = toCopy.getX();
        this.y = toCopy.getY();
        this.flipped = toCopy.isFlipped();
        if(isFlipped()){
            this.value = toCopy.getValue();
        }
        else this.value = -1;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isFlipped() {
        return flipped;
    }

    public void setFlipped(boolean flipped) {
        this.flipped = flipped;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Field field = (Field) o;
        return x == field.x &&
                y == field.y;
    }
}
