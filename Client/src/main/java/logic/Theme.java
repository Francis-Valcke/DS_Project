package logic;

import javafx.scene.image.Image;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Theme {
    int id;
    List<Image> images = new ArrayList<>();

    public Theme(int id) {
        this.id = id;
        System.out.println(System.getProperty("user.dir"));
        File themeDirectory = new File("Client/src/main/resources/themes/" + id);
        for (File picture : themeDirectory.listFiles()) {
            images.add(new Image("file:"+picture.getPath()));
        }
    }
    public Image getPicture(int id){
        return images.get(id);
    }


    public static void saveNewTheme(int id, List<byte[]> images) {
        try {
            int i = 0;
            for(byte[] byteArray: images) {
                System.out.println(System.getProperty("user.dir"));
                FileUtils.writeByteArrayToFile(new File("Client/src/main/resources/themes/"+id+"/"+i+".jpg"), byteArray);
                i++;
            }
        }
        catch(IOException ioe){
            ioe.printStackTrace();
        }
    }


}
