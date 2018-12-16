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

    public void setId(int id) {
        this.id = id;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public int getSize() {
        return size;
    }

    public String getName() {
        return name;
    }
}
