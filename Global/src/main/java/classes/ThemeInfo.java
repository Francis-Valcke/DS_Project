package classes;

import java.io.Serializable;

public class ThemeInfo implements Serializable {
    int id;
    int size;
    String name;

    public ThemeInfo(int id, String name, int size) {
        this.id = id;
        this.size = size;
        this.name = name;
    }
}
