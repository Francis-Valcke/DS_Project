package logic;

import javafx.scene.image.Image;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Theme {
    int id;
    List<Image> images = new ArrayList<>();

    public Theme(int id, int size) {
        this.id = id;
        File themeDirectory = new File("Client/src/main/resources/themes/" + id);
        for (File picture : themeDirectory.listFiles()) {
            if (images.size() == size) break;
            images.add(new Image("file:" + picture.getPath()));
        }
    }

    public static void saveNewTheme(int id, List<byte[]> images) {
        try {
            int i = 0;
            for (byte[] byteArray : images) {
                FileUtils.writeByteArrayToFile(new File("Client/src/main/resources/themes/" + id + "/" + i + ".jpg"), byteArray);
                i++;
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public Image getPicture(int id) {
        return images.get(id);
    }


}
